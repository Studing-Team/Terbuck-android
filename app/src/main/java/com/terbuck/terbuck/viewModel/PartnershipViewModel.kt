package com.terbuck.terbuck.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terbuck.terbuck.api.ApiClient
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.home.HomeStoreResponse
import com.terbuck.terbuck.api.response.terbuck.MapStoreInfo
import com.terbuck.terbuck.api.response.terbuck.MapStoreListResponse
import com.terbuck.terbuck.api.response.terbuck.StoreDetailResponse
import com.terbuck.terbuck.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PartnershipViewModel: ViewModel() {
    var storeInfo: MutableLiveData<MapStoreListResponse> = MutableLiveData()
    var storeDetailInfo: MutableLiveData<StoreDetailResponse> = MutableLiveData()

    fun getMapStoreList(activity: MainActivity, category: String?, latitude: Double, longitude: Double) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getMapStoreList(tokenManager.getAccessToken().toString(), tokenManager.getUniversity().toString(), category, latitude.toString(), longitude.toString())
            .enqueue(object :
                Callback<BaseResponse<MapStoreListResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<MapStoreListResponse>>,
                    response: Response<BaseResponse<MapStoreListResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<MapStoreListResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        storeInfo.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<MapStoreListResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<MapStoreListResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun getStoreDetailInfo(activity: MainActivity, storeId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getStoreDetailInfo(tokenManager.getAccessToken().toString(), storeId)
            .enqueue(object :
                Callback<BaseResponse<StoreDetailResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<StoreDetailResponse>>,
                    response: Response<BaseResponse<StoreDetailResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        storeDetailInfo.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<StoreDetailResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }
}