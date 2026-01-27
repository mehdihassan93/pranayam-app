package com.pranayam.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pranayam.app.ui.theme.*
import androidx.compose.foundation.BorderStroke

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(CornerRadius.XXL),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) PrimaryGradient 
                    else Color.Gray,
                    shape = RoundedCornerShape(CornerRadius.XXL)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = Spacing.M)
                ) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(IconSize.S),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(Spacing.XS))
                    }
                    Text(
                        text = text,
                        style = PranayamTypography.LabelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(CornerRadius.XXL),
        border = BorderStroke(
            width = 1.dp,
            brush = if (enabled) PrimaryGradient else SolidColor(Color.Gray)
        )
    ) {
        Text(
            text = text,
            style = PranayamTypography.LabelLarge,
            color = if (!enabled) Color.Gray else PranayamColors.RosePrimary // Note: In Jetpack Compose, you might need special handling for gradient text
        )
    }
}

@Composable
fun PranayamTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = PranayamTypography.LabelMedium,
            color = if (enabled) PranayamColors.RosePrimary else Color.Gray
        )
    }
}

@Composable
fun CircularIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    iconColor: Color = PranayamColors.RosePrimary,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(Elevation.Medium, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(IconSize.M)
        )
    }
}
