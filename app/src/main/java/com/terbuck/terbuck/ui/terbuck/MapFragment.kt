package com.terbuck.terbuck.ui.terbuck

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.terbuck.terbuck.api.response.terbuck.MapStoreInfo
import com.terbuck.terbuck.api.response.terbuck.MapStoreListResponse
import com.terbuck.terbuck.databinding.FragmentMapBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.adapter.PartnershipImageAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.CategoryAdapter
import com.terbuck.terbuck.ui.terbuck.adapter.StoreAdapter
import com.terbuck.terbuck.utils.MainUtil
import com.terbuck.terbuck.utils.MainUtil.getCategoryIndex
import com.terbuck.terbuck.utils.MainUtil.getDrawableResIds
import com.terbuck.terbuck.utils.MainUtil.setStatusBarTransparent
import com.terbuck.terbuck.utils.MainUtil.toPx
import com.terbuck.terbuck.viewModel.PartnershipViewModel
import kotlin.text.replace

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


        binding.run {
            NaverMapSdk.getInstance(mainActivity).client =
                NaverMapSdk.NaverCloudPlatformClient("${BuildConfig.MAP_API_KEY}")

            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

            toolbar.buttonSearch.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    toolbar.buttonSearch.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                    val toolbarBottom = toolbar.buttonSearch.bottom

                    val maxHeight = screenHeight - toolbarBottom - 250 // 👈 이 높이가 BottomSheet의 maxHeight가 됨

                    binding.bottomSheet.layoutParams.height = maxHeight
                    binding.bottomSheet.requestLayout()
                }
            })


            recyclerViewCategory.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val height = recyclerViewCategory.measuredHeight
                    Log.d("터벅터벅", "RecyclerViewCategory 높이 = $height")

                    bottomSheetBehavior.peekHeight = (height+28+34).toPx()

                    // 리스너 제거 (중복 호출 방지)
                    binding.recyclerViewCategory.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

            bottomSheetBehavior.run {
                isFitToContents = true
                skipCollapsed = false
                isHideable = false
                state = BottomSheetBehavior.STATE_COLLAPSED
            }


            recyclerViewStore.isNestedScrollingEnabled = true
            recyclerViewStore.overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            recyclerViewStore.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val canScrollUp = recyclerView.canScrollVertically(-1)
                    bottomSheetBehavior.isDraggable = !canScrollUp
                }
            })


            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetBehavior.isDraggable = false
                        }
                        BottomSheetBehavior.STATE_COLLAPSED,
                        BottomSheetBehavior.STATE_HALF_EXPANDED,
                        BottomSheetBehavior.STATE_DRAGGING,
                        BottomSheetBehavior.STATE_SETTLING -> {
                            bottomSheetBehavior.isDraggable = true
                        }

                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // 필요 시 애니메이션 처리 가능
                }
            })


            recyclerViewCategory.apply {
                adapter = categoryAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            recyclerViewStore.apply {
                adapter = storeAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            toolbar.buttonLocation.setOnClickListener {
                // 현재 위치로 이동
                toolbar.imageViewLocation.setImageResource(R.drawable.ic_location_selected)
                moveToCurrentLocation()
            }

            locationSource = FusedLocationSource(this@MapFragment, LOCATION_PERMISSTION_REQUEST_CODE)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        binding.run {
            bottomSheet.visibility = View.VISIBLE
            bottomSheetStoreList.layoutStore.visibility = View.GONE
        }

        mainActivity.hideBottomNavigation(false)
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
                }
            }
        }
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
//            moveToCurrentLocation() // 권한이 이미 부여된 경우
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
//                    moveToCurrentLocation() // 권한 승인 후 위치 이동
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

        if (lastLocation != null) {
            // 위치 정보가 있을 경우, 현재 위치로 지도 초기화
            val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(currentLatLng, 15.0).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 위치 정보가 없을 경우, 추적 모드 활성화 (현재 위치 자동 업데이트)
            checkLocationPermission()
        }

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
                storeAdapter.updateList(getStoreInfo?.list)

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
                        // 하단 바 표시 및 마커 이동 처리
                        binding.run {
                            bottomSheet.visibility = View.GONE
                            bottomSheetStoreList.layoutStore.visibility = View.VISIBLE
                        }

                        binding.bottomSheetStoreList.run {
                            var storeInfo = getStoreInfo?.list?.get(m)
                            var categoryImage = getDrawableResIds(R.array.partnership_category_image_unselected, resources)

                            Glide.with(mainActivity).load(storeInfo?.thumbnailImage)
                                .into(imageViewStore)
                            textViewStoreName.text = storeInfo?.name
                            textViewStoreAddress.text = storeInfo?.address
                            textViewBenefitNum.text = "혜택 ${storeInfo?.benefitCount}가지"
                            imageViewCategory.setImageResource(categoryImage[getCategoryIndex(storeInfo?.category) + 1])

                            layoutStore.setOnClickListener {
                            }
                        }

                        val cameraUpdate = CameraUpdate.scrollTo(marker.position).animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)

                        true
                    }

                    // 지도 클릭한 경우
                    naverMap.setOnMapClickListener { pointF, latLng ->
                        binding.run {
                            bottomSheet.visibility = View.VISIBLE
                            bottomSheetStoreList.layoutStore.visibility = View.GONE
                        }

                        fetchStoresBasedOnMapView()
                    }
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
        viewModel.getMapStoreList(mainActivity, category, latitude, longitude)
    }
}