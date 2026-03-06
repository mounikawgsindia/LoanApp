package com.wingspan.loanapp.ui.theme.screens

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import com.wingspan.loanapp.utils.NetworkUtils
import com.wingspan.loanapp.viewmodel.RegistrationViewModel

@Composable
fun LoanApplicationScreen(onBackNavigationClick :() ->Unit,viewmodel: RegistrationViewModel= hiltViewModel()) {
    val state = viewmodel.state
    var context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }


    LoanApplicationScreenUI(

        onNextButtonClick = {
            when (currentStep) {

                /* Step 1 Validation */
                1 -> {
                    if (!viewmodel.validatePersonalDetails()) return@LoanApplicationScreenUI
                    currentStep++
                }

                2 -> {
                    if (!viewmodel.validateLoanDetails()) return@LoanApplicationScreenUI
                    currentStep++
                }

                3 -> {
                    if (!viewmodel.validateFinancialDetails()) return@LoanApplicationScreenUI
                    currentStep++
                }

                4 -> {
                    if (!viewmodel.validateAddressDetails()) return@LoanApplicationScreenUI
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        //  viewmodel.submitApplication()
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        },
        currentStep = currentStep,
        personalScreen = {
            PersonalScreen(viewmodel)
        },
        loanDetailsScreen = {
            LoanDetailsScreen(
                viewmodel,
            )
        },
        financialScreen = {
            FinancialScreen(viewmodel)
        },
        addressScreen = {
            AddressScreen(viewmodel)
        },
        onBackClick = {
            currentStep--
        },
        onBackNavigationClick = {
            onBackNavigationClick()
        }
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
            keyboardType = KeyboardType.Number,
            maxLength = 6,
            onlyDigits = true,
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
                keyboardType = KeyboardType.Number,
                onlyDigits = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = state.presentEmi,
                error = state.emiError,
                onValueChange = { viewModel.onPresentEmiChange(it) },
                placeholder = "Enter current monthly EMI (0 if none)",
                keyboardType = KeyboardType.Number,
                onlyDigits = true,
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
            keyboardType = KeyboardType.Number,
            maxLength = 10,
            onlyDigits = true
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
    personalScreen: @Composable () -> Unit,
    loanDetailsScreen: @Composable () -> Unit,
    financialScreen: @Composable () -> Unit,
    addressScreen: @Composable () -> Unit
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
                    imageVector = Icons.Default.ArrowBack,
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
                    1 -> personalScreen()
                    2 -> loanDetailsScreen()
                    3 -> financialScreen()
                   4 -> addressScreen()

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
            keyboardType = KeyboardType.Number,
            onlyDigits = true
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
            title = "Salaried",
            selected = state.employmentType == "Salaried",
            onClick = { viewModel.onEmploymentTypeChange("Salaried") }
        )

        EmploymentOption(
            title = "Self Employed",
            selected = state.employmentType == "Self Employed",
            onClick = { viewModel.onEmploymentTypeChange("Self Employed") }
        )

        EmploymentOption(
            title = "Private Employee",
            selected = state.employmentType == "Private Employee",
            onClick = { viewModel.onEmploymentTypeChange("Private Employee") }
        )

        EmploymentOption(
            title = "Government Employee",
            selected = state.employmentType == "Government Employee",
            onClick = { viewModel.onEmploymentTypeChange("Government Employee") }
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



@Composable
fun CustomInputField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int? = null,
    onlyDigits: Boolean = false,
    onClick: (() -> Unit)? = null,
    isSingleLine: Boolean = true
) {

    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFEFEFEF), // Light Grey
                    shape = RoundedCornerShape(10.dp)
                )

                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                BasicTextField(
                    value = value,
                    onValueChange = {
                        if (onClick != null) return@BasicTextField

                        var filtered = it
                        if (onlyDigits) filtered = filtered.filter { ch -> ch.isDigit() }
                        if (maxLength != null) filtered = filtered.take(maxLength)

                        onValueChange(filtered)

                    },


                    minLines = if (isSingleLine) 1 else 5,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation =
                        if (isPassword && !passwordVisible)
                            PasswordVisualTransformation()
                        else
                            VisualTransformation.None,
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                color = Color(0xFF9E9E9E),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

              
            }
        }

        if (!error.isNullOrEmpty()) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
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
                personalScreen = {
                    Text("Personal Screen Preview")
                },
                loanDetailsScreen = {
                    Text("Loan Details Screen Preview")
                },
                financialScreen = {
                    Text("Financial Screen Preview")
                },
                addressScreen = {
                    Text("Address Screen Preview")
                },
                onBackNavigationClick = { }
            )
        }
    }
}
