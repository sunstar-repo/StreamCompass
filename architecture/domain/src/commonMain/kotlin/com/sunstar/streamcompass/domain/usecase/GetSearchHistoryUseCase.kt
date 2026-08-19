package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetSearchHistoryUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(): Flow<List<String>> = streamRepository.getSearchHistoryFlow()
}
