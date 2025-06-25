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

class StoreBenefitAdapter(
    private var activity: MainActivity,
    private var benefits: List<Benefit>?
) :
    RecyclerView.Adapter<StoreBenefitAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = -1

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newBenefits: List<Benefit>?) {
        benefits = newBenefits
        notifyDataSetChanged()
    }

    fun updatePosition(selected: Int) {
        selectedPosition = selected
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

            if(selectedPosition == position) {
                root.setBackgroundResource(R.drawable.background_white3_radius8_stroke_green10)
            } else {
                root.setBackgroundResource(R.drawable.background_white3_radius8)
            }
        }
    }

    override fun getItemCount() = benefits?.size ?: 0


    inner class ViewHolder(val binding: RowStoreBenefitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDetail.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = adapterPosition

                // 클릭 리스너 호출
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)

                // 이전 선택 항목과 현재 선택 항목 갱신
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)

                true
            }
        }
    }
}