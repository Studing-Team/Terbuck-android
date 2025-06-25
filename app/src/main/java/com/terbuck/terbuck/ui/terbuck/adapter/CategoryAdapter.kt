package com.terbuck.terbuck.ui.terbuck.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.RowCategoryBinding

class CategoryAdapter(
    private var activity: Activity,
    private var category: List<String>?,
    private var categoryWhiteImage: List<Int>,
    private var categoryUnselectedImage: List<Int>
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    var selectedPosition = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(memberId: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewCategory.text = category?.get(position)

            if(position == selectedPosition) {
                space.visibility = View.GONE
                root.setBackgroundResource(R.drawable.background_green50_radius100)

                if(position != 0) {
                    textViewCategory.visibility = View.GONE
                    imageViewCategory.visibility = View.VISIBLE
                    imageViewCategory.setImageResource(categoryWhiteImage[position])
                } else {
                    textViewCategory.run {
                        visibility = View.VISIBLE
                        setTextColor(resources.getColor(R.color.white))
                    }
                    imageViewCategory.visibility = View.GONE
                }
            } else {
                root.setBackgroundResource(R.drawable.background_white_radius100)
                textViewCategory.run {
                    visibility = View.VISIBLE
                    setTextColor(resources.getColor(R.color.black_30))
                }
                if(position != 0) {
                    space.visibility = View.VISIBLE
                    imageViewCategory.visibility = View.VISIBLE
                    imageViewCategory.setImageResource(categoryUnselectedImage[position])
                } else {
                    space.visibility = View.GONE
                    imageViewCategory.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount() = category?.size ?: 0


    inner class ViewHolder(val binding: RowCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = adapterPosition

                // 클릭 리스너 호출
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)

                // 이전 선택 항목과 현재 선택 항목 갱신
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }
}