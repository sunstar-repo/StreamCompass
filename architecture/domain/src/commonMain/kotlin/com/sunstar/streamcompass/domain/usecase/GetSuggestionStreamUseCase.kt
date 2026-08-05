package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetSuggestionStreamUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(type: SuggestionType): Flow<PagingData<MovieStream>> {
        return streamRepository.getSuggestionStreamFlow(type)
    }
}
