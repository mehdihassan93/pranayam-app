package com.pranayam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.data.model.Profile
import com.pranayam.app.repository.PranayamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: PranayamRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // Mock loading current user profile
        _userProfile.value = Profile(
            id = "me",
            name = "Mehdi",
            age = 25,
            photos = mutableListOf("https://images.unsplash.com/photo-1500648767791-00dcc994a43e"),
            profession = "Developer",
            distance = 0,
            isVerified = true,
            hasVideo = false,
            prompts = listOf(),
            languages = listOf("English", "Malayalam"),
            bio = "Building the future of dating in Kerala."
        )
    }

    fun updateProfile(updatedProfile: Profile) {
        viewModelScope.launch {
            _isLoading.value = true
            // In a real app: repository.updateProfile(updatedProfile)
            _userProfile.value = updatedProfile
            _isLoading.value = false
        }
    }

    fun addPhoto(uri: android.net.Uri) {
        val current = _userProfile.value ?: return
        val updatedPhotos = current.photos.toMutableList().apply {
            add(uri.toString()) // Placeholder: in real app, upload and use URL
        }
        _userProfile.value = current.copy(photos = updatedPhotos)
    }

    fun removePhoto(index: Int) {
        val current = _userProfile.value ?: return
        val updatedPhotos = current.photos.toMutableList().apply {
            removeAt(index)
        }
        _userProfile.value = current.copy(photos = updatedPhotos)
    }
}
