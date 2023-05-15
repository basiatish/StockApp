package com.example.stocks.ui.fragments.authentication

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stocks.R
import com.example.stocks.databinding.ApiKeyBottomSheetBinding
import com.example.stocks.utils.network.StockStatus
import com.example.stocks.viewmodels.ApiKeyViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ApiKeyBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: ApiKeyBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private val viewModel: ApiKeyViewModel by viewModels()

    private lateinit var apiKey: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.api_key_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = param.behavior

        if (behavior is BottomSheetBehavior<*>) {
            bottomSheetBehavior = behavior

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dismiss()
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ApiKeyBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.status.observe(this.viewLifecycleOwner) { status ->
            if (status == StockStatus.DONE) {
                val action = ApiKeyBottomSheetFragmentDirections.actionApiKeyDialogToStockListFragment(apiKey)
                findNavController().navigate(action)
            } else if (status == StockStatus.ERROR) {
                binding.submitButton.isEnabled = true
                binding.input.text?.clear()
                apiKey = ""
                Toast.makeText(context, R.string.api_key_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            binding.submitButton.isEnabled = false
            apiKey = binding.input.text.toString().trim()
            viewModel.testApiKey(apiKey)
        }
    }
}