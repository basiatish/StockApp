package com.example.stocks.ui.fragments

import android.graphics.Color
import android.graphics.drawable.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.stocks.R
import com.example.stocks.databinding.FragmentStockChartBinding
import com.example.stocks.utils.network.StockApiStatus
import com.example.stocks.viewmodels.SharedViewModel
import com.example.stocks.viewmodels.StockChartViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tradingview.lightweightcharts.api.chart.models.color.toIntColor
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi
import com.tradingview.lightweightcharts.api.options.models.*
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode
import com.tradingview.lightweightcharts.api.series.enums.LastPriceAnimationMode
import com.tradingview.lightweightcharts.api.series.models.*
import com.tradingview.lightweightcharts.runtime.plugins.DateTimeFormat
import com.tradingview.lightweightcharts.runtime.plugins.TimeFormatter
import com.tradingview.lightweightcharts.view.ChartsView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class StockChartFragment : Fragment() {

    private var _binding: FragmentStockChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var chart: ChartsView

    private val viewModel: StockChartViewModel by viewModels()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    private var bottomSheetState: Boolean = true

    private lateinit var dataSeries: SeriesApi
    private lateinit var volumeSeries: SeriesApi

    private lateinit var lastPressedRangeButton: AppCompatButton
    private lateinit var lastPressedChartTypeButton: AppCompatButton

    private var compName = ""
    private var compShortName = ""

    private var chartType = "Line"
    private var prefChartType = "Line"
    private var chartRange = "1min"
    private var volumeClickFlag = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockChartBinding.inflate(inflater, container, false)
        compShortName = sharedViewModel.companyShortName.value.toString()
        compName = sharedViewModel.companyName.value.toString()

        if (viewModel.priceDaily.isEmpty() || viewModel.priceChartMH.isEmpty()) {
            viewModel.loadChartData(chartRange, compShortName)
        }

        activity?.findViewById<LinearLayoutCompat>(R.id.btm_bar)?.visibility = View.GONE

        onBackPressed()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupRangeButtons()
        setupChartTypeButtons()
        loadCompanyLogo()

        binding.compShortName.text = compShortName
        binding.loadingText.text = compShortName
        loadingAnimation()

        lastPressedRangeButton = binding.bottomSheet.btn1m
        lastPressedChartTypeButton = binding.bottomSheet.lineChart
        applyBorderAnimation(lastPressedRangeButton, false)
        startButtonAnimation(lastPressedChartTypeButton, true)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior?.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetListener()

        chart = binding.chartView
        setupChart()

        binding.chartSetting.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        chart.subscribeOnChartStateChange { state ->
            when(state) {
                is ChartsView.State.Ready -> {
                    binding.apply {
                        chartView.visibility = View.VISIBLE
                    }
                }
                is ChartsView.State.Preparing -> {
                    binding.apply {
                        chartView.visibility = View.INVISIBLE
                    }
                }
            }
        }
        chart.api.subscribeCrosshairMove { params ->
            val prices = params.seriesPrices
            if (params.time != null && !prices.isNullOrEmpty()) {
                try {
                    if (chartRange == "all") {
                        val time = params.time as Time.BusinessDay
                        val day = viewModel.convertDate(time.day)
                        val month = viewModel.convertDate(time.month)
                        binding.companyDate.text = resources.getString(
                            R.string.chartPointDateBD, day,
                            month, time.year.toString()
                        )
                    } else {
                        val time = params.time as Time.Utc
                        val date = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(time.timestamp),
                            ZoneId.of("Etc/UTC"))
                        val day = date.dayOfWeek.toString().subSequence(0, 3)
                        val hour = viewModel.convertDate(date.hour)
                        val minute = viewModel.convertDate(date.minute)
                        binding.companyDate.text = resources.getString(
                            R.string.chartPointDateUtc,
                            day, hour, minute
                        )
                    }

                    when (chartType) {
                        "Line" -> {
                            binding.companyPrice.text = resources.getString(
                                R.string.chartPriceLine,
                                viewModel.convertPrice(prices.first().prices.value!!)
                            )
                        }
                        "Bar" -> {
                            binding.companyOpen.text = resources.getString(
                                R.string.chartPriceOpen,
                                viewModel.convertPrice(prices.first().prices.open!!)
                            )
                            binding.companyPrice.text = resources.getString(
                                R.string.chartPriceClose,
                                viewModel.convertPrice(prices.first().prices.close!!)
                            )
                            binding.companyHigh.text = resources.getString(
                                R.string.chartPriceHigh,
                                viewModel.convertPrice(prices.first().prices.high!!)
                            )
                            binding.companyLow.text = resources.getString(
                                R.string.chartPriceLow,
                                viewModel.convertPrice(prices.first().prices.low!!)
                            )
                        }
                        "BaseLine" -> {
                            binding.companyPrice.text = resources.getString(
                                R.string.chartPriceLine,
                                viewModel.convertPrice(prices.first().prices.value!!)
                            )
                        }
                        "Candle" -> {
                            binding.companyOpen.text = resources.getString(
                                R.string.chartPriceOpen,
                                viewModel.convertPrice(prices.first().prices.open!!)
                            )
                            binding.companyPrice.text = resources.getString(
                                R.string.chartPriceClose,
                                viewModel.convertPrice(prices.first().prices.close!!)
                            )
                            binding.companyHigh.text = resources.getString(
                                R.string.chartPriceHigh,
                                viewModel.convertPrice(prices.first().prices.high!!)
                            )
                            binding.companyLow.text = resources.getString(
                                R.string.chartPriceLow,
                                viewModel.convertPrice(prices.first().prices.low!!)
                            )
                        }
                        "Area" -> {
                            binding.companyPrice.text = resources.getString(
                                R.string.chartPriceLine,
                                viewModel.convertPrice(prices.first().prices.value!!)
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CrossHair bind error", e.toString())
                }
            } else {
                bindLegend()
            }
        }
    }

    private fun bottomSheetListener() {
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                bottomSheetState = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun setupRangeButtons() {
        binding.bottomSheet.apply {
            btn1m.setOnClickListener {
                binding.chartSetting.isClickable = false
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                loadingAnimation()
                rangeButtonState(it as AppCompatButton)
                chartRangeChange(it)
                resetChart()
                viewModel.loadChartData("1min", compShortName)
            }
            btn5m.setOnClickListener {
                binding.chartSetting.isClickable = false
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                loadingAnimation()
                rangeButtonState(it as AppCompatButton)
                chartRangeChange(it)
                resetChart()
                viewModel.loadChartData("5min", compShortName)
            }
            btn30m.setOnClickListener {
                binding.chartSetting.isClickable = false
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                loadingAnimation()
                rangeButtonState(it as AppCompatButton)
                chartRangeChange(it)
                resetChart()
                viewModel.loadChartData("30min", compShortName)
            }
            btn1h.setOnClickListener {
                binding.chartSetting.isClickable = false
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                loadingAnimation()
                rangeButtonState(it as AppCompatButton)
                chartRangeChange(it)
                resetChart()
                viewModel.loadChartData("1hour", compShortName)
            }
            btn4h.setOnClickListener {
                binding.chartSetting.isClickable = false
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                loadingAnimation()
                rangeButtonState(it as AppCompatButton)
                chartRangeChange(it)
                resetChart()
                viewModel.loadChartData("4hour", compShortName)
            }
            btnAll.setOnClickListener {
                binding.chartSetting.isClickable = false
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                loadingAnimation()
                rangeButtonState(it as AppCompatButton)
                chartRangeChange(it)
                resetChart()
                viewModel.loadChartData("all", compShortName)
            }
        }
    }

    private fun loadingAnimation() {
        binding.chartLegend.visibility = View.INVISIBLE
        binding.loadingLogo.visibility = View.VISIBLE
        binding.loadingText.visibility = View.VISIBLE
        val loading = binding.loadingLogo.drawable as AnimatedVectorDrawable
        loading.start()
        val textAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.chart_loading_animation)
        binding.loadingText.startAnimation(textAnimation)
    }

    private fun deleteLoadingAnimation() {
        binding.loadingLogo.visibility = View.GONE
        binding.loadingText.clearAnimation()
        binding.loadingText.visibility = View.GONE
        binding.chartLegend.visibility = View.VISIBLE
    }

    private fun chartRangeChange(button: AppCompatButton) {
        chartRange = when (button) {
            binding.bottomSheet.btn1m -> "1min"
            binding.bottomSheet.btn5m -> "5min"
            binding.bottomSheet.btn30m -> "30min"
            binding.bottomSheet.btn1h -> "1hour"
            binding.bottomSheet.btn4h -> "4hour"
            binding.bottomSheet.btnAll -> "all"
            else -> ""
        }
    }

    private fun resetChart() {
        try {
            chart.api.removeSeries(dataSeries)
            if (volumeClickFlag) {
                chart.api.removeSeries(volumeSeries)
                volumeClickFlag = false
                val volume = binding.bottomSheet.volumeChart
                startButtonAnimation(volume, false)
            }
            chartStateChange(binding.bottomSheet.lineChart)
            if (lastPressedChartTypeButton != binding.bottomSheet.lineChart) {
                chartTypeButtonState(binding.bottomSheet.lineChart)
            }
        } catch (e: Exception) {
            Log.e("Chart error", "Delete dataSeries error!")
        }
    }

    private fun rangeButtonState(pressed: AppCompatButton) {
        pressed.isEnabled = false
        applyBorderAnimation(pressed, false)
        lastPressedRangeButton.isEnabled = true
        applyBorderAnimation(lastPressedRangeButton, true)
        lastPressedRangeButton = pressed
    }

    private fun setupChartTypeButtons() {
        val bottomSh = binding.bottomSheet
        bottomSh.lineChart.setOnClickListener {
            binding.chartSetting.isClickable = false
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            chartTypeButtonState(it as AppCompatButton)
            chart.api.removeSeries(dataSeries)
            chartStateChange(it)
            viewModel.createData(chartType, chartRange)
        }
        bottomSh.candlestickChart.setOnClickListener {
            binding.chartSetting.isClickable = false
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            chartTypeButtonState(it as AppCompatButton)
            chartStateChange(it)
            chart.api.removeSeries(dataSeries)
            viewModel.createData(chartType, chartRange)
        }
        bottomSh.barChart.setOnClickListener {
            binding.chartSetting.isClickable = false
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            chartTypeButtonState(it as AppCompatButton)
            chartStateChange(it)
            chart.api.removeSeries(dataSeries)
            viewModel.createData(chartType, chartRange)
        }
        bottomSh.baselineChart.setOnClickListener {
            binding.chartSetting.isClickable = false
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            chartTypeButtonState(it as AppCompatButton)
            chartStateChange(it)
            chart.api.removeSeries(dataSeries)
            viewModel.createData(chartType, chartRange)
        }
        bottomSh.areaChart.setOnClickListener {
            binding.chartSetting.isClickable = false
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            chartTypeButtonState(it as AppCompatButton)
            chartStateChange(it)
            chart.api.removeSeries(dataSeries)
            viewModel.createData(chartType, chartRange)
        }
        bottomSh.volumeChart.setOnClickListener {
            binding.chartSetting.isClickable = false
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            volumeButtonState(it as AppCompatButton)
            if (volumeClickFlag) {
                chartStateChange(it)
                viewModel.createData(chartType, chartRange)
            } else {
                chart.api.removeSeries(volumeSeries)
            }
        }
    }

    private fun startButtonAnimation(button: AppCompatButton, pressed: Boolean) {
        if (pressed) {
            applyBorderAnimation(button, false)
            val icon: Drawable? = when (button) {
                binding.bottomSheet.lineChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_line_active, context?.theme)
                binding.bottomSheet.candlestickChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_candle_stick_active, context?.theme)
                binding.bottomSheet.barChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_chart_bar_active, context?.theme)
                binding.bottomSheet.baselineChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_baseline_active, context?.theme)
                binding.bottomSheet.volumeChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_volume_active, context?.theme)
                binding.bottomSheet.areaChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_area_active, context?.theme)
                else -> null
            }
            button.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
            val drawable = button.compoundDrawables[1] as AnimatedVectorDrawable
            drawable.start()
        } else {
            applyBorderAnimation(button, true)
            val icon: Drawable? = when (button) {
                binding.bottomSheet.lineChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_line_default, context?.theme)
                binding.bottomSheet.candlestickChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_candle_stick_default, context?.theme)
                binding.bottomSheet.barChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_chart_bar_default, context?.theme)
                binding.bottomSheet.baselineChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_baseline_default, context?.theme)
                binding.bottomSheet.volumeChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_volume_default, context?.theme)
                binding.bottomSheet.areaChart -> ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_area_default, context?.theme)
                else -> null
            }
            button.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
            val drawable = button.compoundDrawables[1] as AnimatedVectorDrawable
            drawable.start()
        }
    }

    private fun chartStateChange(button: AppCompatButton) {
        prefChartType = chartType
        chartType = when (button) {
            binding.bottomSheet.lineChart -> "Line"
            binding.bottomSheet.candlestickChart -> "Candle"
            binding.bottomSheet.barChart -> "Bar"
            binding.bottomSheet.baselineChart -> "BaseLine"
            binding.bottomSheet.volumeChart -> "Volume"
            binding.bottomSheet.areaChart -> "Area"
            else -> ""
        }
    }

    private fun volumeButtonState(button: AppCompatButton) {
        volumeClickFlag = if (!volumeClickFlag) {
            startButtonAnimation(button, true)
            true
        } else {
            startButtonAnimation(button, false)
            false
        }
    }

    private fun chartTypeButtonState(pressed: AppCompatButton) {
        if (lastPressedChartTypeButton != pressed) {
            pressed.isEnabled = false
            startButtonAnimation(pressed, true)
            lastPressedChartTypeButton.isEnabled = true
            startButtonAnimation(lastPressedChartTypeButton, false)
            lastPressedChartTypeButton = pressed
        }
    }

    private fun applyBorderAnimation(button: AppCompatButton, reverse: Boolean) {
        val transition = button.background as TransitionDrawable
        if (reverse) {
            transition.reverseTransition(200)
        } else {
            transition.startTransition(200)
        }
    }

    private fun bindLegend() {
        val data = viewModel.getLastChartPoint(chartRange)
        if (chartType == "Candle" || chartType == "Bar") {
            val priceConstraint = binding.companyPrice.layoutParams as ConstraintLayout.LayoutParams
            priceConstraint.topToBottom = binding.companyOpen.id
            binding.companyOpen.visibility = View.VISIBLE
            binding.companyHigh.visibility = View.VISIBLE
            binding.companyLow.visibility = View.VISIBLE
        } else {
            val priceConstraint = binding.companyPrice.layoutParams as ConstraintLayout.LayoutParams
            priceConstraint.topToBottom = binding.companyDate.id
            binding.companyOpen.visibility = View.GONE
            binding.companyHigh.visibility = View.GONE
            binding.companyLow.visibility = View.GONE
        }

        binding.companyDate.text = data["date"]
        binding.companyOpen.text = resources.getString(R.string.chartPriceOpen, data["open"])
        if (chartType == "bar" || chartType == "Candle") {
            binding.companyPrice.text = resources.getString(R.string.chartPriceClose, data["price"])
        } else {
            binding.companyPrice.text = resources.getString(R.string.chartPriceLine, data["price"])
        }
        binding.companyHigh.text = resources.getString(R.string.chartPriceHigh, data["high"])
        binding.companyLow.text = resources.getString(R.string.chartPriceLow, data["low"])
    }

    private fun setupChart() {
        chart.api.applyOptions {
            rightPriceScale = PriceScaleOptions(borderVisible = false)
            timeScale = TimeScaleOptions(borderVisible = false)
            crosshair = crosshairOptions {
                mode = CrosshairMode.NORMAL
                horzLine = crosshairLineOptions {
                    visible = false
                    labelVisible = false
                }
                vertLine = crosshairLineOptions {
                    visible = true
                    labelVisible = false
                }
            }
            timeScale = timeScaleOptions {
                timeVisible = true
            }
            localization = localizationOptions {
                locale = Locale.getDefault().language
                timeFormatter = TimeFormatter(
                    locale = Locale.getDefault().language,
                    dateTimeFormat = DateTimeFormat.DATE
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.status.observe(this.viewLifecycleOwner) { status ->
            if (status == StockApiStatus.DONE) {
                viewModel.createData(chartType, chartRange)
            } else if (status == StockApiStatus.ERROR) {
                Toast.makeText(requireContext(), "No chart data", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.dataStatus.observe(this.viewLifecycleOwner) { status ->
            if (status == StockApiStatus.DONE) {
                bindLegend()
                bindChartData()
            }
        }
    }

    private fun bindChartData() {
        when (chartType) {
            "Line" -> {
                chart.api.addLineSeries(LineSeriesOptions(
                    priceLineColor = resources.getColor(R.color.orange, requireContext().theme).
                    toIntColor(),
                    color = resources.getColor(R.color.orange, requireContext().theme).
                    toIntColor(),
                    lastPriceAnimation = LastPriceAnimationMode.CONTINUOUS),
                    onSeriesCreated = { series ->
                        dataSeries = series
                        dataSeries.setData(viewModel.getChartData())
                    })
            }
            "Candle" -> {
                chart.api.addCandlestickSeries(
                    onSeriesCreated = { series ->
                        dataSeries = series
                        dataSeries.setData(viewModel.getChartData())
                    })
            }
            "Bar" -> {
                chart.api.addBarSeries(
                    onSeriesCreated = { series ->
                        dataSeries = series
                        dataSeries.setData(viewModel.getChartData())
                    })
            }
            "BaseLine" -> {
                chart.api.addBaselineSeries(BaselineSeriesOptions(baseValue =
                    BaseValuePrice(viewModel.getLastPrice(chartRange))),
                    onSeriesCreated = { series ->
                        dataSeries = series
                        dataSeries.setData(viewModel.getChartData())
                    })
            }
            "Volume" -> {
                chart.api.addHistogramSeries(HistogramSeriesOptions(
                    priceFormat = PriceFormat(type = PriceFormat.Type.VOLUME),
                    priceScaleId = PriceScaleId(""),
                    scaleMargins = PriceScaleMargins(top = 0.9f, bottom = 0f)),
                    onSeriesCreated = { series ->
                        volumeSeries = series
                        volumeSeries.setData(viewModel.getChartData())
                    }
                )
                chartType = prefChartType
            }
            "Area" -> {
                chart.api.addAreaSeries(AreaSeriesOptions(lineColor =
                resources.getColor(R.color.orange, requireContext().theme).toIntColor(), topColor =
                resources.getColor(R.color.orange, requireContext().theme).toIntColor(), bottomColor =
                Color.argb(10, 252, 163, 17).toIntColor()),
                    onSeriesCreated = { series ->
                        dataSeries = series
                        dataSeries.setData(viewModel.getChartData())
                    }
                )
            }
        }
        deleteLoadingAnimation()
        binding.chartSetting.isClickable = true
    }

    private fun loadCompanyLogo() {
        Glide.with(requireContext()).
        load("https://financialmodelingprep.com/image-stock/${compShortName}.png").
        error(R.drawable.ic_warning)
            .centerCrop().into(binding.logo)
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetState) {
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    findNavController().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


}