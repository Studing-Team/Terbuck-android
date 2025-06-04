package com.terbuck.terbuck.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.ui.MainActivity

class HomeUseFragment : Fragment() {

    lateinit var binding: FragmentHomeUseBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeUseBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }


    fun initView() {
    }

}