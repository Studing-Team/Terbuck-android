package com.terbuck.terbuck.ui.terbuck

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.terbuck.terbuck.BuildConfig
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.response.terbuck.MapStoreListResponse
import com.terbuck.terbuck.databinding.FragmentMapBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.terbuck.adapter.CategoryAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreAdapter
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MainUtil.getCategoryIndex
import com.terbuck.terbuck.utils.MainUtil.getDrawableResIds
import com.terbuck.terbuck.utils.MainUtil.toPx
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.viewModel.PartnershipViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentMapBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PartnershipViewModel by lazy {
        ViewModelProvider(this)[PartnershipViewModel::class.java]
    }

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap


    private val CURRENT_LOCATION_CODE = 200
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private val NOTIFICATION_PERMISSTION_REQUEST_CODE: Int = 123
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체

    var lastCameraPosition: LatLng? = null

    lateinit var categoryAdapter: CategoryAdapter
    lateinit var storeAdapter: StoreAdapter

    var category: String? = null
    var getStoreInfo: MapStoreListResponse? = null

    val markers = mutableListOf<Marker>()

    private var isInitialCameraMoved = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        initAdapter()
        observeViewModel()
        setupBottomSheet()
        setupRecyclerViews()


        binding.run {
            NaverMapSdk.getInstance(mainActivity).client =
                NaverMapSdk.NaverCloudPlatformClient("${BuildConfig.MAP_API_KEY}")

            recyclerViewCategory.apply {
                adapter = categoryAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            recyclerViewStore.apply {
                adapter = storeAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            toolbar.buttonSearch.setOnClickListener {
                mixpanel.track("click_map_searchbar", null)

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, SearchFragment())
                    .addToBackStack(null)
                    .commit()
            }

            toolbar.buttonLocation.setOnClickListener {
                mixpanel.track("click_map_gps", null)

                // 현재 위치로 이동
                toolbar.imageViewLocation.setImageResource(R.drawable.ic_location_selected)
                checkLocationPermission()
            }

            locationSource = FusedLocationSource(this@MapFragment, LOCATION_PERMISSTION_REQUEST_CODE)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        isInitialCameraMoved = false

        binding.run {
            bottomSheet.visibility = View.VISIBLE
            bottomSheetStoreList.layoutStore.visibility = View.GONE
            BottomSheetBehavior.from(bottomSheet).isDraggable = true
        }

        mainActivity.hideBottomNavigation(false)

        if (MyApplication.selectedStoreId != null) {
            moveToStoreMarker(MyApplication.selectedStoreId ?: -1)
            MyApplication.selectedStoreId = null
        }
    }

    fun initAdapter() {
        categoryAdapter = CategoryAdapter(
            mainActivity,
            resources.getTextArray(R.array.partnership_category_name).map { it.toString() },
            getDrawableResIds(R.array.partnership_category_image_white, resources),
            getDrawableResIds(R.array.partnership_category_image_unselected, resources)
        ).apply {
            itemClickListener = object : CategoryAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {

                    when(position) {
                        0 -> { mixpanel.track("click_category_all", null) }
                        1 -> { mixpanel.track("click_category_food", null) }
                        2 -> { mixpanel.track("click_category_cafe", null) }
                        3 -> { mixpanel.track("click_category_drink", null) }
                        4 -> { mixpanel.track("click_category_hospital", null) }
                        5 -> { mixpanel.track("click_category_exercise", null) }
                        6 -> { mixpanel.track("click_category_culture", null) }
                        7 -> { mixpanel.track("click_category_study", null) }
                    }

                    // 카테고리 선택
                    category = if(position == 0) { null } else { resources.getTextArray(R.array.partnership_category_name)[position].toString() }
                    fetchStoresBasedOnMapView()

                    categoryAdapter.notifyDataSetChanged()
                }
            }
        }

        storeAdapter = StoreAdapter(
            mainActivity,
            getStoreInfo?.list,
            getDrawableResIds(R.array.partnership_category_image_unselected, resources)
        ).apply {
            itemClickListener = object : StoreAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    mixpanel.track("move_list_to_detail", null)

                    // 스토어 상세 화면 이동
                    var nextFragment = StoreDetailFragment()

                    val bundle = Bundle().apply { putInt("storeId",
                        getStoreInfo?.list?.get(position)?.shopId?.toInt() ?: 0
                    ) }

                    nextFragment = StoreDetailFragment().apply {
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

    private fun setupRecyclerViews() {
        binding.recyclerViewStore.apply {
            adapter = storeAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val canScrollUp = recyclerView.canScrollVertically(-1)
                    BottomSheetBehavior.from(binding.bottomSheet).isDraggable = !canScrollUp
                }
            })
        }
    }


    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.run {
            isFitToContents = true
            skipCollapsed = false
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.recyclerViewStore.post {
            val canScrollMore = binding.recyclerViewStore.canScrollVertically(1)
            bottomSheetBehavior.isDraggable = !canScrollMore
        }

        binding.bottomsheetContent.setOnTouchListener { _, _ ->
            bottomSheetBehavior.isDraggable = true
            false
        }

        binding.recyclerViewCategory.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.recyclerViewCategory.measuredHeight
                bottomSheetBehavior.peekHeight = (height + 62).toPx()
                binding.recyclerViewCategory.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        binding.toolbar.buttonSearch.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.toolbar.buttonSearch.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val toolbarBottom = binding.toolbar.buttonSearch.bottom
                binding.bottomSheet.layoutParams.height = screenHeight - toolbarBottom - 300
                binding.bottomSheet.requestLayout()
            }
        })

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val canScrollMore = binding.recyclerViewStore.canScrollVertically(1)
                bottomSheetBehavior.isDraggable = newState != BottomSheetBehavior.STATE_EXPANDED || !canScrollMore
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!bottomSheetBehavior.isDraggable) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })
    }

    private fun moveToCurrentLocation() {
        val lastLocation = locationSource.lastLocation
        if (lastLocation != null) {
            // 위치가 유효할 때 카메라 이동
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(
                LatLng(lastLocation.latitude, lastLocation.longitude),
                15.0 // 줌 레벨
            ).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 위치 요청이 아직 활성화되지 않은 경우 강제 요청
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            Log.d("MapFragment", "현재 위치 정보를 가져올 수 없어 추적 모드 활성화")
        }
    }


    private fun checkLocationPermission() {
        if (!locationSource.isActivated) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSTION_REQUEST_CODE
            )
        } else {
            moveToCurrentLocation() // 권한이 이미 부여된 경우
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSTION_REQUEST_CODE) {
            if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                if (locationSource.isActivated) {
                    Log.d("터벅터벅", "위치 권한 승인됨")
                    moveToCurrentLocation()
                } else {
                    Log.e("터벅터벅", "위치 권한 거부됨")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        naverMap.mapType = NaverMap.MapType.Basic

        // 지도 옵션 설정
        naverMap.run {
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            uiSettings.run {
                setLogoMargin(40, 0, 40, 320)
                isScaleBarEnabled = false
                isZoomControlEnabled = false
                isCompassEnabled = false
            }
        }

        // 위치 소스 연결
        naverMap.locationSource = locationSource

        // 현재 위치 가져오기 & 초기 지도 설정
        val lastLocation = locationSource.lastLocation

        fetchStoresBasedOnMapView()

        // 확대/이동이 발생하면 다시 매장 데이터 로드
        naverMap.addOnCameraIdleListener {
            val currentCenter = naverMap.cameraPosition.target

            // 위치 변경 없으면 리턴
            if (lastCameraPosition != null && lastCameraPosition == currentCenter) return@addOnCameraIdleListener

            lastCameraPosition = currentCenter
            fetchStoresBasedOnMapView()
        }

        // 확대 축소 범위 설정
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 10.0

        // 위치 추적 모드 설정
        naverMap.locationTrackingMode = LocationTrackingMode.None
    }


    fun observeViewModel() {
        viewModel.run {
            storeInfo.observe(viewLifecycleOwner) {
                binding.bottomSheet.layoutParams.height = binding.bottomSheet.layoutParams.height
                binding.bottomSheet.requestLayout()

                getStoreInfo = it
                binding.run {
                    if(getStoreInfo?.list?.size == 0) {
                        textViewEmpty.visibility = View.VISIBLE
                        recyclerViewStore.visibility = View.GONE
                    } else {
                        textViewEmpty.visibility = View.GONE
                        recyclerViewStore.visibility = View.VISIBLE
                        storeAdapter.updateList(getStoreInfo?.list)
                    }
                }

                // 기존 마커 클리어
                markers.forEach { it.map = null }
                markers.clear()

                // 새로운 마커 추가
                for (i in 0 until (getStoreInfo?.list?.size ?: 0)) {
                    val marker = Marker()
                    val latitude = getStoreInfo?.list?.get(i)?.latitude?.toDouble()
                    val longitude = getStoreInfo?.list?.get(i)?.longitude?.toDouble()
                    marker.position = LatLng(latitude!!, longitude!!)
                    marker.icon = OverlayImage.fromResource(setMarker(i))

                    markers.add(marker)
                }

                markers.forEachIndexed { m, marker ->
                    marker.map = naverMap
                    marker.setOnClickListener {
                        mixpanel.track("click_map_pin", null)

                        // 하단 바 표시 및 마커 이동 처리
                        var storeInfo = getStoreInfo?.list?.get(m)
                        moveToStoreMarker(storeInfo?.shopId?.toInt() ?: 0)


                        val cameraUpdate = CameraUpdate.scrollTo(marker.position).animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)

                        true
                    }

                    // 지도 클릭한 경우
                    naverMap.setOnMapClickListener { pointF, latLng ->
                        binding.run {
                            bottomSheet.visibility = View.VISIBLE
                            bottomSheetStoreList.layoutStore.visibility = View.GONE
                            BottomSheetBehavior.from(bottomSheet).isDraggable = true
                        }
                        
                        fetchStoresBasedOnMapView()
                    }
                }

                // 마커 중앙 위치로 지도 이동
                if (!isInitialCameraMoved && markers.isNotEmpty()) {
                    val latLngBoundsBuilder = com.naver.maps.geometry.LatLngBounds.Builder()
                    markers.forEach { marker ->
                        latLngBoundsBuilder.include(marker.position)
                    }

                    val bounds = latLngBoundsBuilder.build()
                    val cameraUpdate = CameraUpdate.fitBounds(bounds, 100)
                        .animate(CameraAnimation.Easing)
                    naverMap.moveCamera(cameraUpdate)

                    isInitialCameraMoved = true
                }
            }
        }
    }

    fun setMarker(position: Int): Int {
        val category = getStoreInfo?.list?.get(position)?.category
        val index = getCategoryIndex(category)

        if (index == -1) return 0

        val typedArray = context?.resources?.obtainTypedArray(R.array.partnership_category_image_marker)
        val markerResId = typedArray?.getResourceId(index, 0) ?: 0
        typedArray?.recycle()

        return markerResId
    }

    fun moveToStoreMarker(shopId: Int) {
        val store = getStoreInfo?.list?.find { it.shopId == shopId } ?: return
        val markerIndex = getStoreInfo?.list?.indexOf(store) ?: return

        val position = LatLng(store.latitude, store.longitude)
        val cameraUpdate = CameraUpdate.scrollTo(position).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

        // 기존 마커 클릭 리스너와 동일한 UI 표시
        binding.run {
            bottomSheet.visibility = View.GONE
            bottomSheetStoreList.layoutStore.visibility = View.VISIBLE
        }

        binding.bottomSheetStoreList.run {
            Glide.with(mainActivity).load(store.thumbnailImage)
                .into(imageViewStore)
            textViewStoreName.text = store.name
            textViewStoreAddress.text = store.address
            textViewBenefitNum.text = "혜택 ${store.benefitCount}가지"
            imageViewCategory.setImageResource(getDrawableResIds(R.array.partnership_category_image_unselected, resources)[getCategoryIndex(store.category) + 1])

            layoutStore.setOnClickListener {
                mixpanel.track("move_map_to_detail", null)

                // 스토어 상세 화면 이동
                val bundle = Bundle().apply { putInt("storeId", store.shopId) }
                val nextFragment = StoreDetailFragment().apply { arguments = bundle }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


    private fun fetchStoresBasedOnMapView() {
        if (!this::naverMap.isInitialized) return // 지도 초기화 확인

        // 1️⃣ 현재 지도 중심 좌표 가져오기
        val centerLatLng = naverMap.cameraPosition.target
        val latitude = centerLatLng.latitude
        val longitude = centerLatLng.longitude

        // 2️⃣ 현재 지도 화면의 경계(LatLngBounds) 가져오기
        val bounds = naverMap.contentBounds

        // 3️⃣ 화면 상단의 위도(Latitude) 가져오기 (북쪽 위 경계)
        val northLat = bounds.northLatitude

        // 4️⃣ 반경(Radius) 계산 (중심 좌표 ↔ 북쪽 경계 거리)
        val radius = centerLatLng.distanceTo(LatLng(northLat, longitude))

        // ✅ 현재 지도 중심 좌표 및 반경을 기반으로 매장 목록 요청
        viewModel.getMapStoreList(mainActivity, category, latitude.toString(), longitude.toString())
    }
}