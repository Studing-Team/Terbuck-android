package com.terbuck.terbuck.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terbuck.terbuck.api.ApiClient
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.api.request.onboarding.SignUpRequest
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.home.HomeStoreResponse
import com.terbuck.terbuck.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {
    var partnershipStoreInfo: MutableLiveData<HomeStoreResponse> = MutableLiveData()

    fun getHomeStoreInfo(activity: MainActivity, category: String, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getHomeStoreInfo(tokenManager.getAccessToken().toString(), "서울과학기술대학교", category, null, null)
            .enqueue(object :
                Callback<BaseResponse<HomeStoreResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<HomeStoreResponse>>,
                    response: Response<BaseResponse<HomeStoreResponse>>
                ) {
                    Log.d("TerbuckTerbuck", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<HomeStoreResponse>? = response.body()
                        Log.d("TerbuckTerbuck", "onResponse 성공: " + result?.toString())

                        partnershipStoreInfo.value = result?.data

                        onSuccess()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<HomeStoreResponse>? = response.body()
                        Log.d("TerbuckTerbuck", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("TerbuckTerbuck", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<HomeStoreResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("TerbuckTerbuck", "onFailure 에러: " + t.message.toString())

                }
            })
    }
}