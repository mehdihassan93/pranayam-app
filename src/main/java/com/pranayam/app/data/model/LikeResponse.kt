package com.pranayam.app.data.model

data class LikeResponse(
    val isMatch: Boolean,
    val conversationId: String? = null,
    val matchProfile: Profile? = null
)
