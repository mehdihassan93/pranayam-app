package com.pranayam.app.repository

import com.pranayam.app.api.ApiMessageDto
import com.pranayam.app.api.ApiConversationDto
import com.pranayam.app.api.BlockUserRequest
import com.pranayam.app.api.PranayamApiService
import com.pranayam.app.api.ReportUserRequest
import com.pranayam.app.api.SendMessageRequest
import com.pranayam.app.data.local.entity.MessageEntity
import com.pranayam.app.data.local.dao.MessageDao
import com.pranayam.app.data.model.ContentType
import com.pranayam.app.data.model.Conversation
import com.pranayam.app.data.model.Message
import com.pranayam.app.data.model.MessageStatus
import com.pranayam.app.data.model.MessageType
import com.pranayam.app.data.model.Profile
import com.pranayam.app.di.UserSessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PranayamRepository @Inject constructor(
    private val apiService: PranayamApiService,
    private val messageDao: MessageDao,
    private val sessionManager: UserSessionManager
) {
    private val userId: String
        get() = sessionManager.getUserId() ?: ""

    // Discovery
    fun getDiscoveryProfiles(lat: Double?, long: Double?, isGuest: Boolean = false): Flow<Result<List<Profile>>> = flow {
        try {
            val response = if (isGuest) {
                apiService.getGuestProfiles(lat, long)
            } else {
                apiService.getDiscoveryProfiles(lat, long)
            }
            if (response.isSuccessful) {
                emit(Result.success(response.body() ?: emptyList()))
            } else {
                emit(Result.failure(Exception("Failed to fetch profiles: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun swipeProfile(targetId: String, type: String): Result<com.pranayam.app.data.model.LikeResponse> {
        return try {
            val response = apiService.swipeProfile(com.pranayam.app.api.SwipeRequest(targetId, type))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getConversations(): Flow<Result<List<Conversation>>> = flow {
        try {
            val response = apiService.getConversations()
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val conversations = dtos.map { it.toDomain(userId) }
                emit(Result.success(conversations))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Persist a single incoming message from socket into local DB
    suspend fun persistMessage(conversationId: String, message: Message) {
        messageDao.insertMessage(message.toEntity(conversationId))
    }

    // Chat Logic with Offline Support
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun refreshMessages(conversationId: String): Result<Unit> {
        return try {
            val response = apiService.getMessages(conversationId)
            val dtos = response.body()
            if (response.isSuccessful && dtos != null) {
                val messages = dtos.map { it.toDomain(userId) }
                val entities = messages.map { it.toEntity(conversationId) }
                messageDao.deleteMessagesForConversation(conversationId)
                messageDao.insertMessages(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to refresh messages: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(conversationId: String, text: String): Result<Message> {
        // Create a temporary message for optimistic UI
        val tempId = java.util.UUID.randomUUID().toString()
        val tempMessage = Message(
            id = tempId,
            text = text,
            timestamp = "Just now",
            isSent = true,
            contentType = ContentType.TEXT,
            status = MessageStatus.SENDING
        )

        // Save locally first for instant feedback
        messageDao.insertMessage(tempMessage.toEntity(conversationId))

        return try {
            val response = apiService.sendMessage(conversationId, SendMessageRequest(content = text))
            val dto = response.body()
            if (response.isSuccessful && dto != null) {
                val sentMessage = dto.toDomain(userId)
                // Update local storage with real message
                messageDao.deleteMessage(tempId) // remove temp by ID
                messageDao.insertMessage(sentMessage.toEntity(conversationId))
                Result.success(sentMessage)
            } else {
                // Update status to FAILED
                messageDao.updateMessageStatus(tempId, MessageStatus.FAILED)
                Result.failure(Exception("Send failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            messageDao.updateMessageStatus(tempId, MessageStatus.FAILED)
            Result.failure(e)
        }
    }

    // Safety
    suspend fun reportUser(reportedUserId: String, reason: String, description: String = ""): Result<String> {
        return try {
            val response = apiService.reportUser(ReportUserRequest(reportedUserId, reason, description))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Report submitted")
            } else {
                Result.failure(Exception("Failed to report: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun blockUser(blockedUserId: String): Result<String> {
        return try {
            val response = apiService.blockUser(BlockUserRequest(blockedUserId))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "User blocked")
            } else {
                Result.failure(Exception("Failed to block: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<String> {
        return try {
            val response = apiService.deleteAccount()
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Account deleted")
            } else {
                Result.failure(Exception("Failed to delete account: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Converters
    private fun Message.toEntity(conversationId: String) = MessageEntity(
        id = id,
        conversationId = conversationId,
        text = text,
        timestamp = timestamp,
        isSent = isSent,
        contentType = contentType,
        status = status,
        imageUrl = imageUrl,
        voiceUrl = voiceUrl,
        duration = duration,
        type = type
    )

    private fun MessageEntity.toDomainModel() = Message(
        id = id,
        text = text,
        timestamp = timestamp,
        isSent = isSent,
        contentType = contentType,
        status = status,
        imageUrl = imageUrl,
        voiceUrl = voiceUrl,
        duration = duration,
        type = type
    )

    // --- API DTO → Domain mappers ---

    private fun ApiMessageDto.toDomain(currentUserId: String) = Message(
        id = id,
        text = content ?: "",
        timestamp = createdAt ?: "",
        isSent = senderId == currentUserId,
        contentType = when (type?.uppercase()) {
            "IMAGE" -> ContentType.IMAGE
            "VOICE" -> ContentType.VOICE
            "VIDEO" -> ContentType.VIDEO
            else -> ContentType.TEXT
        },
        status = MessageStatus.DELIVERED,
        imageUrl = if (type?.uppercase() == "IMAGE") mediaUrl else null,
        voiceUrl = if (type?.uppercase() == "VOICE") mediaUrl else null,
        duration = duration?.toString(),
        type = when (type?.uppercase()) {
            "SYSTEM" -> MessageType.SYSTEM
            else -> MessageType.REGULAR
        }
    )

    private fun ApiConversationDto.toDomain(currentUserId: String): Conversation {
        // Find the OTHER participant (not the current user)
        val otherParticipant = participants.firstOrNull { it.id != currentUserId }
            ?: participants.firstOrNull()

        return Conversation(
            id = id,
            name = otherParticipant?.name ?: "Unknown",
            age = 0, // Age not included in populated participant data
            photoUrl = otherParticipant?.photoUrl ?: "",
            lastMessage = lastMessage?.content ?: "",
            timestamp = updatedAt ?: "",
            unreadCount = unreadCounts?.get(currentUserId) ?: 0,
            isOnline = otherParticipant?.isOnline ?: false,
            isVerified = false // Not included in populated participant data
        )
    }
}
