package com.pranayam.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranayam.app.data.model.ContentType
import com.pranayam.app.data.model.MessageStatus
import com.pranayam.app.data.model.MessageType

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val text: String,
    val timestamp: String,
    val isSent: Boolean,
    val contentType: ContentType,
    val status: MessageStatus,
    val imageUrl: String? = null,
    val voiceUrl: String? = null,
    val duration: String? = null,
    val type: MessageType = MessageType.REGULAR
)
