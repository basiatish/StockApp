package com.example.stocks.ui.fragments

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.stocks.*
import com.example.stocks.adapters.StockDividendsAdapter
import com.example.stocks.databinding.FragmentStockOverviewBinding
import com.example.stocks.models.remote.CompanyProfile
import com.example.stocks.models.remote.CompanyQuote
import com.example.stocks.utils.images.loadLogo
import com.example.stocks.utils.network.StockStatus
import com.example.stocks.viewmodels.SharedViewModel
import com.example.stocks.viewmodels.StockOverViewViewModel
import com.example.stocks.viewmodels.StockOverViewViewModelFactory

class StockOverViewFragment: Fragment() {

    private val viewModel: StockOverViewViewModel by viewModels {
        StockOverViewViewModelFactory((requireContext().applicationContext as App).stockDataBase.stockDao())
    }

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val navArgs: StockOverViewFragmentArgs by navArgs()

    private var _binding: FragmentStockOverviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StockDividendsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var compName: String
    private lateinit var shortName: String

    private var isFavourite = false
    private var isExist = false
    private var isMoreButtonClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compName = navArgs.name
        shortName = navArgs.shortName
        sharedViewModel.saveCompanyName(compName, shortName)
        viewModel.getCompanyProfile(shortName)
        viewModel.getCompanyQuote(shortName)
        viewModel.getCompanyDividends(shortName)
        viewModel.isStockFavourite(shortName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockOverviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StockDividendsAdapter()
        binding.dividendsView.adapter = adapter
        layoutManager = LinearLayoutManager(context)
        binding.dividendsView.layoutManager = layoutManager

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {

        viewModel.profileStatus.observe(this.viewLifecycleOwner) {
            if (it == StockStatus.DONE) {
                loadCompanyLogo(viewModel.companyProfile)
                rangeBetaBind(viewModel.companyProfile)
                topBarBind()
                aboutBind(viewModel.companyProfile)
                binding.favoriteBtn.isClickable = true
            }
        }
        viewModel.quoteStatus.observe(this.viewLifecycleOwner) {
            if (it == StockStatus.DONE) {
                statBind(viewModel.companyQuote)
                if (binding.refresh.isRefreshing) binding.refresh.isRefreshing = false
            }
        }
        viewModel.dividendsStatus.observe(this.viewLifecycleOwner) {
            if (it == StockStatus.DONE) {
                viewModel.getDividendsList()
            }
        }
        viewModel.divListStatus.observe(this.viewLifecycleOwner) {
            if (it == StockStatus.DONE) {
                adapter.submitList(viewModel.companyDividendsList)
            }
            else {
                deleteDividendsView()
            }
        }

        viewModel.isFavourite.observe(this.viewLifecycleOwner) {
            if (it == shortName) {
                isExist = true
                isFavourite = true
                favouriteButtonView()
            }
        }
    }

    private fun setupListeners() {

        binding.refresh.setOnRefreshListener {
            viewModel.apply {
                getCompanyProfile(shortName)
                getCompanyQuote(shortName)
                binding.favoriteBtn.isClickable = false
            }
        }

        binding.favoriteBtn.setOnClickListener {
            isFavourite = !isFavourite
            favouriteButtonView()
            manageStockSaving()
        }

        binding.moreBtn.setOnClickListener {
            it.isClickable = false
            if (viewModel.companyDividends.isNotEmpty()) {
                viewModel.getDividendsList()
                if (!isMoreButtonClicked) {
                    binding.moreBtn.setImageResource(R.drawable.avd_anim_more)
                    val less = binding.moreBtn.drawable
                    if (less is AnimatedVectorDrawable) {
                        less.start()
                    }
                    isMoreButtonClicked = true
                } else {
                    binding.moreBtn.setImageResource(R.drawable.avd_anim_less)
                    val more = binding.moreBtn.drawable
                    if (more is AnimatedVectorDrawable) {
                        more.start()
                    }
                    binding.scrollView.smoothScrollTo(binding.keyDividendDivider.x.toInt(),
                        binding.keyDividendDivider.y.toInt(), 800)
                    isMoreButtonClicked = false
                }
            }
            it.isClickable = true
        }

        binding.backBtn.setOnClickListener {
            NavigationUI.navigateUp(findNavController(), null)
        }
    }

    private fun topBarBind() {
        if (compName.length > 22) binding.compName.text = compName.substring(0, 19).trim() + "..."
        else binding.compName.text = compName
    }

    private fun favouriteButtonView() {
        if (!isFavourite) {
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_star_outlined, context?.theme)
            binding.favoriteBtn.setImageDrawable(drawable)
        } else {
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_star, context?.theme)
            binding.favoriteBtn.setImageDrawable(drawable)
        }
    }

    private fun manageStockSaving() {
        when (isExist) {
            true -> {
                if (isFavourite) viewModel.updateStock(shortName, compName)
                else {
                    viewModel.deleteStock(shortName)
                    isExist = false
                }
            }
            false -> {
                if (isFavourite) {
                    viewModel.saveStock(shortName, compName)
                    isExist = true
                }
            }
        }
    }

    private fun statBind(quote: List<CompanyQuote>) {
        binding.apply {
            stockPrice.text = resources.getString(R.string.price, viewModel.cutValueZeros(quote[0].price ?: 0.0))
            priceChange.apply {
                val change = quote[0].change ?: 0.0
                text = if(change < 0) viewModel.cutValueZeros(change) else "+${viewModel.cutValueZeros(change)}"
                if (change >= 0) setTextColor(resources.getColor(R.color.green, context.theme))
                else setTextColor(resources.getColor(R.color.red, context.theme))
            }
            pricePercent.apply {
                val changesPercentage = quote[0].changesPercentage ?: 0.0
                text = resources.getString(R.string.price_percent, viewModel.cutValueZeros(changesPercentage))
                if (changesPercentage >= 0) setTextColor(resources.getColor(R.color.green, context.theme))
                else setTextColor(resources.getColor(R.color.red, context.theme))
            }
            highPrice.text = viewModel.cutValueZeros(quote[0].dayHigh ?: 0.0)
            lowPrice.text = viewModel.cutValueZeros(quote[0].dayLow ?: 0.0)
            volValue.text = viewModel.formatValue(quote[0].volume ?: 0.0)
            marketCapValue.text = viewModel.formatValue(quote[0].marketCap ?: 0.0)
            avgVolumeValue.text = viewModel.formatValue(quote[0].avgVolume ?: 0.0)
            peValue.text = viewModel.cutValueZeros(quote[0].pe ?: 0.0)
        }
    }

    private fun rangeBetaBind(profile: List<CompanyProfile>) {
        val range = viewModel.formatRange(profile[0].range ?: "")
        binding.apply {
            high52w.text = viewModel.cutValueZeros(range[1].toDouble() ?: 0.0)
            low52w.text = viewModel.cutValueZeros(range[0].toDouble() ?: 0.0)
            betaValue.text = viewModel.cutValueZeros(profile[0].beta ?: 0.0)
        }
    }

    private fun aboutBind(value: List<CompanyProfile>) {
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

    private fun loadCompanyLogo(profile: List<CompanyProfile>) {
        loadLogo(
            requireContext(),
            profile[0].image ?: "",
            profile[0].symbol ?: "",
            binding.compLogo
        )
    }
    
    private fun deleteDividendsView() {
        binding.keyDividendDivider.visibility = View.GONE
        binding.dividendsView.visibility = View.GONE
        binding.moreBtn.visibility = View.GONE
        val constraint = binding.keyAboutDivider.layoutParams as ConstraintLayout.LayoutParams
        constraint.topToBottom = binding.volumeStat.id
    }
}
