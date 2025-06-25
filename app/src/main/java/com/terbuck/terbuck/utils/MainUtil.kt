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
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

object MainUtil {
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    // 투명한 status bar
    fun Activity.setStatusBarTransparent() {
        // 상태바를 투명하게 설정하고, 레이아웃을 상태바까지 확장
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        if (Build.VERSION.SDK_INT >= 30) {  // API 30 이상
//            WindowCompat.setDecorFitsSystemWindows(window, false)

            // 레이아웃이 상태바를 침범하되, 네비게이션 바는 침범하지 않도록 설정
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                val systemBarsInsets = insets.getInsets(WindowInsets.Type.systemBars())
                val statusBarHeight = systemBarsInsets.top
                val navigationBarHeight = systemBarsInsets.bottom

                // 상태바 위로부터 시작 (상단 패딩 설정)
                view.setPadding(0, statusBarHeight, 0, 0)

                // 네비게이션 바를 침범하지 않도록 하단 패딩 설정
                view.setPadding(0, 0, 0, navigationBarHeight)

                insets
            }
        } else {
            // API 29 이하: 상태바는 침범, 네비게이션 바는 그대로
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  // 상태바를 침범
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 네비게이션 바는 그대로
                    )
        }
    }

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