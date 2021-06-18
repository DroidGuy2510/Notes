package com.perapps.notes.ui

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.perapps.notes.R

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    val navController by lazy {
        findNavController()
    }

    fun showSnackBar(text: String) {
        Snackbar.make(
            requireActivity().findViewById(R.id.rootLayout),
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}