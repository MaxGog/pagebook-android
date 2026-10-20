package ru.maxgog.pagebook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.viewmodels.NoteEditViewModel

import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Int?,
    onBack: () -> Unit,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val note by viewModel.note.collectAsState()
    val isNewNote = viewModel.isNewNote
    val isLoading = !isNewNote && note == null

    val richTextState = rememberRichTextState()

    var titleState by remember(note?.id) {
        mutableStateOf(note?.title ?: "")
    }

    LaunchedEffect(note?.id) {
        note?.let {
            richTextState.setHtml(it.content)
        }
    }

    val saveEnabled = titleState.isNotBlank() && richTextState.annotatedString.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isNewNote) stringResource(R.string.new_note)
                        else stringResource(R.string.edit_note)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.save(titleState, richTextState.toHtml())
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
                    if (!isNewNote && note != null) {
                        IconButton(
                            onClick = {
                                viewModel.delete()
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            FormattingToolbar(state = richTextState)
        },
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            ) {
                TextField(
                    value = titleState,
                    onValueChange = { titleState = it },
                    placeholder = { Text(stringResource(R.string.title)) },
                    textStyle = MaterialTheme.typography.headlineSmall,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                RichTextEditor(
                    state = richTextState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeholder = { Text(stringResource(R.string.content)) }
                )
            }
        }
    }
}

@Composable
fun FormattingToolbar(
    state: RichTextState,
    modifier: Modifier = Modifier
) {
    val currentStyle = state.currentSpanStyle

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconToggleButton(
            checked = currentStyle.fontWeight == FontWeight.Bold,
            onCheckedChange = {
                state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }
        ) {
            Icon(Icons.Default.FormatBold, contentDescription = "Bold")
        }

        IconToggleButton(
            checked = currentStyle.fontStyle == FontStyle.Italic,
            onCheckedChange = {
                state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }
        ) {
            Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
        }

        IconToggleButton(
            checked = currentStyle.textDecoration == TextDecoration.Underline,
            onCheckedChange = {
                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }
        ) {
            Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline")
        }

        IconButton(
            onClick = {
                state.toggleSpanStyle(SpanStyle(fontSize = 24.sp))
            }
        ) {
            Icon(Icons.Default.FormatSize, contentDescription = "Heading")
        }
    }
}