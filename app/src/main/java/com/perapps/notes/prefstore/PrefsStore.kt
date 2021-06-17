package com.perapps.notes.prefstore

import kotlinx.coroutines.flow.Flow

interface PrefsStore {

    fun loggedUser(): Flow<String>

    suspend fun setLoggedInUser(email: String)
}