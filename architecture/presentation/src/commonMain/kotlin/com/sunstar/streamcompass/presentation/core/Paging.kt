package com.sunstar.streamcompass.presentation.core

import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.filter
import androidx.paging.map

inline fun <reified R : Any> PagingData<*>.filterIsInstance(): PagingData<R> =
    filter { it is R }.map { it as R }

val LazyPagingItems<*>.isInitialLoading: Boolean
    get() = itemCount == 0 && loadState.refresh is LoadState.Loading

val LazyPagingItems<*>.isInitialError: Boolean
    get() = itemCount == 0 && loadState.refresh is LoadState.Error

val LazyPagingItems<*>.isEmpty: Boolean
    get() = itemCount == 0 && loadState.refresh is LoadState.NotLoading
