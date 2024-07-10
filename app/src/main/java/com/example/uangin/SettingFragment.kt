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
import android.os.Build
import android.os.Bundle
import android.os.Environment
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

        val rightArrowCetak = view.findViewById<ImageButton>(R.id.rightArrowCetak)
        val rightArrowKelola = view.findViewById<ImageButton>(R.id.rightArrowKelola)
        val righArrowSetel = view.findViewById<ImageButton>(R.id.righArrowSetel)
        tvNotifTime = view.findViewById(R.id.tvNotifTime)
        switchNotif = view.findViewById(R.id.switchNotif)
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

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
                val timeStamp = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(Date())
                val fileName = "Laporan_UangIn_$timeStamp.pdf"

                val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val pdfFile = File(storageDir, fileName)

                val outputStream = FileOutputStream(pdfFile)
                outputStream.use { stream ->
                    stream.write("Laporan UangIn\n\n".toByteArray())

                    if (listPemasukan.isNotEmpty()) {
                        stream.write("=== Pemasukan ===\n".toByteArray())
                        for (pemasukan in listPemasukan) {
                            val line = "${pemasukan.tanggal} | ${pemasukan.kategori} | ${pemasukan.jumlah} | ${pemasukan.catatan}\n"
                            stream.write(line.toByteArray())
                        }
                        stream.write("\n".toByteArray())
                    }

                    if (listPengeluaran.isNotEmpty()) {
                        stream.write("=== Pengeluaran ===\n".toByteArray())
                        for (pengeluaran in listPengeluaran) {
                            val line = "${pengeluaran.tanggal} | ${pengeluaran.kategori} | ${pengeluaran.jumlah} | ${pengeluaran.catatan}\n"
                            stream.write(line.toByteArray())
                        }
                        stream.write("\n".toByteArray())
                    }

                    stream.flush()
                }

                Toast.makeText(context, "Data berhasil dicetak dan disimpan di ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
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
                tvNotifTime.text = String.format("%02d:%02d", hourOfDay, minute)
                sharedPreferences.edit().putString("notif_time", String.format("%02d:%02d", hourOfDay, minute)).apply()
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
