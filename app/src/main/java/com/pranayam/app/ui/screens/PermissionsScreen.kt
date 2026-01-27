package com.pranayam.app.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.pranayam.app.ui.components.PrimaryButton
import com.pranayam.app.ui.components.SecondaryButton
import com.pranayam.app.ui.theme.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onBack: () -> Unit,
    onAllPermissionsGranted: (() -> Unit)? = null
) {
    val context = LocalContext.current

    // Permission states
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val notificationPermissionState = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    val allGranted = locationPermissionState.allPermissionsGranted &&
            cameraPermissionState.status.isGranted &&
            (notificationPermissionState?.status?.isGranted ?: true)

    LaunchedEffect(allGranted) {
        if (allGranted && onAllPermissionsGranted != null) {
            onAllPermissionsGranted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Permissions", style = PranayamTypography.H3) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.L),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pranayam needs these permissions to work properly",
                style = PranayamTypography.BodyMedium,
                color = PranayamColors.TextSecondaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Spacing.XL)
            )

            // Location Permission
            PermissionCard(
                icon = Icons.Default.LocationOn,
                title = "Location",
                description = "Required to find matches near you and show your distance to others",
                isGranted = locationPermissionState.allPermissionsGranted,
                isRequired = true,
                onRequestPermission = {
                    if (locationPermissionState.shouldShowRationale) {
                        openAppSettings(context)
                    } else {
                        locationPermissionState.launchMultiplePermissionRequest()
                    }
                }
            )

            Spacer(modifier = Modifier.height(Spacing.M))

            // Camera Permission
            PermissionCard(
                icon = Icons.Default.CameraAlt,
                title = "Camera",
                description = "Required to take photos for your profile and verify your identity",
                isGranted = cameraPermissionState.status.isGranted,
                isRequired = false,
                onRequestPermission = {
                    if (cameraPermissionState.status.shouldShowRationale) {
                        openAppSettings(context)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            )

            Spacer(modifier = Modifier.height(Spacing.M))

            // Notification Permission (Android 13+)
            if (notificationPermissionState != null) {
                PermissionCard(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    description = "Get notified about new matches and messages",
                    isGranted = notificationPermissionState.status.isGranted,
                    isRequired = false,
                    onRequestPermission = {
                        if (notificationPermissionState.status.shouldShowRationale) {
                            openAppSettings(context)
                        } else {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!allGranted) {
                PrimaryButton(
                    text = "Grant All Permissions",
                    onClick = {
                        if (!locationPermissionState.allPermissionsGranted) {
                            locationPermissionState.launchMultiplePermissionRequest()
                        } else if (!cameraPermissionState.status.isGranted) {
                            cameraPermissionState.launchPermissionRequest()
                        } else if (notificationPermissionState != null && !notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.S))

                SecondaryButton(
                    text = "Open App Settings",
                    onClick = { openAppSettings(context) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PranayamColors.Success.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.M),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PranayamColors.Success
                        )
                        Spacer(modifier = Modifier.width(Spacing.S))
                        Text(
                            text = "All permissions granted!",
                            style = PranayamTypography.BodyMedium,
                            color = PranayamColors.Success
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    isRequired: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.L),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted)
                PranayamColors.Success.copy(alpha = 0.05f)
            else
                Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isGranted) PranayamColors.Success.copy(alpha = 0.1f)
                        else PranayamColors.RosePrimary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) PranayamColors.Success else PranayamColors.RosePrimary
                )
            }

            Spacer(modifier = Modifier.width(Spacing.M))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = PranayamTypography.LabelLarge,
                        color = PranayamColors.TextPrimaryLight
                    )
                    if (isRequired) {
                        Spacer(modifier = Modifier.width(Spacing.XXS))
                        Text(
                            text = "*",
                            style = PranayamTypography.LabelLarge,
                            color = PranayamColors.Error
                        )
                    }
                }
                Text(
                    text = description,
                    style = PranayamTypography.BodySmall,
                    color = PranayamColors.TextSecondaryLight
                )
            }

            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = PranayamColors.Success
                )
            } else {
                TextButton(onClick = onRequestPermission) {
                    Text("Grant", color = PranayamColors.RosePrimary)
                }
            }
        }
    }
}

private fun openAppSettings(context: android.content.Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
