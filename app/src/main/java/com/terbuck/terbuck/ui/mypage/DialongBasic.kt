package com.terbuck.terbuck.ui.mypage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.terbuck.terbuck.databinding.DialogBasicBinding

interface BasicButtonDialogInterface {
    fun onClickYesButton()
}

class DialogBasic(var title: String, var description: String?, var cancelText: String, var checkText: String) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogBasicBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: BasicButtonDialogInterface? = null

    // 인터페이스 인스턴스
    private var listener: BasicButtonDialogInterface? = null

    // 리스너 설정 메서드
    fun setBasicDialogInterface(listener: BasicButtonDialogInterface) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBasicBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.run {
            textViewTitle.text = title
            if(description != null) {
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = description
            } else {
                textViewDescription.visibility = View.GONE
            }

            buttonClose.run {
                text = cancelText
                setOnClickListener {
                    dismiss()
                }
            }

            buttonConfirm.run {
                text = checkText
                setOnClickListener {
                    listener?.onClickYesButton() // 인터페이스를 통해 이벤트 전달
                    dismiss()
                }
            }
        }

        return binding.root
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