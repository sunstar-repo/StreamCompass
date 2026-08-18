package com.sunstar.streamcompass.presentation.core

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map

inline fun <reified R : Any> PagingData<*>.filterIsInstance(): PagingData<R> =
    filter { it is R }.map { it as R }
