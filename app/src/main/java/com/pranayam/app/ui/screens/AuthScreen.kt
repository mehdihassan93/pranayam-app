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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.pranayam.app.BuildConfig
import com.pranayam.app.ui.components.PrimaryButton
import com.pranayam.app.ui.components.PranayamTextField
import com.pranayam.app.ui.theme.*
import com.pranayam.app.viewmodel.AuthState
import com.pranayam.app.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: (needsOnboarding: Boolean) -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val otp by viewModel.otp.collectAsState()

    // Handle authentication state changes
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onAuthSuccess((authState as AuthState.Authenticated).needsOnboarding)
        }
    }

    val showOtpField = authState is AuthState.OtpSent ||
                       (authState is AuthState.Loading && otp.isNotEmpty()) ||
                       (authState is AuthState.Error && otp.isNotEmpty())
    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

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
            color = PranayamColors.Primary,
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

        // Error message
        AnimatedVisibility(visible = errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PranayamColors.Error.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.M)
            ) {
                Text(
                    text = errorMessage ?: "",
                    style = PranayamTypography.BodySmall,
                    color = PranayamColors.Error,
                    modifier = Modifier.padding(Spacing.M),
                    textAlign = TextAlign.Center
                )
            }
        }

        AnimatedVisibility(visible = !showOtpField) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PranayamTextField(
                    value = phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    label = "Phone Number",
                    placeholder = "+91 98765 43210",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = Icons.Default.Phone
                )

                Spacer(Modifier.height(Spacing.L))

                PrimaryButton(
                    text = "Send OTP",
                    onClick = viewModel::sendOtp,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = phoneNumber.length >= 10 && !isLoading,
                    isLoading = isLoading
                )

                // Only show test credentials in debug builds
                if (BuildConfig.DEBUG) {
                    Spacer(Modifier.height(Spacing.M))

                    Text(
                        text = "Test: Use 9999999999 with OTP 123456",
                        style = PranayamTypography.LabelSmall,
                        color = PranayamColors.TextTertiaryLight,
                        textAlign = TextAlign.Center
                    )
                }
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
                    onValueChange = viewModel::onOtpChange,
                    label = "OTP",
                    placeholder = "123456",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(Spacing.L))

                PrimaryButton(
                    text = "Verify & Continue",
                    onClick = viewModel::verifyOtp,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otp.length >= 4 && !isLoading,
                    isLoading = isLoading
                )

                TextButton(onClick = viewModel::resetToPhoneEntry) {
                    Text("Change Phone Number", color = PranayamColors.RosePrimary)
                }
            }
        }
    }
}
