package com.terbuck.terbuck.ui.terbuck.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.Benefit
import com.terbuck.terbuck.databinding.RowStoreBenefitBinding
import com.terbuck.terbuck.ui.MainActivity

class StoreDetailBenefitAdapter(
    private var activity: MainActivity,
    private var benefits: List<String>?
) :
    RecyclerView.Adapter<StoreDetailBenefitAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newBenefits: List<String>?) {
        benefits = newBenefits
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowStoreBenefitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewBenefit.text = benefits?.get(position)
            buttonDetail.visibility = View.GONE
        }
    }

    override fun getItemCount() = benefits?.size ?: 0


    inner class ViewHolder(val binding: RowStoreBenefitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDetail.setOnClickListener {
                // 클릭 리스너 호출
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)

                true
            }
        }
    }
}