package com.fibonacci.mycontactgue

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.fibonacci.mycontactgue.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // THE FIX: Connect the NavController to the BottomNavigationView, not the BottomAppBar
        binding.bottomNavView.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            // The FAB should only work from the contact list screen
            if (navController.currentDestination?.id == R.id.ContactListFragment) {
                navController.navigate(R.id.action_ContactListFragment_to_CreateContactFragment)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.ContactListFragment,
                R.id.callLogFragment,
                R.id.remindersFragment -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                }
            }
        }
    }
}