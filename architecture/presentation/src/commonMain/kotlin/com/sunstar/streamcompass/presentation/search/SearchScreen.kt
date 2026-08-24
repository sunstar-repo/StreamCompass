package com.sunstar.streamcompass.presentation.search

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.core.LoadingIndicator
import com.sunstar.streamcompass.presentation.core.MessageIndicator
import com.sunstar.streamcompass.presentation.core.PosterCard
import com.sunstar.streamcompass.presentation.core.displayTitle
import com.sunstar.streamcompass.presentation.core.isInitialError
import com.sunstar.streamcompass.presentation.core.isInitialLoading
import com.sunstar.streamcompass.presentation.core.mediaRowItemKey
import com.sunstar.streamcompass.presentation.core.posterOverlayClip
import com.sunstar.streamcompass.presentation.core.posterPath
import com.sunstar.streamcompass.presentation.core.posterSharedElementKey
import com.sunstar.streamcompass.presentation.core.statusBarProtectionHeight
import com.sunstar.streamcompass.presentation.core.tmdbId
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.destination_movie
import streamcompass.architecture.presentation.generated.resources.destination_tv
import streamcompass.architecture.presentation.generated.resources.paging_load_failed
import streamcompass.architecture.presentation.generated.resources.search_hint

private val SEARCH_TABS = listOf(StreamType.Movie, StreamType.Tv)
private const val SEARCH_GRID_COLUMNS = 3

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    val focusManager = LocalFocusManager.current

    // 2-page(Results) 위로 History가 오버레이된 상태에서만 back을 가로채 Results로 되돌린다.
    SearchBackHandler(
        enabled = state.page == SearchViewModel.Page.History && null != state.activeQuery,
        onBack = viewModel::onHistoryOverlayDismissed,
    )

    Column(modifier = Modifier.fillMaxSize().padding(top = statusBarProtectionHeight())) {
        SearchTextField(
            queryText = state.queryText,
            onQueryChanged = viewModel::onQueryChanged,
            onFocused = viewModel::onSearchEditTextFocused,
            onSearch = {
                viewModel.onSearchSubmitted(state.queryText)
                focusManager.clearFocus()
            },
        )

        when (state.page) {
            SearchViewModel.Page.History -> SearchHistoryContent(
                historyFlow = state.history,
                onHistoryItemClick = { query ->
                    viewModel.onSearchSubmitted(query)
                    focusManager.clearFocus()
                },
            )

            SearchViewModel.Page.Results -> SearchResultsContent(
                selectedTab = state.selectedTab,
                onTabSelected = viewModel::onTabSelected,
                movieResults = state.movieResults,
                tvResults = state.tvResults,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onStreamClick = onStreamClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    queryText: String,
    onQueryChanged: (String) -> Unit,
    onFocused: () -> Unit,
    onSearch: () -> Unit,
) {
    OutlinedTextField(
        value = queryText,
        onValueChange = onQueryChanged,
        placeholder = { Text(text = stringResource(Res.string.search_hint)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .onFocusChanged { if (it.isFocused) onFocused() },
    )
}

@Composable
private fun SearchHistoryContent(
    historyFlow: Flow<List<String>>,
    onHistoryItemClick: (String) -> Unit,
) {
    val history by historyFlow.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = history.size, key = { index -> history[index] }) { index ->
            val query = history[index]
            TextButton(
                onClick = { onHistoryItemClick(query) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = query,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultsContent(
    selectedTab: StreamType,
    onTabSelected: (StreamType) -> Unit,
    movieResults: Flow<PagingData<Stream>>,
    tvResults: Flow<PagingData<Stream>>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onStreamClick: (tmdbId: Int, posterPath: String, rowId: String, streamType: StreamType) -> Unit,
) {
    val moviePagingItems = movieResults.collectAsLazyPagingItems()
    val tvPagingItems = tvResults.collectAsLazyPagingItems()
    val selectedTabIndex = SEARCH_TABS.indexOf(selectedTab)

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
            SEARCH_TABS.forEach { tab ->
                Tab(
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = stringResource(
                                if (tab == StreamType.Movie) Res.string.destination_movie else Res.string.destination_tv
                            )
                        )
                    },
                )
            }
        }

        val pagingItems = if (selectedTab == StreamType.Movie) moviePagingItems else tvPagingItems
        val rowId = SEARCH_ROW_ID_PREFIX + selectedTab.rawValue

        if (pagingItems.isInitialLoading) {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
            return@Column
        }

        if (pagingItems.isInitialError) {
            MessageIndicator(
                text = stringResource(Res.string.paging_load_failed),
                modifier = Modifier.fillMaxSize(),
            )
            return@Column
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(SEARCH_GRID_COLUMNS),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                count = pagingItems.itemCount,
                key = { index ->
                    mediaRowItemKey(
                        streamType = selectedTab,
                        rowId = rowId,
                        tmdbId = pagingItems[index]?.tmdbId,
                        index = index
                    )
                },
            ) { index ->
                val stream = pagingItems[index]
                if (null != stream) {
                    PosterCard(
                        imageUrl = stream.posterPath,
                        title = stream.displayTitle,
                        imageModifier = with(sharedTransitionScope) {
                            Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(
                                    key = posterSharedElementKey(
                                        streamType = selectedTab,
                                        rowId = rowId,
                                        tmdbId = stream.tmdbId,
                                    ),
                                ),
                                animatedVisibilityScope = animatedContentScope,
                                clipInOverlayDuringTransition = posterOverlayClip(),
                            )
                        },
                        onClick = {
                            onStreamClick(stream.tmdbId, stream.posterPath, rowId, selectedTab)
                        },
                    )
                }
            }
        }
    }
}

private const val SEARCH_ROW_ID_PREFIX = "search_"
