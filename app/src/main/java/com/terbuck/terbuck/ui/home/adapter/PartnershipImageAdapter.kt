package com.terbuck.terbuck.ui.home.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.terbuck.terbuck.api.response.home.PartnershipInfo
import com.terbuck.terbuck.databinding.RowHomePartnershipBinding
import com.terbuck.terbuck.databinding.RowImageBinding

class PartnershipImageAdapter(
    private var activity: Activity,
    private var images: List<String>?
) :
    RecyclerView.Adapter<PartnershipImageAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newImages: List<String>?) {
        images = newImages
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(memberId: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val imageUrl = images?.get(position)
            Glide.with(activity)
                .load(imageUrl)
                .into(imageViewPartnership)
        }
    }

    override fun getItemCount() = images?.size ?: 0


    inner class ViewHolder(val binding: RowImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }
}