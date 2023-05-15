package com.example.stocks.ui.fragments

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.example.stocks.App
import com.example.stocks.R
import com.example.stocks.adapters.AlertListAdapter
import com.example.stocks.adapters.OnItemClickListener
import com.example.stocks.database.alertdatabase.Alert
import com.example.stocks.databinding.AlertListBottomSheetBinding
import com.example.stocks.viewmodels.AlertListViewModel
import com.example.stocks.viewmodels.AlertListViewModelFactory
import com.example.stocks.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlertListBottomSheetFragment : BottomSheetDialogFragment(), OnItemClickListener {

    private var _binding: AlertListBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel: AlertListViewModel by viewModels {
        AlertListViewModelFactory((requireContext().applicationContext as App).alertDataBase.alertDao(),
            requireContext().applicationContext as App)
    }

    private lateinit var app: App

    private lateinit var workManager: WorkManager

    private lateinit var compShortName: String
    private lateinit var compName: String

    private lateinit var adapter: AlertListAdapter

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    private var deleteBtnState = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        compShortName = sharedViewModel.companyShortName.value.toString()
        compName = sharedViewModel.companyName.value.toString()
        _binding = AlertListBottomSheetBinding.inflate(inflater, container, false)
        viewModel.getAlerts(compShortName)
        workManager = WorkManager.getInstance(requireContext())
        app = requireContext().applicationContext as App
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.alert_list_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val density = requireActivity().resources.displayMetrics.heightPixels

        val behavior = param.behavior
        if (behavior is BottomSheetBehavior<*>) {
            bottomSheetBehavior = behavior
            bottomSheetBehavior?.peekHeight = density
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            bottomSheetBehavior?.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dismiss()
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AlertListAdapter(this)
        binding.list.adapter = adapter
        val layoutManager = LinearLayoutManager(context)
        binding.list.layoutManager = layoutManager

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.addButton.setOnClickListener {
            val action = AlertListBottomSheetFragmentDirections.actionAlertListDialogToAddAlert(
                shortName = compShortName,
            )
            findNavController().navigate(action)
        }

        binding.closeBtn.setOnClickListener {
            if (deleteBtnState) {
                viewModel.deleteAlert()
            } else {
                dismiss()
            }
        }
    }

    private fun setupObservers() {

        viewModel.listEmpty.observe(this.viewLifecycleOwner) {
            val imageBtnDrawable: Drawable
            if (!it) {
                if (!deleteBtnState) {
                    imageBtnDrawable = ResourcesCompat.getDrawable(
                        context?.resources!!, R.drawable.ic_delete, context?.theme)!!
                    changeImageButtonDrawable(imageBtnDrawable)
                    deleteBtnState = true
                }
            }
            else {
                if (deleteBtnState) {
                    imageBtnDrawable = ResourcesCompat.getDrawable(
                        context?.resources!!, R.drawable.ic_close, context?.theme)!!
                    changeImageButtonDrawable(imageBtnDrawable)
                    deleteBtnState = false
                }
            }
        }

        viewModel.deleteStatus.observe(this.viewLifecycleOwner) {
            viewModel.getAlerts(compShortName)
        }

        viewModel.alertList.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun changeImageButtonDrawable(image: Drawable) {
        binding.closeBtn.alpha = 1f
        binding.closeBtn.animate().alphaBy(0f).setDuration(200).start()
        binding.closeBtn.setImageDrawable(image)
        binding.closeBtn.alpha = 0f
        binding.closeBtn.animate().alphaBy(1f).setDuration(200).start()
        binding.closeBtn.alpha = 1f
    }

    override fun onStop() {
        super.onStop()
        Log.i("alert", "Active: ${viewModel.activeAlerts.size}")
        Log.i("alert", "Deactivated: ${viewModel.deactivatedAlerts.size}")
        viewModel.activateAlert()
        viewModel.deactivateAlert()
    }

    override fun onItemClick(alert: Alert) {
        val action = AlertListBottomSheetFragmentDirections.actionAlertListDialogToAddAlert(
            compShortName,
            alertId = alert.id
        )
        findNavController().navigate(action)
    }

    override fun onSwitchStateChange(alert: Alert, state: Boolean) {
        viewModel.updateAlert(alert, state)
    }

    override fun onLongClick(alert: Alert, deleteFlag: Boolean) {
        viewModel.updateAlertList(alert, deleteFlag)
    }
}