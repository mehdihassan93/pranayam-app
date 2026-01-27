package com.pranayam.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.pranayam.app.ui.screens.*
import com.pranayam.app.viewmodel.*
import com.pranayam.app.ui.components.NavigationTab

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun PranayamNavGraph(navController: NavHostController) {
    androidx.compose.animation.SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onSplashFinished = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }

            composable(Screen.Auth.route) {
                AuthScreen(onAuthSuccess = {
                    navController.navigate(Screen.Main.route) {
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
                        // Logic to navigate to specifically that chat
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
                    },
                    onSkip = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Main.route) {
                MainContainer(
                    parentNavController = navController,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
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

            composable(
                route = Screen.ProfileDetail.route,
                arguments = listOf(navArgument("profileId") { type = NavType.StringType })
            ) { backStackEntry ->
                val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
                // In a real app, you'd fetch this from a ViewModel
                val mockProfile = com.pranayam.app.data.model.Profile(
                    id = profileId,
                    name = "Aparna",
                    age = 24,
                    photos = listOf("https://images.unsplash.com/photo-1544005313-94ddf0286df2"),
                    profession = "Software Engineer",
                    distance = 5,
                    isVerified = true,
                    hasVideo = true,
                    prompts = listOf(com.pranayam.app.data.model.Prompt("My favorite thing about Kerala is", "The food!")),
                    languages = listOf("Malayalam", "English"),
                    bio = "Just a software engineer looking for someone to explore the backwaters with.",
                    height = 165,
                    education = "B.Tech Computer Science"
                )

                ProfileDetailScreen(
                    profile = mockProfile,
                    onBack = { navController.popBackStack() },
                    onLike = { /* Like logic */ },
                    onPass = { /* Pass logic */ },
                    animatedVisibilityScope = this@composable
                )
            }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
            val viewModel: ChatViewModel = hiltViewModel()
            
            val messages by viewModel.getMessages(conversationId).collectAsState()
            val messageText by viewModel.messageText.collectAsState()
            val isRecording by viewModel.isRecording.collectAsState()
            val recordingDuration by viewModel.recordingDuration.collectAsState()
            val remoteUserStatus by viewModel.remoteUserStatus.collectAsState()
            val remoteUserTyping by viewModel.remoteUserTyping.collectAsState()

            // Photo Picker Launcher
            val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                viewModel.handleImageSelection(conversationId, uri)
            }

            // In a real app, you'd fetch chatInfo from the repository
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
                onAttachClick = {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onBack = { navController.popBackStack() },
                onProfileClick = { /* Navigate to profile */ },
                onVoiceRecordStart = viewModel::startRecording,
                onVoiceRecordStop = { viewModel.stopRecording(conversationId) },
                onVoiceRecordCancel = viewModel::cancelRecording
            )
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun MainContainer(
    parentNavController: NavHostController,
    sharedTransitionScope: androidx.compose.animation.SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    onEditProfile: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val viewModel: HomeViewModel = hiltViewModel()
    
    val profiles by viewModel.profiles.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val currentUser by viewModel.currentUserProfile.collectAsState()

    // Observe Match Celebrations
    androidx.compose.runtime.LaunchedEffect(Unit) {
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

    androidx.compose.material3.Scaffold(
        bottomBar = {
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            com.pranayam.app.ui.components.PranayamBottomNav(
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
        androidx.compose.foundation.layout.Box(modifier = androidx.compose.foundation.layout.Modifier.padding(padding)) {
            NavHost(
                navController = bottomNavController,
                startDestination = MainTab.Home.route
            ) {
                composable(MainTab.Home.route) {
                    with(sharedTransitionScope) {
                        HomeScreen(
                            profiles = profiles,
                            currentIndex = currentIndex,
                            onProfileClick = { parentNavController.navigate(Screen.ProfileDetail.createRoute(it)) },
                            onLike = viewModel::like,
                            onPass = viewModel::pass,
                            onSuperLike = { /* viewModel.superLike() */ },
                            onMessage = { /* viewModel.messageBeforeMatch() */ },
                            onUndo = viewModel::undo,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
                composable(MainTab.Matches.route) {
                    ChatListScreen(
                        matches = emptyList(),
                        conversations = emptyList(),
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
                composable(MainTab.Settings.route) { /* SettingsScreen() */ }
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
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(Spacing.L),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profile.photos.firstOrNull(),
            contentDescription = null,
            modifier = androidx.compose.ui.Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        
        Spacer(modifier = androidx.compose.ui.Modifier.height(Spacing.M))
        
        Text(
            text = "${profile.name}, ${profile.age}",
            style = PranayamTypography.H2
        )
        
        Text(
            text = profile.profession,
            style = PranayamTypography.BodyMedium,
            color = PranayamColors.TextSecondaryLight
        )
        
        Spacer(modifier = androidx.compose.ui.Modifier.height(Spacing.XL))
        
        PrimaryButton(
            text = "Edit Profile",
            onClick = onEditClick,
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        )
    }
}
