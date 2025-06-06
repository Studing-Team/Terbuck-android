package com.terbuck.terbuck.ui

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.ToastBasicBinding

object BasicToast {
    fun showPopupAboveView(context: Context, message: String, icon: Int, anchorView: View) {
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


    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}