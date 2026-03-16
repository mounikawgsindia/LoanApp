package com.wingspan.loanapp.ui.theme.registration

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.view.ContextThemeWrapper
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.wingspan.loanapp.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wingspan.loanapp.data.InputOptions
import com.wingspan.loanapp.data.LoanScreens
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.repository.RegistrationRepository
import com.wingspan.loanapp.utils.NetworkUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// --- Constants for employment types ---
private const val EMPLOYMENT_SALARIED = "Salaried"
private const val EMPLOYMENT_SELF_EMPLOYED = "Self Employed"
private const val EMPLOYMENT_PRIVATE = "Private Employee"
private const val EMPLOYMENT_GOVERNMENT = "Government Employee"

private val BluePrimary = Color(0xFF2563EB)
private val BlueDeep = Color(0xFF1E40AF)
private val BlueIndigo = Color(0xFF4F46E5)
private val PageBgTop = Color(0xFFF5F7FA)
private val PageBgBottom = Color(0xFFEFF6FF)
private val CardBg = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val FieldBg = Color(0xFFF8FAFF)
private val FieldBorder = Color(0xFFE2E8F0)

private val HeaderGradient = Brush.linearGradient(
    colors = listOf(BluePrimary, BlueIndigo)
)
private val PrimaryButtonGradient = Brush.horizontalGradient(
    colors = listOf(BluePrimary, BlueIndigo)
)


