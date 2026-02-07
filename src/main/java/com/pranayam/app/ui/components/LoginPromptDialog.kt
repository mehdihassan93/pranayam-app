package com.pranayam.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pranayam.app.ui.theme.*

@Composable
fun LoginPromptDialog(
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.M),
            shape = RoundedCornerShape(CornerRadius.L),
            color = Color.White,
            shadowElevation = Elevation.Large
        ) {
            Column(
                modifier = Modifier.padding(Spacing.L),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Want to see more?",
                    style = PranayamTypography.H2,
                    color = PranayamColors.TextPrimaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                Text(
                    text = "Login to view unlimited profiles and connect with your matches",
                    style = PranayamTypography.BodyMedium,
                    color = PranayamColors.TextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.XL))

                PrimaryButton(
                    text = "Login",
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.S))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Maybe later",
                        style = PranayamTypography.BodyMedium,
                        color = PranayamColors.TextSecondaryLight
                    )
                }
            }
        }
    }
}
