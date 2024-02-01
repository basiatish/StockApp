package com.basiatish.stocks.ui.fragments.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.basiatish.stocks.databinding.ViewpagerSecondPageBinding

class SecondPageFragment : Fragment() {

    private var _binding: ViewpagerSecondPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewpagerSecondPageBinding.inflate(inflater, container, false)
        return binding.root
    }

}