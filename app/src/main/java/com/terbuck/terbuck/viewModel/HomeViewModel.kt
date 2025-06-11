package com.terbuck.terbuck.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terbuck.terbuck.api.ApiClient
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.api.request.onboarding.SignUpRequest
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.home.HomePartnershipResponse
import com.terbuck.terbuck.api.response.home.HomeStoreResponse
import com.terbuck.terbuck.api.response.home.PartnershipDetailResponse
import com.terbuck.terbuck.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {
    var storeInfo: MutableLiveData<HomeStoreResponse> = MutableLiveData()
    var partnershipNewInfo: MutableLiveData<HomePartnershipResponse> = MutableLiveData()
    var partnershipInfo: MutableLiveData<HomePartnershipResponse> = MutableLiveData()
    var partnershipDetailInfo: MutableLiveData<PartnershipDetailResponse> = MutableLiveData()

    fun getHomeStoreInfo(activity: MainActivity, category: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getHomeStoreInfo(tokenManager.getAccessToken().toString(), tokenManager.getUniversity().toString(), category, null, null)
            .enqueue(object :
                Callback<BaseResponse<HomeStoreResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<HomeStoreResponse>>,
                    response: Response<BaseResponse<HomeStoreResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<HomeStoreResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        storeInfo.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<HomeStoreResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<HomeStoreResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun getHomePartnershipNewInfo(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getHomePartnershipNewInfo(tokenManager.getAccessToken().toString(), tokenManager.getUniversity().toString())
            .enqueue(object :
                Callback<BaseResponse<HomePartnershipResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<HomePartnershipResponse>>,
                    response: Response<BaseResponse<HomePartnershipResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<HomePartnershipResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        partnershipNewInfo.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<HomePartnershipResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<HomePartnershipResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun getHomePartnershipInfo(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getHomePartnershipInfo(tokenManager.getAccessToken().toString(), tokenManager.getUniversity().toString())
            .enqueue(object :
                Callback<BaseResponse<HomePartnershipResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<HomePartnershipResponse>>,
                    response: Response<BaseResponse<HomePartnershipResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<HomePartnershipResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        partnershipInfo.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<HomePartnershipResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<HomePartnershipResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun getPartnershipDetailInfo(activity: MainActivity, id: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getPartnershipDetailInfo(tokenManager.getAccessToken().toString(), id)
            .enqueue(object :
                Callback<BaseResponse<PartnershipDetailResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<PartnershipDetailResponse>>,
                    response: Response<BaseResponse<PartnershipDetailResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<PartnershipDetailResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        partnershipDetailInfo.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<PartnershipDetailResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<PartnershipDetailResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }
}