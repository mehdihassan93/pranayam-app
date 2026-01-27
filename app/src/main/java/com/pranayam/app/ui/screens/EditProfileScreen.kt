package com.pranayam.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pranayam.app.data.model.Profile
import com.pranayam.app.ui.components.PrimaryButton
import com.pranayam.app.ui.components.PranayamTextField
import com.pranayam.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profile: Profile,
    onBack: () -> Unit,
    onSave: (Profile) -> Unit,
    onPhotoAdded: (android.net.Uri) -> Unit,
    onPhotoRemoved: (Int) -> Unit
) {
    var bio by remember { mutableStateOf(profile.bio ?: "") }
    var profession by remember { mutableStateOf(profile.profession) }
    var education by remember { mutableStateOf(profile.education ?: "") }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onPhotoAdded(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", style = PranayamTypography.H3) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        onSave(profile.copy(bio = bio, profession = profession, education = education))
                    }) {
                        Text("Save", color = PranayamColors.RosePrimary, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.M)
        ) {
            // Photos Section
            Text(
                text = "Photos (${profile.photos.size}/6)",
                style = PranayamTypography.LabelLarge,
                modifier = Modifier.padding(bottom = Spacing.S)
            )
            
            Box(modifier = Modifier.height(400.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.S),
                    verticalArrangement = Arrangement.spacedBy(Spacing.S),
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(profile.photos) { index, photoUrl ->
                        PhotoGridItem(
                            photoUrl = photoUrl,
                            onRemove = { onPhotoRemoved(index) }
                        )
                    }
                    
                    if (profile.photos.size < 6) {
                        item {
                            AddPhotoButton(onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            })
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.L))

            // Bio Section
            PranayamTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "About Me",
                placeholder = "Write a bit about yourself...",
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                singleLine = false
            )

            Spacer(Modifier.height(Spacing.M))

            // Professional Section
            PranayamTextField(
                value = profession,
                onValueChange = { profession = it },
                label = "Profession",
                placeholder = "What do you do?",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.M))

            PranayamTextField(
                value = education,
                onValueChange = { education = it },
                label = "Education",
                placeholder = "Where did you study?",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.XXL))
        }
    }
}

@Composable
fun PhotoGridItem(
    photoUrl: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(CornerRadius.M))
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.XS)
                .size(24.dp)
                .clickable(onClick = onRemove),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.6f)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun AddPhotoButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(CornerRadius.M))
            .background(Color(0xFFF3F4F6))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(CornerRadius.M))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.AddAPhoto,
                contentDescription = null,
                tint = PranayamColors.TextTertiaryLight
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Add",
                style = PranayamTypography.LabelSmall,
                color = PranayamColors.TextTertiaryLight
            )
        }
    }
}
