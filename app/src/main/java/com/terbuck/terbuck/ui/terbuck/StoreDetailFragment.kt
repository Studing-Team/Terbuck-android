package com.terbuck.terbuck.ui.terbuck

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.StoreDetailResponse
import com.terbuck.terbuck.databinding.FragmentStoreDetailBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.terbuck.adapter.PartnershipImageAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreBenefitAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreImageAdapter
import com.terbuck.terbuck.viewModel.PartnershipViewModel

class StoreDetailFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PartnershipViewModel by lazy {
        ViewModelProvider(this)[PartnershipViewModel::class.java]
    }

    lateinit var storeBenefitAdapter: StoreBenefitAdapter
    lateinit var storeImageAdapter: StoreImageAdapter

    var getStoreDetailInfo: StoreDetailResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewImage.apply {
                adapter = storeImageAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }
            recyclerViewStoreBenefit.apply {
                adapter = storeBenefitAdapter
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
        storeImageAdapter = StoreImageAdapter(
            mainActivity,
            getStoreDetailInfo?.imageList
        ).apply {
            itemClickListener = object : StoreImageAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 이미지 확대 기능
                    val bundle = Bundle().apply {
                        putString("partnershipName", (getStoreDetailInfo?.name ?: "").toString())
                    }

                    // 전달할 Fragment 생성
                    var nextFragment = ImageDetailFragment(getStoreDetailInfo?.imageList, position).apply {
                        arguments = bundle
                    }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        storeBenefitAdapter = StoreBenefitAdapter(
            mainActivity,
            getStoreDetailInfo?.benefitList
        ).apply {
            itemClickListener = object : StoreBenefitAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {

                }
            }
        }
    }

    fun observeViewModel() {
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {

            toolbar.run {
                textViewHead.text = "제휴 혜택"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}