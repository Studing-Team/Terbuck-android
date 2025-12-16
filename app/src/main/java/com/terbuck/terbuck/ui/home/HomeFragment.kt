package com.terbuck.terbuck.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentHomeBinding
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.user.StudentCardFragment
import com.terbuck.terbuck.ui.user.StudentCardOnboardingFragment
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.viewModel.UserViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.ui.mypage.MypageNotificationFragment
import com.terbuck.terbuck.ui.user.StudentCardRegisterFragment
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication.Companion.isRegisterStudentCard
import com.terbuck.terbuck.viewModel.HomeViewModel


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)

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
        mainActivity.run {
            hideBottomNavigation(false)
        }
        showToast()

        if(MyApplication.preferences.getIsFirst() == true) {
            MyApplication.preferences.setIsFirst(false)

            StudentCardOnboardingFragment().show(parentFragmentManager, "StudentCardOnboardingDialog")

            checkLocationPermission()
        }

        binding.run {
            if(isRegisterStudentCard) {
                // 학생증 등록 O
                toolbar.imageViewCard.setImageResource(R.drawable.ic_studentcard_green10)
            } else {
                // 학생증 등록 X
                toolbar.imageViewCard.setImageResource(R.drawable.ic_studentcard_black5)
            }

            toolbar.imageViewCard.setOnClickListener {
                mixpanel.track("click_student_card", null)

                if(isRegisterStudentCard) {
                    // 학생증 등록 O
                    StudentCardFragment().show(parentFragmentManager, "StudentCardDialog")
                } else {
                    // 학생증 등록 X
                    viewModel.getIsStudentCardPending(mainActivity,
                        onPending = {
                            MyApplication.isPendingStudentCard = true
                            BasicToast.showBasicTextToast(requireContext(), "학생증을 확인 중이에요. 잠시만 기다려주세요 :)", mainActivity.binding.bottomNavBar)
                        },
                        onNotPending = {
                            if (MyApplication.isPendingStudentCard) {
                                MyApplication.isPendingStudentCard = false
                                viewModel.getStudentCard(mainActivity) {
                                    mixpanel.people.set(
                                        "School",
                                        "${TokenManager(mainActivity).getUniversity()}"
                                    )
                                    mixpanel.people.set("Platform", "Android")

                                    toolbar.imageViewCard.setImageResource(R.drawable.ic_studentcard_green10)
                                    StudentCardFragment().show(
                                        parentFragmentManager,
                                        "StudentCardDialog"
                                    )
                                }
                            } else {
                                BasicToast.showBasicButtonToast(
                                    requireContext(),
                                    mainActivity,
                                    "아직 학생증이 등록되지 않았어요!",
                                    R.drawable.ic_face,
                                    resources.getString(R.string.register_button),
                                    mainActivity.binding.bottomNavBar,
                                    binding.root,
                                    StudentCardRegisterFragment()
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocationAndCallApi()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndCallApi()
            } else {
                Log.e("HomeFragment", "위치 권한 거부됨")
            }
        }
    }

    private fun getCurrentLocationAndCallApi() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    MyApplication.latitude = it.latitude.toString()
                    MyApplication.longitude = it.longitude.toString()
                }
            }
            .addOnFailureListener {

            }
    }

    fun showToast() {
        binding.root.post {
            if(MyApplication.isUniversityChanged) {
                MyApplication.isUniversityChanged = false

                BasicToast.showBasicButtonToast(
                    requireContext(),
                    mainActivity,
                    "학교가 변경되었어요",
                    R.drawable.ic_pencil,
                    resources.getString(R.string.re_register_button),
                    mainActivity.binding.bottomNavBar,
                    binding.root,
                    StudentCardRegisterFragment()
                )
            } else if(MyApplication.isStudentCardChanged) {
                MyApplication.isStudentCardChanged = false

                BasicToast.showBasicButtonToast(
                    requireContext(),
                    mainActivity,
                    "등록까지 최대 24시간이 걸려요",
                    R.drawable.ic_bell,
                    resources.getString(R.string.notification_button),
                    mainActivity.binding.bottomNavBar,
                    binding.root,
                    MypageNotificationFragment()
                )
            }
        }
    }

}


class TemplateCategoryVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                mixpanel.track("click_top_tab_eating", null)

                // 먹고가기 Fragment
                HomeEatFragment()
            }
            1 -> {
                mixpanel.track("click_top_tab_using", null)

                // 이용하기 Fragment
                HomeUseFragment()
            }
            2 -> {
                mixpanel.track("click_top_tab_partnership", null)

                // 파트너십 Fragment
                HomePartnershipFragment()
            }
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}