package com.perapps.notes.ui.auth

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.perapps.notes.R
import com.perapps.notes.data.remote.BasicAuthInterceptor
import com.perapps.notes.databinding.FragmentAuthBinding
import com.perapps.notes.other.Status
import com.perapps.notes.prefstore.PrefsStore
import com.perapps.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authInterceptor: BasicAuthInterceptor
    private val viewModel: AuthViewModel by viewModels()

    private var curEmail: String? = null
    private var curPassword: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        _binding = FragmentAuthBinding.bind(view)

        subscribeToObservers()
        setListeners()
    }

    private fun subscribeToObservers() {
        viewModel.registrationStatus.observe(viewLifecycleOwner, { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    progressVisibility(false)
                    showSnackBar(result?.data ?: "Successfully registered account")
                    viewModel.saveLoggedInUser(curEmail!!, curPassword!!)
                    authenticateApi(curEmail ?: "", curPassword ?: "")
                }
                Status.ERROR -> {
                    progressVisibility(false)
                    showSnackBar(result?.message ?: "An Error occurred")
                }
                Status.LOADING -> {
                    progressVisibility(true)
                }
            }
        })
        viewModel.loginStatus.observe(viewLifecycleOwner, { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    progressVisibility(false)
                    showSnackBar(result?.data ?: "Successfully logged in")
                    viewModel.saveLoggedInUser(curEmail!!, curPassword!!)
                    authenticateApi(curEmail ?: "", curPassword ?: "")
                }
                Status.ERROR -> {
                    progressVisibility(false)
                    showSnackBar(result?.message ?: "An Error occurred")
                }
                Status.LOADING -> {
                    progressVisibility(true)
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.btnSignIn.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                signUp()
            }
            false
        }
        binding.btnRegister.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                register()
            }
            false
        }
    }

    private fun register() {
        if (binding.tietEmail.text.toString()
                .isNotBlank() && binding.tietPassword.text.toString()
                .isNotBlank() && binding.tietConfirmPassword.text.toString().isNotBlank()
        ) {
            val email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()
            val confirmPassword = binding.tietPassword.text.toString()

            curEmail = email
            curPassword = password

            viewModel.register(email, password, confirmPassword)
        }
    }

    private fun signUp() {
        if (binding.tietEmail.text.toString()
                .isNotBlank() && binding.tietPassword.text.toString().isNotBlank()
        ) {
            val email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()
            curEmail = email
            curPassword = password

            viewModel.login(email, password)
        }
    }

    private fun authenticateApi(email: String, password: String) {
        authInterceptor.email = email
        authInterceptor.password = password
        redirectLogin()
    }

    private fun redirectLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()
        navController.navigate(
            AuthFragmentDirections.actionAuthFragmentToNotesFragment(),
            navOptions
        )
    }

    private fun progressVisibility(show: Boolean) {
        if (show) {
            binding.flProgress.visibility = View.VISIBLE
        } else {
            binding.flProgress.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}