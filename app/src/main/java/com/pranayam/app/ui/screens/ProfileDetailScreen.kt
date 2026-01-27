package com.pranayam.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pranayam.app.data.model.Profile
import com.pranayam.app.ui.components.PrimaryButton
import com.pranayam.app.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDetailScreen(
    profile: Profile,
    onBack: () -> Unit,
    onLike: () -> Unit,
    onPass: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                AsyncImage(
                    model = profile.photos.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 300f
                            )
                        )
                )

                // Info Overlay on Photo
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Spacing.L)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${profile.name}, ${profile.age}",
                            style = PranayamTypography.H1,
                            color = Color.White
                        )
                        if (profile.isVerified) {
                            Spacer(Modifier.width(Spacing.XS))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PranayamColors.Info,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = profile.profession,
                        style = PranayamTypography.BodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Details Content
            Column(
                modifier = Modifier
                    .padding(Spacing.L)
                    .fillMaxWidth()
            ) {
                // About Section
                Text("About", style = PranayamTypography.H3)
                Spacer(Modifier.height(Spacing.S))
                Text(
                    text = profile.bio ?: "No bio provided.",
                    style = PranayamTypography.BodyMedium,
                    color = PranayamColors.TextSecondaryLight,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(Spacing.L))

                // Prompts Section
                profile.prompts.forEach { prompt ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.S),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        shape = RoundedCornerShape(CornerRadius.L)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.M)) {
                            Text(
                                text = prompt.question,
                                style = PranayamTypography.LabelSmall,
                                color = PranayamColors.RosePrimary
                            )
                            Spacer(Modifier.height(Spacing.XS))
                            Text(
                                text = prompt.answer,
                                style = PranayamTypography.H3.copy(fontSize = 20.sp),
                                color = PranayamColors.TextPrimaryLight
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.L))

                // Basic Info
                Text("Basic Information", style = PranayamTypography.H4)
                Spacer(Modifier.height(Spacing.M))

                InfoRow(icon = Icons.Default.Person, label = "Height", value = "${profile.height ?: "---"} cm")
                InfoRow(icon = Icons.Default.School, label = "Education", value = profile.education ?: "---")
                InfoRow(icon = Icons.Default.Language, label = "Languages", value = profile.languages.joinToString(", "))

                Spacer(Modifier.height(100.dp)) // Padding for bottom actions
            }
        }

        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = Spacing.M, end = Spacing.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
                    .clip(CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        // Bottom Action Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(Spacing.M),
                horizontalArrangement = Arrangement.spacedBy(Spacing.M)
            ) {
                OutlinedButton(
                    onClick = onPass,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(CornerRadius.XXL)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = PranayamColors.Error)
                    Spacer(Modifier.width(8.dp))
                    Text("Pass", color = PranayamColors.Error)
                }

                PrimaryButton(
                    text = "Like",
                    onClick = onLike,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Favorite
                )
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PranayamColors.TextTertiaryLight, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Spacing.M))
        Column {
            Text(text = label, style = PranayamTypography.LabelSmall, color = PranayamColors.TextTertiaryLight)
            Text(text = value, style = PranayamTypography.BodyMedium, color = PranayamColors.TextPrimaryLight)
        }
    }
}
