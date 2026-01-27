package com.pranayam.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pranayam.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToPermissions: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Preference states
    var notificationsEnabled by remember { mutableStateOf(true) }
    var matchNotifications by remember { mutableStateOf(true) }
    var messageNotifications by remember { mutableStateOf(true) }
    var showOnlineStatus by remember { mutableStateOf(true) }
    var showDistance by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = PranayamTypography.H3) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Account Section
            item {
                SettingsSectionHeader("Account")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update your photos and info",
                    onClick = { }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Phone,
                    title = "Phone Number",
                    subtitle = "+91 •••••• 9999",
                    onClick = { }
                )
            }

            // Notifications Section
            item {
                SettingsSectionHeader("Notifications")
            }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    subtitle = "Enable all notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.Favorite,
                    title = "New Matches",
                    subtitle = "Get notified when you match",
                    checked = matchNotifications,
                    onCheckedChange = { matchNotifications = it },
                    enabled = notificationsEnabled
                )
            }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.Chat,
                    title = "Messages",
                    subtitle = "Get notified for new messages",
                    checked = messageNotifications,
                    onCheckedChange = { messageNotifications = it },
                    enabled = notificationsEnabled
                )
            }

            // Privacy Section
            item {
                SettingsSectionHeader("Privacy")
            }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.Visibility,
                    title = "Show Online Status",
                    subtitle = "Let others see when you're active",
                    checked = showOnlineStatus,
                    onCheckedChange = { showOnlineStatus = it }
                )
            }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.LocationOn,
                    title = "Show Distance",
                    subtitle = "Display your distance to others",
                    checked = showDistance,
                    onCheckedChange = { showDistance = it }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Block,
                    title = "Blocked Users",
                    subtitle = "Manage blocked profiles",
                    onClick = { }
                )
            }

            // App Preferences Section
            item {
                SettingsSectionHeader("App Preferences")
            }
            item {
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "App Permissions",
                    subtitle = "Camera, Location, Notifications",
                    onClick = onNavigateToPermissions
                )
            }

            // Support Section
            item {
                SettingsSectionHeader("Support")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Help Center",
                    subtitle = "FAQs and support",
                    onClick = { }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Feedback,
                    title = "Send Feedback",
                    subtitle = "Help us improve",
                    onClick = { }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Policy,
                    title = "Privacy Policy",
                    onClick = { }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    onClick = { }
                )
            }

            // Danger Zone
            item {
                SettingsSectionHeader("Account Actions")
            }
            item {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Log Out",
                    titleColor = PranayamColors.Warning,
                    onClick = { showLogoutDialog = true }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Delete Account",
                    titleColor = PranayamColors.Error,
                    subtitle = "Permanently delete your account",
                    onClick = { showDeleteDialog = true }
                )
            }

            // App Version
            item {
                Text(
                    text = "Version 1.0.0",
                    style = PranayamTypography.LabelSmall,
                    color = PranayamColors.TextTertiaryLight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.L)
                )
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = PranayamColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account?") },
            text = {
                Text("This action cannot be undone. All your data, matches, and conversations will be permanently deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    }
                ) {
                    Text("Delete", color = PranayamColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = PranayamTypography.LabelLarge,
        color = PranayamColors.RosePrimary,
        modifier = Modifier.padding(
            start = Spacing.L,
            end = Spacing.L,
            top = Spacing.L,
            bottom = Spacing.S
        )
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = PranayamColors.TextPrimaryLight,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.L, vertical = Spacing.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = titleColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(Spacing.M))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = PranayamTypography.BodyMedium,
                    color = titleColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = PranayamTypography.BodySmall,
                        color = PranayamColors.TextSecondaryLight
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PranayamColors.TextTertiaryLight
            )
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.L, vertical = Spacing.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) PranayamColors.TextPrimaryLight else PranayamColors.TextTertiaryLight,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(Spacing.M))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = PranayamTypography.BodyMedium,
                    color = if (enabled) PranayamColors.TextPrimaryLight else PranayamColors.TextTertiaryLight
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = PranayamTypography.BodySmall,
                        color = PranayamColors.TextSecondaryLight
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PranayamColors.RosePrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = PranayamColors.TextTertiaryLight
                )
            )
        }
    }
}
