package com.terbuck.terbuck.ui.user.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.RowSchoolBinding

class SchoolAdapter(
    private var activity: Activity,
    private var schools: List<String>?
) :
    RecyclerView.Adapter<SchoolAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    var selectedPosition: Int = -1

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newSchools: List<String>?, newlySelectedPosition: Int?) {
        schools = newSchools
        selectedPosition = newlySelectedPosition ?: -1
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(memberId: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowSchoolBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewSchoolName.text = schools?.get(position) ?: ""

            imageViewCheckbox.setImageResource(if(position == selectedPosition) R.drawable.ic_checkbox_green50  else R.drawable.ic_checkbox_white)
        }
    }

    override fun getItemCount() = schools?.size ?: 0


    inner class ViewHolder(val binding: RowSchoolBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }
}