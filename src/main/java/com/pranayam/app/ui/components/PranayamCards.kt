package com.pranayam.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pranayam.app.data.model.Profile
import com.pranayam.app.ui.theme.*

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun androidx.compose.animation.SharedTransitionScope.ProfileCard(
    profile: Profile,
    onCardClick: () -> Unit,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    sharedElementKey: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(CornerRadius.L),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.Medium)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo with pager
            ProfilePhotoPager(
                photos = profile.photos,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedElementKey = sharedElementKey,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay for text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 400f
                        )
                    )
            )
            
            // Badges (top)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.M),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (profile.isVerified) {
                    PranayamBadge(
                        icon = Icons.Default.Verified,
                        backgroundColor = Color.White.copy(alpha = 0.9f),
                        iconColor = PranayamColors.Info
                    )
                }
                Spacer(Modifier.weight(1f))
                if (profile.hasVideo) {
                    PranayamBadge(
                        icon = Icons.Default.Videocam,
                        backgroundColor = Color.White.copy(alpha = 0.9f),
                        iconColor = PranayamColors.RosePrimary
                    )
                }
            }
            
            // Info (bottom)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.M)
            ) {
                // Photo indicators
                PhotoIndicators(
                    currentIndex = 0,
                    totalCount = profile.photos.size,
                    modifier = Modifier.padding(bottom = Spacing.S)
                )
                
                // Name and age
                Text(
                    text = "${profile.name}, ${profile.age}",
                    style = PranayamTypography.H2,
                    color = Color.White
                )
                
                // Profession
                Text(
                    text = profile.profession,
                    style = PranayamTypography.BodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = Spacing.XXS)
                )
                
                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = Spacing.XXS)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(IconSize.S),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.width(Spacing.XXS))
                    Text(
                        text = "${profile.distance} km away",
                        style = PranayamTypography.BodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Prompt preview
                if (profile.prompts.isNotEmpty()) {
                    Text(
                        text = "\"${profile.prompts.first().answer}\"",
                        style = PranayamTypography.BodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = Spacing.S)
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun androidx.compose.animation.SharedTransitionScope.ProfilePhotoPager(
    photos: List<String>,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    sharedElementKey: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = if (sharedElementKey != null) {
            modifier.sharedElement(
                rememberSharedContentState(key = sharedElementKey),
                animatedVisibilityScope = animatedVisibilityScope
            )
        } else {
            modifier
        }
    ) {
        AsyncImage(
            model = photos.firstOrNull(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun PranayamBadge(
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color
) {
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        modifier = Modifier.size(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(IconSize.S)
            )
        }
    }
}

@Composable
fun PhotoIndicators(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(totalCount) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentIndex) Color.White 
                        else Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}
