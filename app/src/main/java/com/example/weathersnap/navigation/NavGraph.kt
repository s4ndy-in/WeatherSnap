package com.example.weathersnap.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersnap.ui.screens.WeatherScreen
import com.example.weathersnap.ui.screens.camera.CameraScreen
import com.example.weathersnap.ui.screens.report.ReportScreen
import com.example.weathersnap.ui.screens.report.SavedReportsScreen
import com.example.weathersnap.viewmodel.ReportViewModel
import com.example.weathersnap.viewmodel.WeatherViewModel

object Routes {
    const val WEATHER = "weather"
    const val CREATE_REPORT = "create_report"
    const val CAMERA = "camera"
    const val SAVED_REPORTS = "saved_reports"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    // ViewModel
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val reportViewModel: ReportViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER
    ) {
        composable(Routes.WEATHER) {
            WeatherScreen(
                viewModel = weatherViewModel,
                onCreateReport = { weatherData ->
                    reportViewModel.setWeatherData(weatherData)
                    navController.navigate(Routes.CREATE_REPORT)
                },
                onViewReports = {
                    navController.navigate(Routes.SAVED_REPORTS)
                }
            )
        }

        composable(Routes.CREATE_REPORT) {
            ReportScreen(
                viewModel = reportViewModel,
                onCapturePhoto = {
                    navController.navigate(Routes.CAMERA)
                },
                onReportSaved = {
                    navController.navigate(Routes.SAVED_REPORTS) {
                        popUpTo(Routes.WEATHER)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CAMERA) {
            CameraScreen(
                onImageCaptured = { originalPath, compressedPath, originalKb, compressedKb ->
                    reportViewModel.onImageCaptured(
                        originalPath, compressedPath, originalKb, compressedKb
                    )
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }

        composable(Routes.SAVED_REPORTS) {
            SavedReportsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}