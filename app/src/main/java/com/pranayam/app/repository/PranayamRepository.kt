package com.pranayam.app.repository

import com.pranayam.app.api.PranayamApiService
import com.pranayam.app.api.SendMessageRequest
import com.pranayam.app.data.local.entity.MessageEntity
import com.pranayam.app.data.local.dao.MessageDao
import com.pranayam.app.data.model.ContentType
import com.pranayam.app.data.model.Conversation
import com.pranayam.app.data.model.Message
import com.pranayam.app.data.model.MessageStatus
import com.pranayam.app.data.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PranayamRepository @Inject constructor(
    private val apiService: PranayamApiService,
    private val messageDao: MessageDao
) {

    // Discovery
    fun getDiscoveryProfiles(userId: String, lat: Double?, long: Double?): Flow<Result<List<Profile>>> = flow {
        try {
            val response = apiService.getDiscoveryProfiles(userId, lat, long)
            if (response.isSuccessful) {
                emit(Result.success(response.body() ?: emptyList()))
            } else {
                emit(Result.failure(Exception("Failed to fetch profiles: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun swipeProfile(userId: String, targetId: String, type: String): Result<com.pranayam.app.data.model.LikeResponse> {
        return try {
            val response = apiService.swipeProfile(com.pranayam.app.api.SwipeRequest(userId, targetId, type))
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
                emit(Result.success(response.body() ?: emptyList()))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
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
            val messages = response.body()
            if (response.isSuccessful && messages != null) {
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
            val response = apiService.sendMessage(conversationId, SendMessageRequest(text))
            val sentMessage = response.body()
            if (response.isSuccessful && sentMessage != null) {
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
}
