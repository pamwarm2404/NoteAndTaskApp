package com.example.notetaskappgemini.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetaskappgemini.NoteApplication
import com.example.notetaskappgemini.R
import com.example.notetaskappgemini.data.entity.Category
import com.example.notetaskappgemini.databinding.FragmentCategoryListBinding
import com.example.notetaskappgemini.ui.adapter.CategoryAdapter
import com.example.notetaskappgemini.viewmodel.NoteViewModel
import com.example.notetaskappgemini.viewmodel.NoteViewModelFactory

class CategoryListFragment : Fragment() {
    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((requireActivity().application as NoteApplication).repository)
    }

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryAdapter(
            onItemClick = { }, // Có thể thêm logic sửa sau này
            onDeleteClick = { category ->
                viewModel.deleteCategory(category)
                Toast.makeText(context, "Deleted: ${category.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)

        AlertDialog.Builder(context)
            .setTitle("Add New Category")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etCategoryName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val newCategory = Category(id = 0, name = name)
                    viewModel.insertCategory(newCategory)
                    Toast.makeText(context, "Added: $name", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}