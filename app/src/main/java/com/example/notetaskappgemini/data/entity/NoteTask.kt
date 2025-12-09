package com.example.notetaskappgemini.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val date: Long,
    val type: String,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false // Cột mới: false = hiện, true = trong thùng rác
)