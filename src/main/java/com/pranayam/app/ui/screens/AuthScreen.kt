package com.pranayam.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pranayam.app.ui.components.PrimaryButton
import com.pranayam.app.ui.components.PranayamTextField
import com.pranayam.app.ui.theme.*

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showOtpField by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.L),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Pranayam",
            style = PranayamTypography.H2,
            brush = PrimaryGradient,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(Spacing.S))
        
        Text(
            text = "Kerala's premium dating experience",
            style = PranayamTypography.BodyMedium,
            color = PranayamColors.TextSecondaryLight,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.XXL))

        AnimatedVisibility(visible = !showOtpField) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PranayamTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Phone Number",
                    placeholder = "+91 98765 43210",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, null) }
                )
                
                Spacer(Modifier.height(Spacing.L))
                
                PrimaryButton(
                    text = "Send OTP",
                    onClick = {
                        isLoading = true
                        // Mock API call
                        showOtpField = true
                        isLoading = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = phoneNumber.length >= 10 && !isLoading
                )
            }
        }

        AnimatedVisibility(visible = showOtpField) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Enter the 6-digit code sent to $phoneNumber",
                    style = PranayamTypography.BodySmall,
                    color = PranayamColors.TextSecondaryLight,
                    modifier = Modifier.padding(bottom = Spacing.M)
                )

                PranayamTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it },
                    label = "OTP",
                    placeholder = "000000",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(Spacing.L))
                
                PrimaryButton(
                    text = "Verify & Continue",
                    onClick = {
                        isLoading = true
                        // Mock verification
                        onAuthSuccess()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otp.length >= 4 && !isLoading
                )
                
                TextButton(onClick = { showOtpField = false }) {
                    Text("Change Phone Number", color = PranayamColors.RosePrimary)
                }
            }
        }
    }
}
