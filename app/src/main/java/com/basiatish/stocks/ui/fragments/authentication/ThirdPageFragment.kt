package com.basiatish.stocks.ui.fragments.authentication

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.basiatish.stocks.databinding.ViewpagerThirdPageBinding

class ThirdPageFragment : Fragment() {

    private var _binding: ViewpagerThirdPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewpagerThirdPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.instruction.setOnClickListener {
            binding.instruction.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}