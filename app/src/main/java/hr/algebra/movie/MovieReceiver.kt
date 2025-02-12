package hr.algebra.movie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import hr.algebra.movie.framework.setBooleanPreference
import hr.algebra.movie.framework.startActivity

class MovieReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Reminder"
        showNotification(context, movieTitle)
    }

    private fun showNotification(context: Context, movieTitle: String) {
        val channelId = "movie_reminder_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Movie Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies about upcoming movie releases."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, HostActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(movieTitle)
            .setContentText(context.getString(R.string.don_t_forget_to_watch_your_movie))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }



}