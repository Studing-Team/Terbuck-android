package com.terbuck.terbuck.ui.terbuck

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.terbuck.terbuck.ui.terbuck.adapter.StoreSearchAdapter
import com.terbuck.terbuck.utils.MainUtil.getDrawableResIds
import com.terbuck.terbuck.viewModel.PartnershipViewModel

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PartnershipViewModel by lazy {
        ViewModelProvider(this)[PartnershipViewModel::class.java]
    }

    private lateinit var searchAdapter: StoreSearchAdapter
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

        binding.recyclerViewSearchResult.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMapStoreList(mainActivity, null, null, null)
    }

    private fun initView() {
        binding.toolbar.textViewSearch.addTextChangedListener {
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

    private fun initAdapter() {
        searchAdapter = StoreSearchAdapter(mainActivity, filteredStoreList).apply {
            itemClickListener = object : StoreSearchAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val store = filteredStoreList[position]
                    val bundle = Bundle().apply { putInt("storeId", store.shopId) }
                    val nextFragment = StoreDetailFragment().apply { arguments = bundle }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, nextFragment)
                        .addToBackStack(null)
                        .commit()
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

        Log.d("##", filteredStoreList.toString())

        searchAdapter.updateList(filteredStoreList, keyword)
    }
}

