package com.terbuck.terbuck.utils

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

object MainUtil {
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    // 스크롤뷰 스크롤 & 키보드 동작
    fun applyWindowInsetsListenerForKeyboard(
        scrollView: ViewGroup,
        basePaddingBottomDp: Int = 10
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { _, insets ->
            val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val basePaddingPx = basePaddingBottomDp.toPx()

            val keyboardPadding = (imeHeight - sysBarInsets.bottom).coerceAtLeast(0)

            scrollView.setPadding(
                scrollView.paddingLeft,
                scrollView.paddingTop,
                scrollView.paddingRight,
                basePaddingPx + keyboardPadding
            )

            insets
        }
    }

    // 키보드 숨김 처리
    fun Activity.hideKeyboard() {
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocusView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    // 카테고리 변환
    fun getCategoryIndex(category: String?): Int {
        return when (category) {
            "음식" -> 0
            "카페" -> 1
            "주점" -> 2
            "병원" -> 3
            "운동" -> 4
            "문화" -> 5
            "스터디" -> 6
            else -> 0
        }
    }

    fun getDrawableResIds(arrayResId: Int, resources: Resources): List<Int> {
        val typedArray = resources.obtainTypedArray(arrayResId)
        val resIds = mutableListOf<Int>()
        for (i in 0 until typedArray.length()) {
            resIds.add(typedArray.getResourceId(i, 0))
        }
        typedArray.recycle()
        return resIds
    }

    fun resizeImageAndCache(context: Context, imageUri: Uri, scale: Float = 0.5f, quality: Int = 90): Uri {
        val contentResolver = context.contentResolver
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        // 이미지 리사이즈 (기본: 절반 크기)
        val resizedBitmap =
            bitmap.scale((bitmap.width * scale).toInt(), (bitmap.height * scale).toInt())

        // 임시 파일 생성
        val tempFile = File.createTempFile("resized_image", ".jpg", context.cacheDir)

        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(byteArrayOutputStream.toByteArray())
                outputStream.flush()
            }

        } catch (e: Exception) {
            Log.e("ImageResize", "Failed to write file: ${e.message}")
        } finally {
            resizedBitmap.recycle()
        }

        return Uri.fromFile(tempFile)
    }
}