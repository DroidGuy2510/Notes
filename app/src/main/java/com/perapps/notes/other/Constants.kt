package com.perapps.notes.other

object Constants {

    const val DATABASE_NAME = "notes_db"
    const val STORE_NAME = "notes_data_store"

    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_PASSWORD = "KEY_PASSWORD"
    const val NO_EMAIL = "NO_EMAIL"
    const val NO_PASSWORD = "NO_PASSWORD"

    const val DEFAULT_NOTE_COLOR = "#FFA500"

    //const val BASE_URL = "http://10.0.2.2:8080"     for testing on emulator
    const val BASE_URL = "http://192.168.13.25:8080"

    val IGNORE_PATH_URLS = listOf("/login", "/register")
}