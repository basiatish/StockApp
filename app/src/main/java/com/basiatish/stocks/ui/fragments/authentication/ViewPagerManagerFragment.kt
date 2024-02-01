package com.basiatish.stocks.ui.fragments.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.basiatish.stocks.adapters.ViewPagerAdapter
import com.basiatish.stocks.databinding.ViewpagerManagerFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerManagerFragment : Fragment() {

    private var _binding: ViewpagerManagerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewpagerManagerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(binding.viewPagerIndicator, binding.viewPager) { _, _ ->}.attach()
        binding.viewPager.offscreenPageLimit = 1

        binding.startButton.setOnClickListener {
            val action = ViewPagerManagerFragmentDirections.actionViewPagerManagerFragmentToApiKeyDialog()
            findNavController().navigate(action)
        }
    }

}