package com.pranayam.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pranayam.app.di.UserSessionManager
import com.pranayam.app.ui.components.LoginPromptDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.pranayam.app.ui.screens.*
import com.pranayam.app.viewmodel.*
import com.pranayam.app.ui.components.NavigationTab
import com.pranayam.app.ui.components.PranayamBottomNav
import com.pranayam.app.ui.components.PrimaryButton
import com.pranayam.app.ui.theme.PranayamColors
import com.pranayam.app.ui.theme.PranayamTypography
import com.pranayam.app.ui.theme.Spacing

@Composable
fun PranayamNavGraph(
    navController: NavHostController,
    sessionManager: UserSessionManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onComplete = {
                // Go directly to Main - guests will see blurred profiles
                // and get prompted to login after 3 swipes
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Auth.route) {
            AuthScreen(onAuthSuccess = { needsOnboarding ->
                val destination = if (needsOnboarding) Screen.Onboarding.route else Screen.Main.route
                navController.navigate(destination) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }

        composable(
            route = Screen.MatchCelebration.route,
            arguments = listOf(
                navArgument("userPhoto") { type = NavType.StringType },
                navArgument("matchPhoto") { type = NavType.StringType },
                navArgument("matchName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userPhoto = backStackEntry.arguments?.getString("userPhoto") ?: ""
            val matchPhoto = backStackEntry.arguments?.getString("matchPhoto") ?: ""
            val matchName = backStackEntry.arguments?.getString("matchName") ?: ""

            MatchCelebrationScreen(
                userPhotoUrl = userPhoto,
                matchPhotoUrl = matchPhoto,
                matchName = matchName,
                onSendMessageClick = {
                    navController.popBackStack()
                },
                onKeepSwipingClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainContainer(
                parentNavController = navController,
                onEditProfile = { navController.navigate(Screen.EditProfile.route) }
            )
        }

        composable(Screen.EditProfile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val profile by viewModel.userProfile.collectAsState()

            profile?.let {
                EditProfileScreen(
                    profile = it,
                    onBack = { navController.popBackStack() },
                    onSave = { updated ->
                        viewModel.updateProfile(updated)
                        navController.popBackStack()
                    },
                    onPhotoAdded = viewModel::addPhoto,
                    onPhotoRemoved = viewModel::removePhoto
                )
            }
        }

        composable(Screen.Settings.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDeleteAccount = {
                    authViewModel.deleteAccount { success ->
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavigateToPermissions = {
                    navController.navigate(Screen.Permissions.route)
                }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProfileDetail.route,
            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
            val homeViewModel: HomeViewModel = hiltViewModel()
            val profiles by homeViewModel.profiles.collectAsState()
            val profile = profiles.find { it.id == profileId }

            if (profile != null) {
                ProfileDetailScreen(
                    profile = profile,
                    onBack = { navController.popBackStack() },
                    onLike = {
                        homeViewModel.like()
                        navController.popBackStack()
                    },
                    onPass = {
                        homeViewModel.pass()
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
            val viewModel: ChatViewModel = hiltViewModel()

            LaunchedEffect(conversationId) {
                viewModel.joinConversation(conversationId)
            }

            val messages by viewModel.getMessages(conversationId).collectAsState()
            val messageText by viewModel.messageText.collectAsState()
            val isRecording by viewModel.isRecording.collectAsState()
            val recordingDuration by viewModel.recordingDuration.collectAsState()
            val remoteUserStatus by viewModel.remoteUserStatus.collectAsState()
            val remoteUserTyping by viewModel.remoteUserTyping.collectAsState()

            ChatScreen(
                conversationId = conversationId,
                chatInfo = com.pranayam.app.data.model.Conversation(
                    conversationId, "Priya", 23, "", "", "", 0, true, true
                ),
                messages = messages,
                messageText = messageText,
                isRecording = isRecording,
                recordingDuration = recordingDuration,
                remoteUserStatus = remoteUserStatus,
                remoteUserTyping = remoteUserTyping,
                onMessageChange = viewModel::onMessageChange,
                onSendClick = { viewModel.sendMessage(conversationId) },
                onBack = { navController.popBackStack() },
                onProfileClick = { },
                onVoiceRecordStart = viewModel::startRecording,
                onVoiceRecordStop = { viewModel.stopRecording(conversationId) },
                onVoiceRecordCancel = viewModel::cancelRecording,
                onReportUser = {
                    // TODO: show reason picker dialog; for now report as OTHER
                    viewModel.reportUser(conversationId, "OTHER") { navController.popBackStack() }
                },
                onBlockUser = {
                    viewModel.blockUser(conversationId) { navController.popBackStack() }
                }
            )
        }
    }
}

@Composable
fun MainContainer(
    parentNavController: NavHostController,
    onEditProfile: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val viewModel: HomeViewModel = hiltViewModel()

    val profiles by viewModel.profiles.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val currentUser by viewModel.currentUserProfile.collectAsState()
    val isGuestMode by viewModel.isGuestMode.collectAsState()

    // State for login prompt dialog
    var showLoginDialog by remember { mutableStateOf(false) }

    // Observe Match Celebrations
    LaunchedEffect(Unit) {
        viewModel.matchEvent.collect { match ->
            val encodedUserPhoto = java.net.URLEncoder.encode(currentUser?.photos?.firstOrNull() ?: "", "UTF-8")
            val encodedMatchPhoto = java.net.URLEncoder.encode(profiles.getOrNull(currentIndex - 1)?.photos?.firstOrNull() ?: "", "UTF-8")
            val matchName = profiles.getOrNull(currentIndex - 1)?.name ?: "Someone"

            parentNavController.navigate(
                Screen.MatchCelebration.createRoute(
                    encodedUserPhoto,
                    encodedMatchPhoto,
                    matchName
                )
            )
        }
    }

    // Observe login prompt event for guest users
    LaunchedEffect(Unit) {
        viewModel.showLoginPrompt.collect {
            showLoginDialog = true
        }
    }

    // Login prompt dialog
    if (showLoginDialog) {
        LoginPromptDialog(
            onLoginClick = {
                showLoginDialog = false
                parentNavController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            },
            onDismiss = {
                showLoginDialog = false
                viewModel.resetGuestSwipeCount()
            }
        )
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            PranayamBottomNav(
                selectedTab = when (currentRoute) {
                    MainTab.Home.route -> NavigationTab.HOME
                    MainTab.Matches.route -> NavigationTab.MATCHES
                    MainTab.Profile.route -> NavigationTab.PROFILE
                    else -> NavigationTab.SETTINGS
                },
                onTabSelected = { tab ->
                    val route = when (tab) {
                        NavigationTab.HOME -> MainTab.Home.route
                        NavigationTab.MATCHES -> MainTab.Matches.route
                        NavigationTab.PROFILE -> MainTab.Profile.route
                        NavigationTab.SETTINGS -> MainTab.Settings.route
                    }
                    bottomNavController.navigate(route) {
                        popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = bottomNavController,
                startDestination = MainTab.Home.route
            ) {
                composable(MainTab.Home.route) {
                    HomeScreen(
                        profiles = profiles,
                        currentIndex = currentIndex,
                        isGuestMode = isGuestMode,
                        onProfileClick = { parentNavController.navigate(Screen.ProfileDetail.createRoute(it)) },
                        onLike = viewModel::like,
                        onPass = viewModel::pass,
                        onSuperLike = { },
                        onMessage = { },
                        onUndo = viewModel::undo
                    )
                }
                composable(MainTab.Matches.route) {
                    val chatViewModel: ChatViewModel = hiltViewModel()
                    val conversations by chatViewModel.conversations.collectAsState()

                    ChatListScreen(
                        matches = emptyList(),
                        conversations = conversations,
                        onChatClick = { parentNavController.navigate(Screen.Chat.createRoute(it)) }
                    )
                }
                composable(MainTab.Profile.route) {
                    val profileViewModel: ProfileViewModel = hiltViewModel()
                    val profile by profileViewModel.userProfile.collectAsState()

                    profile?.let {
                        ProfileScreen(
                            profile = it,
                            onEditClick = onEditProfile
                        )
                    }
                }
                composable(MainTab.Settings.route) {
                    val authViewModel: AuthViewModel = hiltViewModel()

                    SettingsScreen(
                        onBack = { bottomNavController.navigate(MainTab.Home.route) },
                        onLogout = {
                            authViewModel.logout()
                            parentNavController.navigate(Screen.Auth.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onDeleteAccount = {
                            authViewModel.deleteAccount { success ->
                                parentNavController.navigate(Screen.Auth.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onNavigateToPermissions = {
                            parentNavController.navigate(Screen.Permissions.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    profile: com.pranayam.app.data.model.Profile,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.L),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profile.photos.firstOrNull(),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(Spacing.M))

        Text(
            text = "${profile.name}, ${profile.age}",
            style = PranayamTypography.H2
        )

        Text(
            text = profile.profession.orEmpty(),
            style = PranayamTypography.BodyMedium,
            color = PranayamColors.TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(Spacing.XL))

        PrimaryButton(
            text = "Edit Profile",
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
