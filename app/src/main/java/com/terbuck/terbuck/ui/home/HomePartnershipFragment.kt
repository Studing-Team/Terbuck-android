package com.terbuck.terbuck.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentHomePartnershipBinding
import com.terbuck.terbuck.ui.MainActivity

class HomePartnershipFragment : Fragment() {

    lateinit var binding: FragmentHomePartnershipBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomePartnershipBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

}