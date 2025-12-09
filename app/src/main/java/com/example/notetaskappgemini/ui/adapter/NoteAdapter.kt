package com.example.notetaskappgemini.ui.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
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
    private val onItemClick: (NoteTask) -> Unit,
    private val onDeleteClick: (NoteTask) -> Unit
) : ListAdapter<NoteTask, NoteAdapter.NoteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteTask) {
            binding.tvTitle.text = note.title

            // --- ĐÃ SỬA: Chuyển mã HTML thành văn bản hiển thị được ---
            // Nếu không có dòng này, bạn sẽ thấy các thẻ <font>, <br> hiện ra màn hình
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                binding.tvContent.text = Html.fromHtml(note.content, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                binding.tvContent.text = Html.fromHtml(note.content)
            }
            // ---------------------------------------------------------

            // Format ngày tháng từ Long sang String
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(Date(note.date))

            // Hiển thị icon Ghim nếu isPinned = true
            binding.ivPinIcon.visibility = if (note.isPinned) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onItemClick(note)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(note)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NoteTask>() {
        override fun areItemsTheSame(oldItem: NoteTask, newItem: NoteTask) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: NoteTask, newItem: NoteTask) = oldItem == newItem
    }
}