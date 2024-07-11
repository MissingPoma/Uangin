package com.example.uangin

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.uangin.EditCategory
import com.example.uangin.R
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.PemasukanDao
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101

class SettingFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvNotifTime: TextView
    private lateinit var switchNotif: Switch
    private lateinit var rightArrowPin: ImageButton
    private lateinit var tvPin: TextView
    private var notificationPendingIntent: PendingIntent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val savedTime = sharedPreferences.getString("notif_time", "12:00")
        val (hour, minute) = savedTime!!.split(":").map { it.toInt() }
        val rightArrowCetak = view.findViewById<ImageButton>(R.id.rightArrowCetak)
        val rightArrowKelola = view.findViewById<ImageButton>(R.id.rightArrowKelola)
        val righArrowSetel = view.findViewById<ImageButton>(R.id.righArrowSetel)
        tvNotifTime = view.findViewById(R.id.tvNotifTime)
        switchNotif = view.findViewById(R.id.switchNotif)
        tvPin = view.findViewById(R.id.tvPin)
        updatePinStatus()
        rightArrowPin = view.findViewById(R.id.rightArrowPin) // Inisialisasi
        rightArrowPin.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isPinSet = sharedPreferences.contains("app_pin")

            val intent = if (isPinSet) {
                Intent(requireContext(), ChangeOrDisablePinActivity::class.java) // Activity baru untuk mengubah/menonaktifkan PIN
            } else {
                Intent(requireContext(), SetPinActivity::class.java)
            }
            startActivity(intent)
        }

        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_NOTIFICATION_POLICY
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    } else {
                        showTimePickerDialog()
                    }
                } else {
                    showTimePickerDialog()
                }
            } else {
                tvNotifTime.text = "Tidak Aktif"
                cancelScheduledNotification()
            }
        }

        rightArrowKelola.setOnClickListener {
            val intent = Intent(activity, EditCategory::class.java)
            startActivity(intent)
        }

        righArrowSetel.setOnClickListener {
            showConfirmationDialog()
        }

        rightArrowCetak.setOnClickListener {
            showPrintConfirmationDialog()
        }
    }

    private fun updatePinStatus() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isPinSet = sharedPreferences.contains("app_pin")

        tvPin.text = if (isPinSet) "Aktif" else "Tidak aktif"
    }

    private fun showPrintConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Cetak Data")
            .setMessage("Apakah Anda ingin mencetak data?")
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val database = AppDatabase.getDatabase(requireContext())
                        val pemasukanDao: PemasukanDao = database.pemasukanDao()
                        val pengeluaranDao: PengeluaranDao = database.pengeluaranDao()

                        val listPemasukan = withContext(Dispatchers.IO) { pemasukanDao.getAll() }
                        val listPengeluaran = withContext(Dispatchers.IO) { pengeluaranDao.getAll() }

                        printData(listPemasukan, listPengeluaran)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Terjadi kesalahan saat mencetak data", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    private fun printData(listPemasukan: List<Pemasukan>, listPengeluaran: List<Pengeluaran>) {
        lifecycleScope.launch {
            try {
                val timeStamp = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                val fileName = "Laporan_UangIn_$timeStamp.pdf"

                // Create a new PdfDocument
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size page

                // Start a new page
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = Paint()

                // Set up table headers
                val tableHeaderBackground = Paint()
                tableHeaderBackground.color = Color.parseColor("#CCCCCC")
                tableHeaderBackground.style = Paint.Style.FILL

                val tableHeaderPaint = Paint()
                tableHeaderPaint.color = Color.BLACK
                tableHeaderPaint.textSize = 12f

                val tableContentPaint = Paint()
                tableContentPaint.color = Color.BLACK
                tableContentPaint.textSize = 12f

                val tableMarginLeft = 40f
                val tableMarginTop = 80f
                val cellMargin = 10f
                val rowHeight = 40f

                // Draw headers
                var xPos = tableMarginLeft
                var yPos = tableMarginTop

                canvas.drawRect(tableMarginLeft, yPos, page.info.pageWidth.toFloat() - tableMarginLeft, yPos + rowHeight, tableHeaderBackground)

                canvas.drawText("Tanggal", xPos, yPos + rowHeight / 2, tableHeaderPaint)
                xPos += 120f + cellMargin
                canvas.drawText("Kategori", xPos, yPos + rowHeight / 2, tableHeaderPaint)
                xPos += 180f + cellMargin
                canvas.drawText("Jumlah", xPos, yPos + rowHeight / 2, tableHeaderPaint)
                xPos += 120f + cellMargin
                canvas.drawText("Catatan", xPos, yPos + rowHeight / 2, tableHeaderPaint)
                xPos += 120f + cellMargin
                canvas.drawText("Jenis", xPos, yPos + rowHeight / 2, tableHeaderPaint)
                yPos += rowHeight

                // Draw pemasukan data
                if (listPemasukan.isNotEmpty()) {
                    for (pemasukan in listPemasukan) {
                        xPos = tableMarginLeft
                        canvas.drawRect(tableMarginLeft, yPos, page.info.pageWidth.toFloat() - tableMarginLeft, yPos + rowHeight, tableHeaderBackground)

                        val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(pemasukan.tanggal)
                        Log.d("DEBUG", "Formatted Date: $formattedDate")
                        canvas.drawText(formattedDate, xPos, yPos + rowHeight / 2, tableContentPaint)
                        xPos += 120f + cellMargin
                        pemasukan.kategori?.let { canvas.drawText(it, xPos, yPos + rowHeight / 2, tableContentPaint) }
                        xPos += 180f + cellMargin

                        // Set color to green for pemasukan
                        tableContentPaint.color = Color.GREEN
                        canvas.drawText("${pemasukan.jumlah}", xPos, yPos + rowHeight / 2, tableContentPaint)

                        // Reset color back to black
                        tableContentPaint.color = Color.BLACK
                        xPos += 120f + cellMargin
                        pemasukan.catatan?.let { canvas.drawText(it, xPos, yPos + rowHeight / 2, tableContentPaint) }
                        xPos += 120f + cellMargin
                        canvas.drawText("Pemasukan", xPos, yPos + rowHeight / 2, tableContentPaint)
                        yPos += rowHeight
                    }
                }

                // Draw pengeluaran data
                if (listPengeluaran.isNotEmpty()) {
                    for (pengeluaran in listPengeluaran) {
                        xPos = tableMarginLeft
                        canvas.drawRect(tableMarginLeft, yPos, page.info.pageWidth.toFloat() - tableMarginLeft, yPos + rowHeight, tableHeaderBackground)

                        val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(pengeluaran.tanggal)
                        canvas.drawText(formattedDate, xPos, yPos + rowHeight / 2, tableContentPaint)
                        xPos += 120f + cellMargin
                        pengeluaran.kategori?.let { canvas.drawText(it, xPos, yPos + rowHeight / 2, tableContentPaint) }
                        xPos += 180f + cellMargin

                        // Set color to red for pengeluaran
                        tableContentPaint.color = Color.RED
                        canvas.drawText("${pengeluaran.jumlah}", xPos, yPos + rowHeight / 2, tableContentPaint)

                        // Reset color back to black
                        tableContentPaint.color = Color.BLACK
                        xPos += 120f + cellMargin
                        pengeluaran.catatan?.let { canvas.drawText(it, xPos, yPos + rowHeight / 2, tableContentPaint) }
                        xPos += 120f + cellMargin
                        canvas.drawText("Pengeluaran", xPos, yPos + rowHeight / 2, tableContentPaint)
                        yPos += rowHeight
                    }
                }

                // Finish the page
                pdfDocument.finishPage(page)

                // Save the document
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                val outputStream = FileOutputStream(file)
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()

                Toast.makeText(context, "Data berhasil dicetak dan disimpan di ${file.absolutePath}", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Toast.makeText(context, "Terjadi kesalahan saat mencetak data", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }


    private fun cancelScheduledNotification() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationPendingIntent?.let { alarmManager.cancel(it) }
        notificationPendingIntent = null
        Toast.makeText(requireContext(), "Notifikasi dibatalkan", Toast.LENGTH_SHORT).show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                tvNotifTime.text = timeString
                sharedPreferences.edit().putString("notif_time", timeString).apply() // Menggunakan apply() untuk menyimpan perubahan
                scheduleNotification(hourOfDay, minute)
            },
            12, 0, true
        )
        timePickerDialog.show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, you can now schedule notifications
            val savedTime = sharedPreferences.getString("notif_time", "12:00")
            val (hour, minute) = savedTime!!.split(":").map { it.toInt() }
            scheduleNotification(hour, minute)
        } else {
            // Permission denied, inform the user or take appropriate action
            switchNotif.isChecked = false // Turn off the switch
            Toast.makeText(requireContext(), "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleNotification(hourOfDay: Int, minute: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationPendingIntent = pendingIntent

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(
            requireContext(),
            "Notifikasi berhasil diatur pada ${String.format("%02d:%02d", hourOfDay, minute)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notificationId = 1 // Unique ID for the notification

            val channelId = "pengingat_catatan_keuangan"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val existingChannel = notificationManager.getNotificationChannel(channelId)
                if (existingChannel == null) {
                    val name = "Pengingat Keuangan"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(channelId, name, importance)
                    notificationManager.createNotificationChannel(channel)
                }
            }

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.bell_pin)
                .setContentTitle("Pengingat Catatan Keuangan")
                .setContentText("Jangan lupa catat pengeluaran atau pemasukanmu hari ini!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, notificationBuilder.build())
            }
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Reset Data")
            .setMessage("Apakah Anda yakin ingin menghapus semua data?")
            .setPositiveButton("Hapus") { _, _ ->
                resetData()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        updatePinStatus() // Perbarui status PIN saat fragment kembali aktif
    }

    private fun resetData() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val pengeluaranDao: PengeluaranDao = database.pengeluaranDao()
            val pemasukanDao: PemasukanDao = database.pemasukanDao()

            pengeluaranDao.deleteAll()
            pemasukanDao.deleteAll()

            Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}