package com.terbuck.terbuck.ui.home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.home.PartnershipInfo
import com.terbuck.terbuck.api.response.home.StoreInfo
import com.terbuck.terbuck.databinding.RowHomePartnershipBinding
import com.terbuck.terbuck.databinding.RowHomeStoreBinding
import com.terbuck.terbuck.databinding.RowSchoolBinding

class HomePartnershipAdapter(
    private var activity: Activity,
    private var partnershipInfos: List<PartnershipInfo>?
) :
    RecyclerView.Adapter<HomePartnershipAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newPartnershipInfos: List<PartnershipInfo>?) {
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
            RowHomePartnershipBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewName.text = partnershipInfos?.get(position)?.name
            textViewInstitution.text = partnershipInfos?.get(position)?.institution
            textViewCategory.text = partnershipInfos?.get(position)?.category

            if(position == (partnershipInfos?.size ?: 0) -1) {
                space.visibility = View.GONE
            } else {
                space.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = partnershipInfos?.size ?: 0


    inner class ViewHolder(val binding: RowHomePartnershipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }
}