package com.terbuck.terbuck.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.ApiClient
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.api.request.onboarding.LoginRequest
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.home.HomeStoreResponse
import com.terbuck.terbuck.api.response.onboarding.LoginResponse
import com.terbuck.terbuck.api.response.user.StudentCardResponse
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.home.HomeFragment
import com.terbuck.terbuck.ui.onboarding.SignUpAgreementFragment
import com.terbuck.terbuck.utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel: ViewModel() {
    var studentCardImage: MutableLiveData<String?> = MutableLiveData()
    var studentNumber: MutableLiveData<String?> = MutableLiveData()
    var name: MutableLiveData<String?> = MutableLiveData()

    fun registerStudentCard(activity: MainActivity, image: MultipartBody.Part?, name: String, studentId: String, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        val params = HashMap<String, RequestBody>()

        params["name"] =
            name.toRequestBody("text/plain".toMediaTypeOrNull())
        params["studentNumber"] = studentId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        apiClient.apiService.registerStudentCard(tokenManager.getAccessToken().toString(), image, params)
            .enqueue(object :
                Callback<BaseResponse<String?>> {
                override fun onResponse(
                    call: Call<BaseResponse<String?>>,
                    response: Response<BaseResponse<String?>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String?>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        onSuccess()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun getStudentCard(activity: MainActivity, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getStudentCard(tokenManager.getAccessToken().toString())
            .enqueue(object :
                Callback<BaseResponse<StudentCardResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<StudentCardResponse>>,
                    response: Response<BaseResponse<StudentCardResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<StudentCardResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        MyApplication.isRegisterStudentCard = result?.data?.isRegistered == true
                        TokenManager(activity).saveUniversity(result?.data?.university.toString())

                        if(result?.data?.imageURL != null) {
                            studentCardImage.value = result.data.imageURL
                            studentNumber.value = result.data.studentNumber
                            name.value = result.data.name
                            onSuccess()
                        } else {
                            onFailure()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<StudentCardResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")

                        when(response.code()) {
                            400 -> { onFailure() }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<StudentCardResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun withdrawal(activity: MainActivity, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.withdrawal(tokenManager.getAccessToken().toString())
            .enqueue(object :
                Callback<BaseResponse<String?>> {
                override fun onResponse(
                    call: Call<BaseResponse<String?>>,
                    response: Response<BaseResponse<String?>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String?>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        onSuccess()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

}