@Composable
fun LoanApplicationScreen(onBackNavigationClick :() ->Unit,viewmodel: RegistrationViewModel=hiltViewModel(),navigateToHomeScreen:()->Unit) {

    var context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }
    //convert stateflow to composable state
    val uiState by viewmodel.uiState.collectAsState()
    var showSuccessDialog by remember {mutableStateOf(false)}



    LaunchedEffect(uiState) {
        when(uiState) {
            is RegistrationState.Loading -> {
                // Show a progress indicator if needed
            }
            is RegistrationState.OtpSent -> {

                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            }
            is RegistrationState.OtpVerified -> {
                Toast.makeText(context, "OTP verified successfully", Toast.LENGTH_SHORT).show()
            }
            is RegistrationState.FormSubmitted -> {
                showSuccessDialog=true

            }
            is RegistrationState.Error -> {
                val message = (uiState as RegistrationState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // --- Local helper functions ---
    fun handleStep1(): Boolean = viewmodel.validatePersonalDetails().also { valid ->
        if (!valid) return@also

        if (uiState is RegistrationState.OtpVerified) {
            currentStep++
        } else {
            Toast.makeText(context, "OTP not verified", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleStep2(): Boolean = viewmodel.validateLoanDetails().also { valid ->
        if (valid) currentStep++
    }

    fun handleStep3(): Boolean = viewmodel.validateFinancialDetails().also { valid ->
        if (valid) currentStep++
    }

    fun handleStep4() {
        if (!viewmodel.validateAddressDetails()) return
        if (NetworkUtils.isNetworkAvailable(context)) {
             viewmodel.submitForm()
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }
    val stepActions = mapOf(
        1 to { handleStep1() },
        2 to { handleStep2() },
        3 to { handleStep3() },
        4 to { handleStep4() }
    )


    LoanApplicationScreenUI(

        onNextButtonClick = {
            stepActions[currentStep]?.invoke()
        },
        currentStep = currentStep,

        onBackClick = {
            currentStep--
        },
        onBackNavigationClick = {
            onBackNavigationClick()
        },
        screens = LoanScreens(
            personal = { PersonalScreen(
                viewmodel,
                onOtpClick = { number ->
                    viewmodel.sendOtp(number)
                },
                onOtpVerify = { number, otp ->
                    viewmodel.verifyOtp(OtpVerifyRequest(number, otp))
                },
                dobOnClick = {
                    dateSelectDialog(context,viewmodel)
                }
            ) },
            loanDetails = { LoanDetailsScreen(viewmodel) },
            financial = { FinancialScreen(viewmodel) },
            address = { AddressScreen(viewmodel) }
        )
    )

    if (showSuccessDialog) {

        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = "Success")
            },
            text = {
                Text(" We received your application and our team will get back to you soon, Thank You")
            },
            confirmButton = {
                GradientButton(
                    onClick = {
                        navigateToHomeScreen() },
                ) {
                    Text("OK")
                }

            }
        )
    }
}

fun dateSelectDialog(context: Context, viewmodel: RegistrationViewModel) {
    val calendar = Calendar.getInstance()
    val dialog = DatePickerDialog(
        ContextThemeWrapper(context, R.style.BlueDatePickerDialogTheme), // added theme
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = formatter.format(selectedDate.time)
            viewmodel.onDobChange(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    dialog.setOnShowListener {
        // make OK / CANCEL buttons blue
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context,R.color.blue_500))
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context,R.color.blue_500))
    }
    dialog.show()
}

@Composable
fun PersonalScreen(viewModel: RegistrationViewModel,
                   dobOnClick :() -> Unit,onOtpClick:(String)->Unit, onOtpVerify:(String, String) -> Unit) {
    var state = viewModel.state
    val uiState by viewModel.uiState.collectAsState() // observe API call state
    Column {
        // --- Name field ---
        CustomInputField(
            value = state.name,
            error = state.nameError,
            onValueChange = { viewModel.onNameChange(it) },
            placeholder = "Enter your name",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        // --- DOB field ---
        CustomInputField(
            value = state.dob,
            error = state.dobError,
            onValueChange = { viewModel.onDobChange(it) },
            placeholder = "Select Date of Birth",
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
             trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { dobOnClick() }
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
        // --- Mobile number field ---
        CustomInputField(
            value = state.mobile,
            error = state.mobileError,
            onValueChange = { viewModel.onMobileChange(it) },
            placeholder = "Enter your mobile number",
            modifier = Modifier.fillMaxWidth(),
            options=InputOptions( keyboardType = KeyboardType.Number, maxLength = 10,
                onlyDigits = true),
        )

        Spacer(modifier = Modifier.height(12.dp))
        if(uiState == RegistrationState.OtpSent){
            CustomInputField(
                value = state.otp,
                error = state.otpError,
                onValueChange = { viewModel.onOtpChange(it) },
                placeholder = "Enter OTP",
                modifier = Modifier.fillMaxWidth(),
                options = InputOptions(
                    keyboardType = KeyboardType.Number,
                    maxLength = 6,
                    onlyDigits = true
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            GradientButton(
                onClick = {
                    val error = viewModel.validateOtp()

                    if (error == null) {
                        onOtpVerify(state.mobile,state.otp)
                    }else{
                        viewModel.updateOtpError(error)
                    }
                }
            ) {
                Text("Verify OTP")
            }
        }else{
            // --- Send OTP button (before OTP sent) ---
            GradientButton(
                onClick = {
                    val error = viewModel.validateMobile()

                    if (error == null) {
                        onOtpClick(state.mobile)
                    }else{
                        viewModel.updateMobileError(error)
                    }
                },
                enabled = uiState != RegistrationState.Loading
            ) {
                if (uiState == RegistrationState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Send OTP")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            value = state.email,
            error = state.emailError,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "Enter your email",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddressScreen(viewModel: RegistrationViewModel) {

    val state = viewModel.addressState

    Column {

        CustomInputField(
            value = state.address,
            error = state.addressError,
            onValueChange = { viewModel.onAddressChange(it) },
            placeholder = "Enter your complete address",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            value = state.pinCode,
            error = state.pinError,
            onValueChange = { viewModel.onPinCodeChange(it) },
            placeholder = "Enter 6-digit PIN code",
            options=InputOptions( keyboardType = KeyboardType.Number,
                maxLength = 6,
                onlyDigits = true),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FinancialScreen(viewModel: RegistrationViewModel) {


        val state = viewModel.financialState

        Column {

            CustomInputField(
                value = state.cibilScore,
                error = state.cibilError,
                onValueChange = { viewModel.onCibilChange(it) },
                placeholder = "Enter CIBIL score (300-900)",

                options=InputOptions( keyboardType = KeyboardType.Number,
                    onlyDigits = true),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = state.presentEmi,
                error = state.emiError,
                onValueChange = { viewModel.onPresentEmiChange(it) },
                placeholder = "Enter current monthly EMI (0 if none)",
                options=InputOptions( keyboardType = KeyboardType.Number,
                    onlyDigits = true),

                modifier = Modifier.fillMaxWidth()
            )
        }

}


@Composable
fun LoanApplicationScreenUI(
    onNextButtonClick: () -> Unit,
    onBackClick: () -> Unit,
    onBackNavigationClick:() -> Unit,
    currentStep: Int,
    screens: LoanScreens
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .systemBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .imePadding(),   // apply here instead of root
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeaderGradient, RoundedCornerShape(18.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {

                    IconButton(
                        onClick = { onBackNavigationClick() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Apply for a Loan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fill in the details below to start your loan application",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            StepIndicator(currentStep)

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    when (currentStep) {
                        1 -> screens.personal()
                        2 -> screens.loanDetails()
                        3 -> screens.financial()
                        4 -> screens.address()

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        /* Back Button */

                        if (currentStep > 1) {
                            OutlinedButton(
                                onClick = { onBackClick() },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Back")
                            }
                        }

                        /* Next Button */

                        GradientButton(
                            onClick = { onNextButtonClick() },
                            modifier = Modifier
                                .height(44.dp)
                        ) {
                            Text(
                                if (currentStep == 4) "Submit Application"
                                else "Next Step"
                            )
                        }
                    }


                }
            }
        }
    }

}


@Composable
fun LoanDetailsScreen(
    viewModel: RegistrationViewModel,

) {

    val state = viewModel.loanState

    Column {

        /* Loan Type */

        Text(
            text = "Loan Type *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        LoanTypeDropdown(
            selected = state.loanType,
            onSelected = { viewModel.onLoanTypeChange(it) },
            placeholder = "Select Loan Type",
            error = state.loanTypeError
        )


        Spacer(modifier = Modifier.height(16.dp))

        /* Monthly Income */

        Text(
            text = "Monthly Income (₹) *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        CustomInputField(
            value = state.monthlyIncome,
            error = state.incomeError,
            onValueChange = { viewModel.onMonthlyIncomeChange(it) },
            placeholder = "Enter monthly income",
            options=InputOptions( keyboardType = KeyboardType.Number,
                onlyDigits = true),
        )

        Spacer(modifier = Modifier.height(16.dp))

        /* Employment Type */

        Text(
            text = "Employment Type *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        EmploymentOption(
            title = EMPLOYMENT_SALARIED,
            selected = state.employmentType == EMPLOYMENT_SALARIED,
            onClick = { viewModel.onEmploymentTypeChange(EMPLOYMENT_SALARIED) }
        )

        EmploymentOption(
            title = EMPLOYMENT_SELF_EMPLOYED,
            selected = state.employmentType == EMPLOYMENT_SELF_EMPLOYED,
            onClick = { viewModel.onEmploymentTypeChange(EMPLOYMENT_SELF_EMPLOYED) }
        )

        EmploymentOption(
            title = EMPLOYMENT_PRIVATE,
            selected = state.employmentType == EMPLOYMENT_PRIVATE,
            onClick = { viewModel.onEmploymentTypeChange(EMPLOYMENT_PRIVATE) }
        )

        EmploymentOption(
            title = EMPLOYMENT_GOVERNMENT,
            selected = state.employmentType == EMPLOYMENT_GOVERNMENT,
            onClick = { viewModel.onEmploymentTypeChange(EMPLOYMENT_GOVERNMENT) }
        )

        state.employmentError?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))


    }
}
@Composable
fun LoanTypeDropdown(
    selected: String,
    placeholder: String,
    error: String?,
    onSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val loanTypes = listOf(
        "Personal Loan",
        "Home Loan",
        "Business Loan",
        "Car Loan"
    )

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = FieldBg,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(1.dp, FieldBorder, RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (selected.isEmpty()) placeholder else selected,
                    color = if (selected.isEmpty()) TextSecondary else TextPrimary,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            loanTypes.forEach {

                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }

        if (!error.isNullOrEmpty()) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun EmploymentOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(
                1.dp,
                if (selected) BluePrimary else FieldBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },

        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = title,
            fontSize = 14.sp,
            color = TextPrimary
        )
    }
}
@Composable
fun StepIndicator(currentStep: Int) {

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {

        StepItem(1, "Personal", currentStep >= 1)
        StepItem(2, "Loan", currentStep >= 2)
        StepItem(3, "Financial", currentStep >= 3)
        StepItem(4, "Address", currentStep >= 4)

    }
}

@Composable
fun StepItem(number: Int, title: String, active: Boolean) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (active) BlueDeep else Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color= MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            color= MaterialTheme.colorScheme.onSurface
        )
    }
}

@SuppressLint("ModifierParameter")
@Composable
fun CustomInputField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    options: InputOptions = InputOptions(),
    onClick: (() -> Unit)? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    readOnly: Boolean = false
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FieldBg, RoundedCornerShape(12.dp))
                .border(1.dp, FieldBorder, RoundedCornerShape(12.dp))
                .clickableIfNotNull(onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                BasicTextField(
                    value = value,
                    onValueChange = { input ->
                        if (onClick != null) return@BasicTextField
                        onValueChange(input.filterInput(options))
                    },
                    readOnly = readOnly,
                    textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = options.keyboardType),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )
                // Right icon
                trailingIcon?.invoke()
            }
        }

        error?.takeIf { it.isNotEmpty() }?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/** Helper extension for input filtering */
private fun String.filterInput(options: InputOptions): String {
    var result = this
    if (options.onlyDigits) result = result.filter { it.isDigit() }
    if (options.maxLength != null) result = result.take(options.maxLength)
    return result
}

/** Helper extension for clickable modifier */
private fun Modifier.clickableIfNotNull(action: (() -> Unit)?): Modifier =
    if (action != null) this.clickable { action() } else this

@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = shape,
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = modifier
            .clip(shape)
            .background(
                brush = if (enabled) PrimaryButtonGradient else Brush.horizontalGradient(
                    colors = listOf(BluePrimary.copy(alpha = 0.5f), BlueIndigo.copy(alpha = 0.5f))
                )
            )
    ) {
        content()
    }
}
@Preview(showBackground = true)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoanApplicationScreenPreview() {
    MaterialTheme {
        // Root container to give size in preview
        Box(modifier = Modifier.fillMaxSize()) {
            LoanApplicationScreenUI(
                onNextButtonClick = {},
                onBackClick = {},
                currentStep = 1,

                onBackNavigationClick = { },
                screens = LoanScreens(
                    personal = {  Text("Personal Details Screen") },
                    loanDetails = { Text("Loan Details Screen") },
                    financial = { Text("Financial Details Screen") },
                    address = {  Text("Address Screen") }
                )
            )
        }
    }
}

