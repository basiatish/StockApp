package com.example.stocks.ui.fragments


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stocks.App
import com.example.stocks.R
import com.example.stocks.databinding.FragmentSettingsBinding
import com.example.stocks.viewmodels.SettingsViewModel
import com.example.stocks.viewmodels.SettingsViewModelFactory

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            (requireContext().applicationContext as App).stockDataBase.stockDao(),
            (requireContext().applicationContext as App).alertDataBase.alertDao(),
            requireContext().applicationContext as App
        )
    }

    private lateinit var sharedPreferences: SharedPreferences
    private var isThemeLight: Boolean = true
    private var nightModeFlag = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        isThemeLight = (activity?.applicationContext as App).getValue()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isThemeLight) makeButtonActive(binding.lightTheme, binding.lightCheck)
        else makeButtonActive(binding.darkTheme, binding.darkCheck)

        binding.apply {
            setupTextView(stockStatus)
            setupTextView(alertStatus)
            setupTextView(imagesStatus)
        }
        setupListeners()
    }

    private fun setupListeners() {

        binding.deleteStocks.setOnClickListener {
            binding.apply {
                stocks.isSelected = true
                deleteStocks.isClickable = false
                actionOnClick(stockStatus, deleteStocks, "Deleted")
            }
            //viewModel.deleteStocks()
        }

        binding.deleteAlerts.setOnClickListener {
            binding.apply {
                alerts.isSelected = true
                deleteAlerts.isClickable = false
                actionOnClick(alertStatus, deleteAlerts, "Deleted")
            }
            //viewModel.deleteAlerts()
        }

        binding.deleteImages.setOnClickListener {
            binding.apply {
                images.isSelected = true
                deleteImages.isClickable = false
                actionOnClick(imagesStatus, deleteImages, "Cleared")
            }
            //viewModel.clearImagesCache()
        }

        binding.lightTheme.setOnClickListener {
            if (!isThemeLight) {
                isThemeLight = !isThemeLight
                makeButtonActive(binding.lightTheme, binding.lightCheck)
                makeButtonNormal(binding.darkTheme, binding.darkCheck)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                (requireContext().applicationContext as App).save(isThemeLight)
                Toast.makeText(requireContext(), "Restart to apply", Toast.LENGTH_SHORT).show()
            }
        }

        binding.darkTheme.setOnClickListener {
            if (isThemeLight) {
                isThemeLight = !isThemeLight
                makeButtonActive(binding.darkTheme, binding.darkCheck)
                makeButtonNormal(binding.lightTheme, binding.lightCheck)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                (requireContext().applicationContext as App).save(isThemeLight)
                Toast.makeText(requireContext(), "Restart to apply", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeButtonActive(viewContainer: ConstraintLayout, checkView: View) {
        viewContainer.isSelected = true
        checkView.isSelected = true
    }

    private fun makeButtonNormal(viewContainer: ConstraintLayout, checkView: View) {
        viewContainer.isSelected = false
        checkView.isSelected = false
    }

    private fun actionOnClick(textView: TextSwitcher, button: AppCompatImageButton, text: String) {
        textView.setText(text)
        button.setColorFilter(resources.getColor(R.color.orange, context?.theme))
    }

    private fun setupTextView(view: TextSwitcher) {
        view.setFactory {
            val textView = TextView(context)
            textView.apply {
                setTextAppearance(R.style.SettingsText)
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
        view.setInAnimation(context, R.anim.rigth_to_left)
    }

    private fun getSystemUIMode() {
        nightModeFlag = requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        when (nightModeFlag) {
            Configuration.UI_MODE_NIGHT_YES -> isThemeLight = false
            Configuration.UI_MODE_NIGHT_NO -> isThemeLight = true
        }
    }
}