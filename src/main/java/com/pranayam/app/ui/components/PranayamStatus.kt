package com.pranayam.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranayam.app.ui.theme.CornerRadius
import com.pranayam.app.ui.theme.Spacing

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE0E0E0),
                        Color(0xFFF5F5F5),
                        Color(0xFFE0E0E0)
                    ),
                    start = Offset(translateAnim.value - 200f, 0f),
                    end = Offset(translateAnim.value, 100f)
                )
            )
    )
}

@Composable
fun ProfileCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp),
        shape = RoundedCornerShape(CornerRadius.L)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ShimmerEffect(modifier = Modifier.fillMaxSize())
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.M)
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .width(150.dp)
                        .height(32.dp)
                )
                Spacer(Modifier.height(Spacing.S))
                ShimmerEffect(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                )
            }
        }
    }
}
