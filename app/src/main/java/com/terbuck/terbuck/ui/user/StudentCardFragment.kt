package com.terbuck.terbuck.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.terbuck.terbuck.R
import com.terbuck.terbuck.databinding.FragmentStudentCardBinding
import com.terbuck.terbuck.ui.MainActivity

class StudentCardFragment : DialogFragment() {

    lateinit var binding: FragmentStudentCardBinding
    lateinit var mainActivity: MainActivity

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


}