package com.pranayam.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.pranayam.app.ui.theme.Elevation
import com.pranayam.app.ui.theme.PranayamColors
import com.pranayam.app.ui.theme.PranayamTypography

@Composable
fun PranayamBottomNav(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = Elevation.Medium
    ) {
        NavigationTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == tab) tab.selectedIcon else tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    if (selectedTab == tab) {
                        Text(
                            text = tab.label,
                            style = PranayamTypography.LabelSmall
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PranayamColors.RosePrimary,
                    selectedTextColor = PranayamColors.RosePrimary,
                    unselectedIconColor = PranayamColors.TextSecondaryLight,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

enum class NavigationTab(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
) {
    HOME(Icons.Outlined.Favorite, Icons.Filled.Favorite, "Home"),
    MATCHES(Icons.Outlined.ChatBubble, Icons.Filled.ChatBubble, "Matches"),
    PROFILE(Icons.Outlined.Person, Icons.Filled.Person, "You"),
    SETTINGS(Icons.Outlined.Settings, Icons.Filled.Settings, "More")
}
