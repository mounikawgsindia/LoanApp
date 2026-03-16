package com.wingspan.loanapp.ui.theme.homescreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.loanapp.data.LoanCaliculator
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.PersonalDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.wingspan.loanapp.data.repository.HomeScreenRepository
import com.wingspan.loanapp.ui.theme.registration.RegistrationState
import com.wingspan.loanapp.utils.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewmodel @Inject constructor(private val repository:HomeScreenRepository): ViewModel() {


    private var _uiState= MutableStateFlow<HomeState>(HomeState.Idle)
    var uiState: StateFlow<HomeState> = _uiState

    var caliculaterState by mutableStateOf(LoanCaliculator())
        private set

    fun onloanAmountChange(value:String){
        caliculaterState = caliculaterState.copy(loanAmount = value, loanAmountError = null)
    }

    fun onintrestRateChange(value:String){
        caliculaterState =caliculaterState.copy(intrestRate = value, intrestRateError = null)
    }

    fun onTenureChange(value:String){
        caliculaterState =caliculaterState.copy(tenure = value, tenureError = null)
    }
    fun onMobileChange(value:String){
        caliculaterState =caliculaterState.copy(mobile = value, mobileError = null)
    }

    fun onNameChange(value:String){
        caliculaterState =caliculaterState.copy(name = value, nameError = null)
    }
    fun onOtpChange(value:String){
        caliculaterState =caliculaterState.copy(otp = value, otpError = null)
    }
    fun validateOtp(): String? {
        return when {
            caliculaterState.otp.isBlank() -> "Enter Otp number"
            caliculaterState.otp.length != 6 -> "Enter a valid 6-digit otp number"
            else -> null
        }
    }
    fun validateName(): String? {
        return when {
            caliculaterState.name.isBlank() -> "Enter Name"
            else -> null
        }
    }
    fun updateNameError(error: String?) {
        caliculaterState = caliculaterState.copy(nameError = error)
    }
    fun updateOtpError(error: String?) {
        caliculaterState = caliculaterState.copy(otpError = error)
    }
    fun caliculaterValidation():Boolean{
        var isValid = true
        val loanAmountError =
            if (caliculaterState.loanAmount.isEmpty()) {
                isValid = false
                "Enter Loan Amount"
            } else null

        val intrestRateError =
            if (caliculaterState.intrestRate.isBlank()) {
                isValid = false
                "Enter Interest Rate"
            } else null

        val tenureError =
            if (caliculaterState.tenure.isBlank()) {
                isValid = false
                "Enter Tenure"
            } else null
        val mobileError =
            if (caliculaterState.mobile.isBlank()) {
                isValid = false
                "Enter Mobile number"
            } else null
        caliculaterState = caliculaterState.copy(
            loanAmountError = loanAmountError,
            intrestRateError = intrestRateError,
            tenureError = tenureError,
            mobileError = mobileError
        )


        return isValid
    }


    fun checkMobileNumberRegistered(){

        viewModelScope.launch {
            _uiState.value = HomeState.Loading
           val response= repository.verifyNumber(caliculaterState.mobile)
            when (response) {
                is ApiResult.Success -> {

                    val data = response.data

                    if (data.exists) {
                        _uiState.value = HomeState.NumberVerify
                        Log.d("check number", "User already exists")
                    } else {
                        _uiState.value = HomeState.NumberNotVerify

                        Log.d("check number", "User not found")
                    }

                }

                is ApiResult.Error -> {
                    _uiState.value = HomeState.Error(response.message)
                    Log.d("check number", "Error: ${response.message}")

                }
            }
        }
    }

    fun sendCaliculationOtp() = viewModelScope.launch {
        _uiState.value = HomeState.Loading
        val result = repository.sendCaliculationOtp(caliculaterState.mobile,caliculaterState.name)

        when (result) {
            is ApiResult.Success -> {
                _uiState.value = HomeState.OtpSent
                Log.d("sendCaliculationOtp", "OTP sent successfully")

            }

            is ApiResult.Error -> {
                _uiState.value = HomeState.Error(result.message)
                Log.d("sendCaliculationOtp", "Error: ${result.message}")

            }
        }
    }


    fun verifyOtp(request: OtpVerifyRequest) = viewModelScope.launch {
        _uiState.value = HomeState.Loading

        val result = repository.verifyOtp(request)
        when (result) {
            is ApiResult.Success -> {
                Log.d("verifyOtp", "OTP verified successfully ${result.data}")
                _uiState.value = HomeState.OtpVerified
            }

            is ApiResult.Error -> {
                Log.d("verifyOtp", "Error: ${result.message}")
                _uiState.value = HomeState.Error(result.message)
            }

        }
    }

    fun resetCalculatorState() {
        caliculaterState = LoanCaliculator()
        _uiState.value = HomeState.Idle
    }
}