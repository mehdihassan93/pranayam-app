package com.pranayam.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranayam.app.data.model.Profile
import com.pranayam.app.ui.components.CircularIconButton
import com.pranayam.app.ui.components.ProfileCard
import com.pranayam.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    profiles: List<Profile>,
    currentIndex: Int,
    onProfileClick: (String) -> Unit,
    onLike: () -> Unit,
    onPass: () -> Unit,
    onSuperLike: () -> Unit,
    onMessage: () -> Unit,
    onUndo: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.height(32.dp), contentAlignment = Alignment.Center) {
                        Text("Pranayam", style = PranayamTypography.H3, brush = PrimaryGradient)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Navigate to profile */ }) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open notifications */ }) {
                        BadgedBox(
                            badge = {
                                Badge { Text("3") }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, "Notifications")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            SwipeActionBar(
                onPass = onPass,
                onSuperLike = onSuperLike,
                onLike = onLike,
                onMessage = onMessage,
                onUndo = onUndo
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PranayamColors.BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            if (profiles.isNotEmpty() && currentIndex < profiles.size) {
                // Background card (Simplified)
                if (currentIndex + 1 < profiles.size) {
                    val nextProfile = profiles[currentIndex + 1]
                    ProfileCard(
                        profile = nextProfile,
                        onCardClick = { },
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier
                            .padding(Spacing.M)
                            .scale(0.95f)
                            .alpha(0.5f)
                    )
                }

                // Foreground card (active profile)
                val currentProfile = profiles[currentIndex]
                ProfileCard(
                    profile = currentProfile,
                    onCardClick = { onProfileClick(currentProfile.id) },
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedElementKey = "photo-${currentProfile.id}",
                    modifier = Modifier.padding(Spacing.M)
                )
            } else {
                EmptyProfilesState()
            }
        }
    }
}

@Composable
fun SwipeActionBar(
    onPass: () -> Unit,
    onSuperLike: () -> Unit,
    onLike: () -> Unit,
    onMessage: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = Elevation.Medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.M),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularIconButton(
                icon = Icons.Default.Close,
                onClick = onPass,
                backgroundColor = Color.White,
                iconColor = PranayamColors.Error,
                size = 56.dp
            )
            
            CircularIconButton(
                icon = Icons.Default.Star,
                onClick = onSuperLike,
                backgroundColor = Color.White,
                iconColor = PranayamColors.WarmAccent,
                size = 56.dp
            )
            
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(Elevation.Large, CircleShape)
                    .clip(CircleShape)
                    .background(PrimaryGradient)
                    .clickable { onLike() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            CircularIconButton(
                icon = Icons.Default.ChatBubble,
                onClick = onMessage,
                backgroundColor = Color.White,
                iconColor = PranayamColors.PurplePrimary,
                size = 56.dp
            )
            
            CircularIconButton(
                icon = Icons.Default.Refresh,
                onClick = onUndo,
                backgroundColor = Color.White,
                iconColor = PranayamColors.TextSecondaryLight,
                size = 48.dp
            )
        }
    }
}

@Composable
fun EmptyProfilesState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = PranayamColors.TextTertiaryLight
        )
        Spacer(Modifier.height(Spacing.M))
        Text(
            text = "No more profiles nearby",
            style = PranayamTypography.H3,
            color = PranayamColors.TextSecondaryLight
        )
        Spacer(Modifier.height(Spacing.S))
        Text(
            text = "Try expanding your search distance",
            style = PranayamTypography.BodyMedium,
            color = PranayamColors.TextTertiaryLight
        )
    }
}
