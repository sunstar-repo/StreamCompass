package com.sunstar.streamcompass.data.di

import com.sunstar.streamcompass.data.datasource.firestore.FirestoreDataSource
import com.sunstar.streamcompass.data.datasource.local.AppDatabase
import com.sunstar.streamcompass.data.datasource.local.LocalDataSource
import com.sunstar.streamcompass.data.datasource.local.createDatabase
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.mapper.LocalDeeplinkEntityMapper
import com.sunstar.streamcompass.data.datasource.local.mapper.LocalMovieDetailEntityMapper
import com.sunstar.streamcompass.data.datasource.local.mapper.LocalMovieHistoryEntityMapper
import com.sunstar.streamcompass.data.datasource.local.mapper.LocalTvDetailEntityMapper
import com.sunstar.streamcompass.data.datasource.local.mapper.LocalTvHistoryEntityMapper
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbReviewDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTrendingItemDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbMovieSummaryMapper
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbReviewMapper
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbTrendingItemMapper
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbTvSummaryMapper
import com.sunstar.streamcompass.data.repository.StreamRepositoryImpl
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.TvStreamDetail
import com.sunstar.streamcompass.domain.repository.StreamRepository
import com.sunstar.streamcompass.domain.usecase.GetHistoryStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetRecommendationsUseCase
import com.sunstar.streamcompass.domain.usecase.GetReviewsUseCase
import com.sunstar.streamcompass.domain.usecase.GetStreamDetailUseCase
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetTrendingStreamUseCase
import com.sunstar.streamcompass.domain.usecase.RecordHistoryUseCase
import com.sunstar.streamcompass.domain.usecase.RemoveHistoryUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val TMDB_DATA_SOURCE = named("tmdbDataSource")
private val SA_DATA_SOURCE = named("saDataSource")
private val TMDB_MOVIE_SUMMARY_MAPPER = named("tmdbMovieSummaryMapper")
private val TMDB_TV_SUMMARY_MAPPER = named("tmdbTvSummaryMapper")
private val TMDB_TRENDING_MAPPER = named("tmdbTrendingMapper")
private val TMDB_REVIEW_MAPPER = named("tmdbReviewMapper")
private val STREAM_REPOSITORY = named("streamRepository")
private val APP_DATABASE = named("appDatabase")
private val MOVIE_DETAIL_DAO = named("movieDetailDao")
private val TV_DETAIL_DAO = named("tvDetailDao")
private val DEEPLINK_DAO = named("deeplinkDao")
private val MOVIE_HISTORY_DAO = named("movieHistoryDao")
private val TV_HISTORY_DAO = named("tvHistoryDao")
private val LOCAL_DATA_SOURCE = named("localDataSource")
private val MOVIE_DETAIL_ENTITY_MAPPER = named("movieDetailEntityMapper")
private val TV_DETAIL_ENTITY_MAPPER = named("tvDetailEntityMapper")
private val DEEPLINK_ENTITY_MAPPER = named("deeplinkEntityMapper")
private val MOVIE_HISTORY_ENTITY_MAPPER = named("movieHistoryEntityMapper")
private val TV_HISTORY_ENTITY_MAPPER = named("tvHistoryEntityMapper")
private val FIRESTORE = named("firestore")
private val FIRESTORE_DATA_SOURCE = named("firestoreDataSource")

