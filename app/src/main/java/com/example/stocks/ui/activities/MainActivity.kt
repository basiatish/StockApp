package com.example.stocks.ui.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.stocks.R
import com.example.stocks.databinding.ActivityMainBinding
import com.example.stocks.ui.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController
import com.example.stocks.App

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var nav: BottomNavigationView

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private var isThemeLight = true

    override fun onCreate(savedInstanceState: Bundle?) {

        isThemeLight = (applicationContext as App).getValue()
        if (isThemeLight) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setNavigationGraph()

//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.fragment_container) as NavHostFragment
//
////        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
//
//        navController = navHostFragment.navController
        setContentView(binding.root)
//        graph.setStartDestination(R.id.stockListFragment)
//        navController.setGraph(graph.parent.id)

        nav = binding.nav
        nav.setupWithNavController(navController)

        binding.nav.background = null
        binding.fragmentNav.background = null

        binding.fragmentNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.chart -> {
                    if (!it.isChecked) {
                        binding.btmBar.visibility = View.GONE
                        val action = StockOverViewFragmentDirections.actionStockOverViewFragmentToStockChartFragment()
                        navController.navigate(action)
                    }
                }
                R.id.alert -> {
                    if (!it.isChecked) {
                        navController.navigate(R.id.action_stockOverViewFragment_to_alertListDialog)
                    }
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.stockListFragment -> {
                    binding.nav.visibility = View.VISIBLE
                    binding.fragmentNav.visibility = View.GONE
                    nav.menu.getItem(0).isChecked = true
                }
                R.id.stockSearchFragment -> {
                    binding.nav.visibility = View.VISIBLE
                    binding.fragmentNav.visibility = View.GONE
                }
                R.id.stockOverViewFragment -> {
                    binding.nav.visibility = View.GONE
                    binding.fragmentNav.visibility = View.VISIBLE
                    binding.btmBar.visibility = View.VISIBLE
                    binding.fragmentNav.menu.getItem(0).isChecked = true
                }
                R.id.alertListDialog -> {
                    binding.fragmentNav.selectedItemId = R.id.home
                }
                R.id.newsFragment -> {
                    binding.nav.visibility = View.GONE
                    binding.btmBar.visibility = View.GONE
                }
                R.id.newsListFragment -> {
                    binding.nav.visibility = View.VISIBLE
                    binding.btmBar.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    private fun setNavigationGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.stockSearchFragment)

        navController.graph = navGraph
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
