package com.terbuck.terbuck.api

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.request.user.RefreshTokenRequest
import com.terbuck.terbuck.api.response.BaseResponse
import com.terbuck.terbuck.api.response.user.RefreshTokenResponse
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.ui.onboarding.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TokenUtil {
    fun refreshToken(activity: MainActivity, retryRequest: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.refreshToken(RefreshTokenRequest(tokenManager.getRefreshToken().toString()))
            .enqueue(object : Callback<BaseResponse<RefreshTokenResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<RefreshTokenResponse>>,
                    response: Response<BaseResponse<RefreshTokenResponse>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        when(result?.status) {
                            200 -> {
                                tokenManager.saveTokens(
                                    "Bearer ${result.data?.accessToken}",
                                    result.data?.refreshToken.toString()
                                )

                                retryRequest()
                            }

                            400 -> {
                                TokenManager(activity).clearAll()

                                activity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                activity.supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView, LoginFragment())
                                    .commit()
                            }
                        }
                    } else {
                        Log.e("TokenUtil", "재발급 실패: ${response.errorBody()?.string()}")

                        TokenManager(activity).clearAll()

                        activity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, LoginFragment())
                            .commit()
                    }
                }

                override fun onFailure(call: Call<BaseResponse<RefreshTokenResponse>>, t: Throwable) {
                    Log.e("TokenUtil", "onFailure: ${t.message}")

                    TokenManager(activity).clearAll()

                    activity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, LoginFragment())
                        .commit()
                }
            })
    }
}
