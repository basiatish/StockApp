package com.example.stocks

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.stocks.databinding.FragmentStockDividendsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet

class StockDividendsFragment : Fragment() {

    private val viewModel: StockOverViewViewModel by activityViewModels()
    private var _binding: FragmentStockDividendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var chart: BarChart
    private var xMax = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockDividendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chart = binding.dividendChart
        viewModel.companyDividends.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setChart(it)
                chart.moveViewTo((xMax-9), 0f, YAxis.AxisDependency.LEFT)
            }
        }
    }

    private fun setChart(mutableList: MutableList<StockDividendsHeader>) {
        chart.fitScreen()
        chart.renderer = CustomBarChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler())
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.isDragDecelerationEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        chart.setDrawGridBackground(false)
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = false
        chart.setDrawBorders(false)
        chart.isHighlightPerDragEnabled = false
        chart.isHighlightPerTapEnabled = false
        chart.isHighlightFullBarEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.isDoubleTapToZoomEnabled = false
        val x = chart.xAxis
        x.isEnabled = true
        x.setDrawGridLines(false)

        val y = chart.axisRight
        y.isEnabled = false
        chart.axisLeft.isEnabled = false

        x.valueFormatter = BarChartValueFormatter(mutableList)

        x.position = XAxis.XAxisPosition.BOTTOM
        x.textSize = 8f
        x.typeface = Typeface.SANS_SERIF
        x.labelRotationAngle = -30f
        x.granularity = 1f
        x.axisMinimum = 0f
        x.isGranularityEnabled = true

        setupChartData()

        chart.setKeepPositionOnRotation(true)
        chart.setVisibleXRangeMinimum(8f)
        chart.setVisibleXRangeMaximum(10f)
        chart.animateY(2000)
        chart.setFitBars(true)
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    private fun setupChartData() {
        val dataSet = BarDataSet(viewModel.getBarChart(), "Label")
        dataSet.valueTextColor = requireContext().getColor(R.color.black)
        dataSet.valueTextSize = 8f
        dataSet.setColor(requireContext().getColor(R.color.orange))

        dataSet.notifyDataSetChanged()

        val barData = BarData(dataSet)

        xMax = barData.xMax
        chart.data = barData
        barData.notifyDataChanged()
    }
}