package com.wingspan.loanapp.ui.theme.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.ArrowBack
import com.wingspan.loanapp.data.InputOptions
import com.wingspan.loanapp.data.LoanScreens
import com.wingspan.loanapp.utils.NetworkUtils
import com.wingspan.loanapp.viewmodel.RegistrationViewModel

// --- Constants for employment types ---
private const val EMPLOYMENT_SALARIED = "Salaried"
private const val EMPLOYMENT_SELF_EMPLOYED = "Self Employed"
private const val EMPLOYMENT_PRIVATE = "Private Employee"
private const val EMPLOYMENT_GOVERNMENT = "Government Employee"

@Composable
fun LoanApplicationScreen(onBackNavigationClick :() ->Unit,viewmodel: RegistrationViewModel=hiltViewModel()) {

    var context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }



    // --- Local helper functions ---
    fun handleStep1(): Boolean = viewmodel.validatePersonalDetails().also { valid ->
        if (valid) currentStep++
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
            // viewmodel.submitApplication()
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
            personal = { PersonalScreen(viewmodel) },
            loanDetails = { LoanDetailsScreen(viewmodel) },
            financial = { FinancialScreen(viewmodel) },
            address = { AddressScreen(viewmodel) }
        )
    )


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
fun PersonalScreen(viewModel: RegistrationViewModel) {


    val state = viewModel.state

    Column {

        CustomInputField(
            value = state.name,
            error = state.nameError,
            onValueChange = { viewModel.onNameChange(it) },
            placeholder = "Enter your name",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            value = state.dob,
            error = state.dobError,
            onValueChange = { viewModel.onDobChange(it) },
            placeholder = "Date of Birth",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

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
fun LoanApplicationScreenUI(
    onNextButtonClick: () -> Unit,
    onBackClick: () -> Unit,
    onBackNavigationClick:() -> Unit,
    currentStep: Int,
    screens: LoanScreens
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {

            IconButton(
                onClick = { onBackNavigationClick() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Apply for a Loan",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Fill in the details below to start your loan application",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        StepIndicator(currentStep)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
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
                            onClick = { onBackClick() }
                        ) {
                            Text("Back")
                        }
                    }

                    /* Next Button */

                    Button(
                        onClick = { onNextButtonClick() }
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
                    color = Color(0xFFEFEFEF),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (selected.isEmpty()) placeholder else selected,
                    color = if (selected.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
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
                if (selected) Color(0xFF2563EB) else Color.LightGray,
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
            fontSize = 14.sp
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
                .background(if (active) Color(0xFF1E3A8A) else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 12.sp
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
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFEFEF), RoundedCornerShape(10.dp))
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
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = options.keyboardType),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color(0xFF9E9E9E),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )
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
@Preview(showBackground = true)
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
                    personal = {  },
                    loanDetails = {  },
                    financial = { },
                    address = {  }
                )
            )
        }
    }
}
