package com.perapps.notes.data.remote.requests

data class AddOwnerRequest(
    val owner: String,
    val noteId: String
)
