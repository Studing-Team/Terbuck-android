package com.terbuck.terbuck.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.api.response.home.StoreInfo
import com.terbuck.terbuck.databinding.RowHomeStoreBenefitBinding
import com.terbuck.terbuck.databinding.RowHomeStoreBenefitDetailBinding

class HomeStoreBenefitDetailAdapter(
    private var partnershipBenefitDetails: List<String>?
) :
    RecyclerView.Adapter<HomeStoreBenefitDetailAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newPartnershipBenefitDetails: List<String>?) {
        partnershipBenefitDetails = newPartnershipBenefitDetails
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(memberId: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowHomeStoreBenefitDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewBenefit.text = partnershipBenefitDetails?.get(position)

            if(position == ((partnershipBenefitDetails?.size ?: 0) - 1)) {
                space.visibility = View.GONE
            } else {
                space.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = partnershipBenefitDetails?.size ?: 0


    inner class ViewHolder(val binding: RowHomeStoreBenefitDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}