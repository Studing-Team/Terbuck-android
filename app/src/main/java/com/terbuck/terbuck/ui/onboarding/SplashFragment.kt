package com.terbuck.terbuck.ui.onboarding

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.databinding.FragmentSplashBinding
import com.terbuck.terbuck.ui.MainActivity
import kotlin.text.replace

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        Handler().postDelayed({
            val tokenManager = TokenManager(mainActivity)
            if(tokenManager.getAccessToken() != null && tokenManager.getIsSignUp()) {
                mainActivity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                mainActivity.setBottomNavigationHome()
            } else {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, LoginFragment())
                    .commit()
            }
        }, 2000)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation(true)
    }

}