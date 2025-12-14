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
import com.terbuck.terbuck.api.response.user.University
import com.terbuck.terbuck.api.response.user.UniversityByRegionResponse
import com.terbuck.terbuck.databinding.FragmentUniversityBinding
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.HomeFragment
import com.terbuck.terbuck.ui.user.adapter.UniversityAdapter
import com.terbuck.terbuck.ui.user.adapter.UniversityRegionAdapter
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

    lateinit var universityAdapter: UniversityAdapter
    lateinit var universityRegionAdapter: UniversityRegionAdapter
    var getUniversityByRegionList = mutableListOf<UniversityByRegionResponse>()


    var selectedSchool = ""
    var selectedUniversityList = mutableListOf<University>()

    val regionMap = mapOf(
        0 to 5, // 강남, 서초, 송파, 강동 → regionId: 5
        1 to 1, // 관악, 동작, 영등포 → regionId: 1
        2 to 7, // 광진, 성동, 중랑, 동대문 → regionId: 7
        3 to 3, // 노원, 도봉, 강북, 성북 → regionId: 3
        4 to 2, // 마포, 서대문, 은평 → regionId: 2
        5 to 6, // 종로, 중구, 용산 → regionId: 6
        6 to 8,  // 강서, 금천, 양천, 구로 → regionId: 8
        7 to 4
    )

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

            recyclerViewRegion.apply {
                adapter = universityRegionAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            layoutRegion.setOnClickListener {
                recyclerViewRegion.visibility = View.VISIBLE
                recyclerViewSchool.visibility = View.GONE
                textViewUniversityEmpty.visibility = View.GONE
                binding.buttonNext.isEnabled = false

                imageViewRegionArrow.animate().apply {
                    duration = 100
                    rotation(270f)
                }
            }

            buttonNext.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("university", selectedSchool)
                    putBoolean("isEdit", arguments?.getBoolean("isEdit") == true)
                }

                // 전달할 Fragment 생성
                var nextFragment = CollegeFragment().apply {
                    arguments = bundle
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, nextFragment)
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

    fun initAdapter() {
        universityAdapter = UniversityAdapter(
            mainActivity,
            selectedUniversityList
        ).apply {
            itemClickListener = object : UniversityAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 학교 선택
                    binding.buttonNext.isEnabled = true

                    selectedSchool = selectedUniversityList[position].name
                    universityAdapter.updateList(selectedUniversityList, position)
                }
            }
        }

        universityRegionAdapter = UniversityRegionAdapter(
            mainActivity,
            resources.getTextArray(R.array.university_region).map { it.toString() }
        ).apply {
            itemClickListener = object : UniversityRegionAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 지역 선택
                    binding.run {
                        textViewRegion.text = resources.getTextArray(R.array.university_region)[position]

                        val regionId = regionMap[position]
                        selectedUniversityList = getUniversityByRegionList
                            .find { it.region.id.toInt() == regionId }
                            ?.universities
                            ?.toMutableList()
                            ?: mutableListOf()

                        if(selectedUniversityList.isEmpty()) {
                            recyclerViewRegion.visibility = View.GONE
                            textViewUniversityEmpty.visibility = View.VISIBLE
                        } else {
                            recyclerViewSchool.visibility = View.VISIBLE
                            recyclerViewRegion.visibility = View.GONE
                            textViewUniversityEmpty.visibility = View.GONE
                            universityAdapter.updateList(selectedUniversityList, null)
                        }

                        imageViewRegionArrow.animate().apply {
                            duration = 100
                            rotation(90f)
                        }
                    }
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            universitiesByRegion.observe(viewLifecycleOwner) {
                getUniversityByRegionList = it as MutableList<UniversityByRegionResponse>
            }
        }
    }

    private fun initView() {
        mainActivity.hideBottomNavigation(true)

//        viewModel.getUniversities(mainActivity)
        viewModel.getUniversityByRegion(mainActivity)

        binding.run {
            recyclerViewRegion.visibility = View.VISIBLE
            textViewRegion.text = resources.getTextArray(R.array.university_region)[0]

            toolbar.run {
                textViewHead.text = if(arguments?.getBoolean("isEdit") == true) "학교 변경" else "회원가입"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}