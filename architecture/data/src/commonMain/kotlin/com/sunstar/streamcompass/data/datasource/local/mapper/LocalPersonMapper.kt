package com.sunstar.streamcompass.data.datasource.local.mapper

import com.sunstar.streamcompass.data.datasource.local.entity.LocalPerson
import com.sunstar.streamcompass.domain.model.Person

internal fun LocalPerson.toPerson(): Person =
    Person(name = name, role = role, profilePath = profilePath)
