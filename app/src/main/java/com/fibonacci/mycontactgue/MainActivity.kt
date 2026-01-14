package com.fibonacci.mycontactgue

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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

        binding.bottomNavView.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            if (navController.currentDestination?.id == R.id.ContactListFragment) {
                navController.navigate(R.id.action_ContactListFragment_to_CreateContactFragment)
            }
        }

        // Fix for Bottom Navigation obscuring text when keyboard is shown
        setupKeyboardListener()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavigationVisibility(destination.id)
        }
    }

    private fun setupKeyboardListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            
            if (isKeyboardVisible) {
                binding.bottomAppBar.visibility = View.GONE
                binding.fab.visibility = View.GONE
            } else {
                navController.currentDestination?.let { 
                    updateBottomNavigationVisibility(it.id)
                }
            }
            insets
        }
    }

    private fun updateBottomNavigationVisibility(destinationId: Int) {
        when (destinationId) {
            R.id.ContactListFragment,
            R.id.callLogFragment,
            R.id.smsInboxFragment,
            R.id.profileFragment,
            R.id.chatFragment,
            R.id.CreateContactFragment -> {
                binding.bottomAppBar.visibility = View.VISIBLE
                binding.fab.visibility = View.VISIBLE
                
                when (destinationId) {
                    R.id.chatFragment -> binding.bottomNavView.menu.findItem(R.id.smsInboxFragment).isChecked = true
                    R.id.CreateContactFragment -> binding.bottomNavView.menu.findItem(R.id.ContactListFragment).isChecked = true
                }
            }
            else -> {
                binding.bottomAppBar.visibility = View.GONE
                binding.fab.visibility = View.GONE
            }
        }
    }
}