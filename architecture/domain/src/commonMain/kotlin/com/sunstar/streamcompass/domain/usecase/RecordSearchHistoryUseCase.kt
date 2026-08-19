package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.repository.StreamRepository

class RecordSearchHistoryUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(query: String) = streamRepository.recordSearchHistory(query = query)
}
