package com.pranayam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.data.model.Profile
import com.pranayam.app.repository.PranayamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PranayamRepository
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _matchEvent = kotlinx.coroutines.flow.MutableSharedFlow<com.pranayam.app.data.model.LikeResponse>()
    val matchEvent = _matchEvent.asSharedFlow()

    private val _currentUserProfile = MutableStateFlow<Profile?>(null)
    val currentUserProfile: StateFlow<Profile?> = _currentUserProfile.asStateFlow()

    init {
        loadProfiles()
        // Mocking current user profile for celebration
        _currentUserProfile.value = Profile(
            id = "me",
            name = "Me",
            age = 25,
            photos = emptyList(),
            profession = "Designer",
            distance = 0,
            isVerified = true,
            hasVideo = false,
            prompts = emptyList()
        )
    }

    fun loadProfiles(lat: Double? = null, long: Double? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            // Using "me" as mock userId
            repository.getDiscoveryProfiles("me", lat, long).collect { result ->
                result.onSuccess {
                    _profiles.value = it
                    _error.value = null
                }.onFailure {
                    _error.value = it.message
                }
                _isLoading.value = false
            }
        }
    }

    fun like() {
        val currentProfile = _profiles.value.getOrNull(_currentIndex.value) ?: return
        viewModelScope.launch {
            repository.swipeProfile("me", currentProfile.id, "LIKE").onSuccess { response ->
                if (response.isMatch) {
                    _matchEvent.emit(response)
                }
            }
            // Move to next card anyway for a smooth UX
            _currentIndex.value++
        }
    }

    fun pass() {
        val currentProfile = _profiles.value.getOrNull(_currentIndex.value) ?: return
        viewModelScope.launch {
            repository.swipeProfile("me", currentProfile.id, "PASS")
            _currentIndex.value++
        }
    }

    fun undo() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
        }
    }
}
