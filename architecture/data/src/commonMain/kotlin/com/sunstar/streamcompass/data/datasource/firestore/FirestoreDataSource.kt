package com.sunstar.streamcompass.data.datasource.firestore

import com.sunstar.streamcompass.data.converter.toDeeplink
import com.sunstar.streamcompass.data.datasource.firestore.dto.FirestoreDeeplinkDto
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.StreamType
import dev.gitlive.firebase.firestore.FirebaseFirestore

internal class FirestoreDataSource(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getDeeplinks(tmdbId: Int, streamType: StreamType, locale: String): List<Deeplink> =
        firestore
            .collection(streamType.rawValue)
            .document(tmdbId.toString())
            .collection(FirestoreConstants.LOCALES)
            .document(locale)
            .collection(FirestoreConstants.SERVICES)
            .get()
            .documents
            .map { snapshot ->
                snapshot.data(strategy = FirestoreDeeplinkDto.serializer())
                    .toDeeplink(tmdbId = tmdbId, streamType = streamType, locale = locale, service = snapshot.id)
            }

    suspend fun setDeeplinks(
        tmdbId: Int,
        streamType: StreamType,
        locale: String,
        dtos: Map<String, FirestoreDeeplinkDto>,
    ) {
        val batch = firestore.batch()
        dtos.forEach { (service, dto) ->
            val documentRef =
                firestore
                    .collection(streamType.rawValue)
                    .document(tmdbId.toString())
                    .collection(FirestoreConstants.LOCALES)
                    .document(locale)
                    .collection(FirestoreConstants.SERVICES)
                    .document(service)
            batch.set(documentRef = documentRef, strategy = FirestoreDeeplinkDto.serializer(), data = dto)
        }
        batch.commit()
    }
}
