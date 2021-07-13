package com.perapps.notes.ui.notedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perapps.notes.other.Event
import com.perapps.notes.other.Resource
import com.perapps.notes.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _addOwnerStatus = MutableLiveData<Event<Resource<String>>>()
    val addOwnerStatus: LiveData<Event<Resource<String>>> = _addOwnerStatus

    fun observeNoteByNoteId(noteId: String) = repository.observeNoteById(noteId)

    fun addOwnerToNote(owner: String, noteId: String) {
        _addOwnerStatus.postValue(Event(Resource.loading(null)))
        if (owner.isEmpty() || noteId.isEmpty()) {
            _addOwnerStatus.postValue(
                Event(
                    Resource
                        .error("Owner Email cannot be empty", null)
                )
            )
            return
        }
        viewModelScope.launch {
            val result = repository.addOwnerToNote(owner, noteId)
            _addOwnerStatus.postValue(Event(result))
        }
    }
}