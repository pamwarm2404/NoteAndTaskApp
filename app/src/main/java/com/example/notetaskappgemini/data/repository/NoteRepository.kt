package com.example.notetaskappgemini.data.repository

import com.example.notetaskappgemini.data.dao.NoteDao
import com.example.notetaskappgemini.data.entity.Category
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.data.entity.User
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    // Notes
    val allNotes: Flow<List<NoteTask>> = noteDao.getAllNotes()

    fun getNoteById(id: Int): Flow<NoteTask?> = noteDao.getNoteById(id)

    fun searchNotes(query: String): Flow<List<NoteTask>> = noteDao.searchNotes(query)

    suspend fun insertNote(note: NoteTask) = noteDao.insertNote(note)

    suspend fun deleteNote(note: NoteTask) = noteDao.deleteNote(note)

    suspend fun updateNote(note: NoteTask) = noteDao.updateNote(note)

    // Categories
    val allCategories: Flow<List<Category>> = noteDao.getAllCategories()

    suspend fun insertCategory(category: Category) = noteDao.insertCategory(category)

    // SỬA: Gọi trực tiếp hàm suspend từ DAO
    suspend fun deleteCategory(category: Category) = noteDao.deleteCategory(category)

    // User
    val currentUser: Flow<User?> = noteDao.getCurrentUser()

    suspend fun insertUser(user: User) = noteDao.insertUser(user)
}