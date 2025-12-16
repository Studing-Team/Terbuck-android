package com.terbuck.terbuck.ui.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.api.response.user.CollegeResponse
import com.terbuck.terbuck.api.response.user.UniversityByRegionResponse
import com.terbuck.terbuck.databinding.FragmentCollegeBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.user.adapter.CollegeAdapter
import com.terbuck.terbuck.ui.user.adapter.UniversityAdapter
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.viewModel.HomeViewModel
import com.terbuck.terbuck.viewModel.OnboardingViewModel

class CollegeFragment : Fragment() {

    lateinit var binding: FragmentCollegeBinding
    lateinit var mainActivity: MainActivity

    private val viewModel: OnboardingViewModel by lazy {
        ViewModelProvider(requireActivity())[OnboardingViewModel::class.java]
    }
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    lateinit var collegeAdapter: CollegeAdapter

    var getCollegeList = mutableListOf<CollegeResponse>()

    var selectedCollege = 0L
    var university = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCollegeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewSchool.apply {
                adapter = collegeAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonNext.setOnClickListener {
                if(arguments?.getBoolean("isEdit") == true) {
                    // 학교 변경 API 호출
                    viewModel.editUniversity(mainActivity, university, selectedCollege) {
                        TokenManager(mainActivity).saveUniversity(university)
                        mixpanel.people.set("School", "$university")

                        MyApplication.isUniversityChanged = true
                        MyApplication.isRegisterStudentCard = false

                        homeViewModel.getUniversityIsRegistered(mainActivity, TokenManager(mainActivity).getUniversity().toString()) {
                            mainActivity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                    }
                } else {
                    // 회원가입 API 호출
                    viewModel.signUp(mainActivity, university, selectedCollege) {
                        TokenManager(mainActivity).saveUniversity(university)
                        MyApplication.preferences.saveIsSignUp(true)

                        mixpanel.people.set("School", "$university")
                        mixpanel.people.set("Platform", "Android")

                        mixpanel.track("click_signup2", null)

                        homeViewModel.getUniversityIsRegistered(mainActivity, TokenManager(mainActivity).getUniversity().toString()) {
                            mainActivity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            mainActivity.setBottomNavigationHome()
                        }
                    }
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
        collegeAdapter = CollegeAdapter(
            mainActivity,
            getCollegeList
        ).apply {
            itemClickListener = object : CollegeAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 단과대학 선택
                    binding.buttonNext.isEnabled = true

                    selectedCollege = getCollegeList[position].id
                    collegeAdapter.updateList(getCollegeList, position)
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            colleges.observe(viewLifecycleOwner) {
                getCollegeList = it as MutableList<CollegeResponse>

                collegeAdapter.updateList(getCollegeList, null)
            }
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        university = arguments?.getString("university").toString()
        viewModel.getColleges(mainActivity, university)

        binding.run {
            textViewTitle.text = university
            buttonNext.text = if(arguments?.getBoolean("isEdit") == true) "저장하기" else "터벅 들어가기"

            toolbar.run {
                textViewHead.text = if(arguments?.getBoolean("isEdit") == true) "학교 변경" else "회원가입"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}