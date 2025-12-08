package com.example.notetaskappgemini.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.data.repository.NoteRepository
import com.example.notetaskappgemini.data.entity.Category // ĐẢM BẢO CHỈ CÓ MỘT DÒNG NÀY CHO Category
import com.example.notetaskappgemini.data.entity.User
import kotlinx.coroutines.Dispatchers // ĐẢM BẢO CHỈ CÓ MỘT DÒNG NÀY CHO Coroutines
import kotlinx.coroutines.launch

class NoteViewModel (private val repository: NoteRepository) : ViewModel() {

    // --- NOTES ---
    val allNotes: LiveData<List<NoteTask>> = repository.allNotes.asLiveData()

    fun getNoteById(id: Int): LiveData<NoteTask?> = repository.getNoteById(id).asLiveData()

    // Sử dụng flow().asLiveData() để ViewModel xử lý tìm kiếm không đồng bộ
    fun searchNotes(query: String): LiveData<List<NoteTask>> {
        return repository.searchNotes(query).asLiveData()
    }

    fun insertNote(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(note)
    }

    fun deleteNote(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    fun updateNote(note: NoteTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note)
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
