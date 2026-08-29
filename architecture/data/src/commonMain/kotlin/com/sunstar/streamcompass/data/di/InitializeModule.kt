package com.sunstar.streamcompass.data.di

import com.sunstar.streamcompass.data.datasource.firestore.FirestoreDataSource
import com.sunstar.streamcompass.data.repository.InitializeRepositoryImpl
import com.sunstar.streamcompass.domain.repository.InitializeRepository
import com.sunstar.streamcompass.domain.usecase.InitializeAppUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.qualifier.named
import org.koin.dsl.module

// dataModule(apiKey)이 만들어지기 전(=API 키를 아직 모르는 시점)에 부트스트랩으로 먼저 떠야 하므로,
// dataModule의 FirebaseFirestore/FirestoreDataSource와 별개로 자체 인스턴스를 갖는다.
private val INITIALIZE_FIRESTORE = named("initializeFirestore")
private val INITIALIZE_FIRESTORE_DATA_SOURCE = named("initializeFirestoreDataSource")
private val INITIALIZE_REPOSITORY = named("initializeRepository")

val initializeModule =
    module {
        single<FirebaseFirestore>(INITIALIZE_FIRESTORE) { Firebase.firestore }
        single(INITIALIZE_FIRESTORE_DATA_SOURCE) {
            FirestoreDataSource(firestore = get(qualifier = INITIALIZE_FIRESTORE))
        }
        single<InitializeRepository>(INITIALIZE_REPOSITORY) {
            InitializeRepositoryImpl(firestoreDataSource = get(qualifier = INITIALIZE_FIRESTORE_DATA_SOURCE))
        }
        single { InitializeAppUseCase(initializeRepository = get(qualifier = INITIALIZE_REPOSITORY)) }
    }
