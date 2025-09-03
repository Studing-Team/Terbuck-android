package com.terbuck.terbuck.ui.terbuck.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.MapStoreInfo
import com.terbuck.terbuck.databinding.RowStoreBenefitBinding
import com.terbuck.terbuck.databinding.RowStoreMapBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.utils.MainUtil
import com.terbuck.terbuck.utils.MainUtil.getCategoryIndex
import com.terbuck.terbuck.utils.MainUtil.getDrawableResIds

class StoreAdapter(
    private var activity: MainActivity,
    private var stores: List<MapStoreInfo>?,
    private var categoryImage: List<Int>
) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newStores: List<MapStoreInfo>?) {
        stores = newStores
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowStoreMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            if(position == ((stores?.size ?: 0) - 1) ) {
                space.visibility = View.VISIBLE
            } else {
                space.visibility = View.GONE
            }
            layoutStore.setBackgroundResource(0)
            textViewStoreName.text = stores?.get(position)?.name
            textViewStoreName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                categoryImage[MainUtil.getCategoryIndex(stores?.get(position)?.category) + 1],
                0)
            textViewStoreAddress.text = stores?.get(position)?.address
            textViewBenefitNum.text = "혜택 ${stores?.get(position)?.benefitCount}가지"

            var storeImage = if(stores?.get(position)?.thumbnailImage.isNullOrEmpty()) R.drawable.img_store_basic else stores?.get(position)?.thumbnailImage
            Glide.with(activity).load(storeImage).into(imageViewStore)
        }
    }

    override fun getItemCount() = stores?.size ?: 0


    inner class ViewHolder(val binding: RowStoreMapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)

                true
            }
        }
    }
}