package com.basiatish.stocks.ui.fragments.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.basiatish.stocks.databinding.ViewpagerFirstPageBinding

class FirstPageFragment : Fragment() {

    private var _binding: ViewpagerFirstPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewpagerFirstPageBinding.inflate(inflater, container, false)
        return binding.root
    }

}