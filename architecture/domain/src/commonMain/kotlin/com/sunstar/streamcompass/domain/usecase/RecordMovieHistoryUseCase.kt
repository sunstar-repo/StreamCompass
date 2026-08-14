package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.repository.StreamRepository

class RecordMovieHistoryUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(movieStream: MovieStream) = streamRepository.recordMovieHistory(movieStream)
}
