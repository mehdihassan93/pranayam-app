package com.pranayam.app.api

import com.pranayam.app.data.model.Profile
import retrofit2.Response
import retrofit2.http.*

interface PranayamApiService {

    // Auth endpoints
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<OtpResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<Profile>

    @GET("discovery/recommendations")
    suspend fun getDiscoveryProfiles(
        @Query("lat") latitude: Double?,
        @Query("long") longitude: Double?,
        @Query("distance") maxDistance: Int = 50
    ): Response<List<Profile>>

    @GET("discovery/recommendations")
    suspend fun getGuestProfiles(
        @Query("lat") latitude: Double?,
        @Query("long") longitude: Double?
    ): Response<List<Profile>>

    @POST("discovery/swipe")
    suspend fun swipeProfile(@Body swipeRequest: SwipeRequest): Response<com.pranayam.app.data.model.LikeResponse>

    @GET("chat/conversations")
    suspend fun getConversations(): Response<List<ApiConversationDto>>

    @GET("chat/messages/{id}")
    suspend fun getMessages(
        @Path("id") conversationId: String,
        @Query("limit") limit: Int = 20,
        @Query("before") timestamp: String? = null
    ): Response<List<ApiMessageDto>>

    @POST("chat/messages/{id}")
    suspend fun sendMessage(
        @Path("id") conversationId: String,
        @Body messageRequest: SendMessageRequest
    ): Response<ApiMessageDto>

    // Safety endpoints
    @POST("safety/report")
    suspend fun reportUser(@Body request: ReportUserRequest): Response<ApiMessage>

    @POST("safety/block")
    suspend fun blockUser(@Body request: BlockUserRequest): Response<ApiMessage>

    // Account deletion
    @DELETE("auth/account")
    suspend fun deleteAccount(): Response<ApiMessage>
}

data class SendMessageRequest(
    val content: String,
    val type: String = "TEXT"
)

data class SwipeRequest(
    val targetId: String,
    val type: String // "LIKE", "PASS", "SUPERLIKE"
)

// Auth DTOs
data class SendOtpRequest(val phoneNumber: String)

data class VerifyOtpRequest(
    val phoneNumber: String,
    val otp: String
)

data class OtpResponse(
    val type: String,
    val message: String
)

data class AuthResponse(
    val access_token: String,
    val user: UserInfo
)

data class UserInfo(
    val id: String,
    val name: String,
    val phoneNumber: String
)

data class UpdateProfileRequest(
    val name: String? = null,
    val dob: String? = null,
    val gender: String? = null,
    val interests: List<String>? = null,
    val bio: String? = null,
    val photoUrl: String? = null,
    val distancePreference: Int? = null,
    val genderPreference: List<String>? = null
)

// Safety DTOs
data class ReportUserRequest(
    val reportedUserId: String,
    val reason: String,
    val description: String = ""
)

data class BlockUserRequest(
    val blockedUserId: String
)

data class ApiMessage(
    val message: String
)
