package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.TvSuggestionType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetTvSuggestionStreamUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(type: TvSuggestionType): Flow<PagingData<TvStream>> {
        return streamRepository.getTvSuggestionStreamFlow(type)
    }
}
