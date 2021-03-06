package hu.bme.aut.android.shoppinglist.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        when(sharedPreferences.getString("theme", "default")){
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        navController = this.findNavController(R.id.myNavHostFragment)
        appBarConfiguration = AppBarConfiguration
                .Builder(
                        R.id.myListsFragment,
                        R.id.profileFragment,
                        R.id.newListFragment, 
                        R.id.welcomeFragment)
                .build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        val bottomNavigationView = binding.bottomNavigation
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.listFragment, R.id.welcomeFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }


    private fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}