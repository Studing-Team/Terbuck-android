package com.terbuck.terbuck.ui.mypage

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentMypageNotificationBinding
import com.terbuck.terbuck.ui.MainActivity

class MypageNotificationFragment : Fragment() {

    lateinit var binding: FragmentMypageNotificationBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageNotificationBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNotificationSetting.setOnClickListener {
                // 앱 알림 설정 화면으로 이동
                presentNotificationSetting(mainActivity)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            if(NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()) {
                buttonNotificationSetting.run {
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_5))
                    text = "알림 끄기"
                }
            } else {
                buttonNotificationSetting.run {
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green_50))
                    text = "알림 켜기"
                }
            }

            toolbar.run {
                textViewHead.text = "알림 설정"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun presentNotificationSetting(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationSettingOreo(context)
        } else {
            notificationSettingOreoLess(context)
        }
        try {
            context.startActivity(intent)
        }catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationSettingOreo(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun notificationSettingOreoLess(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo?.uid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}