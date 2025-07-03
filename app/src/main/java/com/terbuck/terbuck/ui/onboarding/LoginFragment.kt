package com.terbuck.terbuck.ui.onboarding

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentLoginBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.viewModel.OnboardingViewModel
import com.terbuck.terbuck.viewModel.UserViewModel

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: OnboardingViewModel by lazy {
        ViewModelProvider(requireActivity())[OnboardingViewModel::class.java]
    }
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }


    // 카카오 로그인
    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            // 로그인 기능 구현
            viewModel.login(mainActivity, token.accessToken.toString()) {
                userViewModel.getStudentCard(mainActivity)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        if(MyApplication.preferences.getIsFirstLoginView() == true) {
            MyApplication.preferences.setIsFirstLoginView(false)

            checkNotificationPermission()
        }

        binding.run {
            buttonKakao.setOnClickListener {
                mixpanel.track("click_login_kakao", null)

                // 카카오 로그인
                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡으로 로그인 실패", error)

                            // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }

                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
                        } else if (token != null) {
                            Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                            // 로그인 기능 구현
                            viewModel.login(mainActivity, token.accessToken.toString()) {
                                userViewModel.getStudentCard(mainActivity)
                            }
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
                }
            }
        }

        return binding.root
    }

    fun checkNotificationPermission() {
        // 알림 권한 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            // 푸쉬 권한 없음 -> 권한 요청
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                123
            )
        } else {
            // 이미 권한이 있는 경우 바로 화면 전환
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation(true)
    }

}