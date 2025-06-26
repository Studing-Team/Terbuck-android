package com.terbuck.terbuck.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.ToastBasicBinding
import com.terbuck.terbuck.databinding.ToastBasicButtonBinding
import com.terbuck.terbuck.ui.user.StudentCardRegisterFragment
import com.terbuck.terbuck.utils.MainUtil.toPx

object BasicToast {
    fun showBasicToast(context: Context, message: String, icon: Int, anchorView: View) {
        val inflater = LayoutInflater.from(context)
        val binding: ToastBasicBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_basic, null, false)

        binding.run {
            textViewTooltip.text = message
            imageViewTooltip.setImageResource(icon)
        }

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val marginPx = 20.toPx()
        val popupWidth = screenWidth - marginPx * 2

        val popupWindow = PopupWindow(binding.root,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )

        anchorView.post {
            binding.root.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            val popupHeight = binding.root.measuredHeight

            val yOffset = -(anchorView.height + popupHeight + 8.toPx())

            popupWindow.showAsDropDown(anchorView, 0, yOffset)

            binding.root.postDelayed({
                popupWindow.dismiss()
            }, 2000)
        }
    }

    fun showBasicButtonToast(
        context: Context,
        activity: MainActivity,
        message: String,
        icon: Int,
        buttonText: String,
        anchorView: View,
        rootView: View,
        fragment: Fragment
    ) {
        val inflater = LayoutInflater.from(context)
        val binding: ToastBasicButtonBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_basic_button, null, false)

        binding.run {
            textViewTooltip.text = message
            imageViewTooltip.setImageResource(icon)
            buttonRegister.run {
                text = buttonText
                paintFlags = Paint.UNDERLINE_TEXT_FLAG
                setOnClickListener {
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val marginPx = 20.toPx()
        val popupWidth = screenWidth - marginPx * 2

        val popupWindow = PopupWindow(
            binding.root,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )

        // Fragment 전환 시 Popup 자동 dismiss
        val backStackListener = object : androidx.fragment.app.FragmentManager.OnBackStackChangedListener {
            override fun onBackStackChanged() {
                popupWindow.dismiss()
                activity.supportFragmentManager.removeOnBackStackChangedListener(this)
            }
        }

        activity.supportFragmentManager.addOnBackStackChangedListener(backStackListener)

        anchorView.post {
            binding.root.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            val popupHeight = binding.root.measuredHeight
            val popupWidth = binding.root.measuredWidth

            val yOffset = -(anchorView.height + popupHeight + 8.toPx())
            val xOffset = (20.toPx())

            popupWindow.showAsDropDown(anchorView, xOffset, yOffset)

            binding.root.postDelayed({
                popupWindow.dismiss()
                activity.supportFragmentManager.removeOnBackStackChangedListener(backStackListener)
            }, 5000)
        }
    }

    fun showBasicButtonToastFragment(
        context: Context,
        activity: MainActivity,
        message: String,
        icon: Int,
        buttonText: String,
        anchorView: View,
        rootView: View,
        fragment: Fragment
    ) {
        val inflater = LayoutInflater.from(context)
        val binding: ToastBasicButtonBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_basic_button, null, false)

        binding.run {
            textViewTooltip.text = message
            imageViewTooltip.setImageResource(icon)
            buttonRegister.run {
                text = buttonText
                paintFlags = Paint.UNDERLINE_TEXT_FLAG
                setOnClickListener {
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val marginPx = 20.toPx()
        val popupWidth = screenWidth - marginPx * 2

        val popupWindow = PopupWindow(
            binding.root,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )

        // Fragment 전환 시 Popup 자동 dismiss
        val backStackListener = object : androidx.fragment.app.FragmentManager.OnBackStackChangedListener {
            override fun onBackStackChanged() {
                popupWindow.dismiss()
                activity.supportFragmentManager.removeOnBackStackChangedListener(this)
            }
        }

        activity.supportFragmentManager.addOnBackStackChangedListener(backStackListener)

        anchorView.post {
            binding.root.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            val popupHeight = binding.root.measuredHeight
            val popupWidth = binding.root.measuredWidth

            val yOffset = -(anchorView.height + popupHeight + 8.toPx())
            val xOffset = (rootView.width / 2 - popupWidth / 2 - 10.toPx())

            popupWindow.showAsDropDown(anchorView, xOffset, yOffset)

            binding.root.postDelayed({
                popupWindow.dismiss()
                activity.supportFragmentManager.removeOnBackStackChangedListener(backStackListener)
            }, 5000)
        }
    }
}