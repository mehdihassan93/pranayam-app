package com.pranayam.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.mergeDescendants
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pranayam.app.data.model.Profile

// Accessibility Utilities
val MinimumTouchTarget = 48.dp

@Composable
fun AccessiblePranayamButton(
    text: String,
    onClick: () -> Unit,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = MinimumTouchTarget)
            .semantics {
                contentDescription?.let {
                    this.contentDescription = it
                }
            }
    ) {
        Text(text)
    }
}

@Composable
fun AccessibleProfileCardWrapper(
    profile: Profile,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = buildString {
                append("${profile.name}, ${profile.age} years old. ")
                append("${profile.profession}. ")
                append("${profile.distance} kilometers away. ")
                if (profile.isVerified) append("Verified profile. ")
                if (profile.hasVideo) append("Has video intro. ")
                append(profile.prompts.firstOrNull()?.answer ?: "")
            }
        }
    ) {
        content()
    }
}

// Performance Utilities
@Composable
fun OptimizedAsyncImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .memoryCacheKey(url)
            .diskCacheKey(url)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
