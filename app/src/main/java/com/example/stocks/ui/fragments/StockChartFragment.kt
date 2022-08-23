package com.example.stocks.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.stocks.databinding.FragmentStockChartBinding
import com.example.stocks.viewmodels.StockOverViewViewModel
import com.tradingview.lightweightcharts.api.chart.models.color.surface.SolidColor
import com.tradingview.lightweightcharts.api.chart.models.color.toIntColor
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi
import com.tradingview.lightweightcharts.api.options.models.layoutOptions
import com.tradingview.lightweightcharts.api.options.models.localizationOptions
import com.tradingview.lightweightcharts.api.series.common.SeriesData
import com.tradingview.lightweightcharts.api.series.models.LineData
import com.tradingview.lightweightcharts.api.series.models.Time
import com.tradingview.lightweightcharts.runtime.plugins.DateTimeFormat
import com.tradingview.lightweightcharts.runtime.plugins.PriceFormatter
import com.tradingview.lightweightcharts.runtime.plugins.TimeFormatter
import com.tradingview.lightweightcharts.view.ChartsView

class StockChartFragment : Fragment() {

    private var _binding: FragmentStockChartBinding? = null
    private val binding get() = _binding!!

    private lateinit var chart: ChartsView

    private val viewModel: StockOverViewViewModel by activityViewModels()

    private lateinit var lineSeries: SeriesApi

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockChartBinding.inflate(inflater, container, false)

        viewModel.getChartData("1day", "GM")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = binding.chartView
        chart.api.applyOptions {
            layout = layoutOptions {
                background = SolidColor(Color.WHITE)
                textColor = Color.BLACK.toIntColor()
            }
            localization = localizationOptions {
                locale = "ru-RU"
                priceFormatter = PriceFormatter(template = "{price:#2:#3}$")
                timeFormatter = TimeFormatter(
                    locale = "ru-RU",
                    dateTimeFormat = DateTimeFormat.DATE_TIME
                )
            }
        }

        viewModel.priceChartDaily.observe(this.viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                chart.api.addLineSeries(
                    onSeriesCreated = { series ->
                        lineSeries = series
                        lineSeries.setData(viewModel.setupChartData())
                    }
                )
            } else {
                Toast.makeText(requireContext(), "No chart data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}