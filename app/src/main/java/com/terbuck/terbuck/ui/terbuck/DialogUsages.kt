package com.terbuck.terbuck.ui.terbuck

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
import com.terbuck.terbuck.api.response.terbuck.Usage
import com.terbuck.terbuck.databinding.DialogPartnershipBinding
import com.terbuck.terbuck.databinding.DialogUsagesBinding
import com.terbuck.terbuck.ui.home.adapter.HomeStoreBenefitAdapter

class DialogUsages(var usages: List<Usage>?) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogUsagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUsagesBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.run {
            textViewDescription.text = usages?.joinToString("\n") { it.introduction }


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