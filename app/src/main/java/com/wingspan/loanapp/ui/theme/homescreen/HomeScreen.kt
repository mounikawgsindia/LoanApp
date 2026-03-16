package com.wingspan.loanapp.ui.theme.homescreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wingspan.loanapp.R
import com.wingspan.loanapp.data.LoanProduct
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wingspan.loanapp.data.InputOptions
import com.wingspan.loanapp.data.LoanCaliculator
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.ui.theme.registration.CustomInputField
import com.wingspan.loanapp.ui.theme.registration.GradientButton

import com.wingspan.loanapp.utils.NetworkUtils
import kotlin.math.pow

@Composable
fun HomeScreen(navigateToLoanScreen:()->Unit,viewmodel: HomeScreenViewmodel = hiltViewModel()) {

    val uiState by viewmodel.uiState.collectAsState()
    val context =LocalContext.current
    LaunchedEffect(uiState) {
        when(uiState){
            is HomeState.Error -> {
                val message = (uiState as HomeState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            HomeState.Loading -> {}
            HomeState.NumberVerify -> {
              //  Toast.makeText(context, "User Already Exist", Toast.LENGTH_SHORT).show()
            }
            HomeState.OtpSent -> {
                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            }
            HomeState.OtpVerified -> {
                Toast.makeText(context, "OTP Verified", Toast.LENGTH_SHORT).show()
            }

            HomeState.Idle -> {}

            HomeState.NumberNotVerify -> {
                Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT).show()

            }
        }
    }
    HomeScreenUI(
        navigateToLoanScreen = {
            navigateToLoanScreen()
        }
    )
}

@Composable
fun HomeScreenUI(navigateToLoanScreen :()->Unit) {
    var caliculatorDialog by remember { mutableStateOf(false) }
    val viewmodel: HomeScreenViewmodel= hiltViewModel()
    var context =LocalContext.current.applicationContext
    Scaffold(
        topBar = { AppTopBar() }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .padding(top = 70.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    HeroSection(
                        navigateToLoanScreen = {
                            navigateToLoanScreen()
                        },
                        onCaliculaterClick = {
                            viewmodel.resetCalculatorState()
                            caliculatorDialog=true
                        }
                    )
                }

                item {
                    LoanProductsSection(
                        onApplyNowClick = {
                            navigateToLoanScreen()
                        }
                    )
                }

                item {
                    WhyChooseSection()
                }
            }
        }
    }
    if(caliculatorDialog){
        Dialog(
            onDismissRequest = { caliculatorDialog = false }
        ) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {

                LoanCalculatorScreen(
                    viewmodel,
                    onCaliculateClick = {
                        val valid = viewmodel.caliculaterValidation()

                        if (!valid) return@LoanCalculatorScreen

                        if (NetworkUtils.isNetworkAvailable(context)) {
                            viewmodel.checkMobileNumberRegistered()
                        }
                    },
                    onOtpVerify = { number, otp ->
                        viewmodel.verifyOtp(OtpVerifyRequest(number, otp))
                    },
                    onNameClick = {
                        viewmodel.sendCaliculationOtp()
                    }
                )

            }
        }
    }
}

