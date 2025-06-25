package com.terbuck.terbuck.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.databinding.FragmentUniversityBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.HomeFragment
import com.terbuck.terbuck.ui.user.adapter.UniversityAdapter
import com.terbuck.terbuck.viewModel.OnboardingViewModel

class UniversityFragment : Fragment() {

    lateinit var binding: FragmentUniversityBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: OnboardingViewModel by lazy {
        ViewModelProvider(requireActivity())[OnboardingViewModel::class.java]
    }

    lateinit var universityAdapter: UniversityAdapter

    var schoolList = mutableListOf<String>("광운대학교", "서울과학기술대학교", "성신여자대학교", "삼육대학교")

    var selectedSchool = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUniversityBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            recyclerViewSchool.apply {
                adapter = universityAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonNext.setOnClickListener {
                // 회원가입 API 호출
                viewModel.signUp(mainActivity, selectedSchool) {
                    TokenManager(mainActivity).saveUniversity(selectedSchool)

                    // 홈화면 이동
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        universityAdapter = UniversityAdapter(
            mainActivity,
            schoolList
        ).apply {
            itemClickListener = object : UniversityAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 학교 선택
                    binding.buttonNext.isEnabled = true

                    selectedSchool = schoolList[position]
                    universityAdapter.updateList(schoolList, position)
                }
            }
        }
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