package com.pranayam.app.data.model

import com.google.gson.annotations.SerializedName

data class Profile(
    val id: String,
    val name: String,
    val age: Int?,
    val photos: List<String>,
    val videoUrl: String? = null,
    @SerializedName("jobTitle") val profession: String? = null,
    val city: String? = null,
    val distance: Int? = null,
    val isVerified: Boolean = false,
    val hasVideo: Boolean = false,
    val prompts: List<Prompt> = emptyList(),
    val bio: String? = null,
    val height: Int? = null,
    val education: String? = null,
    val languages: List<String> = emptyList(),
    val isGuestProfile: Boolean = false
)

data class Prompt(
    val question: String,
    val answer: String
)

data class Message(
    val id: String,
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

enum class ContentType {
    TEXT, IMAGE, VOICE, VIDEO
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ, FAILED
}

enum class MessageType {
    REGULAR, DATE_SEPARATOR, SYSTEM
}

data class Conversation(
    val id: String,
    val name: String,
    val age: Int,
    val photoUrl: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val isVerified: Boolean
)

data class Match(
    val id: String,
    val name: String,
    val photoUrl: String,
    val isUnread: Boolean
)
