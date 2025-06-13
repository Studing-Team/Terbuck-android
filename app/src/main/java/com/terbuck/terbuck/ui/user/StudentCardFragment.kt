package com.terbuck.terbuck.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentStudentCardBinding
import com.terbuck.terbuck.ui.MainActivity
import com.terbuck.terbuck.viewModel.UserViewModel

class StudentCardFragment : DialogFragment() {

    lateinit var binding: FragmentStudentCardBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStudentCardBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonClose.setOnClickListener {
                dismiss()
            }

            buttonReRegister.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, StudentCardRegisterFragment())
                    .addToBackStack(null)
                    .commit()

                dismiss()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            Glide.with(mainActivity)
                .load(viewModel.studentCardImage.value)
                .into(imageViewStudentCard)
        }
    }
}