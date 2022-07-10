package com.example.stocks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stocks.R
import com.example.stocks.databinding.FragmentStockListBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StockListFragment() : Fragment() {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val navbar = requireActivity().findViewById<BottomAppBar>(R.id.btm_bar)
//        navbar.hideOnScroll = true

        //val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

//        fab.setOnClickListener {
//            val action = StockListFragmentDirections.actionStockListFragmentToStockSearchFragment()
//            this.findNavController().navigate(action)
//        }
    }
}