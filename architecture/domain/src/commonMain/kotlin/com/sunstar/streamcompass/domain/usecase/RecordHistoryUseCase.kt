package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.repository.StreamRepository

class RecordHistoryUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(stream: Stream) = streamRepository.recordHistory(stream = stream)
}
