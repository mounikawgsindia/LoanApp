package com.wingspan.loanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wingspan.loanapp.ui.theme.LoanAppTheme
import com.wingspan.loanapp.ui.theme.screens.HomeScreen
import com.wingspan.loanapp.ui.theme.screens.LoanApplicationScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoanAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(){
    var navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("loan") {
            LoanApplicationScreen(
                onBackNavigationClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("home") {
            HomeScreen(navigateToLoanScreen ={
                navController.navigate("loan")
            })
        }

    }
}