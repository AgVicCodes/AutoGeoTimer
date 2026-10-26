package com.example.autogeotimer.util

import android.Manifest
import android.os.Build
import android.util.Log
import android.content.Context
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent

import com.example.autogeotimer.R

import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


object Notifier {
    private const val CH_ID = "geofence_events"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CH_ID, "Geofence Events", NotificationManager.IMPORTANCE_DEFAULT)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

//    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notify(context: Context, title: String, text: String, tapIntent: Intent? = null) {
        ensureChannel(context)

        val builder = NotificationCompat.Builder(context, CH_ID)
            .setSmallIcon(R.drawable.ic_stat_location) // App icon
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
//            .build()

        tapIntent?.let {
            val pi = PendingIntent.getActivity(
                context, System.currentTimeMillis().toInt(), it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pi)
        }

        try {
            NotificationManagerCompat.from(context).notify(
                (System.currentTimeMillis() % Int.MAX_VALUE)
                    .toInt(), builder.build())
        } catch (se: SecurityException) {
//            In case OEMs still throw despite the check
            Log.e("Notifier", "Notify failed: ${se.message}", se)
        }
    }
}