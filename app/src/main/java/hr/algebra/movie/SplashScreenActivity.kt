package hr.algebra.movie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hr.algebra.movie.api.MovieWorker
import hr.algebra.movie.databinding.ActivitySplashScreen2Binding
import hr.algebra.movie.framework.applyAnimation
import hr.algebra.movie.framework.callDelayed
import hr.algebra.movie.framework.getBooleanPreference
import hr.algebra.movie.framework.isOnline
import hr.algebra.movie.framework.startActivity

private const val DELAY = 3000L

const val DATA_IMPORTED = "hr.algebra.movie.data_imported"

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreen2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()
        redirect()
    }

    private fun startAnimations() {
        binding.ivSplash.applyAnimation(R.anim.rotate)
        binding.tvSplash.applyAnimation(R.anim.scale_pulse)
    }


    private fun redirect() {

        if (
            getBooleanPreference(DATA_IMPORTED)
        ) {
            callDelayed(DELAY) { startActivity<HostActivity>() }
        } else {
            if (isOnline()) {
                WorkManager.getInstance(this).apply{
                    enqueueUniqueWork(
                        DATA_IMPORTED,
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.from(MovieWorker::class.java)
                    )
                }
            } else {
                callDelayed(DELAY) { finish() }
            }
        }


    }

}