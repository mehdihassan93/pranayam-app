package com.pranayam.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pranayam.app.data.local.dao.MessageDao
import com.pranayam.app.data.local.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PranayamDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
