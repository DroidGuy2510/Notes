package com.perapps.notes.prefstore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

class PrefsStoreImpl @Inject constructor(
    @ApplicationContext val context: Context
) : PrefsStore {

    companion object {
        val LOGGED_IN_USER_KEY = stringPreferencesKey("logged_user")
    }

    override fun loggedUser(): Flow<String> = context.dataStore.data.catch{ exception -> // 1
        // dataStore.data throws an IOException if it can't read the data
        if (exception is IOException) { // 2
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[LOGGED_IN_USER_KEY] ?: " "
    }

    override suspend fun setLoggedInUser(email: String) {
        context.dataStore.edit {
            it[LOGGED_IN_USER_KEY] = email
        }
    }

}