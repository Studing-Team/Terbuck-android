package com.terbuck.terbuck.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentSchoolBinding
import com.terbuck.terbuck.ui.MainActivity

class SchoolFragment : Fragment() {

    lateinit var binding: FragmentSchoolBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSchoolBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }



}