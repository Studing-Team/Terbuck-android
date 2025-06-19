package com.terbuck.terbuck.ui.terbuck.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.api.response.terbuck.Benefit
import com.terbuck.terbuck.databinding.RowStoreBenefitBinding
import com.terbuck.terbuck.ui.MainActivity

class StoreBenefitAdapter(
    private var activity: MainActivity,
    private var benefits: List<Benefit>?
) :
    RecyclerView.Adapter<StoreBenefitAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newBenefits: List<Benefit>?) {
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
            textViewBenefit.text = benefits?.get(position)?.title
            buttonDetail.visibility = if(benefits?.get(position)?.detailList?.isEmpty() == true) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    override fun getItemCount() = benefits?.size ?: 0


    inner class ViewHolder(val binding: RowStoreBenefitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDetail.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)

                true
            }
        }
    }
}