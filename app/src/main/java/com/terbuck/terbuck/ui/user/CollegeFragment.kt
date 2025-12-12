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
import com.terbuck.terbuck.api.response.user.UniversityByRegionResponse
import com.terbuck.terbuck.databinding.FragmentCollegeBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.user.adapter.UniversityAdapter
import com.terbuck.terbuck.viewModel.OnboardingViewModel

class CollegeFragment : Fragment() {

    lateinit var binding: FragmentCollegeBinding
    lateinit var mainActivity: MainActivity

    private val viewModel: OnboardingViewModel by lazy {
        ViewModelProvider(requireActivity())[OnboardingViewModel::class.java]
    }

    lateinit var universityAdapter: UniversityAdapter

    var selectedCollege = ""

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
                adapter = universityAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonNext.setOnClickListener {

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
    }

    fun observeViewModel() {
        viewModel.run {
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                buttonNext.text = if(arguments?.getBoolean("isEdit") == true) "저장하기" else "터벅 들어가기"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}