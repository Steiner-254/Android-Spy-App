package me.hawkshaw.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.log

class NotificationReadService : NotificationListenerService() {
    val tag = "suthar"
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        Log.i(tag, "********** onNotificationPosted");
        Log.i(tag, "ID : " + sbn?.id + "\t" + sbn?.notification?.tickerText + "\t" + sbn?.packageName)
    }

    override fun onNotificstionRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.i(tag, "********** onNotificationRemoved");
        Log.i(tag, "ID" + sbn?.id + "\t" + sbn?.notification + "\t" + sbn?.packageName)
    }
}
