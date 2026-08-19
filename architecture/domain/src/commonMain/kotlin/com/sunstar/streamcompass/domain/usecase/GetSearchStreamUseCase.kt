package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetSearchStreamUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(query: String, streamType: StreamType): Flow<PagingData<Stream>> =
        streamRepository.getSearchStreamFlow(query = query, streamType = streamType)
}
