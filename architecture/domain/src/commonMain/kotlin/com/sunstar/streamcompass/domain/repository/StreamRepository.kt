package com.sunstar.streamcompass.domain.repository

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.SuggestionType
import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<Stream>>

    suspend fun getStream(streamId: Int): Stream
}
