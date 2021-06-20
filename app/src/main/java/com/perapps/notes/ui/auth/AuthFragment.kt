package com.perapps.notes.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.perapps.notes.R
import com.perapps.notes.databinding.FragmentAuthBinding
import com.perapps.notes.ui.BaseFragment

class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthBinding.bind(view)
        setListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.btnSignIn.setOnTouchListener { _, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP){
                signUp()
            }
            false
        }
        binding.btnRegister.setOnTouchListener { _, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP){
                register()
            }
            false
        }
    }

    private fun register(){
        if (binding.tietEmail.text.toString()
                .isNotBlank() && binding.tietPassword.text.toString().isNotBlank()
        ){
            navController.navigate(AuthFragmentDirections.actionAuthFragmentToNotesFragment())
        }
    }

    private fun signUp(){
        if (binding.tietEmail.text.toString()
                .isNotBlank() && binding.tietPassword.text.toString().isNotBlank()
        ){
            navController.navigate(AuthFragmentDirections.actionAuthFragmentToNotesFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}