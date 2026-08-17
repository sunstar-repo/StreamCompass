package com.sunstar.streamcompass.presentation.detail.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail

@Composable
fun About(streamDetail: MovieStreamDetail, modifier: Modifier = Modifier) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Text(
            text = streamDetail.overview,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}
