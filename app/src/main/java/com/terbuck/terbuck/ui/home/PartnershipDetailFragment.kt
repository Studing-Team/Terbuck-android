package com.terbuck.terbuck.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentPartnershipDetailBinding
import com.terbuck.terbuck.ui.MainActivity

class PartnershipDetailFragment : Fragment() {

    lateinit var binding: FragmentPartnershipDetailBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPartnershipDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    fun initView() {
        binding.run {

        }
    }

}