package com.terbuck.terbuck.ui.home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.home.StoreInfo
import com.terbuck.terbuck.databinding.RowHomeStoreBinding
import com.terbuck.terbuck.databinding.RowSchoolBinding

class HomeStoreAdapter(
    private var activity: Activity,
    private var partnershipInfos: List<StoreInfo>?
) :
    RecyclerView.Adapter<HomeStoreAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newPartnershipInfos: List<StoreInfo>?) {
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
            RowHomeStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewStoreName.text = partnershipInfos?.get(position)?.name
            textViewStoreAddress.text = partnershipInfos?.get(position)?.address
            textViewStoreBenefit.text = partnershipInfos?.get(position)?.benefitList?.get(0)?.title

            imageViewCategory.setImageResource(
                when(partnershipInfos?.get(position)?.category) {
                    "음식" -> { R.drawable.ic_food_selected }
                    "카페" -> { R.drawable.ic_cafe_selected }
                    "문화" -> { R.drawable.ic_culture_selected }
                    "주점" -> { R.drawable.ic_alcohol_selected }
                    "운동" -> { R.drawable.ic_exercise_selected }
                    "스터디" -> { R.drawable.ic_study_selected }
                    "병원" -> { R.drawable.ic_hospital_selected }
                    else -> { R.drawable.ic_cafe_selected }
                }
            )
        }
    }

    override fun getItemCount() = partnershipInfos?.size ?: 0


    inner class ViewHolder(val binding: RowHomeStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonMore.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }
}