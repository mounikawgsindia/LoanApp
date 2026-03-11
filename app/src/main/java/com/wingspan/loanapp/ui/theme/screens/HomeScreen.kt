package com.wingspan.loanapp.ui.theme.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.navigation.compose.rememberNavController
import com.wingspan.loanapp.R
import com.wingspan.loanapp.data.LoanProduct
import com.wingspan.loanapp.ui.theme.screens.HomeScreen
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme

@Composable
fun HomeScreen(navigateToLoanScreen:()->Unit) {

    HomeScreenUI(
        navigateToLoanScreen = {
            navigateToLoanScreen()
        }
    )

}

@Composable
fun HomeScreenUI(navigateToLoanScreen :()->Unit) {
    Scaffold(
        topBar = { AppTopBar() }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    HeroSection(navigateToLoanScreen={
                        navigateToLoanScreen()
                    })
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
fun HeroSection(navigateToLoanScreen : ()->Unit) {
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
                            fontSize = 20.sp,
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
                        Button(
                            onClick = { navigateToLoanScreen()},
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
                                imageVector =Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.home_loan),
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp)
                    )
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
                    modifier = Modifier.fillMaxSize(),
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