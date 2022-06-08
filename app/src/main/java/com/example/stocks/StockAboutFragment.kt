package com.example.stocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.stocks.databinding.FragmentStockAboutBinding

class StockAboutFragment : Fragment() {

    private val viewModel: StockOverViewViewModel by activityViewModels()

    private var _binding: FragmentStockAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.companyProfile.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                bind(it[0])
            }
        }
    }

    private fun bind(profile: CompanyProfile) {
        binding.apply {
            compSymbol.text = profile.symbol
            exchangeName.text = profile.exchangeShortName
            industryName.text = profile.industry
            sectorName.text = profile.sector
            countryName.text = profile.country
            ceoName.text = profile.ceo
            profileInfo.text = profile.description
        }
    }
}