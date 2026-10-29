package ru.maxgog.pagebook.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteEditScreen(
    onBack: () -> Unit,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val taskCreating by viewModel.taskCreating.collectAsStateWithLifecycle()
    val snackbarMessageRes by viewModel.snackbarMessageRes.collectAsStateWithLifecycle()
    val richTextState = rememberRichTextState()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
    val canCreateTask = currentNote != null && !taskCreating

    var titleState by remember(currentNote?.id) {
        mutableStateOf(currentNote?.title ?: "")
    }

    LaunchedEffect(currentNote?.id) {
        currentNote?.let { richTextState.setHtml(it.content) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NoteEditEvent.Saved, NoteEditEvent.Deleted -> onBack()
            }
        }
    }

    LaunchedEffect(snackbarMessageRes) {
        snackbarMessageRes?.let { resId ->
            snackbarHostState.showSnackbar(context.getString(resId))
            viewModel.clearSnackbar()
        }
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
                    if (currentNote != null) {
                        IconButton(
                            onClick = { viewModel.createTaskFromCurrentNote() },
                            enabled = canCreateTask
                        ) {
                            if (taskCreating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddTask,
                                    contentDescription = stringResource(R.string.create_task)
                                )
                            }
                        }
                    }
                    IconButton(onClick = { showTagPicker = true }) {
                        Icon(Icons.Default.Label, contentDescription = stringResource(R.string.tags))
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            ) {
                // 📝 Заголовок — без рамки, крупным шрифтом (как в Google Keep)
                BasicTextField(
                    value = titleState,
                    onValueChange = { titleState = it },
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    decorationBox = { innerTextField ->
                        Box {
                            if (titleState.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.title),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // 🏷️ Теги — FlowRow без Card, чипы переносятся на новые строки
                if (currentTags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
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

                // 📄 Контент на весь оставшийся экран
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
            onConfirm = { name, color, emoji ->
                viewModel.createAndToggleTag(name, color, emoji)
                showCreateTag = false
            }
        )
    }
}

/**
 * Тулбар форматирования. `.windowInsetsPadding(navigationBars.union(ime))`
 * — когда клавиатура закрыта, отступ = высота навбара; когда открыта —
 * высота клавиатуры. Тулбар всегда оказывается над клавиатурой.
 */
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
                .windowInsetsPadding(
                    WindowInsets.navigationBars.union(WindowInsets.ime)
                )
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