package com.example.stocks.ui.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.stocks.*
import com.example.stocks.databinding.FragmentStockOverviewBinding
import com.example.stocks.models.remote.CompanyProfile
import com.example.stocks.models.remote.StockChart
import com.example.stocks.models.remote.StockDailyPriceHeader
import com.example.stocks.models.remote.StockYearlyPriceHeader
import com.example.stocks.ui.chart.linechart.MarkerView
import com.example.stocks.utils.network.StockApiStatus
import com.example.stocks.utils.chart.linechart.LineChartValueFormatter
import com.example.stocks.viewmodels.StockOverViewViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.bottomappbar.BottomAppBar

class StockOverViewFragment: Fragment() {

    private val viewModel: StockOverViewViewModel by activityViewModels()

    private val navArgs: StockOverViewFragmentArgs by navArgs()

    private var _binding: FragmentStockOverviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var chart: LineChart
    private lateinit var range: String
    private var lineColor: Int = 0
    private var gridColor: Int = 0
    private var lastClosePrice: Float = 0F
    private var closePrice: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {  
        _binding = FragmentStockOverviewBinding.inflate(inflater, container, false)
        //viewModel.getChartData("1day", navArgs.shortName)
        viewModel.getCompanyProfile(navArgs.shortName)
        viewModel.getCompanyDividends(navArgs.shortName)
        range = "1day"
        lineColor = requireContext().getColor(R.color.orange)
        gridColor = requireContext().getColor(R.color.light_grey)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val navbar = requireActivity().findViewById<BottomAppBar>(R.id.btm_bar)
//        navbar.hideOnScroll = false

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StockKeyStatisticFragment()).commit()
        chart = binding.chart
        viewModel.priceChart.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                //setChart(it, listOf(), listOf())
                //setPrice(it)
            }
        }
        viewModel.priceChartDaily.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                //setChart(listOf(), it, listOf())
            }
        }
        viewModel.priceChartYearly.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                //setChart(listOf(), listOf(), it)
            }
        }
        viewModel.status.observe(this.viewLifecycleOwner) {
            if (it == StockApiStatus.ERROR) {
                setErrorMessage()
            }
        }
        viewModel.companyProfile.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                loadCompanyLogo(it)
            }
        }
        binding.backBtn.setOnClickListener {
            NavigationUI.navigateUp(findNavController(), null)
        }
        binding.btn1m.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("1min", navArgs.shortName)
                range = "1min"
            }
        }
        binding.btn5m.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("5min", navArgs.shortName)
                range = "5min"
            }
        }
        binding.btn15m.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("15min", navArgs.shortName)
                range = "15min"
            }
        }
        binding.btn1h.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("1hour", navArgs.shortName)
                range = "1hour"
            }
        }
        binding.btn4h.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("4hour", navArgs.shortName)
                range = "4hour"
            }
        }
        binding.btn1d.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("1day", navArgs.shortName)
                range = "1day"
            }
        }
        binding.btn5d.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("1week", navArgs.shortName)
                range = "1week"
            }
        }
        binding.btnAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getChartData("all", navArgs.shortName)
                range = "all"
            }
        }
        binding.overview.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                StockKeyStatisticFragment()
                ).commit()
            }
        }
        binding.dividend.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                StockDividendsFragment()
                ).commit()
            }
        }
        binding.about.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                StockAboutFragment()
                ).commit()
            }
        }
        binding.refresh.setOnRefreshListener {
            viewModel.getChartData(range, navArgs.shortName)
            viewModel.getCompanyProfile(navArgs.shortName)
            viewModel.getCompanyDividends(navArgs.shortName)
            binding.refresh.isRefreshing = false
        }

        binding.compName.text = formattedCompanyName(navArgs.name)
    }

    private fun loadCompanyLogo(profile: MutableList<CompanyProfile>) {
        Glide.with(requireContext()).load(profile[0].image).error(R.drawable.ic_warning)
            .centerCrop().circleCrop().
            into(binding.compLogo)
    }

    private fun formattedCompanyName(compName: String): String {
        return when {
            compName.length > 15 -> {
                "${compName.subSequence(0, 15).trim()}..."
            }
            else -> compName
        }

    }

    override fun onStop() {
        super.onStop()
        viewModel.clearData()
    }

    private fun setErrorMessage() {
        Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
    }

    private fun setPrice(price: MutableList<StockChart>) {
        val currentPrice = price[0].close
        if (!closePrice) {
            lastClosePrice = viewModel.getClosePrice(price)
            if (lastClosePrice == 0f) {
                lastClosePrice = currentPrice!!
            }
        }
        closePrice = true
        if (lastClosePrice < currentPrice!!) {
            binding.priceChange.text = getString(
                R.string.price_change_above,
                currentPrice - lastClosePrice )
            binding.priceChange.setTextColor(requireContext().getColor(R.color.green))
            binding.pricePercent.text = getString(
                R.string.price_change_percent,
                    (currentPrice - lastClosePrice) / currentPrice * 100)
            binding.pricePercent.setTextColor(requireContext().getColor(R.color.green))
        } else if (lastClosePrice > currentPrice) {
            binding.priceChange.text = getString(
                R.string.price_change_below,
                    currentPrice - lastClosePrice)
            binding.priceChange.setTextColor(requireContext().getColor(R.color.red))
            binding.pricePercent.text = getString(
                R.string.price_change_percent,
                    (currentPrice - lastClosePrice) / currentPrice * 100)
            binding.pricePercent.setTextColor(requireContext().getColor(R.color.red))
        } else if (lastClosePrice == currentPrice) {
            binding.priceChange.text = getString(
                R.string.price_change_equals,
                currentPrice - lastClosePrice)
            binding.pricePercent.text = getString(
                R.string.price_change_percent,
                (currentPrice - lastClosePrice) / currentPrice * 100)
        }
        binding.stockPrice.text = getString(R.string.price, currentPrice)
    }

    private fun setChart(price: List<StockChart> = listOf(),
                         priceDaily: List<StockDailyPriceHeader> = listOf(),
                         priceChartYearly: List<StockYearlyPriceHeader> = listOf()) {
        chart.fitScreen()
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.isDragDecelerationEnabled = false
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 100f
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = false
        chart.setDrawBorders(false)
        chart.description.text = ""
        chart.legend.isEnabled = false
        chart.isDoubleTapToZoomEnabled = false
        chart.isHighlightPerDragEnabled = false
        chart.isHighlightPerTapEnabled = true
        val x = chart.xAxis
        x.isEnabled = true
        x.setDrawGridLines(false)

        val y = chart.axisRight
        y.isEnabled = true
        chart.axisLeft.isEnabled = false

        x.valueFormatter = LineChartValueFormatter(viewModel.getLineChartDate(range), range)

        x.position = XAxis.XAxisPosition.BOTTOM
        x.textSize = 8f
        x.typeface = Typeface.SANS_SERIF
        x.labelRotationAngle = -30f
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        y.textSize = 8f
        y.typeface = Typeface.SANS_SERIF
        y.enableGridDashedLine(10f, 10f, 0f)
        y.gridLineWidth = 1f

        setupChartData()

        chart.isHighlightPerTapEnabled = true
        chart.setDrawMarkers(false)
        chart.axisLeft.gridColor = gridColor
        chart.axisLeft.gridLineWidth = 3f
        chart.isHighlightPerDragEnabled = true
        chart.dragDecelerationFrictionCoef = 0.3f
        chart.setDrawMarkerViews(true)

        val marker: MarkerView
        if (range == "1day" || range == "1week") {
            marker = MarkerView(requireContext(), R.layout.custom_marker_view, price.reversed(),
                priceDaily, priceChartYearly, range)
        } else if (range == "all") {
            marker = MarkerView(requireContext(), R.layout.custom_marker_view, price.reversed(),
                priceDaily, priceChartYearly, range)
        } else {
            marker = MarkerView(requireContext(), R.layout.custom_marker_view, price.reversed(),
                priceDaily, priceChartYearly, range)
        }

        chart.setMarker(marker)
        chart.isAutoScaleMinMaxEnabled = true
        chart.setKeepPositionOnRotation(true)
        chart.extraBottomOffset = 5f
        chart.setVisibleXRangeMinimum(20f)
        chart.animateX(2000)
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    private fun setupChartData() {
        val dataSet = LineDataSet(viewModel.getLineChart(range), "Label")
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 3f
        dataSet.setColor(lineColor, 255)
        dataSet.setDrawValues(false)
        dataSet.highlightLineWidth = 2f
        dataSet.highLightColor = gridColor
        dataSet.setDrawFilled(true)
        dataSet.fillColor = lineColor
        dataSet.fillAlpha = 50
        dataSet.setCircleColor(lineColor)
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.notifyDataSetChanged()

        val lineData = LineData(dataSet)
        chart.data = lineData
        lineData.notifyDataChanged()
    }
}
