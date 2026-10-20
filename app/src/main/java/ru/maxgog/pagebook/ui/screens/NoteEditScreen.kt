package ru.maxgog.pagebook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.data.models.NoteModel
import ru.maxgog.pagebook.ui.theme.SetTransparentSystemBars
import ru.maxgog.pagebook.viewmodels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    note: NoteModel? = null,
    viewModel: NotesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNewNote = note == null
    var titleState by remember(note?.id) {
        mutableStateOf(TextFieldValue(note?.title ?: ""))
    }
    var contentState by remember(note?.id) {
        mutableStateOf(TextFieldValue(note?.content ?: ""))
    }

    val saveEnabled by remember {
        derivedStateOf {
            titleState.text.isNotBlank() && contentState.text.isNotBlank()
        }
    }

    SetTransparentSystemBars()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isNewNote) stringResource(R.string.new_note)
                        else stringResource(R.string.edit_note),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val updatedNote = NoteModel(
                                id = note?.id ?: 0,
                                title = titleState.text,
                                content = contentState.text
                            )
                            if (isNewNote) viewModel.insert(updatedNote)
                            else viewModel.update(updatedNote)
                            onBack()
                        },
                        enabled = saveEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save),
                            tint = if (saveEnabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }

                    if (!isNewNote) {
                        IconButton(
                            onClick = {
                                note.let { viewModel.delete(it) }
                                onBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = titleState,
                onValueChange = { titleState = it },
                label = {
                    Text(
                        text = stringResource(R.string.title),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contentState,
                onValueChange = { contentState = it },
                label = {
                    Text(
                        text = stringResource(R.string.content),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 10
            )
        }
    }
}