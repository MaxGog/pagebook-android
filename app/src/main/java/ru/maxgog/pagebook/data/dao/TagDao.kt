package ru.maxgog.pagebook.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.maxgog.pagebook.data.models.EventTagCrossRef
import ru.maxgog.pagebook.data.models.EventWithTags
import ru.maxgog.pagebook.data.models.NoteTagCrossRef
import ru.maxgog.pagebook.data.models.NoteWithTags
import ru.maxgog.pagebook.data.models.TagModel
import ru.maxgog.pagebook.data.models.TaskTagCrossRef
import ru.maxgog.pagebook.data.models.TaskWithTags

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY Name ASC")
    fun getAllTags(): Flow<List<TagModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagModel): Long

    @Update
    suspend fun updateTag(tag: TagModel)

    @Delete
    suspend fun deleteTag(tag: TagModel)

    // Связи Note ↔ Tag
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNoteTagCrossRef(ref: NoteTagCrossRef)

    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId AND tagId = :tagId")
    suspend fun deleteNoteTagCrossRef(noteId: Int, tagId: Int)

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN note_tag_cross_ref c ON t.id = c.tagId
        WHERE c.noteId = :noteId
    """)
    fun getTagsForNote(noteId: Int): Flow<List<TagModel>>

    @Transaction
    @Query("SELECT * FROM notes WHERE NoteId = :noteId")
    suspend fun getNoteWithTags(noteId: Int): NoteWithTags?

    // Связи Task ↔ Tag
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTagCrossRef(ref: TaskTagCrossRef)

    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun deleteTaskTagCrossRef(taskId: Int, tagId: Int)

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN task_tag_cross_ref c ON t.id = c.tagId
        WHERE c.taskId = :taskId
    """)
    fun getTagsForTask(taskId: Int): Flow<List<TagModel>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithTags(taskId: Int): TaskWithTags?

    // Связи Event ↔ Tag
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEventTagCrossRef(ref: EventTagCrossRef)

    @Query("DELETE FROM event_tag_cross_ref WHERE eventId = :eventId AND tagId = :tagId")
    suspend fun deleteEventTagCrossRef(eventId: Int, tagId: Int)

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN event_tag_cross_ref c ON t.id = c.tagId
        WHERE c.eventId = :eventId
    """)
    fun getTagsForEvent(eventId: Int): Flow<List<TagModel>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventWithTags(eventId: Int): EventWithTags?

    @Query("SELECT EXISTS(SELECT 1 FROM note_tag_cross_ref WHERE noteId = :noteId AND tagId = :tagId)")
    suspend fun hasTagOnNote(noteId: Int, tagId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId)")
    suspend fun hasTagOnTask(taskId: Int, tagId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM event_tag_cross_ref WHERE eventId = :eventId AND tagId = :tagId)")
    suspend fun hasTagOnEvent(eventId: Int, tagId: Int): Boolean
}