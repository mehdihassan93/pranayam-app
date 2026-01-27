package com.pranayam.app.data.local

import androidx.room.*
import com.pranayam.app.data.local.dao.MessageDao
import com.pranayam.app.data.local.entity.MessageEntity
import com.pranayam.app.data.model.ContentType
import com.pranayam.app.data.model.MessageStatus
import com.pranayam.app.data.model.MessageType

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
@TypeConverters(PranayamTypeConverters::class)
abstract class PranayamDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}

class PranayamTypeConverters {
    @TypeConverter
    fun fromContentType(value: ContentType) = value.name

    @TypeConverter
    fun toContentType(value: String) = ContentType.valueOf(value)

    @TypeConverter
    fun fromMessageStatus(value: MessageStatus) = value.name

    @TypeConverter
    fun toMessageStatus(value: String) = MessageStatus.valueOf(value)

    @TypeConverter
    fun fromMessageType(value: MessageType) = value.name

    @TypeConverter
    fun toMessageType(value: String) = MessageType.valueOf(value)
}
