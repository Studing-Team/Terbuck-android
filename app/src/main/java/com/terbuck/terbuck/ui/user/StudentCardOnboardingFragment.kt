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
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentStudentCardOnboardingBinding
import com.terbuck.terbuck.ui.MainActivity

class StudentCardOnboardingFragment : Fragment() {

    lateinit var binding: FragmentStudentCardOnboardingBinding
    lateinit var mainActivity: MainActivity

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
                // 학생증 등록
            }

            buttonNext.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation(true)
    }

}