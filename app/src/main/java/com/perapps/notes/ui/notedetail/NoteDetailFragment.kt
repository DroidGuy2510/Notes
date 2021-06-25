package com.perapps.notes.ui.notedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.perapps.notes.R
import com.perapps.notes.data.local.entities.Note
import com.perapps.notes.databinding.FragmentNoteDetailBinding
import com.perapps.notes.databinding.FragmentNotesBinding
import com.perapps.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment(R.layout.fragment_note_detail) {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteDetailViewModel by viewModels()

    private var curNote: Note? = null

    private val args: NoteDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNoteDetailBinding.bind(view)
        seListeners()
        handleObservers()
    }

    private fun handleObservers() {
        viewModel.observeNoteByNoteId(args.id).observe(viewLifecycleOwner, {
            it?.let { note ->
                binding.tvNoteTitle.text = note.title
                setMarkdownText(note.content)
                curNote = note
            } ?: showSnackBar("Note not found")
        })
    }

    private fun seListeners() {
        binding.fabEditNote.setOnClickListener {
            navController.navigate(
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id)
            )
        }
    }

    private fun setMarkdownText(text: String) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(binding.tvNoteContent, markdown)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}