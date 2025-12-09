package com.example.notetaskappgemini.data.dao

import androidx.room.*
import com.example.notetaskappgemini.data.entity.Category
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // --- NOTES (Chỉ lấy bài chưa xóa: isDeleted = 0) ---
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, date DESC")
    fun getAllNotes(): Flow<List<NoteTask>>

    // Lấy danh sách Thùng rác (isDeleted = 1)
    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY date DESC")
    fun getTrashNotes(): Flow<List<NoteTask>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<NoteTask?>

    // Tìm kiếm (Chỉ trong bài chưa xóa)
    @Query("SELECT * FROM notes WHERE (isDeleted = 0) AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY isPinned DESC, date DESC")
    fun searchNotes(query: String): Flow<List<NoteTask>>

    // Lọc theo Category (Chỉ bài chưa xóa)
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND type = :categoryName ORDER BY isPinned DESC, date DESC")
    fun getNotesByCategory(categoryName: String): Flow<List<NoteTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteTask)

    @Delete
    suspend fun deleteNote(note: NoteTask) // Xóa vĩnh viễn

    @Update
    suspend fun updateNote(note: NoteTask) // Dùng để chuyển vào thùng rác/khôi phục

    // Xóa ghi chú trong thùng rác quá 30 ngày
    @Query("DELETE FROM notes WHERE isDeleted = 1 AND date < :threshold")
    suspend fun deleteExpiredNotes(threshold: Long)

    // --- CATEGORIES ---
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    // --- USER ---
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}