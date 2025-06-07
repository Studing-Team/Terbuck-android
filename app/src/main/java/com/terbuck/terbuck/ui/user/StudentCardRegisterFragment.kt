package com.terbuck.terbuck.ui.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentStudentCardRegisterBinding
import com.terbuck.terbuck.ui.BasicToast
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.utils.MainUtil
import com.terbuck.terbuck.utils.MainUtil.applyWindowInsetsListenerForKeyboard
import com.terbuck.terbuck.utils.MainUtil.hideKeyboard
import com.terbuck.terbuck.viewModel.OnboardingViewModel
import com.terbuck.terbuck.viewModel.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class StudentCardRegisterFragment : Fragment() {

    lateinit var binding: FragmentStudentCardRegisterBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    var isImageUpload = false
    var studentCardImage: MultipartBody.Part? = null

    private lateinit var pickMediaLauncher: androidx.activity.result.ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStudentCardRegisterBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        pickMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                isImageUpload = true

                // 이미지 처리 및 압축
                val resizedUri = MainUtil.resizeImageAndCache(requireActivity(), uri)
                val compressedFile = File(resizedUri.path!!)

                if (compressedFile.exists() && compressedFile.length() > 0) {
                    val requestFile: RequestBody =
                        compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    studentCardImage = MultipartBody.Part.createFormData(
                        "image",
                        compressedFile.name,
                        requestFile
                    )

                    binding.run {
                        imageViewStudentCard.setImageURI(uri)
                        textViewImageDescription.visibility = View.INVISIBLE
                    }
                } else {
                    Log.e("ImageCompression", "압축된 파일이 존재하지 않거나 비어 있습니다.")
                    Toast.makeText(mainActivity, "파일 변환에 실패하였습니다.\n이미지를 다시 업로드해주세요", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        applyWindowInsetsListenerForKeyboard(binding.scrollView)
        BasicToast.showPopupAboveView(requireContext(), "얼굴, 이름, 학번이 보이는 이미지를 넣어주세요", R.drawable.ic_star, binding.buttonRegister)

        binding.run {
            scrollView.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()

                true
            }

            imageViewStudentCard.setOnClickListener {
                pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

                checkComplete()
            }

            editTextName.addTextChangedListener {
                checkComplete()
            }

            editTextStudentId.addTextChangedListener {
                checkComplete()
            }

            buttonRegister.setOnClickListener {
                // 학생증 등록
                viewModel.registerStudentCard(mainActivity, studentCardImage, editTextName.text.toString(), editTextStudentId.text.toString()) {
                    fragmentManager?.popBackStack()
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun checkComplete() {
        binding.run {
            buttonRegister.isEnabled =
                isImageUpload && editTextName.text.isNotEmpty() && editTextStudentId.text.isNotEmpty()
        }
    }

    fun initView() {
        mainActivity.hideBottomNavigation(true)

        binding.run {
            toolbar.run {
                textViewHead.text = "학생증 등록"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}