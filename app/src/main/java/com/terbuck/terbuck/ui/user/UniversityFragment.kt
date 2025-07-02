package com.terbuck.terbuck.ui.user

import android.os.Bundle
import android.util.Log
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
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.HomeFragment
import com.terbuck.terbuck.ui.user.adapter.UniversityAdapter
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.viewModel.OnboardingViewModel
import com.terbuck.terbuck.viewModel.UserViewModel
import kotlin.collections.set

class UniversityFragment : Fragment() {

    lateinit var binding: FragmentUniversityBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: OnboardingViewModel by lazy {
        ViewModelProvider(requireActivity())[OnboardingViewModel::class.java]
    }
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    lateinit var universityAdapter: UniversityAdapter

    var getUniversityList = mutableListOf<String>()

    var selectedSchool = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUniversityBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        Log.d("##", "${arguments?.getBoolean("isEdit")}")

        binding.run {
            recyclerViewSchool.apply {
                adapter = universityAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonNext.setOnClickListener {
                if(arguments?.getBoolean("isEdit") == true) {
                    // 학교 변경 API 호출
                    viewModel.editUniversity(mainActivity, selectedSchool) {
                        TokenManager(mainActivity).saveUniversity(selectedSchool)
                        mixpanel.people.set("school", "$selectedSchool")

                        MyApplication.isUniversityChanged = true
                        MyApplication.isRegisterStudentCard = false

                        fragmentManager?.popBackStack()
                    }
                } else {
                    // 회원가입 API 호출
                    viewModel.signUp(mainActivity, selectedSchool) {
                        TokenManager(mainActivity).saveUniversity(selectedSchool)
                        mixpanel.people.set("school", "$selectedSchool")
                        mixpanel.people.set("platform", "Android")

                        // 홈화면 이동
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, HomeFragment())
                            .addToBackStack(null)
                            .commit()
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
        universityAdapter = UniversityAdapter(
            mainActivity,
            getUniversityList
        ).apply {
            itemClickListener = object : UniversityAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 학교 선택
                    binding.buttonNext.isEnabled = true

                    selectedSchool = getUniversityList[position]
                    universityAdapter.updateList(getUniversityList, position)
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            universities.observe(viewLifecycleOwner) {
                getUniversityList = it as MutableList<String>

                universityAdapter.updateList(getUniversityList, -1)
            }
        }
    }

    private fun initView() {
        mainActivity.hideBottomNavigation(true)

        viewModel.getUniversities(mainActivity)

        binding.run {
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