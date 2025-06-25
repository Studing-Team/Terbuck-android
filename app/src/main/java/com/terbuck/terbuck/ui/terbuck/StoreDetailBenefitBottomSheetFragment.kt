package com.terbuck.terbuck.ui.terbuck

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentStoreDetailBenefitBottomSheetBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.terbuck.adapter.StoreBenefitAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreDetailBenefitAdapter

class StoreDetailBenefitBottomSheetFragment(var activity: Activity, var benefits: List<String>?) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentStoreDetailBenefitBottomSheetBinding
    lateinit var mainActivity: MainActivity

    lateinit var storeBenefitAdapter: StoreDetailBenefitAdapter

    interface OnBottomSheetDismissListener {
        fun onBottomSheetDismissed()
    }

    var dismissListener: OnBottomSheetDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailBenefitBottomSheetBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            recyclerViewStoreDetailBenefit.apply {
                adapter = storeBenefitAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onBottomSheetDismissed()
    }

    fun initAdapter() {
        storeBenefitAdapter = StoreDetailBenefitAdapter(
            mainActivity,
            benefits
        )
    }
}