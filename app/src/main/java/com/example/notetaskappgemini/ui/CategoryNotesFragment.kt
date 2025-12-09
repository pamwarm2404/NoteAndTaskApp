package com.example.notetaskappgemini.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetaskappgemini.NoteApplication
import com.example.notetaskappgemini.databinding.FragmentCategoryNotesBinding
import com.example.notetaskappgemini.ui.adapter.NoteAdapter
import com.example.notetaskappgemini.viewmodel.NoteViewModel
import com.example.notetaskappgemini.viewmodel.NoteViewModelFactory

class CategoryNotesFragment : Fragment() {

    private var _binding: FragmentCategoryNotesBinding? = null
    private val binding get() = _binding!!

    // Nhận tên Category được gửi sang
    private val args: CategoryNotesFragmentArgs by navArgs()

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((requireActivity().application as NoteApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryName = args.categoryName
        binding.tvCategoryTitle.text = categoryName // Hiện tên category lên tiêu đề

        // Tái sử dụng NoteAdapter cũ
        val noteAdapter = NoteAdapter(
            onItemClick = { note ->
                // Bấm vào note thì mở chi tiết để sửa
                val action = CategoryNotesFragmentDirections.actionCategoryNotesFragmentToDetailFragment(note.id, note.type)
                findNavController().navigate(action)
            },
            onDeleteClick = { note ->
                viewModel.deleteNote(note)
                Toast.makeText(context, "Deleted Note", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewCategoryNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Quan sát dữ liệu đã lọc
        viewModel.getNotesByCategory(categoryName).observe(viewLifecycleOwner) { notes ->
            noteAdapter.submitList(notes)
            if (notes.isEmpty()) {
                Toast.makeText(context, "Không có ghi chú nào thuộc mục này", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}