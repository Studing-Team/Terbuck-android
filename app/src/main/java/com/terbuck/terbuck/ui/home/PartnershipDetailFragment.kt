package com.terbuck.terbuck.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentPartnershipDetailBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.viewModel.HomeViewModel
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.home.adapter.HomePartnershipAdapter
import com.terbuck.terbuck.ui.home.adapter.PartnershipImageAdapter

class PartnershipDetailFragment : Fragment() {

    lateinit var binding: FragmentPartnershipDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    lateinit var partnershipImageAdapter: PartnershipImageAdapter

    private var tooltipShown = false
    var imageUrls: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPartnershipDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewPartnershipImage.apply {
                adapter = partnershipImageAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        partnershipImageAdapter = PartnershipImageAdapter(
            mainActivity,
            imageUrls
        ).apply {
            itemClickListener = object : PartnershipImageAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {

                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            partnershipDetailInfo.observe(viewLifecycleOwner) {
                binding.run {
                    textViewPartnershipName.text = it?.name
                    textViewPartnershipInstitution.text = it?.institution
                    textViewPartnershipDescription.text = it?.detail

                    var instaUrl = it?.snsLink.toString()

                    buttonInsta.setOnClickListener {
                        var intent = Intent(Intent.ACTION_VIEW, instaUrl.toUri())
                        startActivity(intent)
                    }

                    imageUrls = it?.imageList
                    partnershipImageAdapter.updateList(imageUrls)

                    scrollView.post {
                        setupTooltipBehavior()
                    }
                }
            }
        }
    }

    fun initView() {
        viewModel.getPartnershipDetailInfo(mainActivity, arguments?.getInt("partnershipId") ?: 0)

        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "파트너십 상세"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }


    private fun setupTooltipBehavior() {
        val scrollView = binding.scrollView

        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val canScroll = scrollView.getChildAt(0).measuredHeight > scrollView.measuredHeight

                if (!canScroll && !tooltipShown) {
                    tooltipShown = true
                    BasicToast.showBasicToast(requireContext(), "문의하려면 아래 버튼을 눌러주세요.", R.drawable.ic_finger_down, binding.buttonInsta)
                }
            }
        })

        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (tooltipShown) return@setOnScrollChangeListener

            if (scrollY > 0) {
                tooltipShown = true
                BasicToast.showBasicToast(requireContext(), "문의하려면 아래 버튼을 눌러주세요.", R.drawable.ic_finger_down, binding.buttonInsta)
            }
        }
    }
}