package com.example.notetaskappgemini.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notetaskappgemini.data.entity.Category
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.data.entity.User
import com.example.notetaskappgemini.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    // --- NOTES ---
    val allNotes: LiveData<List<NoteTask>> = repository.allNotes.asLiveData()
    val trashNotes: LiveData<List<NoteTask>> = repository.trashNotes.asLiveData() // Danh sách thùng rác

    fun getNoteById(id: Int): LiveData<NoteTask?> = repository.getNoteById(id).asLiveData()

    fun searchNotes(query: String): LiveData<List<NoteTask>> {
        return repository.searchNotes(query).asLiveData()
    }

    fun getNotesByCategory(categoryName: String): LiveData<List<NoteTask>> {
        return repository.getNotesByCategory(categoryName).asLiveData()
    }

    fun insertNote(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(note)
    }

    fun updateNote(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note)
    }

    // Xóa vĩnh viễn
    fun deleteNote(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    // Chuyển vào thùng rác (Soft Delete)
    fun moveToTrash(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note.copy(isDeleted = true, isPinned = false))
    }

    // Khôi phục từ thùng rác
    fun restoreNote(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note.copy(isDeleted = false))
    }

    // --- CATEGORIES ---
    val allCategories: LiveData<List<Category>> = repository.allCategories.asLiveData()

    fun insertCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteCategory(category)
    }

    // --- USER ---
    val currentUser: LiveData<User?> = repository.currentUser.asLiveData()

    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUser(user)
    }
}