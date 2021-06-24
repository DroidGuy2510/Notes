package com.perapps.notes.di

import android.content.Context
import androidx.room.Room
import com.perapps.notes.data.local.NoteDao
import com.perapps.notes.data.local.NoteDatabase
import com.perapps.notes.data.local.entities.MIGRATION_1_2
import com.perapps.notes.data.remote.BasicAuthInterceptor
import com.perapps.notes.data.remote.NoteApi
import com.perapps.notes.other.Constants.BASE_URL
import com.perapps.notes.other.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room
            .databaseBuilder(context, NoteDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: NoteDatabase) = database.noteDao()

    @Provides
    @Singleton
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

    @Provides
    @Singleton
    fun provideNoteApi(interceptor: BasicAuthInterceptor): NoteApi {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)


        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(logInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build().create(NoteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope{
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}