package com.sunstar.streamcompass.domain.repository

import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamListType

interface StreamRepository {
    suspend fun getStreams(
        type: StreamListType,
        page: Int = 1,
    ): List<Stream>

    suspend fun getStream(streamId: Int): Stream
}
