package com.wingspan.loanapp.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType

data class LoanProduct(
    val title: String,
    val image: Int
)


data class PersonalDataState(
    val name: String = "mounika",
    val email: String = "mounika@wgsindia.com",
    val mobile: String = "8125342434",
    val dob: String = "20/05/1990",
    val otp:String ="67890",

    val nameError: String? = null,
    val emailError: String? = null,
    val mobileError: String? = null,
    val dobError: String? = null,
    val otpError :String ?=null
)

data class LoanDetailsState(

    val loanType: String = "",
    val monthlyIncome: String = "",
    val employmentType: String = "",

    val loanTypeError: String? = null,
    val incomeError: String? = null,
    val employmentError: String? = null

)
data class FinancialState(

    val cibilScore: String = "",
    val cibilError: String? = null,

    val presentEmi: String = "",
    val emiError: String? = null

)

data class AddressState(

    val address: String = "",
    val addressError: String? = null,

    val pinCode: String = "",
    val pinError: String? = null

)

data class LoanScreens(
    val personal: @Composable () -> Unit,
    val loanDetails: @Composable () -> Unit,
    val financial: @Composable () -> Unit,
    val address: @Composable () -> Unit
)

data class InputOptions(
    val keyboardType: KeyboardType = KeyboardType.Text,
    val maxLength: Int? = null,
    val onlyDigits: Boolean = false
)

data class FormData(
val name: String,
val dob: String,
val loanType: String,
val income: String,      // Using String to match JSON, or use Int/Double if numeric
val email: String,
val employment: String,
val cibil: String,
val emi: String,
val address: String,
val pincode: String
)


data class ResponseData(var msg:String?,var error:String?, var success:Boolean)

data class OtpVerifyRequest( val phone: String,

                              val otp: String)

data class OtpResponse(
    val success: Boolean,
    val message: String?,
    val data: String
)
data class OtpRequest(val phone:String)
data class OtpData(
    val otpId: String?
)