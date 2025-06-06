package com.terbuck.terbuck.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.home.StoreInfo
import com.terbuck.terbuck.databinding.DialogPartnershipBinding
import com.terbuck.terbuck.ui.home.adapter.HomeStoreBenefitAdapter

class DialogPartnership(var storeInfo: StoreInfo?) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogPartnershipBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPartnershipBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.run {
            textViewStoreName.text = storeInfo?.name ?: ""
            textViewStoreAddress.text = storeInfo?.address ?: ""
            imageViewCategory.setImageResource(
                when(storeInfo?.category) {
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

            recyclerViewPartnership.run {
                adapter = HomeStoreBenefitAdapter(storeInfo)
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonClose.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            // 포커스 잃었을 때 깜빡이거나 깨지는 현상 방지
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}