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
import com.terbuck.terbuck.api.response.home.PartnershipInfo
import com.terbuck.terbuck.api.response.home.StoreInfo
import com.terbuck.terbuck.databinding.FragmentHomePartnershipBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.adapter.HomePartnershipAdapter
import com.terbuck.terbuck.ui.home.adapter.HomeStoreAdapter
import com.terbuck.terbuck.ui.terbuck.PartnershipDetailFragment
import com.terbuck.terbuck.viewModel.HomeViewModel

class HomePartnershipFragment : Fragment() {

    lateinit var binding: FragmentHomePartnershipBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    lateinit var homePartnershipNewAdapter: HomePartnershipAdapter
    lateinit var homePartnershipAdapter: HomePartnershipAdapter

    var getPartnershipNewInfo: List<PartnershipInfo>? = null
    var getPartnershipInfo: List<PartnershipInfo>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomePartnershipBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewPartnershipNew.apply {
                adapter = homePartnershipNewAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            recyclerViewPartnership.apply {
                adapter = homePartnershipAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        homePartnershipNewAdapter = HomePartnershipAdapter(
            mainActivity,
            getPartnershipNewInfo
        ).apply {
            itemClickListener = object : HomePartnershipAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 파트너십 상세 정보 화면
                    val bundle = Bundle().apply { putInt("partnershipId", getPartnershipNewInfo?.get(position)?.id ?: 0) }

                    var nextFragment = PartnershipDetailFragment().apply {
                        arguments = bundle
                    }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        homePartnershipAdapter = HomePartnershipAdapter(
            mainActivity,
            getPartnershipInfo
        ).apply {
            itemClickListener = object : HomePartnershipAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 파트너십 상세 정보 화면
                    val bundle = Bundle().apply { putInt("partnershipId", getPartnershipInfo?.get(position)?.id ?: 0) }

                    var nextFragment = PartnershipDetailFragment().apply {
                        arguments = bundle
                    }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            partnershipNewInfo.observe(viewLifecycleOwner) {
                getPartnershipNewInfo = it?.list

                if(getPartnershipNewInfo?.size == 0) {
                    binding.layoutNew.visibility = View.GONE
                } else {
                    binding.layoutNew.visibility = View.VISIBLE
                    homePartnershipNewAdapter.updateList(getPartnershipNewInfo)
                }
            }

            partnershipInfo.observe(viewLifecycleOwner) {
                getPartnershipInfo = it?.list

                homePartnershipAdapter.updateList(getPartnershipInfo)
            }
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(false)
        viewModel.getHomePartnershipNewInfo(mainActivity)
        viewModel.getHomePartnershipInfo(mainActivity)
    }

}