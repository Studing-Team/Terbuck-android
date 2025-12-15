package com.terbuck.terbuck.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.terbuck.terbuck.R
import com.terbuck.terbuck.api.TokenManager
import com.terbuck.terbuck.databinding.ActivityMainBinding
import com.terbuck.terbuck.ui.home.HomeEmptyFragment
import com.terbuck.terbuck.ui.home.HomeFragment
import com.terbuck.terbuck.ui.mypage.MypageFragment
import com.terbuck.terbuck.ui.mypage.MypageNotificationFragment
import com.terbuck.terbuck.ui.terbuck.MapFragment
import com.terbuck.terbuck.ui.user.StudentCardRegisterFragment
import com.terbuck.terbuck.utils.GlobalApplication.Companion.mixpanel
import com.terbuck.terbuck.utils.MainUtil.setStatusBarTransparent
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.utils.PreferenceUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferenceManager: PreferenceUtil

    private var backPressedTime: Long = 0
    private val FINISH_INTERVAL_TIME: Long = 2000 // 2초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent)

        binding = ActivityMainBinding.inflate(layoutInflater)
        MyApplication.preferences = PreferenceUtil(applicationContext)

        setBottomNavigationView()
        setFCMToken()

        binding.bottomNavBar.itemIconTintList = null

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        setStatusBarTransparent()
        hideBottomNavigation(false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun setBottomNavigationView() {
        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    mixpanel.track("click_bottom_tab_home", null)

                    if(TokenManager(this).getIsUniversityRegistered()) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, HomeFragment())
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, HomeEmptyFragment())
                            .commit()
                    }

                    true
                }

                R.id.menu_partnership -> {
                    mixpanel.track("click_bottom_tab_terbuck", null)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, MapFragment())
                        .commit()
                    true
                }

                R.id.menu_mypage -> {
                    mixpanel.track("click_bottom_tab_mypage", null)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, MypageFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }

    fun setBottomNavigationHome() {
        binding.bottomNavBar.selectedItemId = R.id.menu_home
    }

    fun hideBottomNavigation(isHide: Boolean) {
        binding.bottomNavBar.visibility = if(isHide) View.GONE else View.VISIBLE
    }

    private fun handleNotificationIntent(intent: Intent) {
        val fromNotification = intent.getBooleanExtra("notification", false)
        if (fromNotification) {
            mixpanel.track("click_push_alarm", null)
        }
    }

    fun setFCMToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM Token", "$token")
            MyApplication.preferences.setFCMToken(token)
            Log.d("FCM Token", "FCM 토큰 : ${MyApplication.preferences.getFCMToken()}")

            if (this::sharedPreferenceManager.isInitialized) {
                Log.d("FCM Token", "this::sharedPreferenceManager.isInitialized")
                sharedPreferenceManager.setFCMToken(token)
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)

        // 백스택이 비어있고, 현재 화면이 첫 화면일 경우 → 두 번 눌러 종료 로직
        if (supportFragmentManager.backStackEntryCount == 0 && (currentFragment is HomeFragment || currentFragment is MapFragment || currentFragment is MypageFragment)) {
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime

            if (intervalTime in 0..FINISH_INTERVAL_TIME) {
                super.onBackPressed() // 앱 종료
            } else {
                backPressedTime = tempTime
                Toast.makeText(this, "뒤로가기를 한 번 더 누르면 앱이 종료됩니다", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 기본 뒤로가기 동작
            super.onBackPressed()
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            for (signature in info.signingInfo?.apkContentsSigners!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", keyHash)  // 키 해시를 로그로 출력
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash", "Unable to get MessageDigest. signature= $e")
        } catch (e: Exception) {
            Log.e("KeyHash", "Exception: $e")
        }
    }
}