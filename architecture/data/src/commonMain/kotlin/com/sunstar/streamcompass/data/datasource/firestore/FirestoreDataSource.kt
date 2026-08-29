package com.sunstar.streamcompass.data.datasource.firestore

import com.sunstar.streamcompass.data.converter.toDeeplink
import com.sunstar.streamcompass.data.datasource.firestore.dto.ApiKeyDto
import com.sunstar.streamcompass.data.datasource.firestore.dto.FirestoreDeeplinkDto
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.StreamType
import dev.gitlive.firebase.firestore.FirebaseFirestore

internal class FirestoreDataSource(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getApiKey(): ApiKeyDto =
        firestore
            .collection(FirestoreConstants.COLLECTION_INITIALIZE)
            .document(FirestoreConstants.DOCUMENT_API_KEY)
            .get()
            .data(strategy = ApiKeyDto.serializer())

    suspend fun getDeeplinks(tmdbId: Int, streamType: StreamType, country: String): List<Deeplink> =
        firestore
            .collection(streamType.rawValue)
            .document(tmdbId.toString())
            .collection(FirestoreConstants.DOCUMENT_COUNTRY)
            .document(country)
            .collection(FirestoreConstants.DOCUMENT_SERVICE)
            .get()
            .documents
            .map { snapshot ->
                snapshot.data(strategy = FirestoreDeeplinkDto.serializer())
                    .toDeeplink(
                        tmdbId = tmdbId,
                        streamType = streamType,
                        country = country,
                        service = snapshot.id
                    )
            }

    suspend fun setDeeplinks(
        tmdbId: Int,
        streamType: StreamType,
        country: String,
        dtos: Map<String, FirestoreDeeplinkDto>,
    ) {
        val batch = firestore.batch()
        dtos.forEach { (service, dto) ->
            val documentRef =
                firestore
                    .collection(streamType.rawValue)
                    .document(tmdbId.toString())
                    .collection(FirestoreConstants.DOCUMENT_COUNTRY)
                    .document(country)
                    .collection(FirestoreConstants.DOCUMENT_SERVICE)
                    .document(service)
            batch.set(
                documentRef = documentRef,
                strategy = FirestoreDeeplinkDto.serializer(),
                data = dto
            )
        }
        batch.commit()
    }
}
