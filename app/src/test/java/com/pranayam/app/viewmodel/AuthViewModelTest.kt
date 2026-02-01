package com.pranayam.app.viewmodel

import com.pranayam.app.api.PranayamApiService
import com.pranayam.app.di.UserSessionManager
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val apiService: PranayamApiService = mockk(relaxed = true)
    private val sessionManager: UserSessionManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(apiService, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `phone validation returns false for empty input`() {
        viewModel.onPhoneNumberChange("")
        assertFalse(viewModel.isValidPhoneNumber())
    }

    @Test
    fun `phone validation returns false for short phone number`() {
        viewModel.onPhoneNumberChange("12345")
        assertFalse(viewModel.isValidPhoneNumber())
    }

    @Test
    fun `phone validation returns true for valid 10 digit number`() {
        viewModel.onPhoneNumberChange("9876543210")
        assertTrue(viewModel.isValidPhoneNumber())
    }

    @Test
    fun `phone validation returns true for number with country code`() {
        viewModel.onPhoneNumberChange("919876543210")
        assertTrue(viewModel.isValidPhoneNumber())
    }

    @Test
    fun `phone validation handles formatted input`() {
        viewModel.onPhoneNumberChange("+91 987 654 3210")
        assertTrue(viewModel.isValidPhoneNumber())
    }

    @Test
    fun `otp input is limited to 6 digits`() {
        // First set a valid 6 digit OTP
        viewModel.onOtpChange("123456")
        assertEquals("123456", viewModel.otp.value)

        // Try to add more digits - should be rejected
        viewModel.onOtpChange("1234567")
        assertEquals("123456", viewModel.otp.value)
    }

    @Test
    fun `initial auth state is Idle`() {
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }

    @Test
    fun `sendOtp sets error state for invalid phone`() {
        viewModel.onPhoneNumberChange("123")
        viewModel.sendOtp()

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Error)
    }

    @Test
    fun `resetToPhoneEntry clears otp and resets state`() {
        viewModel.onOtpChange("123456")
        viewModel.resetToPhoneEntry()

        assertEquals("", viewModel.otp.value)
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }

    @Test
    fun `logout clears phone and otp`() {
        viewModel.onPhoneNumberChange("9876543210")
        viewModel.onOtpChange("123456")
        viewModel.logout()

        assertEquals("", viewModel.phoneNumber.value)
        assertEquals("", viewModel.otp.value)
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }
}
