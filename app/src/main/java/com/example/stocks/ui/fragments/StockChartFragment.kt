package com.example.stocks.ui.fragments

import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.stocks.R
import com.example.stocks.databinding.FragmentStockChartBinding
import com.example.stocks.models.remote.CompanyProfile
import com.example.stocks.utils.network.StockApiStatus
import com.example.stocks.viewmodels.SharedViewModel
import com.example.stocks.viewmodels.StockChartViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tradingview.lightweightcharts.api.chart.models.color.surface.SolidColor
import com.tradingview.lightweightcharts.api.chart.models.color.toIntColor
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi
import com.tradingview.lightweightcharts.api.options.models.*
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode
import com.tradingview.lightweightcharts.api.series.enums.LastPriceAnimationMode
import com.tradingview.lightweightcharts.api.series.enums.LineStyle
import com.tradingview.lightweightcharts.api.series.enums.LineWidth
import com.tradingview.lightweightcharts.api.series.models.Time
import com.tradingview.lightweightcharts.runtime.plugins.DateTimeFormat
import com.tradingview.lightweightcharts.runtime.plugins.PriceFormatter
import com.tradingview.lightweightcharts.runtime.plugins.TimeFormatter
import com.tradingview.lightweightcharts.view.ChartsView
import java.util.*
import kotlin.math.roundToInt

class StockChartFragment : Fragment() {

    private var _binding: FragmentStockChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var chart: ChartsView

    private val viewModel: StockChartViewModel by viewModels()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null

    private lateinit var lineSeries: SeriesApi

    private lateinit var lastPressedButton: AppCompatButton

    private var compName = ""
    private var compShortName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockChartBinding.inflate(inflater, container, false)
        compShortName = sharedViewModel.companyShortName.value.toString()
        compName = sharedViewModel.companyName.value.toString()

        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        if (viewModel.priceDaily.isNullOrEmpty()) {
            viewModel.getChartData("1day", compShortName)
        }

        activity?.findViewById<LinearLayoutCompat>(R.id.btm_bar)?.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindCompName()
        setupObservers()
        setupButtonsOnClick()
        loadCompanyLogo()

        lastPressedButton = binding.bottomSheet.btn1m

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomMenu)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        chart = binding.chartView
        setupChart()

        binding.chartSetting.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        chart.subscribeOnChartStateChange { state ->
            when(state) {
                is ChartsView.State.Ready -> {
                    binding.apply {
                        chartView.visibility = View.VISIBLE
                    }
                }
            }
        }
        chart.api.subscribeCrosshairMove { params ->
            val prices = params.seriesPrices
            if (params.time != null && !prices.isNullOrEmpty()) {
                val time = params.time as Time.BusinessDay
                binding.companyDate.text = "${time.day} - ${time.month} - ${time.year}"
                binding.companyPrice.text = ((prices.first().prices.value!! * 100.0).
                roundToInt() / 100.0).toString()
            } else {
                bindLegend()
            }
        }
    }

    private fun setupButtonsOnClick() {
        binding.bottomSheet.apply {
            btn1m.setOnClickListener {
                buttonState(it as AppCompatButton)
            }
            btn5m.setOnClickListener {
                buttonState(it as AppCompatButton)
            }
            btn30m.setOnClickListener {
                buttonState(it as AppCompatButton)
            }
            btn1h.setOnClickListener {
                buttonState(it as AppCompatButton)
            }
            btn4h.setOnClickListener {
                buttonState(it as AppCompatButton)
            }
            btnAll.setOnClickListener {
                buttonState(it as AppCompatButton)
            }
        }
    }

    private fun buttonState(pressed: AppCompatButton) {
        pressed.isEnabled = false
        lastPressedButton.isEnabled = true
        lastPressedButton = pressed
    }

    private fun bindLegend() {
        val data = viewModel.getLastChartPoint()
        binding.companyDate.text = data["date"]
        binding.companyPrice.text = data["price"]
    }

    private fun bindCompName() {
        binding.companyName.text = compName
    }

    private fun setupChart() {
        chart.api.applyOptions {
            layout = layoutOptions {
                background = SolidColor(Color.WHITE)
                textColor = Color.BLACK.toIntColor()
            }
            rightPriceScale = PriceScaleOptions(borderVisible = false)
            timeScale = TimeScaleOptions(borderVisible = false)
            crosshair = crosshairOptions {
                mode = CrosshairMode.MAGNET
                horzLine = crosshairLineOptions {
                    visible = false
                    labelVisible = false
                }
                vertLine = crosshairLineOptions {
                    visible = true
                    style = LineStyle.SOLID
                    width = LineWidth.TWO
                    color = resources.getColor(R.color.light_grey_2).toIntColor()
                    labelVisible = false
                }
            }
            localization = localizationOptions {
                locale = Locale.getDefault().language
                priceFormatter = PriceFormatter(template = "{price:#2:#2}$")
                timeFormatter = TimeFormatter(
                    locale = Locale.getDefault().language,
                    dateTimeFormat = DateTimeFormat.DATE
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.status.observe(this.viewLifecycleOwner) { status ->
            if (status == StockApiStatus.DONE && !viewModel.priceDaily.isNullOrEmpty()) {
//                chart.api.addLineSeries(
//                    onSeriesCreated = { series ->
//                        lineSeries = series
//                        lineSeries.setData(viewModel.getChartData())
//                    }
//                )
                bindLegend()
                chart.api.addLineSeries(LineSeriesOptions(priceLineColor =
                resources.getColor(R.color.orange).toIntColor(),
                    color = resources.getColor(R.color.orange).toIntColor(),
                lastPriceAnimation = LastPriceAnimationMode.CONTINUOUS),
                    onSeriesCreated = { series ->
                    lineSeries = series
                    lineSeries.setData(viewModel.getChartData())
                })
            } else if (status == StockApiStatus.ERROR) {
                Toast.makeText(requireContext(), "No chart data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCompanyLogo() {
        Glide.with(requireContext()).
        load("https://financialmodelingprep.com/image-stock/${compShortName}.png").
        error(R.drawable.ic_warning)
            .centerCrop().into(binding.logo)
    }

}