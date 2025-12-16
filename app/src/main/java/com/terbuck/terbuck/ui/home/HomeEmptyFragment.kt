package com.terbuck.terbuck.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.databinding.FragmentHomeEmptyBinding
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.mypage.MypageNotificationFragment
import com.terbuck.terbuck.ui.user.StudentCardFragment
import com.terbuck.terbuck.ui.user.StudentCardOnboardingFragment
import com.terbuck.terbuck.ui.user.StudentCardRegisterFragment
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.utils.MyApplication.Companion.isRegisterStudentCard
import com.terbuck.terbuck.viewModel.HomeViewModel

class HomeEmptyFragment : Fragment() {

    lateinit var binding: FragmentHomeEmptyBinding
    lateinit var mainActivity: MainActivity

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeEmptyBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonRequestUniversity.setOnClickListener {
                // 제휴 혜택 정보 요청 API 호출
                homeViewModel.openUniversity(mainActivity, TokenManager(mainActivity).getUniversity().toString()) {

                    layoutRequestUniversity.visibility = View.GONE
                    buttonRequestUniversity.visibility = View.GONE
                    layoutSuccessRequestUniversity.visibility = View.VISIBLE

                    MyApplication.preferences.saveIsUniversityRegistered(false, true)

                    BasicToast.showBasicButtonToast(
                        requireContext(),
                        mainActivity,
                        "업데이트 되는대로 알려드릴게요 :)",
                        0,
                        resources.getString(R.string.notification_button),
                        mainActivity.binding.bottomNavBar,
                        binding.root,
                        MypageNotificationFragment()
                    )
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(false)
        }
        showToast()

        if(MyApplication.preferences.getIsFirst()) {
            MyApplication.preferences.setIsFirst(false)

            StudentCardOnboardingFragment().show(parentFragmentManager, "StudentCardOnboardingDialog")

            checkLocationPermission()
        }

        binding.run {
            if(MyApplication.preferences.getIsUniversityRequestRegistered()) {
                // 제휴업체 등록 신청 O
                layoutRequestUniversity.visibility = View.GONE
                buttonRequestUniversity.visibility = View.GONE
                layoutSuccessRequestUniversity.visibility = View.VISIBLE
            } else {
                // 제휴업체 등록 신청 X
                layoutRequestUniversity.visibility = View.VISIBLE
                buttonRequestUniversity.visibility = View.VISIBLE
                layoutSuccessRequestUniversity.visibility = View.GONE
            }

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