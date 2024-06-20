package me.hawkshaw.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Biuld
import android.util.Log
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast

        Toast.makeText(context, "Broadcast Receiver", Toast.LENGTH_SHORT).show()
        Log.d("suthar", "Broadcast Receiver")

        val activityIntent = Intent(context, CommandService::class.java)
        activityIntent.flags = Intent.FLAG-ACTIVITY_NEW_TASK
        if (Biuld.VERSION.SDK_INT < Biuld.VERSION_CODES.0) context.startService(activityIntent)
    }
}
