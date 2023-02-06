package com.example.stocks.ui.activities

import android.animation.LayoutTransition
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.stocks.R
import com.example.stocks.databinding.ActivityMainBinding
import com.example.stocks.ui.fragments.StockListFragmentDirections
import com.example.stocks.ui.fragments.StockOverViewFragment
import com.example.stocks.ui.fragments.StockOverViewFragmentDirections
import com.example.stocks.ui.fragments.StockSearchFragmentDirections
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var nav: BottomNavigationView

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        nav = binding.nav

        binding.nav.background = null
        binding.fragmentNav.background = null

        binding.nav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.favorite -> {
                    if (!it.isChecked) {
                        val action = StockSearchFragmentDirections.actionStockSearchFragmentToStockListFragment()
                        navController.navigate(action)
                    }
                }
                R.id.search -> {
                    if (!it.isChecked) {
                        val action = StockListFragmentDirections.actionStockListFragmentToStockSearchFragment()
                        navController.navigate(action)
                    }
                }
            }
            true
        }

        binding.fragmentNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.chart -> {
                    if (!it.isChecked) {
                        binding.btmBar.visibility = View.GONE
                        val action = StockOverViewFragmentDirections.actionStockOverViewFragmentToStockChartFragment()
                        navController.navigate(action)
                    }
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.stockListFragment -> {
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
                R.id.stockChartFragment -> {
                }
                else -> {
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
