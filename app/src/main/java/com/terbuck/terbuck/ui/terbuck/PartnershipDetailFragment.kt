package com.terbuck.terbuck.ui.terbuck

import android.content.Intent
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
import androidx.lifecycle.Lifecycle

class PartnershipDetailFragment : Fragment() {

    private var _binding: FragmentPartnershipDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private lateinit var partnershipImageAdapter: PartnershipImageAdapter

    private var tooltipShown = false
    private var partnershipName: String? = null
    private var imageUrls: List<String>? = null

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var scrollChangeListener: View.OnScrollChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartnershipDetailBinding.inflate(inflater, container, false)
        mainActivity = (requireActivity() as MainActivity)

        initAdapter()
        observeViewModel()

        binding.recyclerViewPartnershipImage.apply {
            adapter = partnershipImageAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initAdapter() {
        partnershipImageAdapter = PartnershipImageAdapter(
            mainActivity,
            imageUrls
        ).apply {
            itemClickListener = object : PartnershipImageAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("partnershipName", (partnershipName ?: "").toString())
                    }
                    val nextFragment = ImageDetailFragment(imageUrls, position).apply {
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

    private fun observeViewModel() {
        viewModel.partnershipDetailInfo.observe(viewLifecycleOwner) {
            val b = _binding ?: return@observe  // 뷰 파괴 시 안전 탈출
            b.textViewPartnershipName.text = it?.name
            b.textViewPartnershipInstitution.text = it?.institution
            b.textViewPartnershipDescription.text = it?.detail

            val instaUrl = it?.snsLink.orEmpty()
            b.buttonInsta.setOnClickListener { _ ->
                mixpanel.track("move_partnership_to_instagram", null)
                startActivity(Intent(Intent.ACTION_VIEW, instaUrl.toUri()))
            }

            imageUrls = it?.imageList
            if (imageUrls.isNullOrEmpty()) {
                b.recyclerViewPartnershipImage.visibility = View.GONE
            } else {
                b.recyclerViewPartnershipImage.visibility = View.VISIBLE
                partnershipImageAdapter.updateList(imageUrls)
            }

            // 레이아웃 완성 후 tooltip 보여주기
            b.scrollView.post { setupTooltipBehavior() }
        }
    }

    private fun initView() {
        viewModel.getPartnershipDetailInfo(
            mainActivity,
            arguments?.getInt("partnershipId") ?: 0
        )

        mainActivity.hideBottomNavigation(true)

        binding.toolbar.apply {
            textViewHead.text = "파트너십 상세"
            buttonBack.setOnClickListener { parentFragmentManager.popBackStack() }
        }
    }

    private fun setupTooltipBehavior() {
        val b = _binding ?: return
        val scrollView = b.scrollView

        removeTooltipListeners()

        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (!isAdded || viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) return@OnGlobalLayoutListener
            val ctx = _binding?.root?.context ?: return@OnGlobalLayoutListener

            if (scrollView.viewTreeObserver.isAlive) {
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            }

            val child = scrollView.getChildAt(0)
            val canScroll = child != null && child.measuredHeight > scrollView.measuredHeight

            if (!canScroll && !tooltipShown) {
                tooltipShown = true
                BasicToast.showBasicToast(ctx, "문의하려면 아래 버튼을 눌러주세요.", R.drawable.ic_finger_down, b.buttonInsta)
            }
        }.also {
            scrollView.viewTreeObserver.addOnGlobalLayoutListener(it)
        }

        scrollChangeListener = View.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (tooltipShown) return@OnScrollChangeListener
            if (!isAdded || viewLifecycleOwner.lifecycle.currentState.ordinal < Lifecycle.State.STARTED.ordinal) return@OnScrollChangeListener

            val ctx = _binding?.root?.context ?: return@OnScrollChangeListener
            if (scrollY > 0) {
                tooltipShown = true
                BasicToast.showBasicToast(ctx, "문의하려면 아래 버튼을 눌러주세요.", R.drawable.ic_finger_down, b.buttonInsta)
            }
        }.also {
            scrollView.setOnScrollChangeListener(it)
        }
    }

    private fun removeTooltipListeners() {
        _binding?.scrollView?.viewTreeObserver?.let { vto ->
            if (vto.isAlive) {
                globalLayoutListener?.let { vto.removeOnGlobalLayoutListener(it) }
            }
        }
        _binding?.scrollView?.setOnScrollChangeListener(null)
        globalLayoutListener = null
        scrollChangeListener = null
    }

    override fun onDestroyView() {
        removeTooltipListeners()
        tooltipShown = false
        _binding = null
        super.onDestroyView()
    }
}
