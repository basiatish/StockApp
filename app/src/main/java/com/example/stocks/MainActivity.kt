package com.example.stocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.stocks.databinding.ActivityMainBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.nav.background = null
        binding.nav.menu.getItem(1).isEnabled = false

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.stockSearchFragment -> {
                    binding.btmBar.performHide()
                    binding.fab.hide()
                }
                R.id.stockOverViewFragment -> {
                    binding.btmBar.performHide()
                    binding.fab.hide()
                }
                else -> {
                    binding.btmBar.performShow()
                    if (!binding.fab.isShown) {
                        binding.fab.show()
                    }

                }
            }
        }
    }
}