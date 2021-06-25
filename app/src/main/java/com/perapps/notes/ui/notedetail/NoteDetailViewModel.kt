package com.perapps.notes.ui.notedetail

import androidx.lifecycle.ViewModel
import com.perapps.notes.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    fun observeNoteByNoteId(noteId: String) = repository.observeNoteById(noteId)
}