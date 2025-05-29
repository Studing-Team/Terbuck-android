package com.terbuck.terbuck.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentSchoolBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.user.adapter.SchoolAdapter
import kotlin.text.replace

class SchoolFragment : Fragment() {

    lateinit var binding: FragmentSchoolBinding
    lateinit var mainActivity: MainActivity
    lateinit var schoolAdapter: SchoolAdapter

    var schoolList = mutableListOf<String>("광운대학교", "서울과학기술대학교", "성신여자대학교", "삼육대학교")

    var selectedSchool = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSchoolBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            recyclerViewSchool.apply {
                adapter = schoolAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonNext.setOnClickListener {
                // 회원가입 API 호출
                
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        schoolAdapter = SchoolAdapter(
            mainActivity,
            schoolList
        ).apply {
            itemClickListener = object : SchoolAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 학교 선택
                    binding.buttonNext.isEnabled = true

                    selectedSchool = schoolList[position]
                    schoolAdapter.updateList(schoolList, position)
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