package com.pranayam.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pranayam.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        // Logo scale animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        // Fade in
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(300)
        )
        
        // Pulse animation
        repeat(1) {
            scale.animateTo(1.1f, animationSpec = tween(200))
            scale.animateTo(1f, animationSpec = tween(200))
        }
        
        delay(500)
        onComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF1F3), // Soft rose
                        Color(0xFFFAF5FF)  // Soft purple
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha.value)
        ) {
            // Logo placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Pranayam Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(Modifier.height(Spacing.M))
            
            // App name
            Text(
                text = "Pranayam",
                style = PranayamTypography.H1,
                color = PranayamColors.Primary
            )
            
            Spacer(Modifier.height(Spacing.S))
            
            // Tagline
            Text(
                text = "Find your malayali",
                style = PranayamTypography.BodyMedium,
                color = PranayamColors.TextSecondaryLight
            )
        }
    }
}