fun dataModule(apiKey: ApiKey): Module =
    module {
        single(TMDB_DATA_SOURCE) { TmdbDataSource(apiKey = apiKey.tmdbKey) }
        single(SA_DATA_SOURCE) { SaDataSource(apiKey = apiKey.saKey) }

        single<Mapper<TmdbMovieSummaryDto, MovieStream>>(TMDB_MOVIE_SUMMARY_MAPPER) { TmdbMovieSummaryMapper() }
        single<Mapper<TmdbTvSummaryDto, TvStream>>(TMDB_TV_SUMMARY_MAPPER) { TmdbTvSummaryMapper() }
        single<Mapper<TmdbTrendingItemDto, Stream>>(TMDB_TRENDING_MAPPER) { TmdbTrendingItemMapper() }
        single<Mapper<TmdbReviewDto, Review>>(TMDB_REVIEW_MAPPER) { TmdbReviewMapper() }
        single<Mapper<LocalMovieDetailEntity, MovieStreamDetail>>(MOVIE_DETAIL_ENTITY_MAPPER) {
            LocalMovieDetailEntityMapper()
        }
        single<Mapper<LocalTvDetailEntity, TvStreamDetail>>(TV_DETAIL_ENTITY_MAPPER) {
            LocalTvDetailEntityMapper()
        }
        single<Mapper<LocalDeeplinkEntity, Deeplink>>(DEEPLINK_ENTITY_MAPPER) { LocalDeeplinkEntityMapper() }
        single<Mapper<LocalMovieHistoryEntity, MovieStream>>(MOVIE_HISTORY_ENTITY_MAPPER) {
            LocalMovieHistoryEntityMapper()
        }
        single<Mapper<LocalTvHistoryEntity, TvStream>>(TV_HISTORY_ENTITY_MAPPER) {
            LocalTvHistoryEntityMapper()
        }

        single<AppDatabase>(APP_DATABASE) { createDatabase() }
        single(MOVIE_DETAIL_DAO) { get<AppDatabase>(qualifier = APP_DATABASE).movieDetailDao() }
        single(TV_DETAIL_DAO) { get<AppDatabase>(qualifier = APP_DATABASE).tvDetailDao() }
        single(DEEPLINK_DAO) { get<AppDatabase>(qualifier = APP_DATABASE).deeplinkDao() }
        single(MOVIE_HISTORY_DAO) { get<AppDatabase>(qualifier = APP_DATABASE).movieHistoryDao() }
        single(TV_HISTORY_DAO) { get<AppDatabase>(qualifier = APP_DATABASE).tvHistoryDao() }
        single(LOCAL_DATA_SOURCE) {
            LocalDataSource(
                movieDetailDao = get(qualifier = MOVIE_DETAIL_DAO),
                tvDetailDao = get(qualifier = TV_DETAIL_DAO),
                deeplinkDao = get(qualifier = DEEPLINK_DAO),
                movieHistoryDao = get(qualifier = MOVIE_HISTORY_DAO),
                tvHistoryDao = get(qualifier = TV_HISTORY_DAO),
            )
        }

        single<FirebaseFirestore>(FIRESTORE) { Firebase.firestore }
        single(FIRESTORE_DATA_SOURCE) { FirestoreDataSource(firestore = get(qualifier = FIRESTORE)) }

        single<StreamRepository>(STREAM_REPOSITORY) {
            StreamRepositoryImpl(
                tmdbDataSource = get(qualifier = TMDB_DATA_SOURCE),
                saDataSource = get(qualifier = SA_DATA_SOURCE),
                localDataSource = get(qualifier = LOCAL_DATA_SOURCE),
                firestoreDataSource = get(qualifier = FIRESTORE_DATA_SOURCE),
                movieSummaryMapper = get(qualifier = TMDB_MOVIE_SUMMARY_MAPPER),
                tvSummaryMapper = get(qualifier = TMDB_TV_SUMMARY_MAPPER),
                trendingMapper = get(qualifier = TMDB_TRENDING_MAPPER),
                detailEntityMapper = get(qualifier = MOVIE_DETAIL_ENTITY_MAPPER),
                tvDetailEntityMapper = get(qualifier = TV_DETAIL_ENTITY_MAPPER),
                deeplinkEntityMapper = get(qualifier = DEEPLINK_ENTITY_MAPPER),
                movieHistoryEntityMapper = get(qualifier = MOVIE_HISTORY_ENTITY_MAPPER),
                tvHistoryEntityMapper = get(qualifier = TV_HISTORY_ENTITY_MAPPER),
                reviewMapper = get(qualifier = TMDB_REVIEW_MAPPER),
            )
        }

        single { GetSuggestionStreamUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
        single { GetTrendingStreamUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
        single { GetStreamDetailUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
        single { GetRecommendationsUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
        single { GetReviewsUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
        single { RecordHistoryUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
        single { GetHistoryStreamUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
        single { RemoveHistoryUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
    }
