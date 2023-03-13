package com.example.stocks.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.findNavController
import com.example.stocks.R
import com.example.stocks.databinding.AlertListBottomSheetBinding
import com.example.stocks.databinding.TestBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class AlertListBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AlertListBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlertListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addButton.setOnClickListener {
            //bottomSheetBehavior?.peekHeight = 100
            val action = AlertListBottomSheetFragmentDirections.actionAlertListDialogToAddAlert()
            findNavController().navigate(action)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.alert_list_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val density = requireActivity().resources.displayMetrics.heightPixels

        val behavior = param.behavior
        if (behavior is BottomSheetBehavior<*>){
            bottomSheetBehavior = behavior
            bottomSheetBehavior?.peekHeight = density
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            bottomSheetBehavior?.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dismiss()
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

            })
        }

        return dialog
    }
}