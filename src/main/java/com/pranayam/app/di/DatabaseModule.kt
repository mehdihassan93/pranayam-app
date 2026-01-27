package com.pranayam.app.di

import android.content.Context
import androidx.room.Room
import com.pranayam.app.data.local.PranayamDatabase
import com.pranayam.app.data.local.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PranayamDatabase {
        return Room.databaseBuilder(
            context,
            PranayamDatabase::class.java,
            "pranayam_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMessageDao(database: PranayamDatabase): MessageDao {
        return database.messageDao()
    }
}
