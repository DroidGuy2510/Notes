package com.perapps.notes.ui.notes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.perapps.notes.R
import com.perapps.notes.databinding.FragmentAuthBinding
import com.perapps.notes.databinding.FragmentNotesBinding
import com.perapps.notes.other.Constants.NO_EMAIL
import com.perapps.notes.other.Constants.NO_PASSWORD
import com.perapps.notes.prefstore.PrefsStore
import com.perapps.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefsStore: PrefsStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNotesBinding.bind(view)
        setListeners()
    }

    private fun setListeners() {
        binding.ibLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            prefsStore.setLoggedInUser(NO_EMAIL, NO_PASSWORD)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.notesFragment, true)
                .build()
            navController.navigate(
                NotesFragmentDirections.actionNotesFragmentToAuthFragment(),
                navOptions
            )
            showSnackBar("Logged Out!!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}