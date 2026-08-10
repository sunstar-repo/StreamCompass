package com.sunstar.streamcompass.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sunstar.streamcompass.presentation.home.HomeScreen
import com.sunstar.streamcompass.presentation.movie.MovieScreen
import com.sunstar.streamcompass.presentation.search.SearchScreen
import com.sunstar.streamcompass.presentation.setting.SettingScreen
import com.sunstar.streamcompass.presentation.streamdetail.StreamDetailScreen
import com.sunstar.streamcompass.presentation.tv.TvScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            MainDestination.values().forEach { destination ->
                item(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(route = destination::class) } ?: false,
                    onClick = {
                        navController.navigate(route = destination) {
                            popUpTo(id = navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text(text = destination.icon) },
                    label = { Text(text = destination.label) },
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                PresenterTopBar(
                    onSearchClick = { navController.navigate(route = MainDestination.SearchDestination) },
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MainDestination.HomeDestination,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable<MainDestination.HomeDestination> {
                    HomeScreen(
                        onStreamClick = { tmdbId ->
                            navController.navigate(route = MainDestination.StreamDetailDestination(tmdbId = tmdbId))
                        },
                    )
                }
                composable<MainDestination.MovieDestination> { MovieScreen() }
                composable<MainDestination.TvDestination> { TvScreen() }
                composable<MainDestination.SettingDestination> { SettingScreen() }
                composable<MainDestination.StreamDetailDestination> { backStackEntry ->
                    val destination: MainDestination.StreamDetailDestination = backStackEntry.toRoute()
                    StreamDetailScreen(tmdbId = destination.tmdbId)
                }
                composable<MainDestination.SearchDestination> { SearchScreen() }
            }
        }
    }
}

@Composable
private fun PresenterTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "StreamCompass") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Text(text = "🔍")
            }
        },
    )
}
