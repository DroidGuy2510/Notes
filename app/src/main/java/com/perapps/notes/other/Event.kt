package com.perapps.notes.other

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
    private set                             // value can be changed from this class only

    fun getContentIfHandled() = if(hasBeenHandled){
        null
    }else{
        hasBeenHandled = true
        content
    }

    fun peekContent() = content
}