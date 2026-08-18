package com.sunstar.streamcompass.presentation.core

import androidx.compose.ui.unit.dp
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Person
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.TvStreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import org.jetbrains.compose.resources.StringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.destination_movie
import streamcompass.architecture.presentation.generated.resources.destination_tv

val POSTER_WIDTH = 120.dp
val POSTER_HEIGHT = 180.dp
val BACKDROP_WIDTH = 200.dp
val BACKDROP_HEIGHT = 112.dp
val AVATAR_SIZE = 40.dp

val MEDIA_TITLE_SPACING = 4.dp
private val MEDIA_TITLE_LINE_HEIGHT = 16.dp

val POSTER_ROW_MIN_HEIGHT = POSTER_HEIGHT + MEDIA_TITLE_SPACING + MEDIA_TITLE_LINE_HEIGHT
val BACKDROP_ROW_MIN_HEIGHT = BACKDROP_HEIGHT + MEDIA_TITLE_SPACING + MEDIA_TITLE_LINE_HEIGHT

// Movie/Tv 구분해서 사용할 데이터를 확정짓는 지점들 — domain policy로 옮길지 UI 전용으로 둘지 아직 미정,
// 우선 한 곳에 모아 두고 추후 정리한다. 화면 코드(domain 모델 포함)에서는 이 프로퍼티들만 한 줄로 참조해서 쓸 것 —
// 값이 두 타입에서 동일하더라도(tmdbId/posterPath/backdropPath) domain sealed interface에 직접 얹지 않고 여기 모은다.
val Stream.tmdbId: Int
    get() = when (this) {
        is MovieStream -> tmdbId
        is TvStream -> tmdbId
    }

val Stream.posterPath: String
    get() = when (this) {
        is MovieStream -> posterPath
        is TvStream -> posterPath
    }

val Stream.backdropPath: String
    get() = when (this) {
        is MovieStream -> backdropPath
        is TvStream -> backdropPath
    }

val Stream.streamType: StreamType
    get() = when (this) {
        is MovieStream -> StreamType.Movie
        is TvStream -> StreamType.Tv
    }

val Stream.displayTitle: String
    get() = when (this) {
        is MovieStream -> title
        is TvStream -> name
    }

val Stream.typeLabelRes: StringResource
    get() = when (this) {
        is MovieStream -> Res.string.destination_movie
        is TvStream -> Res.string.destination_tv
    }

val StreamDetail.displayTitle: String
    get() = when (this) {
        is MovieStreamDetail -> title
        is TvStreamDetail -> name
    }

val StreamDetail.overview: String
    get() = when (this) {
        is MovieStreamDetail -> overview
        is TvStreamDetail -> overview
    }

val StreamDetail.deeplinks: List<Deeplink>
    get() = when (this) {
        is MovieStreamDetail -> deeplinks
        is TvStreamDetail -> deeplinks
    }

val StreamDetail.genres: List<String>
    get() = when (this) {
        is MovieStreamDetail -> genres
        is TvStreamDetail -> genres
    }

val StreamDetail.logo: String
    get() = when (this) {
        is MovieStreamDetail -> logo
        is TvStreamDetail -> logo
    }

val StreamDetail.releaseDate: String
    get() = when (this) {
        is MovieStreamDetail -> releaseDate
        is TvStreamDetail -> firstAirDate
    }

val StreamDetail.runtimeMinutes: Int
    get() = when (this) {
        is MovieStreamDetail -> runtime
        is TvStreamDetail -> episodeRunTime.firstOrNull() ?: 0
    }

val StreamDetail.certification: String
    get() = when (this) {
        is MovieStreamDetail -> certification
        is TvStreamDetail -> certification
    }

val StreamDetail.originalTitle: String
    get() = when (this) {
        is MovieStreamDetail -> originalTitle
        is TvStreamDetail -> originalName
    }

val StreamDetail.networkLogo: String
    get() = when (this) {
        is MovieStreamDetail -> productionCompanyLogo
        is TvStreamDetail -> networkLogo
    }

val StreamDetail.trailerKeys: List<String>
    get() = when (this) {
        is MovieStreamDetail -> trailerKeys
        is TvStreamDetail -> trailerKeys
    }

val StreamDetail.backdrops: List<String>
    get() = when (this) {
        is MovieStreamDetail -> backdrops
        is TvStreamDetail -> backdrops
    }

val StreamDetail.posters: List<String>
    get() = when (this) {
        is MovieStreamDetail -> posters
        is TvStreamDetail -> posters
    }

// TMDB에 tv budget/revenue가 없어 Tv는 항상 0 — UI에서 0이면 배지를 숨긴다.
val StreamDetail.budget: Long
    get() = when (this) {
        is MovieStreamDetail -> budget
        is TvStreamDetail -> 0L
    }

val StreamDetail.revenue: Long
    get() = when (this) {
        is MovieStreamDetail -> revenue
        is TvStreamDetail -> 0L
    }

val StreamDetail.cast: List<Person>
    get() = when (this) {
        is MovieStreamDetail -> cast
        is TvStreamDetail -> cast
    }

val StreamDetail.crew: List<Person>
    get() = when (this) {
        is MovieStreamDetail -> crew
        is TvStreamDetail -> crew
    }
