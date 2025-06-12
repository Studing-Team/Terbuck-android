package com.terbuck.terbuck.ui

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.ActivityMainBinding
import com.terbuck.terbuck.ui.home.HomeFragment
import com.terbuck.terbuck.ui.terbuck.MapFragment
import com.terbuck.terbuck.utils.MyApplication
import com.terbuck.terbuck.utils.PreferenceUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        MyApplication.preferences = PreferenceUtil(applicationContext)

        setBottomNavigationView()

        binding.bottomNavBar.itemIconTintList = null

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(false)
    }

    private fun setBottomNavigationView() {
        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_partnership -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, MapFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_mypage -> {

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