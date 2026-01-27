package com.pranayam.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pranayam.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MatchCelebrationScreen(
    userPhotoUrl: String,
    matchPhotoUrl: String,
    matchName: String,
    onSendMessageClick: () -> Unit,
    onKeepSwipingClick: () -> Unit
) {
    var animateIn by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        animateIn = true
    }

    val scale by animateInternalScale(animateIn)
    val alpha by animateInternalAlpha(animateIn)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.9f),
                        PranayamColors.RosePrimary.copy(alpha = 0.8f),
                        Color.Black.copy(alpha = 0.9f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.XL)
        ) {
            // "It's a Match!" Heading
            Text(
                text = "It's a Match!",
                style = PranayamTypography.H1.copy(
                    fontSize = 42.sp,
                    color = Color.White,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha),
                textAlign = TextAlign.Center
            )

            Text(
                text = "You and $matchName liked each other",
                style = PranayamTypography.BodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = Spacing.S)
            )

            Spacer(Modifier.height(Spacing.XXL))

            // Overlapping Avatars
            Box(
                modifier = Modifier.height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // User Avatar
                Surface(
                    modifier = Modifier
                        .offset(x = (-40).dp)
                        .size(120.dp)
                        .scale(scale)
                        .border(4.dp, Color.White, CircleShape),
                    shape = CircleShape,
                    shadowElevation = 12.dp
                ) {
                    AsyncImage(
                        model = userPhotoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Match Avatar
                Surface(
                    modifier = Modifier
                        .offset(x = 40.dp)
                        .size(120.dp)
                        .scale(scale)
                        .border(4.dp, Color.White, CircleShape),
                    shape = CircleShape,
                    shadowElevation = 12.dp
                ) {
                    AsyncImage(
                        model = matchPhotoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Heart Icon in middle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(scale * 1.2f)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("❤️", fontSize = 24.sp)
                }
            }

            Spacer(Modifier.height(64.dp))

            // Action Buttons
            Button(
                onClick = onSendMessageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(scale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Send a Message",
                    color = PranayamColors.RosePrimary,
                    style = PranayamTypography.LabelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(Spacing.M))

            OutlinedButton(
                onClick = onKeepSwipingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(scale),
                border = BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Keep Swiping",
                    color = Color.White,
                    style = PranayamTypography.LabelLarge
                )
            }
        }
    }
}

@Composable
private fun animateInternalScale(target: Boolean): State<Float> {
    return animateFloatAsState(
        targetValue = if (target) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
}

@Composable
private fun animateInternalAlpha(target: Boolean): State<Float> {
    return animateFloatAsState(
        targetValue = if (target) 1f else 0f,
        animationSpec = tween(500)
    )
}
