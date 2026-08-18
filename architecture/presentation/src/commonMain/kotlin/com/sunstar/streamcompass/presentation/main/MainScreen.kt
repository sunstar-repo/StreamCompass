package com.sunstar.streamcompass.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.detail.DetailScreen
import com.sunstar.streamcompass.presentation.home.HomeScreen
import com.sunstar.streamcompass.presentation.home.HomeViewModel
import com.sunstar.streamcompass.presentation.movie.MovieScreen
import com.sunstar.streamcompass.presentation.search.SearchScreen
import com.sunstar.streamcompass.presentation.setting.SettingScreen
import com.sunstar.streamcompass.presentation.tv.TvScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.app_title
import streamcompass.architecture.presentation.generated.resources.ic_search

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val currentMainDestination = currentDestination?.let { destination ->
        MainDestination.allValues().firstOrNull { entry ->
            destination.hierarchy.any { it.hasRoute(route = entry::class) }
        }
    }
    val isNavigationUsed = currentMainDestination?.isNavigationUsed ?: true
    val isTopBarUsed = currentMainDestination?.isTopBarUsed ?: true

    val homeScrollState = rememberScrollState()
    val movieScrollState = rememberScrollState()
    val tvScrollState = rememberScrollState()
    val isScrolledToTop = when (currentMainDestination) {
        MainDestination.HomeDestination -> homeScrollState.value == 0
        MainDestination.MovieDestination -> movieScrollState.value == 0
        MainDestination.TvDestination -> tvScrollState.value == 0
        else -> true
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
        topBar = {
            AnimatedVisibility(
                visible = isTopBarUsed && isScrolledToTop,
                enter = slideInVertically(initialOffsetY = { -it }) + expandVertically(expandFrom = Alignment.Top),
                exit = slideOutVertically(targetOffsetY = { -it }) + shrinkVertically(shrinkTowards = Alignment.Top),
            ) {
                MainTopBar(
                    onSearchClick = { navController.navigate(route = MainDestination.SearchDestination) },
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isNavigationUsed && isScrolledToTop,
                enter = slideInVertically(initialOffsetY = { it }) + expandVertically(expandFrom = Alignment.Bottom),
                exit = slideOutVertically(targetOffsetY = { it }) + shrinkVertically(shrinkTowards = Alignment.Bottom),
            ) {
                MainBottomBar(
                    currentDestination = currentDestination,
                    onDestinationClick = { destination ->
                        navController.navigate(route = destination) {
                            popUpTo(id = navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = MainDestination.HomeDestination,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable<MainDestination.HomeDestination> {
                    HomeScreen(
                        scrollState = homeScrollState,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this,
                        onStreamClick = { tmdbId, posterPath, rowId, streamType ->
                            navController.navigate(
                                route = MainDestination.DetailDestination(
                                    tmdbId = tmdbId,
                                    posterPath = posterPath,
                                    rowId = rowId,
                                    recordHistory = rowId != HomeViewModel.RowType.MovieHistory.id,
                                    streamType = streamType.rawValue,
                                )
                            )
                        },
                    )
                }
                composable<MainDestination.MovieDestination> {
                    MovieScreen(
                        scrollState = movieScrollState,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this,
                        onStreamClick = { tmdbId, posterPath, rowId, streamType ->
                            navController.navigate(
                                route = MainDestination.DetailDestination(
                                    tmdbId = tmdbId,
                                    posterPath = posterPath,
                                    rowId = rowId,
                                    recordHistory = rowId != HomeViewModel.RowType.MovieHistory.id,
                                    streamType = streamType.rawValue,
                                )
                            )
                        },
                    )
                }
                composable<MainDestination.TvDestination> {
                    TvScreen(
                        scrollState = tvScrollState,
                        onStreamClick = { tmdbId, posterPath, rowId, streamType ->
                            navController.navigate(
                                route = MainDestination.DetailDestination(
                                    tmdbId = tmdbId,
                                    posterPath = posterPath,
                                    rowId = rowId,
                                    recordHistory = rowId != HomeViewModel.RowType.MovieHistory.id,
                                    streamType = streamType.rawValue,
                                )
                            )
                        },
                    )
                }
                composable<MainDestination.SettingDestination> { SettingScreen() }
                composable<MainDestination.DetailDestination> { backStackEntry ->
                    val destination: MainDestination.DetailDestination =
                        backStackEntry.toRoute()
                    DetailScreen(
                        tmdbId = destination.tmdbId,
                        posterPath = destination.posterPath,
                        rowId = destination.rowId,
                        recordHistory = destination.recordHistory,
                        streamType = StreamType.from(rawValue = destination.streamType),
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this,
                        onStreamClick = { tmdbId, posterPath, rowId, streamType ->
                            navController.navigate(
                                route = MainDestination.DetailDestination(
                                    tmdbId = tmdbId,
                                    posterPath = posterPath,
                                    rowId = rowId,
                                    recordHistory = rowId != HomeViewModel.RowType.MovieHistory.id,
                                    streamType = streamType.rawValue,
                                )
                            )
                        },
                    )
                }
                composable<MainDestination.SearchDestination> { SearchScreen() }
            }
        }
    }
}

@Composable
private fun MainTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(Res.string.app_title)) },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(painter = painterResource(Res.drawable.ic_search), contentDescription = null)
            }
        },
    )
}

@Composable
private fun MainBottomBar(
    currentDestination: NavDestination?,
    onDestinationClick: (MainDestination) -> Unit,
) {
    NavigationBar {
        MainDestination.values().forEach { destination ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(route = destination::class) }
                    ?: false,
                onClick = { onDestinationClick(destination) },
                icon = {
                    Icon(
                        painter = painterResource(destination.icon),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(destination.label)) },
            )
        }
    }
}
