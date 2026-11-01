package ru.maxgog.pagebook.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.maxgog.pagebook.R

@Composable
fun FilterBadgeIcon(
    count: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (count > 0) {
                    Badge { Text(if (count > 99) "99+" else count.toString()) }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = stringResource(R.string.filter_tags)
            )
        }
    }
}