package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetTvHistoryStreamUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(): Flow<List<TvStream>> = streamRepository.getTvHistoryStreamFlow()
}
