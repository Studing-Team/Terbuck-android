package com.terbuck.terbuck.ui.terbuck.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.MapStoreInfo
import com.terbuck.terbuck.databinding.RowRecentSearchBinding
import com.terbuck.terbuck.databinding.RowStoreBenefitBinding
import com.terbuck.terbuck.databinding.RowStoreMapBinding
import com.terbuck.terbuck.databinding.RowStoreSearchBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.utils.MainUtil
import com.terbuck.terbuck.utils.MyApplication

class StoreRecentSearchAdapter(
    private var activity: MainActivity,
    private var recentSearchKeywords: List<String>?
) :
    RecyclerView.Adapter<StoreRecentSearchAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newRecentSearchKeywords: List<String>?) {
        recentSearchKeywords = newRecentSearchKeywords
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewRecentSearch.text = recentSearchKeywords?.get(position)
        }
    }

    override fun getItemCount() = recentSearchKeywords?.size ?: 0


    inner class ViewHolder(val binding: RowRecentSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDelete.setOnClickListener {
                MyApplication.preferences.removeRecentSearch(activity,
                    MyApplication.preferences.getRecentSearchesLimited(activity)[position]
                )

                updateList(MyApplication.preferences.getRecentSearchesLimited(activity))

                true
            }

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)

                true
            }
        }
    }
}