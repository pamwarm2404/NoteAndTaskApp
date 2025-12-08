package com.example.notetaskappgemini.data.dao

import androidx.room.*
import com.example.notetaskappgemini.data.entity.Category
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // --- NOTES ---
    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAllNotes(): Flow<List<NoteTask>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<NoteTask?>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<NoteTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteTask)

    @Delete
    suspend fun deleteNote(note: NoteTask)

    @Update
    suspend fun updateNote(note: NoteTask)

    // --- CATEGORIES ---
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    // SỬA: Dùng annotation @Delete thay vì viết hàm logic
    @Delete
    suspend fun deleteCategory(category: Category)

    // --- USERS ---
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}