package com.terbuck.terbuck.ui.terbuck.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.MapStoreInfo
import com.terbuck.terbuck.databinding.RowStoreBenefitBinding
import com.terbuck.terbuck.databinding.RowStoreMapBinding
import com.terbuck.terbuck.databinding.RowStoreSearchBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.utils.MainUtil

class StoreSearchAdapter(
    private var activity: MainActivity,
    private var stores: List<MapStoreInfo>?,
    private var keyword: String = ""
) :
    RecyclerView.Adapter<StoreSearchAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newStores: List<MapStoreInfo>?, newKeyword: String = "") {
        stores = newStores
        keyword = newKeyword
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowStoreSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewStoreName.text = stores?.get(position)?.name
            // 키워드 색상 변경
            val name = stores?.get(position)?.name ?: ""
            if (keyword.isNotEmpty() && name.contains(keyword, ignoreCase = true)) {
                val startIndex = name.indexOf(keyword, ignoreCase = true)
                val endIndex = startIndex + keyword.length

                val spannable = SpannableString(name)
                val color = ContextCompat.getColor(activity, R.color.green_50)
                spannable.setSpan(
                    ForegroundColorSpan(color),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                textViewStoreName.text = spannable
            } else {
                textViewStoreName.text = name
            }

            imageViewCategory.setImageResource(
                when(stores?.get(position)?.category) {
                    "음식" -> { R.drawable.ic_food_selected }
                    "카페" -> { R.drawable.ic_cafe_selected }
                    "문화" -> { R.drawable.ic_culture_selected }
                    "주점" -> { R.drawable.ic_alcohol_selected }
                    "운동" -> { R.drawable.ic_exercise_selected }
                    "스터디" -> { R.drawable.ic_study_selected }
                    "병원" -> { R.drawable.ic_hospital_selected }
                    else -> { 0 }
                }
            )

            textViewStoreAddress.text = stores?.get(position)?.address
            textViewBenefitNum.text = "혜택 ${stores?.get(position)?.benefitCount}가지"
        }

    }

    override fun getItemCount() = stores?.size ?: 0


    inner class ViewHolder(val binding: RowStoreSearchBinding) :
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