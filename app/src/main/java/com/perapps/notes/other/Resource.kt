package com.perapps.notes.other

sealed class Resource{
    data class Success(val data: Any) : Resource()
    data class Error(val msg: String) : Resource()
    object Loading : Resource()
}
