package com.wingspan.loanapp.ui.theme.registration

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.loanapp.data.AddressState
import com.wingspan.loanapp.data.FinancialState
import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.LoanDetailsState
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.PersonalDataState
import com.wingspan.loanapp.data.repository.RegistrationRepository
import com.wingspan.loanapp.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.onFailure
import kotlin.onSuccess

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: RegistrationRepository): ViewModel() {


    private val _uiState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()


    var state by mutableStateOf(PersonalDataState())
        private set


    fun onNameChange(value: String) {
        state = state.copy(name = value, nameError = null)
    }

    fun onEmailChange(value: String) {
        state = state.copy(email = value, emailError = null)
    }

    fun onMobileChange(value: String) {
        state = state.copy(mobile = value, mobileError = null)
    }

    fun onDobChange(value: String) {
        state = state.copy(dob = value, dobError = null)
    }

    fun onOtpChange(value: String) {
        state = state.copy(otp = value, otpError = null)
    }


    var loanState by mutableStateOf(LoanDetailsState())
        private set

    fun onLoanTypeChange(value: String) {
        loanState = loanState.copy(
            loanType = value,
            loanTypeError = null
        )
    }

    fun onMonthlyIncomeChange(value: String) {
        loanState = loanState.copy(
            monthlyIncome = value,
            incomeError = null
        )
    }

    fun onEmploymentTypeChange(value: String) {
        loanState = loanState.copy(
            employmentType = value,
            employmentError = null
        )
    }

    var financialState by mutableStateOf(FinancialState())
        private set

    var addressState by mutableStateOf(AddressState())
        private set

    fun onCibilChange(value: String) {
        financialState = financialState.copy(cibilScore = value, cibilError = null)
    }

    fun onPresentEmiChange(value: String) {
        financialState = financialState.copy(presentEmi = value, emiError = null)
    }
    fun onAddressChange(value: String) {
        addressState = addressState.copy(address = value, addressError = null)
    }

    fun onPinCodeChange(value: String) {
        addressState = addressState.copy(pinCode = value, pinError = null)
    }

    fun validateMobile(): String? {
        return when {
            state.mobile.isBlank() -> "Enter mobile number"
            state.mobile.length != 10 -> "Enter a valid 10-digit mobile number"
            else -> null
        }
    }


    fun validateOtp(): String? {
        return when {
            state.otp.isBlank() -> "Enter Otp number"
            state.otp.length != 6 -> "Enter a valid 6-digit otp number"
            else -> null
        }
    }
    fun validateFinancialDetails(): Boolean {
        var isValid = true

        val cibilError =
            if (financialState.cibilScore.isNotEmpty()) {
                val score = financialState.cibilScore.toIntOrNull()
                if (score == null || score !in 300..900) {
                    isValid = false
                    "CIBIL score must be between 300 and 900"
                } else null
            } else {
                isValid = false
                "Enter CIBIL score"
            }

        val emiError =
            if (financialState.presentEmi.isBlank()) {
                isValid = false
                "Enter present monthly EMI"
            } else null

        financialState = financialState.copy(
            cibilError = cibilError,
            emiError = emiError
        )

        return isValid
    }

    fun validateAddressDetails(): Boolean {

        var isValid = true

        val addressError =
            if (addressState.address.isBlank()) {
                isValid = false
                "Enter full address"
            } else null


        val pinError =
            if (addressState.pinCode.length != 6) {
                isValid = false
                "Enter valid 6 digit PIN code"
            } else null


        addressState = addressState.copy(
            addressError = addressError,
            pinError = pinError
        )

        return isValid
    }

    fun validateLoanDetails(): Boolean {

        var isValid = true

        var loanTypeError: String? = null
        var incomeError: String? = null
        var employmentError: String? = null

        if (loanState.loanType.isBlank()) {
            loanTypeError = "Please select loan type"
            isValid = false
        }

        if (loanState.monthlyIncome.isBlank()) {
            incomeError = "Please enter monthly income"
            isValid = false
        } else if (loanState.monthlyIncome.toIntOrNull() == null) {
            incomeError = "Invalid income"
            isValid = false
        }

        if (loanState.employmentType.isBlank()) {
            employmentError = "Please select employment type"
            isValid = false
        }

        loanState = loanState.copy(
            loanTypeError = loanTypeError,
            incomeError = incomeError,
            employmentError = employmentError
        )

        return isValid
    }

    fun validatePersonalDetails(): Boolean {

        var valid = true

        var nameError: String? = null
        var emailError: String? = null
        var mobileError: String? = null
        var dobError: String? = null

        if (state.name.isBlank()) {
            nameError = "Enter your name"
            valid = false
        }

        if (state.email.isBlank()) {
            emailError = "Enter email"
            valid = false
        }

        if (state.mobile.length != 10) {
            mobileError = "Enter valid mobile number"
            valid = false
        }

        if (state.dob.isBlank()) {
            dobError = "Select date of birth"
            valid = false
        }

        state = state.copy(
            nameError = nameError,
            emailError = emailError,
            mobileError = mobileError,
            dobError = dobError
        )

        return valid
    }



    fun sendOtp(phone: String) = viewModelScope.launch {
        _uiState.value = RegistrationState.Loading
        val result = repository.sendOtp(phone)

        when (result) {
            is ApiResult.Success -> {
                _uiState.value = RegistrationState.OtpSent
                  Log.d("verifyOtp", "OTP sent successfully")

            }

            is ApiResult.Error -> {
                _uiState.value = RegistrationState.Error(result.message)
                Log.d("verifyOtp", "Error: ${result.message}")

            }
        }
    }


    fun verifyOtp(request: OtpVerifyRequest) = viewModelScope.launch {
        _uiState.value = RegistrationState.Loading

        val result = repository.verifyOtp(request)
        when (result) {
            is ApiResult.Success -> {
                Log.d("verifyOtp", "OTP sent successfully")
                _uiState.value = RegistrationState.OtpVerified
            }

            is ApiResult.Error -> {
                Log.d("verifyOtp", "Error: ${result.message}")
                _uiState.value = RegistrationState.Error(result.message)
            }

        }
    }


    fun submitForm() = viewModelScope.launch {
        _uiState.value = RegistrationState.Loading
        var formData= FormData(
            name =state.name,
            dob = state.dob,
            loanType = loanState.loanType,
            income = loanState.monthlyIncome,
            email = state.email,
            employment = loanState.employmentType,
            cibil = financialState.cibilScore,
            emi = financialState.presentEmi,
            address = addressState.address,
            pincode = addressState.pinCode
        )
        Log.d("submitForm", "---${formData}")
        val result = repository.submitForm("69aac27ecbf8b3f0d0db611f",formData)
        when (result) {
            is ApiResult.Success -> {
                Log.d("submitForm", "OTP sent successfully")
                _uiState.value = RegistrationState.FormSubmitted
            }

            is ApiResult.Error -> {
                Log.d("submitForm", "Error: ${result.message}")
                _uiState.value = RegistrationState.Error(result.message)
            }

        }
    }

}