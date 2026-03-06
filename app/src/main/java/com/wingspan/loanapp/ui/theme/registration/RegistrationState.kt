package com.wingspan.loanapp.ui.theme.registration

sealed interface RegistrationState {
    object Idle : RegistrationState
    object Loading : RegistrationState
    object OtpSent : RegistrationState
    object OtpVerified : RegistrationState
    object FormSubmitted : RegistrationState
    data class Error(val message: String) : RegistrationState
}