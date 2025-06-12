package com.terbuck.terbuck.ui.terbuck

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
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
import com.terbuck.terbuck.databinding.FragmentMapBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.viewModel.PartnershipViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentMapBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PartnershipViewModel by lazy {
        ViewModelProvider(requireActivity())[PartnershipViewModel::class.java]
    }

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap


    private val CURRENT_LOCATION_CODE = 200
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private val NOTIFICATION_PERMISSTION_REQUEST_CODE: Int = 123
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체

    var lastCameraPosition: LatLng? = null


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


        binding.run {
            NaverMapSdk.getInstance(mainActivity).client =
                NaverMapSdk.NaverCloudPlatformClient("${BuildConfig.MAP_API_KEY}")


            locationSource = FusedLocationSource(this@MapFragment, LOCATION_PERMISSTION_REQUEST_CODE)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        mainActivity.run {
            hideBottomNavigation(false)
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
                    moveToCurrentLocation() // 권한 승인 후 위치 이동
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

        naverMap.mapType = NaverMap.MapType.Navi // 네비게이션 스타일 (다크 테마 적용됨)

        // 지도 옵션 설정
        naverMap.run {
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            isIndoorEnabled = true
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

        // 지도 화면이 로딩된 후, 현재 보이는 지도를 기준으로 매장 리스트 가져오기
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

        Log.d("터벅터벅", "현재 지도 중심: lat=$latitude, lng=$longitude, 반경=$radius")

    }
}