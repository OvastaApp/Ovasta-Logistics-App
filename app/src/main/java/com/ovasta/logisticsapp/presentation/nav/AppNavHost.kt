package com.ovasta.logisticsapp.presentation.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginScreen
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginViewModel
import com.ovasta.logisticsapp.presentation.auth.splash.SplashScreen
import com.ovasta.logisticsapp.presentation.auth.splash.SplashViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreen
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {}) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = SplashScreen,
            modifier = modifier.padding(paddingValues)
        ) {
            // Auth Screens
            composable<SplashScreen> {
                val viewModel: SplashViewModel = koinViewModel()
                SplashScreen(viewModel, navController)
            }

            composable<LoginScreen> {
                val viewModel: LoginViewModel = koinViewModel()
                LoginScreen(
                    viewModel, navController
                )
            }


            // Main Screens with Bottom Navigation
            composable<HomeScreen> {
                val viewModel: HomeViewModel = koinViewModel()
                HomeScreen(viewModel, navController)
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewHomeNavigationBar() {
    AppNavHost()
}