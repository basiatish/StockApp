package com.basiatish.stocks.ui.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.basiatish.stocks.App
import com.basiatish.stocks.R
import com.basiatish.stocks.databinding.AddAlertBottomSheetBinding
import com.basiatish.stocks.viewmodels.AddAlertViewModel
import com.basiatish.stocks.viewmodels.AddAlertViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

class AddAlertBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AddAlertBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val navArg: AddAlertBottomSheetFragmentArgs by navArgs()

    private lateinit var app: App

    private val viewModel: AddAlertViewModel by viewModels {
        AddAlertViewModelFactory((requireContext().applicationContext as App).alertDataBase.alertDao(),
            requireContext().applicationContext as App)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private var arrowFlag = true
    private var updateAlert = false
    private var isLandScape = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.add_alert_bottom_sheet, null)
        dialog.setContentView(view)

        getScreenOrientation()

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = param.behavior

        if (behavior is BottomSheetBehavior<*>) {
            bottomSheetBehavior = behavior
            if (isLandScape) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            else bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dismiss()
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            if (!isLandScape) {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
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
    ): View {
        _binding = AddAlertBottomSheetBinding.inflate(inflater, container, false)
        app = requireContext().applicationContext as App
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTextView()
        setupListeners()

        if (navArg.alertId != -1) {
            viewModel.getAlert(navArg.alertId)
            updateAlert = true
        }

        viewModel.alert.observe(this.viewLifecycleOwner) { alert ->
            binding.price.text = Editable.Factory.getInstance().newEditable(alert.price.toString())
            arrowFlag = alert.above
            arrowViewStatus()
        }
    }

    private fun setupListeners() {
        binding.closeSheet.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            val price: Double
            try {
                price = (binding.price.text.toString().toDouble() * 100).roundToInt().toDouble() / 100
                viewModel.createAlert(updateAlert, navArg.alertId, navArg.shortName, price, arrowFlag)
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(context, resources.getText(R.string.invalid_price), Toast.LENGTH_SHORT).show()
            }
        }

        binding.viewContainer.setOnClickListener {
            arrowFlag = !arrowFlag
            arrowViewStatus()
        }

        binding.priceUpOrDown.setText(resources.getString(R.string.alert_text_above))
    }

    private fun arrowViewStatus() {
        val editTextDrawable: Drawable?
        val imageViewDrawable: Drawable?
        val editTextColor: Int
        val textViewText: String
        if (!arrowFlag) {
            editTextDrawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_dollar_green_to_red, context?.theme
            )
            imageViewDrawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_arrow_above_to_below, context?.theme
            )
            editTextColor = resources.getColor(R.color.red_60, context?.theme)
            textViewText = resources.getString(R.string.alert_text_below)
        } else {
            editTextDrawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_dollar_red_to_green, context?.theme
            )
            imageViewDrawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_arrow_below_to_above, context?.theme
            )
            editTextColor = resources.getColor(R.color.green_60, context?.theme)
            textViewText = resources.getString(R.string.alert_text_above)
        }
        binding.priceArrow.setImageDrawable(imageViewDrawable)
        binding.price.setCompoundDrawablesWithIntrinsicBounds(
            editTextDrawable, null,
            null, null
        )
        val editTextAnim = binding.price.compoundDrawables[0] as AnimatedVectorDrawable
        editTextAnim.start()
        val imageViewAnim = binding.priceArrow.drawable as AnimatedVectorDrawable
        imageViewAnim.start()
        binding.price.backgroundTintList = ColorStateList.valueOf(editTextColor)
        binding.priceUpOrDown.setText(textViewText)
    }

    private fun setupTextView() {
        binding.priceUpOrDown.setFactory {
            val textView = TextView(context)
            textView.apply {
                gravity = Gravity.CENTER_VERTICAL
                textSize = 18F
                typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
                setTextColor(resources.getColor(R.color.main_text_color, context?.theme))
            }
        }
    }

    private fun getScreenOrientation() {
        isLandScape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            isLandScape = false
        }
    }
}