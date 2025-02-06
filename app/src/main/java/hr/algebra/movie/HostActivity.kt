package hr.algebra.movie

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import hr.algebra.movie.databinding.ActivityHostBinding
import hr.algebra.movie.framework.sendBroadcast
import java.util.Locale

class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initHamburgerMenu()
        initNavigation()

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
                else -> return@setNavigationItemSelectedListener NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

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