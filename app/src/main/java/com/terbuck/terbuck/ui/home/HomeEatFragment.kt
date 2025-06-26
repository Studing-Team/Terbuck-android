package com.terbuck.terbuck.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.api.response.home.StoreInfo
import com.terbuck.terbuck.databinding.FragmentHomeEatBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.adapter.HomeStoreAdapter
import com.terbuck.terbuck.viewModel.HomeViewModel

class HomeEatFragment : Fragment() {

    lateinit var binding: FragmentHomeEatBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    lateinit var homeStoreAdapter: HomeStoreAdapter

    var getPartnershipInfo: List<StoreInfo>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeEatBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewPartnership.apply {
                adapter = homeStoreAdapter
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
        homeStoreAdapter = HomeStoreAdapter(
            mainActivity,
            getPartnershipInfo
        ).apply {
            itemClickListener = object : HomeStoreAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 혜택 더보기 클릭
                    val dialog = DialogPartnership(getPartnershipInfo?.get(position))

                    dialog.show(mainActivity.supportFragmentManager, "DialogPartnership")
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            storeInfo.observe(viewLifecycleOwner) {
                getPartnershipInfo = it?.list

                homeStoreAdapter.updateList(getPartnershipInfo)
            }
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(false)
        viewModel.getHomeStoreInfo(mainActivity, "먹고가기")
    }

}