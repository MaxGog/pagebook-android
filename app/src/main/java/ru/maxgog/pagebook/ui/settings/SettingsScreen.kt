package ru.maxgog.pagebook.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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

    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapAt by remember { mutableLongStateOf(0L) }
    var showFursona by remember { mutableStateOf(false) }

    val onVersionTap: () -> Unit = {
        val now = System.currentTimeMillis()
        if (now - lastTapAt > 1000L) tapCount = 0
        tapCount++
        lastTapAt = now

        if (tapCount >= 7 && !showFursona) {
            showFursona = true
            tapCount = 0
            viewModel.onFursonaEasterEggFound()
        }
    }

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

            item {
                SettingsSection(title = stringResource(R.string.about)) {
                    SettingsGroup {
                        AboutSettings(
                            version = uiState.versionName,
                            author = stringResource(R.string.author_name),
                            onVersionTap = onVersionTap,
                            tapsToEasterEgg = (7 - tapCount).coerceIn(0, 7),
                            fursonaUnlocked = showFursona
                        )
                    }
                }
            }

            item {
                FursonaEasterEgg(visible = showFursona)
            }
        }
    }
}

@Composable
private fun AboutSettings(
    version: String,
    author: String,
    onVersionTap: () -> Unit,
    tapsToEasterEgg: Int,
    fursonaUnlocked: Boolean
) {
    Column {
        ListItem(
            headlineContent = {
                Text(
                    stringResource(R.string.version_label, version),
                    modifier = Modifier.clickable { onVersionTap() }
                )
            },
            supportingContent = {
                if (!fursonaUnlocked && tapsToEasterEgg in 1..6) {
                    Text(stringResource(R.string.fursona_easter_egg_tap_hint, tapsToEasterEgg))
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        ListItem(
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.ic_developer_avatar),
                    contentDescription = stringResource(R.string.developer_avatar_content_description),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            },
            headlineContent = { Text(stringResource(R.string.author_label)) },
            supportingContent = { Text(author) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}