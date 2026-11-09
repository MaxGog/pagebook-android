package ru.maxgog.pagebook.ui.notes

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.core.notification.AppNotificationManager
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.Note
import ru.maxgog.pagebook.domain.model.NotePlatform
import ru.maxgog.pagebook.domain.model.NoteVariant
import ru.maxgog.pagebook.domain.model.OwnerType
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.domain.model.TagColor
import ru.maxgog.pagebook.domain.repository.AttachmentRepository
import ru.maxgog.pagebook.domain.repository.TagRepository
import ru.maxgog.pagebook.domain.usecase.attachment.DeleteAttachmentUseCase
import ru.maxgog.pagebook.domain.usecase.attachment.DownloadAttachmentUseCase
import ru.maxgog.pagebook.domain.usecase.attachment.GetAttachmentsForNoteUseCase
import ru.maxgog.pagebook.domain.usecase.attachment.SaveAttachmentUseCase
import ru.maxgog.pagebook.domain.usecase.note.DeleteNoteUseCase
import ru.maxgog.pagebook.domain.usecase.note.DeleteNoteVariantUseCase
import ru.maxgog.pagebook.domain.usecase.note.GetNoteByIdUseCase
import ru.maxgog.pagebook.domain.usecase.note.GetVariantsForNoteUseCase
import ru.maxgog.pagebook.domain.usecase.note.SaveNoteUseCase
import ru.maxgog.pagebook.domain.usecase.note.SaveNoteVariantUseCase
import ru.maxgog.pagebook.domain.usecase.tag.CreateTagUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetAllTagsUseCase
import ru.maxgog.pagebook.domain.usecase.tag.GetTagsForNoteUseCase
import ru.maxgog.pagebook.domain.usecase.tag.ToggleTagOnNoteUseCase
import ru.maxgog.pagebook.domain.usecase.tag.UpdateTagUseCase
import ru.maxgog.pagebook.domain.usecase.task.CreateTaskFromNoteUseCase
import ru.maxgog.pagebook.domain.usecase.task.SaveTaskUseCase
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NoteEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteById: GetNoteByIdUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    getAllTags: GetAllTagsUseCase,
    private val getTagsForNote: GetTagsForNoteUseCase,
    private val toggleTagOnNote: ToggleTagOnNoteUseCase,
    private val createTag: CreateTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val tagRepository: TagRepository,
    private val createTaskFromNote: CreateTaskFromNoteUseCase,
    private val saveTask: SaveTaskUseCase,
    private val saveAttachment: SaveAttachmentUseCase,
    private val getAttachmentsForNote: GetAttachmentsForNoteUseCase,
    private val deleteAttachment: DeleteAttachmentUseCase,
    private val downloadAttachment: DownloadAttachmentUseCase,
    private val attachmentRepository: AttachmentRepository,
    private val getVariantsForNote: GetVariantsForNoteUseCase,
    private val saveNoteVariant: SaveNoteVariantUseCase,
    private val deleteNoteVariant: DeleteNoteVariantUseCase,
    private val notificationManager: AppNotificationManager
) : ViewModel() {

    private val noteId: Int? = savedStateHandle.get<Int>("noteId")

    private val _uiState = MutableStateFlow<NoteEditUiState>(NoteEditUiState.Loading)
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    private val _events = Channel<NoteEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _taskCreating = MutableStateFlow(false)
    val taskCreating: StateFlow<Boolean> = _taskCreating.asStateFlow()

    val allTags: StateFlow<List<Tag>> = getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private var saved = false

    init {
        viewModelScope.launch {
            _uiState.value = if (noteId == null) {
                NoteEditUiState.NewNote()
            } else {
                val note = getNoteById(noteId)
                if (note == null) NoteEditUiState.Error("Note not found")
                else NoteEditUiState.Editing(note = note, tags = note.tags)
            }
        }

        noteId?.let { id ->
            viewModelScope.launch {
                getTagsForNote(id).collect { tags ->
                    (_uiState.value as? NoteEditUiState.Editing)?.let {
                        _uiState.value = it.copy(tags = tags)
                    }
                }
            }
            viewModelScope.launch {
                getVariantsForNote(id).collect { variants ->
                    (_uiState.value as? NoteEditUiState.Editing)?.let {
                        _uiState.value = it.copy(variants = variants)
                    }
                }
            }
            viewModelScope.launch {
                _uiState.mapNotNull { it as? NoteEditUiState.Editing }
                    .distinctUntilChangedBy { state ->
                        state.activePlatform to state.variants.map { it.id to it.platform }
                    }
                    .flatMapLatest { state ->
                        val ownerFlow: Flow<List<Attachment>> = when (state.activePlatform) {
                            NotePlatform.MAIN -> getAttachmentsForNote(id)
                            else -> state.variants
                                .find { it.platform == state.activePlatform }
                                ?.let { variant ->
                                    attachmentRepository.getAttachmentsForOwner(
                                        OwnerType.NOTE_VARIANT, variant.id
                                    )
                                }
                                ?: flowOf(emptyList())
                        }
                        ownerFlow
                    }
                    .collect { attachments ->
                        (_uiState.value as? NoteEditUiState.Editing)?.let {
                            _uiState.value = it.copy(attachments = attachments)
                        }
                    }
            }
        }
    }

    fun toggleTag(tag: Tag) {
        val current = _uiState.value
        when (current) {
            is NoteEditUiState.Editing -> viewModelScope.launch {
                toggleTagOnNote(current.note.id, tag)
            }
            is NoteEditUiState.NewNote -> {
                val newTags = if (current.selectedTags.any { it.id == tag.id }) {
                    current.selectedTags - tag
                } else current.selectedTags + tag
                _uiState.value = current.copy(selectedTags = newTags)
            }
            else -> Unit
        }
    }

    fun createAndToggleTag(name: String, color: TagColor, emoji: String? = null) {
        viewModelScope.launch {
            val tagId = createTag(name, color, emoji)
            val tag = Tag(id = tagId.toInt(), name = name.trim(), colorHex = color.hex, emoji = emoji)
            toggleTag(tag)
        }
    }

    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            try {
                val newColor = TagColor.entries.firstOrNull { it.hex == tag.colorHex } ?: TagColor.BLUE
                updateTagUseCase(
                    tag = tag,
                    newName = tag.name,
                    newEmoji = tag.emoji,
                    newColor = newColor
                )
                (_uiState.value as? NoteEditUiState.NewNote)?.let { current ->
                    val updated = current.selectedTags.map { if (it.id == tag.id) tag else it }
                    _uiState.value = current.copy(selectedTags = updated)
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateTag failed", e)
                notificationManager.error(R.string.unknown_error)
            }
        }
    }

    fun switchPlatform(
        newPlatform: NotePlatform,
        currentTitle: String,
        currentContent: String
    ) {
        viewModelScope.launch {
            val current = _uiState.value as? NoteEditUiState.Editing ?: return@launch
            if (current.activePlatform == newPlatform) return@launch

            try {
                saveCurrentPlatform(current, currentTitle, currentContent)

                if (newPlatform != NotePlatform.MAIN &&
                    current.variants.none { it.platform == newPlatform }
                ) {
                    saveNoteVariant(
                        NoteVariant(noteId = current.note.id, platform = newPlatform)
                    )
                }

                _uiState.value = current.copy(activePlatform = newPlatform)
            } catch (e: Exception) {
                Log.e(TAG, "switchPlatform failed", e)
                notificationManager.error(R.string.unknown_error)
            }
        }
    }

    fun deleteVariant(platform: NotePlatform) {
        if (platform == NotePlatform.MAIN) return
        viewModelScope.launch {
            val current = _uiState.value as? NoteEditUiState.Editing ?: return@launch
            val variant = current.variants.find { it.platform == platform } ?: return@launch
            try {
                attachmentRepository
                    .getAttachmentsForOwner(OwnerType.NOTE_VARIANT, variant.id)
                    .first()
                    .forEach { runCatching { deleteAttachment(it) } }
                deleteNoteVariant(variant)
                notificationManager.success(R.string.version_deleted)
                if (current.activePlatform == platform) {
                    _uiState.value = current.copy(activePlatform = NotePlatform.MAIN)
                }
            } catch (e: Exception) {
                Log.e(TAG, "deleteVariant failed", e)
                notificationManager.error(R.string.unknown_error)
            }
        }
    }

    // ─────────────────────────── Вложения ───────────────────────────

    fun attachUri(uri: Uri) {
        viewModelScope.launch {
            try {
                when (val current = _uiState.value) {
                    is NoteEditUiState.Editing -> {
                        val ownerType: OwnerType
                        val ownerId: Int
                        if (current.activePlatform == NotePlatform.MAIN) {
                            ownerType = OwnerType.NOTE
                            ownerId = current.note.id
                        } else {
                            val variant = current.variants
                                .find { it.platform == current.activePlatform }
                            if (variant == null) {
                                notificationManager.error(R.string.save_note_first)
                                return@launch
                            }
                            ownerType = OwnerType.NOTE_VARIANT
                            ownerId = variant.id
                        }
                        saveAttachment(uri, ownerType, ownerId)
                    }
                    is NoteEditUiState.NewNote -> {
                        val pending = saveAttachment(uri, OwnerType.NOTE, 0)
                        _uiState.value = current.copy(
                            pendingAttachments = current.pendingAttachments + pending
                        )
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                Log.e(TAG, "attachUri failed", e)
                notificationManager.error(R.string.attachment_save_error)
            }
        }
    }

    fun removeAttachment(attachment: Attachment) {
        val current = _uiState.value
        when (current) {
            is NoteEditUiState.Editing -> viewModelScope.launch {
                deleteAttachment(attachment)
            }
            is NoteEditUiState.NewNote -> {
                viewModelScope.launch {
                    deleteAttachment(attachment)
                    _uiState.value = current.copy(
                        pendingAttachments = current.pendingAttachments - attachment
                    )
                }
            }
            else -> Unit
        }
    }

    fun downloadAttachment(attachment: Attachment) {
        viewModelScope.launch {
            try {
                downloadAttachment.invoke(attachment)
                notificationManager.success(R.string.downloaded)
            } catch (e: Exception) {
                Log.e(TAG, "downloadAttachment failed", e)
                notificationManager.error(R.string.download_error)
            }
        }
    }

    fun save(title: String, content: String) {
        viewModelScope.launch {
            when (val current = _uiState.value) {
                is NoteEditUiState.NewNote -> saveNewNote(current, title, content)
                is NoteEditUiState.Editing -> saveExistingNote(current, title, content)
                else -> Unit
            }
        }
    }

    private suspend fun saveNewNote(
        current: NoteEditUiState.NewNote,
        title: String,
        content: String
    ) {
        try {
            val note = Note(title = title, content = content)
            val savedId = saveNote(note)
            current.selectedTags.forEach { tag ->
                tagRepository.addTagToNote(savedId.toInt(), tag.id)
            }
            current.pendingAttachments.forEach { pending ->
                attachmentRepository.updateAttachment(
                    pending.copy(ownerId = savedId.toInt())
                )
            }
            saved = true
            notificationManager.success(R.string.note_saved)
            _events.send(NoteEditEvent.Saved)
        } catch (e: Exception) {
            Log.e(TAG, "save new note failed", e)
            notificationManager.error(R.string.unknown_error)
        }
    }

    private suspend fun saveExistingNote(
        current: NoteEditUiState.Editing,
        title: String,
        content: String
    ) {
        try {
            saveCurrentPlatform(current, title, content)
            saved = true
            notificationManager.success(R.string.note_saved)
            _events.send(NoteEditEvent.Saved)
        } catch (e: Exception) {
            Log.e(TAG, "save existing note failed", e)
            notificationManager.error(R.string.unknown_error)
        }
    }

    private suspend fun saveCurrentPlatform(
        state: NoteEditUiState.Editing,
        title: String,
        content: String
    ) {
        if (state.activePlatform == NotePlatform.MAIN) {
            saveNote(state.note.copy(title = title, content = content))
        } else {
            val existing = state.variants.find { it.platform == state.activePlatform }
            val variant = (existing ?: NoteVariant(
                noteId = state.note.id,
                platform = state.activePlatform
            )).copy(title = title, content = content)
            saveNoteVariant(variant)
        }
    }

    fun delete() {
        val current = _uiState.value as? NoteEditUiState.Editing ?: return
        viewModelScope.launch {
            try {
                current.variants.forEach { variant ->
                    attachmentRepository
                        .getAttachmentsForOwner(OwnerType.NOTE_VARIANT, variant.id)
                        .first()
                        .forEach { runCatching { deleteAttachment(it) } }
                }
                getAttachmentsForNote(current.note.id).first()
                    .forEach { runCatching { deleteAttachment(it) } }
                deleteNote(current.note)
                notificationManager.success(R.string.note_deleted)
                _events.send(NoteEditEvent.Deleted)
            } catch (e: Exception) {
                Log.e(TAG, "delete note failed", e)
                notificationManager.error(R.string.unknown_error)
            }
        }
    }

    fun createTaskFromCurrentNote() {
        val current = _uiState.value as? NoteEditUiState.Editing ?: return
        viewModelScope.launch {
            _taskCreating.value = true
            try {
                val task = createTaskFromNote(
                    noteContent = current.note.content,
                    noteTitle = current.note.title
                )
                saveTask(task.copy(noteId = current.note.id))
                notificationManager.success(
                    if (task.hasReminder) R.string.task_created_with_reminder
                    else R.string.task_created
                )
            } catch (e: Exception) {
                Log.e(TAG, "createTask failed", e)
                notificationManager.error(R.string.task_creation_error)
            } finally {
                _taskCreating.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!saved) {
            val pending = (_uiState.value as? NoteEditUiState.NewNote)
                ?.pendingAttachments.orEmpty()
            if (pending.isNotEmpty()) {
                kotlinx.coroutines.GlobalScope.launch {
                    pending.forEach { runCatching { deleteAttachment(it) } }
                }
            }
        }
    }

    private companion object {
        const val TAG = "NoteEditViewModel"
    }
}