@Composable
fun LoanCalculatorScreen( viewModel: HomeScreenViewmodel,onCaliculateClick :  () ->Unit,
                          onNameClick : (String) ->Unit,onOtpVerify : (String,String)->Unit) {
    var state = viewModel.caliculaterState
    var emiResult by remember { mutableStateOf<Double?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Loan Calculator",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Estimate your monthly EMI before applying.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                CustomInputField(
                    value = state.loanAmount,
                    error = state.loanAmountError,
                    options = InputOptions( keyboardType = KeyboardType.Number,
                        onlyDigits = true),
                    onValueChange = { viewModel.onloanAmountChange(it) },
                    placeholder = "Loan Amount (₹)"
                )

                Spacer(modifier = Modifier.height(12.dp))



                CustomInputField(
                    value = state.intrestRate,
                    error = state.intrestRateError,
                    options = InputOptions( keyboardType = KeyboardType.Number,
                        onlyDigits = true),
                    onValueChange = { viewModel.onintrestRateChange(it) },
                    placeholder = "Interest Rate (%)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(
                    value = state.tenure,
                    error = state.tenureError,
                    options = InputOptions( keyboardType = KeyboardType.Number,
                        onlyDigits = true),
                    onValueChange = { viewModel.onTenureChange(it) },
                    placeholder = "Tenure (months)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(
                    value = state.mobile,
                    error = state.mobileError,
                    options = InputOptions( keyboardType = KeyboardType.Number,
                        onlyDigits = true),
                    onValueChange = { viewModel.onMobileChange(it) },
                    placeholder = "Enter Mobile Number"
                )

                Spacer(modifier = Modifier.height(20.dp))

                if(uiState == HomeState.NumberNotVerify){

                    CustomInputField(
                        value = state.name,
                        error = state.nameError,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = "Enter Name",
                        modifier = Modifier.fillMaxWidth(),

                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    GradientButton(
                        onClick = {
                            val error = viewModel.validateName()

                            if (error == null) {
                                onNameClick(state.name)
                            }else{
                                viewModel.updateNameError(error)
                            }
                        },
                        enabled = state != HomeState.Loading
                    ) {
                        if (uiState == HomeState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send OTP")
                        }
                    }

                }else if(uiState == HomeState.OtpSent){
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


                }
                else{
                    EmiCalculateButton(
                        uiState = uiState,
                        state = state,
                        onCalculateClick = onCaliculateClick,
                        onResult = { emiResult = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    if(uiState == HomeState.NumberVerify || uiState == HomeState.OtpVerified){
                        emiResult?.let {
                            Text(
                                text = "Monthly EMI: ₹${"%.2f".format(it)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }


                }


            }
        }
    }
}


@Composable
fun EmiCalculateButton(
    uiState: HomeState,
    state: LoanCaliculator,
    onCalculateClick: () -> Unit,
    onResult: (Double?) -> Unit
) {

    GradientButton(
        onClick = {

            val loan = state.loanAmount.toDoubleOrNull()
            val rate = state.intrestRate.toDoubleOrNull()
            val tenure = state.tenure.toIntOrNull()

            val emi = if (loan != null && rate != null && tenure != null) {
                calculateEmi(loan, rate, tenure)
            } else {
                null
            }

            onResult(emi)
            onCalculateClick()
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = uiState != HomeState.Loading
    ) {

        if (uiState == HomeState.Loading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Calculate EMI")
        }
    }
}


fun calculateEmi(
    loanAmount: Double,
    interestRate: Double,
    tenure: Int
): Double {

    val monthlyRate = interestRate / 12 / 100

    return (loanAmount * monthlyRate * (1 + monthlyRate).pow(tenure)) /
            ((1 + monthlyRate).pow(tenure) - 1)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {

    TopAppBar(
        title = {
            Text(
                text = "QuickLoanPro",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
fun HeroSection(navigateToLoanScreen : ()->Unit,onCaliculaterClick:()->Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2563EB),
                                Color(0xFF4F46E5)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Welcome back 👋",
                                color = Color(0xFFE0ECFF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Find the perfect loan\nfor your next move.",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Pre‑approved offers in minutes with zero paperwork.",
                                color = Color(0xFFCBD5FF),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                        }

                        Spacer(modifier = Modifier.size(12.dp))

                        Image(
                            painter = painterResource(id = R.drawable.home_loan),
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        // Loan Calculator Button (Left)
                        Button(
                            onClick = { onCaliculaterClick() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE5E7EB),
                                contentColor =Color(0xFF2563EB)
                            ),
                            shape = RoundedCornerShape(50),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Loan Calculator",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        // Start Application Button (Right)
                        Button(
                            onClick = { navigateToLoanScreen() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF2563EB)
                            ),
                            shape = RoundedCornerShape(50),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Start Application",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.size(4.dp))

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }

            }
        }


    }
}


@Composable
fun LoanProductsSection(onApplyNowClick:()->Unit) {


    val loanList = listOf(
        LoanProduct("Personal Loan", R.drawable.personal_loan),
        LoanProduct("Home Loan", R.drawable.home_loan),
        LoanProduct("Study Loan", R.drawable.study_loan),
        LoanProduct("Business Loan", R.drawable.business_loan),
        LoanProduct("Gold Loan", R.drawable.gold_loan),
        LoanProduct("Car Loan", R.drawable.car_loan),
        LoanProduct("Bike Loan", R.drawable.bike_loan)
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Our Loan Products",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${loanList.size} options",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Choose what fits your need",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )

        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(420.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(loanList) { loan ->
                LoanCard(
                    title = loan.title,
                    image = loan.image,
                    onApplyNowClick =  {
                        onApplyNowClick()
                    }
                )
            }
        }
    }
}
@Composable
fun LoanCard(
    title: String,
    image: Int, onApplyNowClick:() ->Unit
) {

    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp) // fixed height
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFEEF2FF),
                                Color(0xFFE0F2FE)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = image),
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5.dp)),
                    contentScale = ContentScale.Crop ,
                    )


            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF0F172A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Apply now",
                modifier = Modifier.clickable{
                    onApplyNowClick()
                },
                color = Color(0xFF2563EB),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
@Composable
fun WhyChooseSection() {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Why Choose QuickLoanPro?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeatureCard(
                title = "100% Secure",
                icon = Icons.Default.Security,
                cardColor = Color(0xFFE8F0FE), // light blue
                modifier = Modifier.weight(1f)
            )

            FeatureCard(
                title = "Instant Processing",
                icon = Icons.Default.FlashOn,
                cardColor = Color(0xFFEFF6FF), // soft sky blue
                modifier = Modifier.weight(1f)
            )

            FeatureCard(
                title = "Multiple Loan Types",
                icon = Icons.Default.AccountBalance,
                cardColor = Color(0xFFE6F7F1), // light green
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    cardColor: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF1E40AF),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreenUI(navigateToLoanScreen={})
}