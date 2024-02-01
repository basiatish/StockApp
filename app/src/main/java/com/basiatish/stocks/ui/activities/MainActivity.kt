package com.basiatish.stocks.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.basiatish.stocks.App
import com.basiatish.stocks.R
import com.basiatish.stocks.databinding.ActivityMainBinding
import com.basiatish.stocks.ui.fragments.StockOverViewFragmentDirections
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var nav: BottomNavigationView

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var isUserRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {

        if ((applicationContext as App).getApiKey() != null) isUserRegistered = true
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setNavigationGraph()

        setContentView(binding.root)

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
                    binding.btmBar.visibility = View.VISIBLE
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
                R.id.viewPagerManagerFragment -> {
                    binding.nav.visibility = View.GONE
                    binding.btmBar.visibility = View.GONE
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

        if (isUserRegistered) {
            navGraph.setStartDestination(R.id.stockListFragment)
        } else {
            navGraph.setStartDestination(R.id.viewPagerManagerFragment)
        }

        navController.graph = navGraph
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
