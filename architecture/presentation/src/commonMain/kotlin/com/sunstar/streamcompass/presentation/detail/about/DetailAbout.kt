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
import com.sunstar.streamcompass.presentation.core.overview
import com.sunstar.streamcompass.presentation.detail.DetailViewModel

@Composable
fun DetailAbout(state: DetailViewModel.State.Succeed, modifier: Modifier = Modifier) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Text(
            text = state.streamDetail.overview,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}
