package com.perapps.notes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.perapps.notes.data.local.entities.LocallyDeletedNoteID
import com.perapps.notes.data.local.entities.Note

@Database(
    entities = [Note::class, LocallyDeletedNoteID::class],
    version = 2, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
}