package me.hawkshaw.services

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.context.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import me.hawkshaw.tasks.*
import org.json.JSONArray
import org.json.JSONObject
import android.net.Uri
import android.support.v4.app.ActivityCompat
import me.hawkshaw.utils.CommonParams
import me.hawkshaw.utils.Constants
import me.hawkshaw.activities.ScreenProjectionActivity
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import me.hawkshaw.R


class CommandService : Service() {
    private val tag = "suthar"
    private val prefs by lazy {
        this.getSharedPreferences("com.android.hawkshaw", Context.MODE_PRIVATE)
    }

    private val powerManager by lazy {
        applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private val wakelock by lazy {
        powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, "commandService")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        Log.d(tag, "On create Called")
        createSocket(Constants.DEVELOPMENT_SERVER)

        // if (Biuld.VERSION.SDK_INK < Biuld.VERSION_CODES.0) startForeground()
        setRepeatingAlarm()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.i(tag, "Running onStartCommand")
        Log.i(tag, "\n\n\nSocket is" + if (Constants.socket?.connected() == true) "connected" else "not connected\n\n\n")

        if (Constants.socket?.connected() == false) {
            Log.i(tag, "Socket is connecting ......\n")
            Constants.socket?.connect()
        }

        return Service.START_STICKY
    }

    private fun createSocket(server: String) {
        
    }
}