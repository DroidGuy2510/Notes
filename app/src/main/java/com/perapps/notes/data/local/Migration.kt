package com.perapps.notes.data.local.entities

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `locally_deleted_note_ids` (`deletedNoteId` TEXT NOT NULL, PRIMARY KEY(`deletedNoteId`))")
    }
}
