package com.terbuck.terbuck.ui.terbuck

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentPartnershipDetailBinding
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.terbuck.adapter.PartnershipImageAdapter
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.viewModel.HomeViewModel
import kotlin.text.replace

class PartnershipDetailFragment : Fragment() {

    lateinit var binding: FragmentPartnershipDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    lateinit var partnershipImageAdapter: PartnershipImageAdapter

    private var tooltipShown = false
    var partnershipName: String? = null
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
                    // 이미지 확대 기능
                    val bundle = Bundle().apply {
                        putString("partnershipName", (partnershipName ?: "").toString())
                    }

                    // 전달할 Fragment 생성
                    var nextFragment = ImageDetailFragment(imageUrls, position).apply {
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
            partnershipDetailInfo.observe(viewLifecycleOwner) {
                binding.run {
                    partnershipName = it?.name
                    textViewPartnershipName.text = partnershipName
                    textViewPartnershipInstitution.text = it?.institution
                    textViewPartnershipDescription.text = it?.detail

                    var instaUrl = it?.snsLink.toString()

                    buttonInsta.setOnClickListener {
                        mixpanel.track("move_partnership_to_instagram", null)

                        var intent = Intent(Intent.ACTION_VIEW, instaUrl.toUri())
                        startActivity(intent)
                    }

                    imageUrls = it?.imageList
                    if(imageUrls?.size == 0) {
                        recyclerViewPartnershipImage.visibility = View.GONE
                    } else {
                        recyclerViewPartnershipImage.visibility = View.VISIBLE
                        partnershipImageAdapter.updateList(imageUrls)
                    }

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