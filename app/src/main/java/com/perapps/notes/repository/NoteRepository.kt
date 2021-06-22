package com.perapps.notes.repository

import android.app.Application
import android.content.Context
import com.perapps.notes.data.local.NoteDao
import com.perapps.notes.data.local.entities.Note
import com.perapps.notes.data.remote.NoteApi
import com.perapps.notes.data.remote.requests.AccountRequest
import com.perapps.notes.other.Resource
import com.perapps.notes.other.isConnectedToInternet
import com.perapps.notes.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                noteApi.getAllNotes()
            },
            saveFetchedData = { response ->
                response.body()?.let {
                    // TODO: insert data to db
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
}