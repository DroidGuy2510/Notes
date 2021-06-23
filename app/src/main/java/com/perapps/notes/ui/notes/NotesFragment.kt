package com.perapps.notes.ui.notes

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.perapps.notes.R
import com.perapps.notes.adapter.NotesAdapter
import com.perapps.notes.databinding.FragmentAuthBinding
import com.perapps.notes.databinding.FragmentNotesBinding
import com.perapps.notes.other.Constants.NO_EMAIL
import com.perapps.notes.other.Constants.NO_PASSWORD
import com.perapps.notes.other.Status
import com.perapps.notes.prefstore.PrefsStore
import com.perapps.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotesViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter

    @Inject
    lateinit var prefsStore: PrefsStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER

        _binding = FragmentNotesBinding.bind(view)

        setupRecyclerView()
        setListeners()
        subscribeToObservers()
        notesAdapter.setOnItemClickListener {
            navController.navigate(
                NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(it.id)
            )
        }
    }

    private fun setListeners() {
        binding.ibLogout.setOnClickListener {
            logout()
        }
        binding.fabAddNote.setOnClickListener {
            navController.navigate(
                NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(
                    ""
                )
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.allNotes.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        notesAdapter.notes = result.data!!
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackBar(message)
                            }
                        }
                        result.data?.let { notes ->
                            notesAdapter.notes = notes
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        result.data?.let { notes ->
                            notesAdapter.notes = notes
                        }
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() = binding.rvNotes.apply {
        notesAdapter = NotesAdapter()
        adapter = notesAdapter
        layoutManager = LinearLayoutManager(requireContext())
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