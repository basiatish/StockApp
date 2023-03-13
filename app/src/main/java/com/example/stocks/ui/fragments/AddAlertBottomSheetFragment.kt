package com.example.stocks.ui.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import com.example.stocks.R
import com.example.stocks.databinding.AddAlertBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddAlertBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AddAlertBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private var arrowFlag = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddAlertBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.add_alert_bottom_sheet, null)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.saveButton.setOnClickListener {
            dismiss()
        }

        binding.viewContainer.setOnClickListener {
            val editTextDrawable: Drawable?
            val imageViewDrawable: Drawable?
            val editTextColor: Int
            if (!arrowFlag) {
                editTextDrawable = ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_dollar_red_to_green, context?.theme)
                imageViewDrawable = ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_arrow_above_to_below, context?.theme)
                editTextColor = resources.getColor(R.color.red_60, context?.theme)
            } else {
                editTextDrawable = ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_dollar_green_to_red, context?.theme)
                imageViewDrawable = ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_arrow_below_to_above, context?.theme)
                editTextColor = resources.getColor(R.color.green_60, context?.theme)
            }
            arrowFlag = !arrowFlag
            binding.priceArrow.setImageDrawable(imageViewDrawable)
            binding.price.setCompoundDrawablesWithIntrinsicBounds(editTextDrawable, null,
                null, null)
            val editTextAnim = binding.priceArrow.drawable as AnimatedVectorDrawable
            editTextAnim.start()
            val imageViewAnim = binding.priceArrow.drawable as AnimatedVectorDrawable
            imageViewAnim.start()
            binding.price.backgroundTintList = ColorStateList.valueOf(editTextColor)
        }
    }
}