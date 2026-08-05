package com.sunstar.streamcompass.domain.repository

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.SuggestionType
import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<MovieStream>>

    suspend fun getStreamDetail(streamId: Int): MovieStreamDetail
}
