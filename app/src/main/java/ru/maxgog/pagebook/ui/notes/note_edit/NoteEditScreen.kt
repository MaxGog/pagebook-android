package ru.maxgog.pagebook.ui.notes.note_edit

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.ui.components.CreateTagDialog
import ru.maxgog.pagebook.ui.components.TagChip
import ru.maxgog.pagebook.ui.components.TagPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    onBack: () -> Unit,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val richTextState = rememberRichTextState()

    var showTagPicker by remember { mutableStateOf(false) }
    var showCreateTag by remember { mutableStateOf(false) }

    val currentNote = (uiState as? NoteEditUiState.Editing)?.note
    val currentTags = when (val s = uiState) {
        is NoteEditUiState.Editing -> s.tags
        is NoteEditUiState.NewNote -> s.selectedTags
        else -> emptyList()
    }
    val isLoading = uiState is NoteEditUiState.Loading
    val isNewNote = uiState is NoteEditUiState.NewNote

    var titleState by remember(currentNote?.id) {
        mutableStateOf(currentNote?.title ?: "")
    }

    LaunchedEffect(currentNote?.id) {
        currentNote?.let { richTextState.setHtml(it.content) }
    }

    LaunchedEffect(isSaved) {
        if (isSaved) onBack()
    }

    val saveEnabled = titleState.isNotBlank() &&
            richTextState.annotatedString.isNotEmpty() &&
            !isLoading

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
                    IconButton(onClick = { showTagPicker = true }) {
                        Icon(Icons.Default.Label, contentDescription = "Теги")
                    }
                    IconButton(
                        onClick = { viewModel.save(titleState, richTextState.toHtml()) },
                        enabled = saveEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save),
                            tint = if (saveEnabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                    if (currentNote != null) {
                        IconButton(onClick = { viewModel.delete() }) {
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
        bottomBar = { FormattingToolbar(state = richTextState) },
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState is NoteEditUiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (uiState as NoteEditUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> Column(
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

                if (currentTags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentTags.forEach { tag ->
                            TagChip(
                                tag = tag,
                                selected = true,
                                onDelete = { viewModel.toggleTag(tag) }
                            )
                        }
                    }
                }

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

    if (showTagPicker) {
        TagPickerDialog(
            allTags = allTags,
            selectedTags = currentTags,
            onToggle = viewModel::toggleTag,
            onCreateTag = {
                showTagPicker = false
                showCreateTag = true
            },
            onDismiss = { showTagPicker = false }
        )
    }

    if (showCreateTag) {
        CreateTagDialog(
            onDismiss = { showCreateTag = false },
            onConfirm = { name, color ->
                viewModel.createAndToggleTag(name, color)
                showCreateTag = false
            }
        )
    }
}

@Composable
fun FormattingToolbar(
    state: RichTextState,
    modifier: Modifier = Modifier
) {
    val currentStyle = state.currentSpanStyle
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconToggleButton(
                checked = currentStyle.fontWeight == FontWeight.Bold,
                onCheckedChange = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) }
            ) { Icon(Icons.Default.FormatBold, contentDescription = null) }

            IconToggleButton(
                checked = currentStyle.fontStyle == FontStyle.Italic,
                onCheckedChange = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) }
            ) { Icon(Icons.Default.FormatItalic, contentDescription = null) }

            IconToggleButton(
                checked = currentStyle.textDecoration == TextDecoration.Underline,
                onCheckedChange = {
                    state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                }
            ) { Icon(Icons.Default.FormatUnderlined, contentDescription = null) }

            IconButton(onClick = { state.toggleSpanStyle(SpanStyle(fontSize = 24.sp)) }) {
                Icon(Icons.Default.FormatSize, contentDescription = null)
            }
        }
    }
}