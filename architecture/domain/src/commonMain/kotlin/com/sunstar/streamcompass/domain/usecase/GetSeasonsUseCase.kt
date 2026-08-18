package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Season
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetSeasonsUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(tmdbId: Int, locale: String): Flow<PagingData<Season>> =
        streamRepository.getSeasonsStreamFlow(tmdbId = tmdbId, locale = locale)
}
