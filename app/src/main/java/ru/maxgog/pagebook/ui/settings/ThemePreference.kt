package ru.maxgog.pagebook.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
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
            androidx.compose.material3.ListItem(
                headlineContent = {
                    Text(
                        text = when (mode) {
                            AppTheme.ThemeMode.SYSTEM -> androidx.compose.ui.res.stringResource(R.string.theme_system)
                            AppTheme.ThemeMode.LIGHT -> androidx.compose.ui.res.stringResource(R.string.theme_light)
                            AppTheme.ThemeMode.DARK -> androidx.compose.ui.res.stringResource(R.string.theme_dark)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
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
                modifier = Modifier
                    .selectable(
                        selected = mode == selected,
                        onClick = { onSelect(mode) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}