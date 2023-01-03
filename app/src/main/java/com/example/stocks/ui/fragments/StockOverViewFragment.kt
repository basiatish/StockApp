package com.example.stocks.ui.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.stocks.*
import com.example.stocks.adapters.StockDividendsAdapter
import com.example.stocks.databinding.FragmentStockOverviewBinding
import com.example.stocks.models.remote.CompanyProfile
import com.example.stocks.models.remote.CompanyQuote
import com.example.stocks.utils.network.StockApiStatus
import com.example.stocks.viewmodels.SharedViewModel
import com.example.stocks.viewmodels.StockOverViewViewModel
import kotlin.math.roundToInt

class StockOverViewFragment: Fragment() {

    private val viewModel: StockOverViewViewModel by viewModels()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val navArgs: StockOverViewFragmentArgs by navArgs()

    private var _binding: FragmentStockOverviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StockDividendsAdapter
    private lateinit var layoutManager: GridLayoutManager

    private lateinit var compName: String
    private lateinit var shortName: String
    private var week52Low: String = ""
    private var week52High: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compName = navArgs.name
        shortName = navArgs.shortName
        sharedViewModel.saveCompanyName(compName, shortName)
        viewModel.getComp(shortName)
        //viewModel.getCompanyProfile(shortName)
        viewModel.getCompanyQuote(shortName)
        viewModel.getCompanyDividends(shortName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockOverviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StockDividendsAdapter()
        binding.dividendsView.adapter = adapter
        layoutManager = GridLayoutManager(this.context, 1)
        binding.dividendsView.layoutManager = layoutManager

        binding.refresh.setOnRefreshListener {
            viewModel.apply {
                getCompanyProfile(shortName)
                getCompanyQuote(shortName)
                viewModel.quoteStatus.observe(viewLifecycleOwner) { status ->
                    if (status == StockApiStatus.DONE) {
                        binding.refresh.isRefreshing = false
                    }
                }
            }

        }

        viewModel.profileStatus.observe(this.viewLifecycleOwner) {
            if (it == StockApiStatus.DONE && viewModel.compProf.isNotEmpty()) {
                week52High = ""
                week52Low = ""
                loadCompanyLogo(viewModel.compProf)
                rangeBetaBind(viewModel.compProf)
                topBarBind()
                aboutBind(viewModel.compProf)
            }
        }

        setupObservers()

        var clickedFlag = false
        binding.moreBtn.setOnClickListener {
            if (!viewModel.companyDividends.value.isNullOrEmpty()) {
                viewModel.getDividendsList()
                if (!clickedFlag) {
                    binding.moreBtn.setImageResource(R.drawable.avd_anim_more)
                    val less = binding.moreBtn.drawable
                    if (less is AnimatedVectorDrawable) {
                        val anim = less as AnimatedVectorDrawable
                        anim.start()
                    }
                    clickedFlag = true
                } else {
                    binding.moreBtn.setImageResource(R.drawable.avd_anim_less)
                    val more = binding.moreBtn.drawable
                    if (more is AnimatedVectorDrawable) {
                        val anim = more as AnimatedVectorDrawable
                        anim.start()
                    }
                    binding.scrollView.smoothScrollTo(binding.keyDividendDivider.x.toInt(),
                        binding.keyDividendDivider.y.toInt(), 800)
                    clickedFlag = false
                }
            } }

        binding.backBtn.setOnClickListener {
            NavigationUI.navigateUp(findNavController(), null)
        }
    }

