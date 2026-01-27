package com.pranayam.app.data.local

import androidx.room.TypeConverter
import com.pranayam.app.data.model.ContentType
import com.pranayam.app.data.model.MessageStatus
import com.pranayam.app.data.model.MessageType

class Converters {
    @TypeConverter
    fun fromContentType(value: ContentType): String = value.name

    @TypeConverter
    fun toContentType(value: String): ContentType = ContentType.valueOf(value)

    @TypeConverter
    fun fromMessageStatus(value: MessageStatus): String = value.name

    @TypeConverter
    fun toMessageStatus(value: String): MessageStatus = MessageStatus.valueOf(value)

    @TypeConverter
    fun fromMessageType(value: MessageType): String = value.name

    @TypeConverter
    fun toMessageType(value: String): MessageType = MessageType.valueOf(value)
}
