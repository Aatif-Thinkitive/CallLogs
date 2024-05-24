package com.example.calllog

// CallLogService.kt
import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.os.IBinder
import android.provider.CallLog
import android.util.Log

class CallLogService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        retrieveCallLogs()
        return START_NOT_STICKY
    }

    private fun retrieveCallLogs() {
        val cursor: Cursor? = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC")
        cursor?.use {
            if (it.moveToFirst()) {
                val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                val type = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE))
                val date = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                val duration = it.getLong(it.getColumnIndex(CallLog.Calls.DURATION))

                Log.e("CallLogs",number.toString())
                Log.d("CallLogService", "Number: $number, Type: $type, Date: $date, Duration: $duration")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
