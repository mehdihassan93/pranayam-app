package com.pranayam.app.api

import com.pranayam.app.data.model.Profile
import com.pranayam.app.data.model.Conversation
import com.pranayam.app.data.model.Message
import retrofit2.Response
import retrofit2.http.*

interface PranayamApiService {

    @GET("discovery/recommendations")
    suspend fun getDiscoveryProfiles(
        @Query("userId") userId: String,
        @Query("lat") latitude: Double?,
        @Query("long") longitude: Double?,
        @Query("distance") maxDistance: Int = 50
    ): Response<List<Profile>>

    @POST("discovery/swipe")
    suspend fun swipeProfile(@Body swipeRequest: SwipeRequest): Response<com.pranayam.app.model.LikeResponse>

    @GET("conversations")
    suspend fun getConversations(): Response<List<Conversation>>

    @GET("conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") conversationId: String,
        @Query("limit") limit: Int = 20,
        @Query("before") timestamp: String? = null
    ): Response<List<Message>>

    @POST("conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") conversationId: String,
        @Body messageRequest: SendMessageRequest
    ): Response<Message>
}

data class SendMessageRequest(
    val text: String,
    val type: String = "TEXT"
)

data class SwipeRequest(
    val userId: String,
    val targetId: String,
    val type: String // "LIKE", "PASS", "SUPERLIKE"
)
