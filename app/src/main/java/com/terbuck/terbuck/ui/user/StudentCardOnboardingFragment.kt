package com.terbuck.terbuck.ui.user

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentStudentCardOnboardingBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel

class StudentCardOnboardingFragment : DialogFragment() {

    lateinit var binding: FragmentStudentCardOnboardingBinding
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStudentCardOnboardingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            val spannable = SpannableString(textViewOnboarding.text)

            val start = textViewOnboarding.text.indexOf("학생증을 등록")
            val end = start + "학생증 등록".length + 1

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green_50)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.textViewOnboarding.text = spannable


            buttonRegister.setOnClickListener {
                mixpanel.track("click_onboarding_register", null)

                // 학생증 등록
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, StudentCardRegisterFragment())
                    .addToBackStack(null)
                    .commit()

                dismiss()
            }

            buttonNext.setOnClickListener {
                mixpanel.track("click_onboarding_later", null)
                
                dismiss()
            }
        }

        return binding.root
    }
}