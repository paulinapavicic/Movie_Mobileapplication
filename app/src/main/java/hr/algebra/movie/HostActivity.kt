package hr.algebra.movie

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import hr.algebra.movie.databinding.ActivityHostBinding
import hr.algebra.movie.framework.sendBroadcast
import java.util.Locale

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.widget.Button
import java.util.Calendar


class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding
    private lateinit var alarmManager: AlarmManager
    private val REQUEST_PERMISSIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initHamburgerMenu()
        initNavigation()
        checkAndRequestPermissions()

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager



    }



    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_PERMISSIONS
            )
        } else {
            Toast.makeText(this, "All permissions already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "${permissions[i]} Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "${permissions[i]} Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun initHamburgerMenu() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun initNavigation() {
        val navController = Navigation.findNavController(this, R.id.navController)
        NavigationUI.setupWithNavController(binding.navView, navController)

        // Handle language switching inside the drawer menu
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuChangeToEnglish -> {
                    setLocale("en") // Switch to English
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuChangeToCroatian -> {
                    setLocale("hr") // Switch to Croatian
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuScheduleReminder -> {
                    scheduleReminder() // Schedule a reminder when clicked
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

    }

    private fun scheduleReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Grant exact alarm permission in Settings", Toast.LENGTH_LONG).show()
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = android.net.Uri.parse("package:$packageName")
                }
                startActivity(intent)
                return
            }
        }

        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1) // ⏳ Schedule alarm 1 minute later
        }

        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("MOVIE_TITLE", "Movie Reminder!")

        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this,
            getString(R.string.reminder_set_message), Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.host_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                toggleDrawer()
                return true
            }
            R.id.menuExit -> {
                exitApp()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitApp() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.exit)
            setMessage(getString(R.string.are_you_sure_you_want_to_exit))
            setIcon(R.drawable.exit)
            setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
            setNegativeButton(getString(R.string.no), null)
            setCancelable(false)
            show()

        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(binding.navView)
        else binding.drawerLayout.openDrawer(binding.navView)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        // Restart the activity to apply changes
        val intent = Intent(this, HostActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Close old activity instance
    }


}