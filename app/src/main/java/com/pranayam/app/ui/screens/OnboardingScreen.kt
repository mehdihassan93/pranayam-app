package com.pranayam.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pranayam.app.ui.components.PrimaryButton
import com.pranayam.app.ui.components.PranayamTextField
import com.pranayam.app.ui.theme.*
import com.pranayam.app.viewmodel.OnboardingStep
import com.pranayam.app.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val name by viewModel.name.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val selectedInterests by viewModel.selectedInterests.collectAsState()
    val photos by viewModel.photos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Set Up Profile", style = PranayamTypography.H3) },
                navigationIcon = {
                    if (currentStep != OnboardingStep.NAME) {
                        IconButton(onClick = viewModel::previousStep) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
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
            // Progress Indicator
            LinearProgressIndicator(
                progress = (currentStep.ordinal + 1).toFloat() / OnboardingStep.values().size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PranayamColors.RosePrimary,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(Modifier.height(Spacing.XXXL))

            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                        }
                    }
                ) { step ->
                    when (step) {
                        OnboardingStep.NAME -> StepName(name, viewModel::onNameChange)
                        OnboardingStep.BIRTH_DATE -> StepBirthDate(dob, viewModel::onDobChange)
                        OnboardingStep.GENDER -> StepGender(gender, viewModel::onGenderChange)
                        OnboardingStep.INTERESTS -> StepInterests(
                            viewModel.availableInterests,
                            selectedInterests,
                            viewModel::toggleInterest
                        )
                        OnboardingStep.PHOTOS -> StepPhotos(
                            photos = photos,
                            onAddPhotos = viewModel::addPhotos,
                            onRemovePhoto = viewModel::removePhoto
                        )
                    }
                }
            }

            PrimaryButton(
                text = if (currentStep == OnboardingStep.PHOTOS) "Finish" else "Continue",
                onClick = {
                    if (currentStep == OnboardingStep.PHOTOS) {
                        viewModel.completeOnboarding(onComplete)
                    } else {
                        viewModel.nextStep()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun StepName(name: String, onNameChange: (String) -> Unit) {
    Column {
        Text("What's your name?", style = PranayamTypography.H2)
        Text("This is how you'll appear on Pranayam", style = PranayamTypography.BodyMedium, color = Color.Gray)
        Spacer(Modifier.height(Spacing.XL))
        PranayamTextField(
            value = name,
            onValueChange = onNameChange,
            label = "First Name",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StepBirthDate(dob: String, onDobChange: (String) -> Unit) {
    Column {
        Text("When is your birthday?", style = PranayamTypography.H2)
        Text("We use this to show your age", style = PranayamTypography.BodyMedium, color = Color.Gray)
        Spacer(Modifier.height(Spacing.XL))
        PranayamTextField(
            value = dob,
            onValueChange = onDobChange,
            label = "YYYY-MM-DD",
            modifier = Modifier.fillMaxWidth(),
            placeholder = "1995-10-25"
        )
    }
}

@Composable
fun StepGender(selectedGender: String, onGenderChange: (String) -> Unit) {
    val genders = listOf("MALE", "FEMALE", "OTHER")
    Column {
        Text("What's your gender?", style = PranayamTypography.H2)
        Spacer(Modifier.height(Spacing.XL))
        genders.forEach { gender ->
            val isSelected = selectedGender == gender
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.S)
                    .clickable { onGenderChange(gender) },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, if (isSelected) PranayamColors.RosePrimary else Color.LightGray),
                color = if (isSelected) PranayamColors.RosePrimary.copy(alpha = 0.1f) else Color.Transparent
            ) {
                Text(
                    text = gender,
                    modifier = Modifier.padding(Spacing.L),
                    textAlign = TextAlign.Center,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun StepInterests(
    available: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column {
        Text("Your Interests", style = PranayamTypography.H2)
        Text("Select at least 3 to help us find matches", style = PranayamTypography.BodyMedium, color = Color.Gray)
        Spacer(Modifier.height(Spacing.XL))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(available) { interest ->
                val isSelected = selected.contains(interest)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(interest) },
                    label = { Text(interest) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PranayamColors.RosePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun StepPhotos(
    photos: List<Uri>,
    onAddPhotos: (List<Uri>) -> Unit,
    onRemovePhoto: (Int) -> Unit
) {
    val context = LocalContext.current
    val maxPhotos = 6

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxPhotos - photos.size)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAddPhotos(uris)
        }
    }

    Column {
        Text("Add Photos", style = PranayamTypography.H2)
        Text(
            text = if (photos.size < 2)
                "Add at least 2 photos to continue"
            else
                "${photos.size} of $maxPhotos photos added",
            style = PranayamTypography.BodyMedium,
            color = if (photos.size < 2) Color.Gray else PranayamColors.RosePrimary
        )
        Spacer(Modifier.height(Spacing.XL))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show selected photos
            itemsIndexed(photos) { index, uri ->
                PhotoItem(
                    uri = uri,
                    onRemove = { onRemovePhoto(index) },
                    isPrimary = index == 0
                )
            }

            // Show add buttons for remaining slots
            val remainingSlots = maxPhotos - photos.size
            items(remainingSlots) { index ->
                AddPhotoButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    isPrimary = photos.isEmpty() && index == 0
                )
            }
        }

        if (photos.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.M))
            Text(
                text = "Tip: Your first photo will be your main profile picture",
                style = PranayamTypography.BodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PhotoItem(
    uri: Uri,
    onRemove: () -> Unit,
    isPrimary: Boolean
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = "Selected photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove photo",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        // Primary badge
        if (isPrimary) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp),
                shape = RoundedCornerShape(4.dp),
                color = PranayamColors.RosePrimary
            ) {
                Text(
                    text = "Main",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = PranayamTypography.BodySmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AddPhotoButton(
    onClick: () -> Unit,
    isPrimary: Boolean
) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (isPrimary) PranayamColors.RosePrimary else Color.LightGray
        ),
        color = if (isPrimary) PranayamColors.RosePrimary.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add photo",
                tint = if (isPrimary) PranayamColors.RosePrimary else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            if (isPrimary) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Add",
                    style = PranayamTypography.BodySmall,
                    color = PranayamColors.RosePrimary
                )
            }
        }
    }
}
