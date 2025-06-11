package com.terbuck.terbuck.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.api.response.home.StoreInfo
import com.terbuck.terbuck.databinding.RowHomeStoreBenefitBinding

class HomeStoreBenefitAdapter(
    private var partnershipInfos: StoreInfo?
) :
    RecyclerView.Adapter<HomeStoreBenefitAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newPartnershipInfos: StoreInfo?) {
        partnershipInfos = newPartnershipInfos
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(memberId: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowHomeStoreBenefitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewBenefit.text = partnershipInfos?.benefitList?.get(position)?.title
        }
    }

    override fun getItemCount() = partnershipInfos?.benefitList?.size ?: 0


    inner class ViewHolder(val binding: RowHomeStoreBenefitBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}