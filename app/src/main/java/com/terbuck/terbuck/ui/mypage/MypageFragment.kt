package com.terbuck.terbuck.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.databinding.FragmentMypageBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.terbuck.ImageDetailFragment
import com.terbuck.terbuck.ui.user.UniversityFragment
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.viewModel.UserViewModel

class MypageFragment : Fragment() {

    lateinit var binding: FragmentMypageBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonEdit.setOnClickListener {
                // 학교 변경
                val bundle = Bundle().apply {
                    putBoolean("isEdit", true)
                }

                // 전달할 Fragment 생성
                var nextFragment = UniversityFragment().apply {
                    arguments = bundle
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }

            layoutQna.setOnClickListener {
                // 카카오톡 채널
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pf.kakao.com/_BzmZn"))
                startActivity(intent)
            }

            layoutService.setOnClickListener {
                // 서비스 이용 약관
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://terbuck.notion.site/11905c1258e080ee91cecfb7ff633bab"))
                startActivity(intent)
            }

            layoutPrivacy.setOnClickListener {
                // 개인정보 수집 및 이용동의
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://terbuck.notion.site/11905c1258e08063bba2f82d320de454"))
                startActivity(intent)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        mainActivity.hideBottomNavigation(false)

        binding.run {
            textViewUniversity.text = TokenManager(mainActivity).getUniversity()
            if(MyApplication.isRegisterStudentCard) {
                // 학생증 등록 O
                layoutStudentInfo.setBackgroundResource(R.drawable.background_green50_radius16)
                textViewName.run {
                    visibility = View.VISIBLE
                    text = viewModel.name.value
                }
                textViewStudentId.run {
                    visibility = View.VISIBLE
                    text = viewModel.studentNumber.value
                }
                buttonEdit.setImageResource(R.drawable.ic_edit_green10)
            } else {
                // 학생증 등록 X
                layoutStudentInfo.setBackgroundResource(R.drawable.background_black10_radius16)
                textViewName.visibility = View.GONE
                textViewStudentId.visibility = View.GONE
                buttonEdit.setImageResource(R.drawable.ic_edit_black5)
            }

            toolbar.textViewHead.text = "마이페이지"
        }
    }

}