package com.example.notetaskappgemini.data.repository

import com.example.notetaskappgemini.data.dao.NoteDao
import com.example.notetaskappgemini.data.entity.Category
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.data.entity.User
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    // Notes
    val allNotes: Flow<List<NoteTask>> = noteDao.getAllNotes()

    // Thùng rác
    val trashNotes: Flow<List<NoteTask>> = noteDao.getTrashNotes()

    fun getNoteById(id: Int): Flow<NoteTask?> = noteDao.getNoteById(id)

    fun searchNotes(query: String): Flow<List<NoteTask>> = noteDao.searchNotes(query)

    fun getNotesByCategory(categoryName: String): Flow<List<NoteTask>> = noteDao.getNotesByCategory(categoryName)

    suspend fun insertNote(note: NoteTask) = noteDao.insertNote(note)

    suspend fun deleteNote(note: NoteTask) = noteDao.deleteNote(note) // Xóa thật

    suspend fun updateNote(note: NoteTask) = noteDao.updateNote(note) // Chuyển vào thùng rác

    suspend fun deleteExpiredNotes() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        noteDao.deleteExpiredNotes(thirtyDaysAgo)
    }

    // Categories
    val allCategories: Flow<List<Category>> = noteDao.getAllCategories()
    suspend fun insertCategory(category: Category) = noteDao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = noteDao.deleteCategory(category)

    // User
    val currentUser: Flow<User?> = noteDao.getCurrentUser()
    suspend fun insertUser(user: User) = noteDao.insertUser(user)
}