package com.pranayam.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pranayam.app.data.model.Profile
import com.pranayam.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableCard(
    profile: Profile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .rotate(rotation.value)
            .scale(scale.value)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > 300 -> {
                                    offsetX.animateTo(1000f, tween(200))
                                    onSwipeRight()
                                }
                                offsetX.value < -300 -> {
                                    offsetX.animateTo(-1000f, tween(200))
                                    onSwipeLeft()
                                }
                                offsetY.value < -300 -> {
                                    offsetY.animateTo(-1000f, tween(200))
                                    onSwipeUp()
                                }
                                else -> {
                                    launch { offsetX.animateTo(0f, spring()) }
                                    launch { offsetY.animateTo(0f, spring()) }
                                    launch { rotation.animateTo(0f, spring()) }
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                            rotation.snapTo(offsetX.value / 20f)
                        }
                    }
                )
            }
    ) {
        ProfileCard(profile = profile, onCardClick = {})
        
        // Swipe overlays
        if (offsetX.value > 50) {
            SwipeOverlay(
                icon = Icons.Default.Favorite,
                color = PranayamColors.Success,
                alpha = (offsetX.value / 300f).coerceIn(0f, 1f)
            )
        } else if (offsetX.value < -50) {
            SwipeOverlay(
                icon = Icons.Default.Close,
                color = PranayamColors.Error,
                alpha = (-offsetX.value / 300f).coerceIn(0f, 1f)
            )
        } else if (offsetY.value < -50) {
            SwipeOverlay(
                icon = Icons.Default.Star,
                color = PranayamColors.WarmAccent,
                alpha = (-offsetY.value / 300f).coerceIn(0f, 1f)
            )
        }
    }
}

@Composable
fun SwipeOverlay(
    icon: ImageVector,
    color: Color,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color.copy(alpha = alpha * 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = color.copy(alpha = alpha)
        )
    }
}

@Composable
fun MatchAnimation(
    currentUserPhoto: String,
    matchedUserPhoto: String,
    onComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    val photosVisible = remember { mutableStateOf(false) }
    val textVisible = remember { mutableStateOf(false) }
    
    val leftPhotoOffset = remember { Animatable(-500f) }
    val rightPhotoOffset = remember { Animatable(500f) }
    val heartScale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(300)
        photosVisible.value = true
        launch {
            leftPhotoOffset.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
        }
        launch {
            rightPhotoOffset.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
        }
        
        delay(200)
        repeat(3) {
            heartScale.animateTo(1.3f, tween(200))
            heartScale.animateTo(1.0f, tween(200))
            delay(200)
        }
        
        textVisible.value = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onComplete() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (photosVisible.value) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.L),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUserPhoto,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .offset(x = leftPhotoOffset.value.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .scale(heartScale.value),
                        tint = PranayamColors.RosePrimary
                    )
                    
                    AsyncImage(
                        model = matchedUserPhoto,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .offset(x = rightPhotoOffset.value.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Spacer(Modifier.height(Spacing.XL))
            
            AnimatedVisibility(
                visible = textVisible.value,
                enter = slideInVertically { 100 } + fadeIn()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = Spacing.L)
                ) {
                    Text(
                        text = "It's a Match!",
                        style = PranayamTypography.H1.copy(fontSize = 40.sp),
                        brush = PrimaryGradient
                    )
                    
                    Spacer(Modifier.height(Spacing.M))
                    
                    Text(
                        text = "You and your match both liked each other",
                        style = PranayamTypography.BodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        if (textVisible.value) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Spacing.L)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.M)
            ) {
                PrimaryButton(
                    text = "Send Message",
                    onClick = { /* Navigate to chat */ }
                )
                
                PranayamTextButton(
                    text = "Keep Swiping",
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
