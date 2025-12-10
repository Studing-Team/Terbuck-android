package com.terbuck.terbuck.ui.user.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.RowUniversityBinding

class UniversityRegionAdapter(
    private var activity: Activity,
    private var regions: List<String>?
) :
    RecyclerView.Adapter<UniversityRegionAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

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
            RowUniversityBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewSchoolName.text = regions?.get(position) ?: ""
        }
    }

    override fun getItemCount() = regions?.size ?: 0


    inner class ViewHolder(val binding: RowUniversityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }
}