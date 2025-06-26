package com.terbuck.terbuck.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentSignUpAgreementBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.user.UniversityFragment

class SignUpAgreementFragment : Fragment() {

    lateinit var binding: FragmentSignUpAgreementBinding
    lateinit var mainActivity: MainActivity

    private var checkList = mutableListOf(false, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpAgreementBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutAgreementAll.setOnClickListener {
                toggleAllAgreements()
            }
            imageViewCheckbox1.setOnClickListener { toggleAgreement(0) }
            textViewAgreementAgreement1.setOnClickListener { toggleAgreement(0) }
            imageViewCheckbox2.setOnClickListener { toggleAgreement(1) }
            textViewAgreementAgreement2.setOnClickListener { toggleAgreement(1) }

            imageViewNext1.setOnClickListener {
                // 서비스 이용 약관
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://terbuck.notion.site/11905c1258e080ee91cecfb7ff633bab"))
                startActivity(intent)
            }

            imageViewNext2.setOnClickListener {
                // 개인정보 수집 및 이용동의
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://terbuck.notion.site/11905c1258e08063bba2f82d320de454"))
                startActivity(intent)
            }

            buttonNext.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, UniversityFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun toggleAgreement(position: Int) {
        checkList[position] = !checkList[position]
        updateCheckboxUI(position, checkList[position])
        updateAllCheckboxAndButton()
    }

    private fun toggleAllAgreements() {
        val newState = !checkList.all { it }
        for (i in checkList.indices) {
            checkList[i] = newState
            updateCheckboxUI(i, newState)
        }
        updateAllCheckboxAndButton()
    }

    private fun updateCheckboxUI(position: Int, isChecked: Boolean) {
        when (position) {
            0 -> binding.imageViewCheckbox1.setImageResource(
                if (isChecked) R.drawable.ic_checkbox_green50 else R.drawable.ic_checkbox_stroke
            )
            1 -> binding.imageViewCheckbox2.setImageResource(
                if (isChecked) R.drawable.ic_checkbox_green50 else R.drawable.ic_checkbox_stroke
            )
        }
    }

    private fun updateAllCheckboxAndButton() {
        val allChecked = checkList.all { it }
        binding.imageViewCheckboxAll.setImageResource(
            if (allChecked) R.drawable.ic_checkbox_green50 else R.drawable.ic_checkbox_white
        )
        binding.buttonNext.isEnabled = allChecked
    }

    private fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.toolbar.run {
            textViewHead.text = "회원가입"
            buttonBack.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }
    }
}
