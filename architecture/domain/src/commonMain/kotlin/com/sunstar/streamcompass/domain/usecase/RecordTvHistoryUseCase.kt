package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.repository.StreamRepository

class RecordTvHistoryUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(tvStream: TvStream) = streamRepository.recordTvHistory(tvStream)
}
