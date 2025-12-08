package com.example.notetaskappgemini.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class NoteTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis(),
    val type: String = "Note", // "Note" hoặc "Task"
    val isCompleted: Boolean = false // Thêm cái này để làm checkbox cho Task
) : Serializable