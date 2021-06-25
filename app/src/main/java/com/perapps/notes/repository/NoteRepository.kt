package com.perapps.notes.repository

import android.app.Application
import android.content.Context
import com.perapps.notes.data.local.NoteDao
import com.perapps.notes.data.local.entities.LocallyDeletedNoteID
import com.perapps.notes.data.local.entities.Note
import com.perapps.notes.data.remote.NoteApi
import com.perapps.notes.data.remote.requests.AccountRequest
import com.perapps.notes.data.remote.requests.DeleteNoteRequest
import com.perapps.notes.other.Resource
import com.perapps.notes.other.isConnectedToInternet
import com.perapps.notes.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {

    private var currentResponse: Response<List<Note>>? = null

    suspend fun insertNote(note: Note) {

        val response = try {
            noteApi.addNote(note)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            noteDao.insertNote(note.apply { isSynced = true })
        } else {
            noteDao.insertNote(note)
        }
    }

    private suspend fun insertNotes(notes: List<Note>) {
        notes.forEach { insertNote(it) }
    }

    suspend fun getNoteById(noteId: String): Note? {
        return noteDao.getNoteById(noteId)
    }

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                syncNotes()
                currentResponse
            },
            saveFetchedData = { response ->
                response?.body()?.let {
                    insertNotes(it.onEach { note -> note.isSynced = true })
                }
            },
            shouldFetch = {
                isConnectedToInternet(context)
            }
        )
    }

    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.register(AccountRequest(email, password))
            if (response.isSuccessful && response.body()!!.success) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Can`t connect to server", null)
        }
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.login(AccountRequest(email, password))
            if (response.isSuccessful && response.body()!!.success) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Can`t connect to server", null)
        }
    }

    suspend fun deleteNote(noteId: String) {
        val response = try {
            noteApi.deleteNote(DeleteNoteRequest(noteId))
        } catch (e: java.lang.Exception) {
            null
        }
        noteDao.deleteNoteById(noteId)
        if (response == null || !response.isSuccessful) {
            noteDao.insertLocallyDeletedNoteId(LocallyDeletedNoteID(noteId))
        } else {
            deleteLocallyDeletedNoteId(noteId)
        }
    }

    suspend fun deleteLocallyDeletedNoteId(deleteNoteId: String) {
        noteDao.deleteLocallyDeletedNoteId(deleteNoteId)
    }

    private suspend fun syncNotes() {
        val locallyDeletedNote = noteDao.getAllLocallyDeletedNoteIds()
        locallyDeletedNote.forEach { deletedNote ->
            deleteNote(deletedNote.deletedNoteId)
        }

        val unSyncedNotes = noteDao.getAllUnSyncedNotes()
        unSyncedNotes.forEach { notes -> insertNote(notes) }

        currentResponse = noteApi.getAllNotes()
        currentResponse?.body()?.let { notes ->
            noteDao.deleteAllNotes()
            insertNotes(notes.onEach { it.isSynced = true })        // insert to dao could be used
        }
    }

    fun observeNoteById(noteId: String) = noteDao.observeNoteById(noteId)
}