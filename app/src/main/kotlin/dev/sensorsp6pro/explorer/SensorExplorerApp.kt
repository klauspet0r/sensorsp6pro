package dev.sensorsp6pro.explorer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sensorsp6pro.explorer.feature.dashboard.DashboardScreen
import dev.sensorsp6pro.explorer.feature.motion.AccelerationScreen

@Composable
fun SensorExplorerApp() {
    val controller = rememberNavController()
    NavHost(controller, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(onOpenAcceleration = { controller.navigate("sensor/acceleration") })
        }
        composable("sensor/acceleration") {
            AccelerationScreen()
        }
    }
}
