package com.sunstar.streamcompass.data.di

import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbMovieDetailMapper
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbMovieSummaryMapper
import com.sunstar.streamcompass.data.repository.StreamRepositoryImpl
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.repository.StreamRepository
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val TMDB_MOVIE_SUMMARY_MAPPER = named("tmdbMovieSummaryMapper")
private val TMDB_MOVIE_DETAIL_MAPPER = named("tmdbMovieDetailMapper")

val dataModule =
    module {
        single { TmdbDataSource() }

        single<Mapper<TmdbMovieSummaryDto, Stream>>(TMDB_MOVIE_SUMMARY_MAPPER) { TmdbMovieSummaryMapper() }
        single<Mapper<TmdbMovieDetailDto, Stream>>(TMDB_MOVIE_DETAIL_MAPPER) { TmdbMovieDetailMapper() }

        single<StreamRepository> {
            StreamRepositoryImpl(
                tmdbDataSource = get(),
                summaryMapper = get(TMDB_MOVIE_SUMMARY_MAPPER),
                detailMapper = get(TMDB_MOVIE_DETAIL_MAPPER),
            )
        }

        single { GetSuggestionStreamUseCase(get()) }
    }
