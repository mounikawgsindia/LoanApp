package com.wingspan.loanapp.ui.theme.homescreen

import com.wingspan.loanapp.ui.theme.registration.RegistrationState

sealed interface HomeState {
    object Idle : HomeState
    object Loading : HomeState
    object OtpSent : HomeState
    object OtpVerified : HomeState
    object NumberVerify : HomeState
    object NumberNotVerify : HomeState
    data class Error(val message: String) : HomeState
}