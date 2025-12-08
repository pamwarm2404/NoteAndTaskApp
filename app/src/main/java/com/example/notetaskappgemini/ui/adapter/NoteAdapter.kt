package com.example.notetaskappgemini.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onItemClick: (NoteTask) -> Unit,    // Sự kiện click vào item để sửa
    private val onDeleteClick: (NoteTask) -> Unit   // Sự kiện click nút xóa
) : ListAdapter<NoteTask, NoteAdapter.NoteViewHolder>(DiffCallback()) {

    // Tạo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    // Gán dữ liệu vào ViewHolder
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    // Class ViewHolder: Nắm giữ view của 1 dòng
    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteTask) {
            binding.apply {
                tvTitle.text = note.title
                tvContent.text = note.content

                // Format ngày tháng từ Long sang String đẹp mắt
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                tvDate.text = dateFormat.format(Date(note.date))

                // Bắt sự kiện click
                root.setOnClickListener { onItemClick(note) }
                btnDelete.setOnClickListener { onDeleteClick(note) }
            }
        }
    }

    // Class DiffCallback: So sánh dữ liệu cũ và mới để cập nhật thông minh
    class DiffCallback : DiffUtil.ItemCallback<NoteTask>() {
        override fun areItemsTheSame(oldItem: NoteTask, newItem: NoteTask) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: NoteTask, newItem: NoteTask) =
            oldItem == newItem
    }
}