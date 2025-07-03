package com.terbuck.terbuck.ui.terbuck

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.StoreDetailResponse
import com.terbuck.terbuck.databinding.FragmentStoreDetailBinding
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.DialogPartnership
import com.terbuck.terbuck.ui.terbuck.adapter.PartnershipImageAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreBenefitAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreImageAdapter
import com.terbuck.terbuck.ui.user.StudentCardFragment
import com.terbuck.terbuck.ui.user.StudentCardRegisterFragment
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication
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

    private var tooltipShown = false

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

            buttonUsages.setOnClickListener {
                mixpanel.track("click_detail_usage_info", null)

                val dialog = DialogUsages(getStoreDetailInfo?.usagesList)

                dialog.show(mainActivity.supportFragmentManager, "DialogUsages")
            }

            buttonNaver.setOnClickListener {
                mixpanel.track("move_detail_to_naver", null)

                // 네이버 플레이스 이동
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(getStoreDetailInfo?.shopLink))
                startActivity(intent)
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
                    mixpanel.track("click_detail_more_info", null)

                    // 혜택 상세 정보 bottom sheet
                    val benefitBottomSheet = StoreDetailBenefitBottomSheetFragment(mainActivity, getStoreDetailInfo?.benefitList?.get(position)?.detailList).apply {

                        dismissListener = object : StoreDetailBenefitBottomSheetFragment.OnBottomSheetDismissListener {
                            override fun onBottomSheetDismissed() {
                                updatePosition(-1)
                            }
                        }
                    }

                    benefitBottomSheet.show(childFragmentManager, benefitBottomSheet.tag)
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            storeDetailInfo.observe(viewLifecycleOwner) {
                getStoreDetailInfo = it

                binding.run {
                    textViewStoreName.text = getStoreDetailInfo?.name
                    textViewStoreAddress.text = getStoreDetailInfo?.address
                    textViewBenefitNum.text = "혜택 ${getStoreDetailInfo?.benefitCount}가지"

                    if(getStoreDetailInfo?.usagesList?.isEmpty() == true) {
                        buttonUsages.visibility = View.INVISIBLE
                    } else {
                        buttonUsages.visibility = View.VISIBLE
                    }
                }

                storeBenefitAdapter.updateList(getStoreDetailInfo?.benefitList)
                storeImageAdapter.updateList(getStoreDetailInfo?.imageList)

                binding.scrollView.post {
                    setupTooltipBehavior()
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
                    BasicToast.showBasicToast(requireContext(), "더 자세한 정보와 후기를 볼 수 있어요", R.drawable.ic_finger_down, binding.buttonNaver)
                }
            }
        })

        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (tooltipShown) return@setOnScrollChangeListener

            if (scrollY > 0) {
                tooltipShown = true
                BasicToast.showBasicToast(requireContext(), "더 자세한 정보와 후기를 볼 수 있어요", R.drawable.ic_finger_down, binding.buttonNaver)
            }
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            viewModel.getStoreDetailInfo(mainActivity, arguments?.getInt("storeId") ?: 0)
            if(MyApplication.isRegisterStudentCard) {
                toolbar.imageViewCard.run {
                    setImageResource(R.drawable.ic_studentcard_green10)
                    setOnClickListener {
                        mixpanel.track("click_detail_student_card", null)

                        // 학생증 등록 O
                        StudentCardFragment().show(parentFragmentManager, "StudentCardDialog")
                    }
                }
            } else {
                toolbar.imageViewCard.run {
                    setImageResource(R.drawable.ic_studentcard_white5)
                    setOnClickListener {
                        mixpanel.track("click_detail_student_card", null)

                        // 학생증 등록 X
                        BasicToast.showBasicButtonToast(
                            requireContext(),
                            mainActivity,
                            "아직 학생증이 등록되지 않았어요!",
                            R.drawable.ic_face,
                            resources.getString(R.string.register_button),
                            mainActivity.binding.bottomNavBar,
                            binding.root,
                            StudentCardRegisterFragment()
                        )
                    }
                }
            }

            toolbar.run {
                textViewHead.text = "제휴 혜택"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}