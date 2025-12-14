package com.terbuck.terbuck.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.ApiClient
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.api.TokenUtil
import com.terbuck.terbuck.api.request.onboarding.FcmRequest
import com.terbuck.terbuck.api.request.onboarding.LoginRequest
import com.terbuck.terbuck.api.request.onboarding.SignUpRequest
import com.terbuck.terbuck.api.request.user.UniversityRequest
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.onboarding.LoginResponse
import com.terbuck.terbuck.api.response.user.CollegeResponse
import com.terbuck.terbuck.api.response.user.UniversityByRegionResponse
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.onboarding.SignUpAgreementFragment
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnboardingViewModel: ViewModel() {
    var universities: MutableLiveData<List<String>> = MutableLiveData()
    var universitiesByRegion: MutableLiveData<List<UniversityByRegionResponse>> = MutableLiveData()

    var colleges: MutableLiveData<List<CollegeResponse>> = MutableLiveData()


    fun getUniversities(activity: MainActivity) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.getUniversities()
            .enqueue(object :
                Callback<BaseResponse<List<String>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<String>>>,
                    response: Response<BaseResponse<List<String>>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<String>>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        universities.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<String>>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<String>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun getUniversityByRegion(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getUniversitiesByRegion(tokenManager.getAccessToken().toString())
            .enqueue(object :
                Callback<BaseResponse<List<UniversityByRegionResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<UniversityByRegionResponse>>>,
                    response: Response<BaseResponse<List<UniversityByRegionResponse>>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<UniversityByRegionResponse>>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        universitiesByRegion.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<UniversityByRegionResponse>>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<UniversityByRegionResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun getColleges(activity: MainActivity, university: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getColleges(tokenManager.getAccessToken().toString(), university)
            .enqueue(object :
                Callback<BaseResponse<List<CollegeResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<CollegeResponse>>>,
                    response: Response<BaseResponse<List<CollegeResponse>>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<CollegeResponse>>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        colleges.value = result?.data
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<CollegeResponse>>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CollegeResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun login(activity: MainActivity, token: String, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.login(LoginRequest(token))
            .enqueue(object :
                Callback<BaseResponse<LoginResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<LoginResponse>>,
                    response: Response<BaseResponse<LoginResponse>>
                ) {
                    Log.d("터벅터벅", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<LoginResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 성공: " + result?.toString())

                        tokenManager.saveTokens("Bearer ${result?.data?.accessToken}", result?.data?.refreshToken.toString())

                        mixpanel.identify(result?.data?.id?.toString(), true)

                        if(result?.data?.redirect == true) {
                            tokenManager.saveIsSignUp(false)
                            // 회원가입 화면 이동
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, SignUpAgreementFragment())
                                .addToBackStack(null)
                                .commit()
                        } else {
                            // 홈화면 이동
                            onSuccess()
                        }

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<LoginResponse>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<LoginResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun signUp(activity: MainActivity, university: String, collegeId: Long, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.signUp(tokenManager.getAccessToken().toString(), SignUpRequest(university, collegeId))
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
                        setFcmToken(activity, MyApplication.preferences.getFCMToken().toString())
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")

                        when(response.code()) {
                            401 -> {
                                TokenUtil.refreshToken(activity) {
                                    signUp(activity, university, collegeId, onSuccess)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun editUniversity(activity: MainActivity, university: String, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.editUniversity(tokenManager.getAccessToken().toString(), UniversityRequest(university))
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

                        when(response.code()) {
                            401 -> {
                                TokenUtil.refreshToken(activity) {
                                    editUniversity(activity, university, onSuccess)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }

    fun setFcmToken(activity: MainActivity, token: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.setFcmToken(tokenManager.getAccessToken().toString(), FcmRequest(token))
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

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        Log.d("터벅터벅", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("터벅터벅", "Error Response: $errorBody")

                        when(response.code()) {
                            401 -> {
                                TokenUtil.refreshToken(activity) {
                                    setFcmToken(activity, token)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("터벅터벅", "onFailure 에러: " + t.message.toString())

                }
            })
    }
}