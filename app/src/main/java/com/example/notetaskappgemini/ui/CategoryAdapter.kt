package com.example.notetaskappgemini.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notetaskappgemini.data.entity.Category
import com.example.notetaskappgemini.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit // Hàm xử lý khi bấm nút Xóa
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            // 1. Gán tên Category vào TextView (Sửa lỗi hiển thị toàn chữ "Work")
            binding.tvCategoryName.text = category.name

            // 2. Xử lý sự kiện click vào toàn bộ dòng (để sửa)
            binding.root.setOnClickListener {
                onItemClick(category)
            }

            // 3. Xử lý sự kiện click vào nút Xóa (btnDeleteCategory)
            binding.btnDeleteCategory.setOnClickListener {
                onDeleteClick(category)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}