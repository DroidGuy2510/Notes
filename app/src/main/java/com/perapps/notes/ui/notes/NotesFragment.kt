package com.perapps.notes.ui.notes

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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

    private val isSwiping = MutableLiveData(false)

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

    private val swipeItemCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.START or ItemTouchHelper.END
    ) {

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if(isCurrentlyActive){
                isSwiping.postValue(true)
            }else{
                isSwiping.postValue(false)
            }
        }
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = notesAdapter.notes[position]
            viewModel.deleteNote(note.id)
            Snackbar.make(requireView(), "Successfully deleted note", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.insertNote(note)
                    viewModel.deleteLocallyDeletedNoteId(note.id)
                }
                show()
            }
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
        isSwiping.observe(viewLifecycleOwner, {
            binding.swipeRefreshLayout.isEnabled = !it
        })
    }

    private fun setupRecyclerView() = binding.rvNotes.apply {
        notesAdapter = NotesAdapter()
        adapter = notesAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(swipeItemCallback).attachToRecyclerView(this)
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