package com.example.calllog

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CONTACTS
                ),
                REQUEST_CODE_PERMISSIONS
            )
        }
        val scanBtn = findViewById<Button>(R.id.scanBtn)
        scanBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CALL_LOG
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getAllContacts()
                getAllCallLogs()
            }else{
                val toast = Toast.makeText(this,"Please provide all permissions from app settings",Toast.LENGTH_LONG)
                toast.show()
                navigateToAppPermissions()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted
            } else {
                val toast = Toast.makeText(this,"Please provide all permissions from app settings",Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }
//Todo get all contacts
    private fun getAllContacts() {
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.use {

            /*Todo name aur number variables me saare contacts ka data aa raha hai
            * Todo name aur number variables me saare contacts ka data aa raha hai
            *  Todo name aur number variables me saare contacts ka data aa raha hai
            *   Todo name aur number variables me saare contacts ka data aa raha hai
            *    Todo name aur number variables me saare contacts ka data aa raha hai
            *     Todo name aur number variables me saare contacts ka data aa raha hai
            *      Todo name aur number variables me saare contacts ka data aa raha hai
            *
             */
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                Log.d("MainActivity", "Contact - Name: $name, Number: $number")
            }
        }
    }

    //Todo get all logs
    private fun getAllCallLogs() {
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {

                val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                val type = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE))
                val date = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                val duration = it.getLong(it.getColumnIndex(CallLog.Calls.DURATION))
                val blockReason = it.getLong(it.getColumnIndex(CallLog.Calls.BLOCK_REASON))
                val lastModified = it.getLong(it.getColumnIndex(CallLog.Calls.LAST_MODIFIED))

                /*Todo in variables me saare call logs ka data aa raha hai
                *Todo in variables me saare call logs ka data aa raha hai
                * Todo in variables me saare call logs ka data aa raha hai
                * Todo in variables me saare call logs ka data aa raha hai
                * Todo in variables me saare call logs ka data aa raha hai
                * Todo in variables me saare call logs ka data aa raha hai
                 */
                Log.d(
                    "MainActivity",
                    "Call Log - Number: $number, Type: $type, Date: $date, Duration: $duration"
                )
            }
        }
    }
    private fun navigateToAppPermissions() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

}