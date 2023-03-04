package com.example.stocks.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.stocks.R
import com.example.stocks.databinding.AlertListBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlertListBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AlertListBottomSheetBinding? = null
    private val binding get() = _binding!!

    var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlertListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        dialog?.let {
//            val bmSh = it.findViewById(R.layout.alert_list_bottom_sheet)
//            val behavior = BottomSheetBehavior.from(bmSh)
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }
//    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val view = LayoutInflater.from(context).inflate(R.layout.alert_list_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val density = requireActivity().resources.displayMetrics.heightPixels

        val behavior = param.behavior
        if (behavior is BottomSheetBehavior<*>){
            bottomSheetBehavior = behavior
            bottomSheetBehavior?.peekHeight = (density * 0.9).toInt()
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            val callback = object : BottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dismiss()
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

            }
        }
    }
}