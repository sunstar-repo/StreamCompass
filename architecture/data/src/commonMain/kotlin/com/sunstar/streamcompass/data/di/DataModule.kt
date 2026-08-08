package com.sunstar.streamcompass.data.di

import com.sunstar.streamcompass.data.datasource.firestore.FirestoreDataSource
import com.sunstar.streamcompass.data.datasource.local.AppDatabase
import com.sunstar.streamcompass.data.datasource.local.LocalDataSource
import com.sunstar.streamcompass.data.datasource.local.createDatabase
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.mapper.LocalDeeplinkEntityMapper
import com.sunstar.streamcompass.data.datasource.local.mapper.LocalMovieDetailEntityMapper
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.mapper.TmdbMovieSummaryMapper
import com.sunstar.streamcompass.data.repository.StreamRepositoryImpl
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.repository.StreamRepository
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val TMDB_DATA_SOURCE = named("tmdbDataSource")
private val SA_DATA_SOURCE = named("saDataSource")
private val TMDB_MOVIE_SUMMARY_MAPPER = named("tmdbMovieSummaryMapper")
private val STREAM_REPOSITORY = named("streamRepository")
private val APP_DATABASE = named("appDatabase")
private val MOVIE_DETAIL_DAO = named("movieDetailDao")
private val DEEPLINK_DAO = named("deeplinkDao")
private val LOCAL_DATA_SOURCE = named("localDataSource")
private val MOVIE_DETAIL_ENTITY_MAPPER = named("movieDetailEntityMapper")
private val DEEPLINK_ENTITY_MAPPER = named("deeplinkEntityMapper")
private val FIRESTORE = named("firestore")
private val FIRESTORE_DATA_SOURCE = named("firestoreDataSource")

fun dataModule(apiKey: ApiKey): Module =
    module {
        single(TMDB_DATA_SOURCE) { TmdbDataSource(apiKey = apiKey.tmdbKey) }
        single(SA_DATA_SOURCE) { SaDataSource(apiKey = apiKey.saKey) }

        single<Mapper<TmdbMovieSummaryDto, MovieStream>>(TMDB_MOVIE_SUMMARY_MAPPER) { TmdbMovieSummaryMapper() }
        single<Mapper<LocalMovieDetailEntity, MovieStreamDetail>>(MOVIE_DETAIL_ENTITY_MAPPER) {
            LocalMovieDetailEntityMapper()
        }
        single<Mapper<LocalDeeplinkEntity, Deeplink>>(DEEPLINK_ENTITY_MAPPER) { LocalDeeplinkEntityMapper() }

        single<AppDatabase>(APP_DATABASE) { createDatabase() }
        single(MOVIE_DETAIL_DAO) { get<AppDatabase>(qualifier = APP_DATABASE).movieDetailDao() }
        single(DEEPLINK_DAO) { get<AppDatabase>(qualifier = APP_DATABASE).deeplinkDao() }
        single(LOCAL_DATA_SOURCE) {
            LocalDataSource(
                movieDetailDao = get(qualifier = MOVIE_DETAIL_DAO),
                deeplinkDao = get(qualifier = DEEPLINK_DAO),
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
                summaryMapper = get(qualifier = TMDB_MOVIE_SUMMARY_MAPPER),
                detailEntityMapper = get(qualifier = MOVIE_DETAIL_ENTITY_MAPPER),
                deeplinkEntityMapper = get(qualifier = DEEPLINK_ENTITY_MAPPER),
            )
        }

        single { GetSuggestionStreamUseCase(streamRepository = get(qualifier = STREAM_REPOSITORY)) }
    }
