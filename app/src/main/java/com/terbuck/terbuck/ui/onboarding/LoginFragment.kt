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

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonKakao.setOnClickListener {
            }
        }

        return binding.root
    }

}