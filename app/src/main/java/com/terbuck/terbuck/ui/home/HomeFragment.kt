package com.terbuck.terbuck.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentHomeBinding
import com.terbuck.terbuck.ui.MainActivity

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val category: List<String> = listOf("먹고가기", "이용하기", "파트너십")

        val adapter = TemplateCategoryVPAdapter(this)
        binding.viewpager.adapter = adapter

        TabLayoutMediator(binding.tab, binding.viewpager) { tab, position ->
            tab.text = category[position]  // 포지션에 따른 텍스트
        }.attach()  // 탭 레이아웃과 뷰페이저를 붙여주는 기능

        binding.run {
            val customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_partnership, null)
            binding.tab.getTabAt(2)?.customView = customView

            binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position == 2) {
                        val text = tab.customView?.findViewById<TextView>(R.id.tabText)
                        text?.setTextColor(resources.getColor(R.color.black_30))

                        val icon = tab.customView?.findViewById<ImageView>(R.id.tabIcon)
                        icon?.visibility = View.VISIBLE
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    if (tab?.position == 2) {
                        val text = tab.customView?.findViewById<TextView>(R.id.tabText)
                        text?.setTextColor(resources.getColor(R.color.black_10))

                        val icon = tab.customView?.findViewById<ImageView>(R.id.tabIcon)
                        icon?.visibility = View.GONE
                    }
                }


                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        tabItemMargin(binding.tab)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun tabItemMargin(mTabLayout: TabLayout) {
        for (i in 0 until mTabLayout.tabCount) {
            val tab = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(8, 8, 8, 8)
            tab.requestLayout()
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(false)

        binding.run {
            toolbar.imageViewCard.setOnClickListener {

            }
        }
    }
}

class TemplateCategoryVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                // 먹고가기 Fragment
                HomeEatFragment()
            }
            1 -> {
                // 이용하기 Fragment
                HomeUseFragment()
            }
            2 -> {
                // 파트너십 Fragment
                HomePartnershipFragment()
            }
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}