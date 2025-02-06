package hr.algebra.movie.framework

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import hr.algebra.movie.MOVIES_PROVIDER_CONTENT_URI
import hr.algebra.movie.model.Item

fun View.applyAnimation(animationId: Int) =
    startAnimation(AnimationUtils.loadAnimation(context, animationId))


inline fun <reified T : Activity> Context.startActivity() =
    startActivity(Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })

inline fun <reified T : BroadcastReceiver> Context.sendBroadcast() =
    sendBroadcast(Intent(this, T::class.java))

fun Context.setBooleanPreference(key: String, value: Boolean = true) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putBoolean(key, value)
        .apply()

fun Context.getBooleanPreference(key: String) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false)

fun callDelayed(delay: Long, work: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(
        work,
        delay
    )
}

fun Context.isOnline(): Boolean {
    val cm = getSystemService<ConnectivityManager>()
    cm?.activeNetwork?.let { network ->
        cm.getNetworkCapabilities(network)?.let { networkCapabilities ->
            return networkCapabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) || networkCapabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            )
        }
    }
    return false
}

fun Context.fetchItems(): MutableList<Item>{
    var items = mutableListOf<Item>()
    val cursor = contentResolver.query(
        MOVIES_PROVIDER_CONTENT_URI, null, null, null, null
    )

    while (cursor != null && cursor.moveToNext()){
        items.add(
            Item(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
                cursor.getString(cursor.getColumnIndexOrThrow("backdrop_path")),
                cursor.getString(cursor.getColumnIndexOrThrow("release_date")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("vote_average")),
                cursor.getInt(cursor.getColumnIndexOrThrow("vote_count")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("popularity")),
                cursor.getInt(cursor.getColumnIndexOrThrow("adult")) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow("video")) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow("original_language")),
                cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
                cursor.getString(cursor.getColumnIndexOrThrow("genre_ids")).split(",").map { it.toInt() }
            )
        )
    }
    return items
}