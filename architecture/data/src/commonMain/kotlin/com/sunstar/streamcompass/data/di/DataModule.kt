package com.sunstar.streamcompass.data.di

import com.sunstar.streamcompass.data.datasource.streamingavailability.SaDataSource
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaShowDto
import com.sunstar.streamcompass.data.datasource.streamingavailability.mapper.SaShowMapper
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbMovieDetailMapper
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbMovieSummaryMapper
import com.sunstar.streamcompass.data.repository.StreamRepositoryImpl
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.repository.StreamRepository
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val TMDB_DATA_SOURCE = named("tmdbDataSource")
private val SA_DATA_SOURCE = named("saDataSource")
private val TMDB_MOVIE_SUMMARY_MAPPER = named("tmdbMovieSummaryMapper")
private val TMDB_MOVIE_DETAIL_MAPPER = named("tmdbMovieDetailMapper")
private val SA_SHOW_MAPPER = named("saShowMapper")
private val STREAM_REPOSITORY = named("streamRepository")

val dataModule =
    module {
        single(TMDB_DATA_SOURCE) { TmdbDataSource() }
        single(SA_DATA_SOURCE) { SaDataSource() }

        single<Mapper<TmdbMovieSummaryDto, MovieStream>>(TMDB_MOVIE_SUMMARY_MAPPER) { TmdbMovieSummaryMapper() }
        single<Mapper<TmdbMovieDetailDto, MovieStreamDetail>>(TMDB_MOVIE_DETAIL_MAPPER) { TmdbMovieDetailMapper() }
        single<Mapper<SaShowDto, List<Deeplink>>>(SA_SHOW_MAPPER) { SaShowMapper() }

        single<StreamRepository>(STREAM_REPOSITORY) {
            StreamRepositoryImpl(
                tmdbDataSource = get(TMDB_DATA_SOURCE),
                summaryMapper = get(TMDB_MOVIE_SUMMARY_MAPPER),
                detailMapper = get(TMDB_MOVIE_DETAIL_MAPPER),
                saDataSource = get(SA_DATA_SOURCE),
                saShowMapper = get(SA_SHOW_MAPPER),
            )
        }

        single { GetSuggestionStreamUseCase(get(STREAM_REPOSITORY)) }
    }
