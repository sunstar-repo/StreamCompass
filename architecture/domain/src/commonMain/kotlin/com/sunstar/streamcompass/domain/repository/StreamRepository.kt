package com.sunstar.streamcompass.domain.repository

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.model.TvSuggestionType
import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<MovieStream>>

    fun getTvSuggestionStreamFlow(type: TvSuggestionType): Flow<PagingData<TvStream>>

    suspend fun getTrendingStream(): List<Stream>

    suspend fun getStreamDetail(tmdbId: Int, locale: String): MovieStreamDetail

    suspend fun recordMovieHistory(movieStream: MovieStream)

    suspend fun recordTvHistory(tvStream: TvStream)

    fun getMovieHistoryStreamFlow(): Flow<List<MovieStream>>

    fun getTvHistoryStreamFlow(): Flow<List<TvStream>>

    suspend fun removeMovieHistory(tmdbId: Int)

    suspend fun removeTvHistory(tmdbId: Int)
}
