package com.pranayam.app.api

import com.google.gson.annotations.SerializedName

/**
 * Raw message shape from the backend API.
 * Maps to the MongoDB Message schema.
 */
data class ApiMessageDto(
    @SerializedName("_id") val id: String,
    val senderId: String? = null,
    val content: String? = null,
    val type: String? = null,
    val mediaUrl: String? = null,
    val duration: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Raw conversation shape from the backend API.
 * participants and lastMessage are populated by Mongoose.
 */
data class ApiConversationDto(
    @SerializedName("_id") val id: String,
    val participants: List<ApiParticipantDto> = emptyList(),
    val lastMessage: ApiMessageDto? = null,
    val unreadCounts: Map<String, Int>? = null,
    val updatedAt: String? = null
)

data class ApiParticipantDto(
    @SerializedName("_id") val id: String,
    val name: String? = null,
    val photoUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: String? = null
)
