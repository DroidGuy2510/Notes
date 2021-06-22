package com.perapps.notes.prefstore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.perapps.notes.other.Constants.KEY_LOGGED_IN_EMAIL
import com.perapps.notes.other.Constants.KEY_PASSWORD
import com.perapps.notes.other.Constants.NO_EMAIL
import com.perapps.notes.other.Constants.NO_PASSWORD
import com.perapps.notes.other.Constants.STORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = STORE_NAME
)

class PrefsStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val LOGGED_IN_USER_EMAIL = stringPreferencesKey(KEY_LOGGED_IN_EMAIL)
        val LOGGED_IN_USER_PASSWORD = stringPreferencesKey(KEY_PASSWORD)
    }

    fun loggedUser(): Flow<Preferences> = context.dataStore.data

    suspend fun setLoggedInUser(email: String, password: String) {
        context.dataStore.edit {
            it[LOGGED_IN_USER_EMAIL] = email
            it[LOGGED_IN_USER_PASSWORD] = password
        }
    }
}