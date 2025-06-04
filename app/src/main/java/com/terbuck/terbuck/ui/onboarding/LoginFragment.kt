package com.terbuck.terbuck.ui.onboarding

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentLoginBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.viewModel.OnboardingViewModel

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: OnboardingViewModel by lazy {
        ViewModelProvider(requireActivity())[OnboardingViewModel::class.java]
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
            viewModel.login(mainActivity, token.accessToken.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonKakao.setOnClickListener {
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
                            viewModel.login(mainActivity, token.accessToken.toString())
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation(true)
    }

}