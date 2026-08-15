package com.sunstar.streamcompass.presentation.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sunstar.streamcompass.domain.model.ThemeMode
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.setting_theme_mode_dark
import streamcompass.architecture.presentation.generated.resources.setting_theme_mode_light
import streamcompass.architecture.presentation.generated.resources.setting_theme_mode_system
import streamcompass.architecture.presentation.generated.resources.setting_theme_title

@Composable
fun SettingScreen(viewModel: SettingViewModel = koinViewModel()) {
    val state by viewModel.stateFlow.collectAsState()

    Column {
        state.items.forEach { item ->
            when (item) {
                SettingItem.Theme -> ThemeSettingRow(
                    themeMode = state.themeMode,
                    onClick = { viewModel.onItemClick(item = item) },
                )
            }
        }
    }

    if (state.isThemeSheetShown) {
        ThemeModeBottomSheet(
            selectedMode = state.themeMode,
            onModeSelected = viewModel::onThemeModeSelected,
            onDismissRequest = viewModel::onThemeSheetDismissRequest,
        )
    }
}

@Composable
private fun ThemeSettingRow(themeMode: ThemeMode, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Text(text = stringResource(Res.string.setting_theme_title), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = stringResource(themeMode.labelRes()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeBottomSheet(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column {
            ThemeMode.entries.forEach { mode ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onModeSelected(mode) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = mode == selectedMode, onClick = { onModeSelected(mode) })
                        Text(text = stringResource(mode.labelRes()), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

private fun ThemeMode.labelRes(): StringResource = when (this) {
    ThemeMode.System -> Res.string.setting_theme_mode_system
    ThemeMode.Light -> Res.string.setting_theme_mode_light
    ThemeMode.Dark -> Res.string.setting_theme_mode_dark
}
