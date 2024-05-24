package com.example.calllog

// CallLogService.kt
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.Cursor
import android.os.Build
import android.os.IBinder
import android.provider.CallLog
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CallLogService : Service() {
    private lateinit var database: DatabaseReference

    private val NOTIFICATION_ID = 123

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance().reference.child("call_logs")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForegroundServiceWithType()
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
        retrieveAndUploadCallLogs()
        return START_NOT_STICKY
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundServiceWithType() {
        val serviceIntent = Intent(this, CallLogService::class.java)
        startForegroundService(serviceIntent)
        startForeground(NOTIFICATION_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
    }
    private fun retrieveAndUploadCallLogs() {
        val cursor: Cursor? = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC")
        cursor?.use {
            if (it.moveToFirst()) {
                val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                val type = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE))
                val date = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                val duration = it.getLong(it.getColumnIndex(CallLog.Calls.DURATION))

                val callLogEntry = CallLogEntry(number, type, date, duration)
            Log.e("callogging",number.toString())
                // Upload the call log entry to Firebase
                database.push().setValue(callLogEntry)
                    .addOnSuccessListener {
                        Log.d("CallLogService", "Call log uploaded successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("CallLogService", "Failed to upload call log", e)
                    }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun createNotification(): Notification {
        val channelId = "call_log_channel"
        val channelName = "Call Log Channel"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call Log Service")
            .setContentText("Running in background")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }
}
