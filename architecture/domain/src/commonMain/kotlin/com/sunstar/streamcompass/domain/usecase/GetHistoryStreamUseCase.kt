package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryStreamUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(streamType: StreamType): Flow<List<Stream>> =
        streamRepository.getHistoryStreamFlow(streamType = streamType)
}
