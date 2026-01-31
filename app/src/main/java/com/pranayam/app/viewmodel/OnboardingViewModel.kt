package com.pranayam.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.api.PranayamApiService
import com.pranayam.app.api.UpdateProfileRequest
import com.pranayam.app.di.UserSessionManager
import com.pranayam.app.repository.PranayamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OnboardingStep {
    NAME, BIRTH_DATE, GENDER, INTERESTS, PHOTOS
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: PranayamRepository,
    private val apiService: PranayamApiService,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _currentStep = MutableStateFlow(OnboardingStep.NAME)
    val currentStep: StateFlow<OnboardingStep> = _currentStep.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _dob = MutableStateFlow("") // YYYY-MM-DD
    val dob: StateFlow<String> = _dob.asStateFlow()

    private val _gender = MutableStateFlow("FEMALE")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _selectedInterests = MutableStateFlow<Set<String>>(emptySet())
    val selectedInterests: StateFlow<Set<String>> = _selectedInterests.asStateFlow()

    private val _photos = MutableStateFlow<List<Uri>>(emptyList())
    val photos: StateFlow<List<Uri>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val availableInterests = listOf(
        "Architecture", "Art", "Coffee", "Yoga", "Tech", "Cooking", 
        "Hiking", "Music", "Photography", "Travel", "Movies", "Reading"
    )

    fun onNameChange(newName: String) { _name.value = newName }
    fun onDobChange(newDob: String) { _dob.value = newDob }
    fun onGenderChange(newGender: String) { _gender.value = newGender }
    
    fun toggleInterest(interest: String) {
        val current = _selectedInterests.value.toMutableSet()
        if (current.contains(interest)) current.remove(interest)
        else current.add(interest)
        _selectedInterests.value = current
    }

    fun addPhotos(uris: List<Uri>) {
        val current = _photos.value.toMutableList()
        val remaining = 6 - current.size
        current.addAll(uris.take(remaining))
        _photos.value = current
    }

    fun removePhoto(index: Int) {
        val current = _photos.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _photos.value = current
        }
    }

    fun nextStep() {
        val next = when (_currentStep.value) {
            OnboardingStep.NAME -> OnboardingStep.BIRTH_DATE
            OnboardingStep.BIRTH_DATE -> OnboardingStep.GENDER
            OnboardingStep.GENDER -> OnboardingStep.INTERESTS
            OnboardingStep.INTERESTS -> OnboardingStep.PHOTOS
            OnboardingStep.PHOTOS -> null
        }
        if (next != null) _currentStep.value = next
    }

    fun previousStep() {
        val prev = when (_currentStep.value) {
            OnboardingStep.NAME -> null
            OnboardingStep.BIRTH_DATE -> OnboardingStep.NAME
            OnboardingStep.GENDER -> OnboardingStep.BIRTH_DATE
            OnboardingStep.INTERESTS -> OnboardingStep.GENDER
            OnboardingStep.PHOTOS -> OnboardingStep.INTERESTS
        }
        if (prev != null) _currentStep.value = prev
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updateRequest = UpdateProfileRequest(
                    name = _name.value,
                    dob = _dob.value,
                    gender = _gender.value,
                    interests = _selectedInterests.value.toList()
                )
                apiService.updateProfile(updateRequest)
                // Mark onboarding as complete regardless of API response
                // Profile can be updated later if needed
            } catch (e: Exception) {
                // Log error but continue - profile will be incomplete but they can edit later
                e.printStackTrace()
            } finally {
                _isLoading.value = false
                // Always mark onboarding complete and proceed
                sessionManager.saveOnboardingComplete(true)
                onSuccess()
            }
        }
    }
}
