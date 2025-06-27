package com.terbuck.terbuck.ui.terbuck

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.MapStoreInfo
import com.terbuck.terbuck.databinding.FragmentSearchBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.DialogPartnership
import com.terbuck.terbuck.ui.home.adapter.HomeStoreAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreRecentSearchAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreSearchAdapter
import com.terbuck.terbuck.utils.MainUtil.getDrawableResIds
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.viewModel.PartnershipViewModel
import kotlin.collections.get

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PartnershipViewModel by lazy {
        ViewModelProvider(this)[PartnershipViewModel::class.java]
    }

    private lateinit var searchAdapter: StoreSearchAdapter
    private lateinit var recentSearchAdapter: StoreRecentSearchAdapter


    private var fullStoreList: List<MapStoreInfo> = emptyList()
    private var filteredStoreList: List<MapStoreInfo> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        initView()
        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewSearchResult.apply {
                adapter = searchAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            recyclerViewRecentSearch.apply {
                adapter = recentSearchAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMapStoreList(mainActivity, null, null, null)
    }

    private fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.toolbar.textViewSearch.run {
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableStart = binding.toolbar.textViewSearch.compoundDrawables[0]

                    if (drawableStart != null) {
                        val drawableWidth = drawableStart.bounds.width()
                        if (event.x <= binding.toolbar.textViewSearch.paddingStart + drawableWidth) {
                            Log.d("DrawableClick", "drawableStart 클릭됨")
                            // 예: activity?.onBackPressedDispatcher?.onBackPressed()

                            fragmentManager?.popBackStack()

                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }

            addTextChangedListener {
                if(it.isNullOrEmpty() == false) {
                    binding.run {
                        recyclerViewSearchResult.visibility = View.VISIBLE
                        layoutRecentSearch.visibility = View.GONE
                    }

                    val keyword = it.toString().trim()
                    filterSearch(keyword)
                } else {
                    binding.run {
                        layoutRecentSearch.visibility = View.VISIBLE
                        recyclerViewSearchResult.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        searchAdapter = StoreSearchAdapter(mainActivity, filteredStoreList).apply {
            itemClickListener = object : StoreSearchAdapter.OnItemClickListener {
                @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
                override fun onItemClick(position: Int) {
                    val store = filteredStoreList[position]
                    MyApplication.preferences.saveRecentSearchLimited(mainActivity, store.name)

                    MyApplication.selectedStoreId = store.shopId

                    fragmentManager?.popBackStack()
                }
            }
        }

        recentSearchAdapter = StoreRecentSearchAdapter(mainActivity, MyApplication.preferences.getRecentSearchesLimited(mainActivity)).apply {
            itemClickListener = object : StoreRecentSearchAdapter.OnItemClickListener {
                @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
                override fun onItemClick(position: Int) {
                    MyApplication.preferences.removeRecentSearch(mainActivity,
                        MyApplication.preferences.getRecentSearchesLimited(mainActivity)[position]
                    )

                    recentSearchAdapter.updateList(MyApplication.preferences.getRecentSearchesLimited(mainActivity))
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.storeInfo.observe(viewLifecycleOwner) { response ->
            fullStoreList = response.list
            filterSearch(binding.toolbar.textViewSearch.text.toString())
        }
    }

    private fun filterSearch(keyword: String) {
        filteredStoreList = if (keyword.isEmpty()) {
            emptyList()
        } else {
            fullStoreList.filter { it.name.contains(keyword, ignoreCase = true) }
        }

        searchAdapter.updateList(filteredStoreList, keyword)
    }
}

