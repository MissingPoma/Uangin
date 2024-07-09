package com.example.uangin

import android.app.AlarmManager
import android.app.PendingIntent
import android.widget.ImageButton
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.dao.PemasukanDao
import kotlinx.coroutines.launch
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ActivityCompat
import java.util.*
import com.example.uangin.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var sharedPrefs: SharedPreferences
private lateinit var tvNotifTime: TextView
private lateinit var switchNotif: Switch
/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvNotifTime: TextView
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

        val rightArrowKelola = view.findViewById<ImageButton>(R.id.rightArrowKelola)
        val righArrowSetel = view.findViewById<ImageButton>(R.id.righArrowSetel) // Inisialisasi tombol
        tvNotifTime = view.findViewById(R.id.tvNotifTime)
        switchNotif = view.findViewById(R.id.switchNotif)
        sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Check if permission is already granted
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
    }

    private fun cancelScheduledNotification() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationPendingIntent?.let { alarmManager.cancel(it) }
        notificationPendingIntent = null
        Toast.makeText(requireContext(), "Notifikasi dibatalkan", Toast.LENGTH_SHORT).show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(requireContext(),
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                tvNotifTime.text = String.format("%02d:%02d", hourOfDay, minute)
                sharedPrefs.edit().putString("notif_time", String.format("%02d:%02d", hourOfDay, minute)).apply()
                scheduleNotification(hourOfDay, minute)
            },
            // Set the initial time of the dialog (can be from SharedPreferences)
            12, 0, true
        )
        timePickerDialog.show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, you can now schedule notifications
            val savedTime = sharedPrefs.getString("notif_time", "12:00")
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
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // Added FLAG_UPDATE_CURRENT
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        Toast.makeText(requireContext(), "Notifikasi berhasil diatur pada ${String.format("%02d:%02d", hourOfDay, minute)}", Toast.LENGTH_SHORT).show()
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notificationId = 1 // Unique ID for the notification

            val channelId = "pengingat_catatan_keuangan"

            // Create notification channel if it doesn't exist
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

            val notificationBuilder = NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_delete) // Replace with your notification icon
                .setContentTitle("Pengingat Catatan Keuangan")
                .setContentText("Jangan lupa catat pengeluaran atau pemasukan mu hari ini!")
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
            val database = AppDatabase.getDatabase(requireContext()) // Gunakan getDatabase()
            val pengeluaranDao: PengeluaranDao = database.pengeluaranDao()
            val pemasukanDao: PemasukanDao = database.pemasukanDao()
            val kategoriDao = database.kategoriDao() // Dapatkan DAO kategori

            pengeluaranDao.deleteAll()
            pemasukanDao.deleteAll()
            kategoriDao.deleteAll() // Hapus data kategori

            Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
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