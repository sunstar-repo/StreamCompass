package com.sunstar.streamcompass.domain.mapper

interface Mapper<Source, Payload> {
    fun map(source: Source): Payload
}