    private fun setupObservers() {
//        viewModel.companyProfile.observe(this.viewLifecycleOwner) {
//            if (!it.isNullOrEmpty()) {
//                week52High = ""
//                week52Low = ""
//                loadCompanyLogo(it)
//                rangeBetaBind(it)
//                topBarBind()
//                aboutBind(it)
//            }
//        }
        viewModel.companyQuote.observe(this.viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                statBind(it)
            }
        }
        viewModel.companyDividends.observe(this.viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                viewModel.getDividendsList()
            }
        }
        viewModel.companyDividendsList.observe(this.viewLifecycleOwner) {
            if (!it.isNullOrEmpty() && it[0].dividend != 0.0) {
                adapter.submitList(it)
            } else {
                deleteDividendsView()
            }
        }
    }

    private fun topBarBind() {
        if (compName.length > 22) {
            compName = compName.substring(0, 19).trim() + "..."
        }
        binding.compName.text = compName
    }

    private fun statBind(quote: MutableList<CompanyQuote>) {
        binding.apply {
            stockPrice.text = "$ ${cutValueZeros(quote[0].price ?: 0.0)}"
            priceChange.apply {
                val change = quote[0].change ?: 0.0
                text = if(change < 0) cutValueZeros(change) else "+${cutValueZeros(change)}"
                if (change >= 0) setTextColor(resources.getColor(R.color.green))
                else setTextColor(resources.getColor(R.color.red))
            }
            pricePercent.apply {
                val changesPercentage = quote[0].changesPercentage ?: 0.0
                text = "(${cutValueZeros(changesPercentage)}%)"
                if (changesPercentage > 0) setTextColor(resources.getColor(R.color.green))
                else setTextColor(resources.getColor(R.color.red))
            }
            highPrice.text = cutValueZeros(quote[0].dayHigh ?: 0.0)
            lowPrice.text = cutValueZeros(quote[0].dayLow ?: 0.0)
            volValue.text = formattedValue(quote[0].volume ?: 0.0)
            marketCapValue.text = formattedValue(quote[0].marketCap ?: 0.0)
            avgVolumeValue.text = formattedValue(quote[0].avgVolume ?: 0.0)
            peValue.text = cutValueZeros(quote[0].pe ?: 0.0)
        }
    }

    private fun rangeBetaBind(profile: MutableList<CompanyProfile>) {
        formattedRange(profile[0].range ?: "")
        binding.apply {
            high52w.text = cutValueZeros(week52High.toDouble() ?: 0.0)
            low52w.text = cutValueZeros(week52Low.toDouble() ?: 0.0)
            betaValue.text = cutValueZeros(profile[0].beta ?: 0.0)
        }
    }

    private fun aboutBind(value: MutableList<CompanyProfile>) {
        val profile = value[0]
        binding.apply {
            symbol.text = profile.symbol
            exchange.text = profile.exchange
            industry.text = profile.industry
            sector.text = profile.sector
            country.text = profile.country
            ceo.text = profile.ceo
            description.text = profile.description
        }
    }

    private fun loadCompanyLogo(profile: MutableList<CompanyProfile>) {
        Glide.with(requireContext()).load(profile[0].image).error(R.drawable.ic_warning)
            .centerCrop().into(binding.compLogo)
    }
    
    private fun deleteDividendsView() {
        binding.keyDividendDivider.visibility = View.GONE
        binding.dividendsView.visibility = View.GONE
        binding.moreBtn.visibility = View.GONE
        val constraint = binding.keyAboutDivider.layoutParams as ConstraintLayout.LayoutParams
        constraint.topToBottom = binding.volumeStat.id
    }

    private fun formattedValue(value: Double): String {
        return when (value) {
            in 1000.0..999999.0 -> "${(value / 10.0).roundToInt() / 100.0}K"
            in 1000000.0..999999999.0 -> "${(value / 10000.0).roundToInt() / 100.0}M"
            in 1000000000.0..999999999999.0 -> "${(value / 10000000.0).roundToInt() / 100.0}B"
            in 1000000000000.0..999999999999999.0 -> "${(value / 10000000000.0).roundToInt() / 100.0}T"
            0.0 -> "—"
            else -> "$value"
        }
    }

    private fun cutValueZeros(value: Double): String {
        return when (value) {
            0.0 -> "—"
            else -> "${(value * 100.0).roundToInt() / 100.0}"
        }
    }

    private fun formattedRange(range: String) {
        var i = 0
        var flag = false
        while (i <= range.length - 1) {
            if (range[i] == '-') {
                flag = true
                i++
            }
            if (!flag) week52Low += range[i] else week52High += range[i]
            i++
        }
    }
}
