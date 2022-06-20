package com.example.stocks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.stocks.models.remote.CompanyProfile
import com.example.stocks.R
import com.example.stocks.models.remote.StockChart
import com.example.stocks.viewmodels.StockOverViewViewModel
import com.example.stocks.databinding.FragmentStockKeyStatisticBinding

class StockKeyStatisticFragment : Fragment() {

    private val viewModel: StockOverViewViewModel by activityViewModels()

    private var _binding: FragmentStockKeyStatisticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockKeyStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.companyProfile.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                bind(it)
            }
        }
        viewModel.priceChart.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                bindClosePrice(it)
            }
        }
    }

    private fun bind(profileList: List<CompanyProfile>) {
        val profile = profileList[0]
        binding.apply {
            lastDiv.text = profile.lastDiv
            avgVolume.text = formattedVolAvg(profile)
            marketCap.text = formattedMktCap(profile)
            weekRange.text = profile.range
            compBeta.text = profile.beta.toString()
        }
    }

    private fun bindClosePrice(price: MutableList<StockChart>) {
        binding.closePrice.text = viewModel.getClosePrice(price).toString()
    }

    private fun formattedVolAvg(profile: CompanyProfile): String {
        return when {
            profile.volAvg in 1000..99999 -> {
                requireContext().getString(
                    R.string.value_format_K,
                profile.volAvg.toFloat() / 1000
                )
            }
            profile.volAvg in 1000000..999999999 -> {
                requireContext().getString(
                    R.string.value_format_M,
                    profile.volAvg.toFloat() / 1000000
                )
            }
            profile.volAvg in 1000000000..999999999999 -> {
                requireContext().getString(
                    R.string.value_format_B,
                    profile.volAvg.toFloat() / 1000000000
                )
            }
            profile.volAvg >= 1000000000000 -> {
                requireContext().getString(
                    R.string.value_format_T,
                    profile.volAvg.toFloat() / 1000000000000
                )
            }
            else -> {
                profile.volAvg.toString()
            }
        }
    }

    private fun formattedMktCap(profile: CompanyProfile): String {
        return when {
            profile.mktCap in 1000..999999 -> {
                requireContext().getString(
                    R.string.value_format_K,
                    profile.mktCap.toFloat() / 1000
                )
            }
            profile.mktCap in 1000000..999999999 -> {
                requireContext().getString(
                    R.string.value_format_M,
                    profile.mktCap.toFloat() / 1000000
                )
            }
            profile.mktCap in 1000000000..999999999999 -> {
                requireContext().getString(
                    R.string.value_format_B,
                    profile.mktCap.toFloat() / 1000000000
                )
            }
            profile.mktCap >= 1000000000000 -> {
                requireContext().getString(
                    R.string.value_format_T,
                    profile.mktCap.toFloat() / 1000000000000
                )
            }
            else -> {
                profile.mktCap.toString()
            }
        }
    }
}