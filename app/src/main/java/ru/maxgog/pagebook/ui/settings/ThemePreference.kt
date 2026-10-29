package ru.maxgog.pagebook.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.ui.theme.AppTheme

@Composable
fun ThemePreference(
    selected: AppTheme.ThemeMode,
    onSelect: (AppTheme.ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        AppTheme.ThemeMode.entries.forEach { mode ->
            val (titleRes, descRes) = when (mode) {
                AppTheme.ThemeMode.SYSTEM -> R.string.theme_system to R.string.theme_system_desc
                AppTheme.ThemeMode.LIGHT -> R.string.theme_light to R.string.theme_light_desc
                AppTheme.ThemeMode.DARK -> R.string.theme_dark to R.string.theme_dark_desc
            }

            ListItem(
                headlineContent = {
                    Text(stringResource(titleRes))
                },
                supportingContent = {
                    Text(stringResource(descRes))
                },
                leadingContent = {
                    RadioButton(
                        selected = mode == selected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.selectable(
                    selected = mode == selected,
                    onClick = { onSelect(mode) },
                    role = Role.RadioButton
                )
            )
        }
    }
}