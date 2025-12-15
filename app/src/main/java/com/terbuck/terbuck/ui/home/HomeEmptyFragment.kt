package com.terbuck.terbuck.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentHomeEmptyBinding
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.user.StudentCardFragment
import com.terbuck.terbuck.ui.user.StudentCardOnboardingFragment
import com.terbuck.terbuck.ui.user.StudentCardRegisterFragment
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.utils.MyApplication.Companion.isRegisterStudentCard

class HomeEmptyFragment : Fragment() {

    lateinit var binding: FragmentHomeEmptyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeEmptyBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {

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
    }

}