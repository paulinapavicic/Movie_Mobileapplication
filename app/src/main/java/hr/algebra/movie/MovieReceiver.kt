package hr.algebra.movie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.movie.framework.setBooleanPreference
import hr.algebra.movie.framework.startActivity

class MovieReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.setBooleanPreference(DATA_IMPORTED)
        context.startActivity<HostActivity>()
    }
}