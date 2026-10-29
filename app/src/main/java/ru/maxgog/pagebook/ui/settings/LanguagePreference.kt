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

@Composable
fun LanguagePreference(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        AppLanguage.entries.forEach { language ->
            val descRes = when (language) {
                AppLanguage.SYSTEM -> R.string.language_system_desc
                AppLanguage.RUSSIAN -> R.string.language_ru_desc
                AppLanguage.ENGLISH -> R.string.language_en_desc
            }

            ListItem(
                headlineContent = {
                    Text(stringResource(language.labelRes))
                },
                supportingContent = {
                    Text(stringResource(descRes))
                },
                leadingContent = {
                    RadioButton(
                        selected = language == selected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.selectable(
                    selected = language == selected,
                    onClick = { onSelect(language) },
                    role = Role.RadioButton
                )
            )
        }
    }
}