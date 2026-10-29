package ru.maxgog.pagebook.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.maxgog.pagebook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // --- Секция "Тема" ---
            item {
                SettingsSection(title = stringResource(R.string.theme_title)) {
                    SettingsGroup {
                        ThemePreference(
                            selected = uiState.themeMode,
                            onSelect = viewModel::setTheme
                        )
                    }
                }
            }

            // --- Секция "Язык" ---
            item {
                SettingsSection(title = stringResource(R.string.language_title)) {
                    SettingsGroup {
                        LanguagePreference(
                            selected = uiState.language,
                            onSelect = viewModel::setLanguage
                        )
                    }
                }
            }

            // --- Секция "О приложении" ---
            item {
                SettingsSection(title = stringResource(R.string.about)) {
                    SettingsGroup {
                        AboutSettings(
                            version = uiState.versionName,
                            author = stringResource(R.string.author_name)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSettings(version: String, author: String) {
    Column {
        ListItem(
            headlineContent = { Text(stringResource(R.string.version_label, version)) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.author_label)) },
            supportingContent = { Text(author) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}