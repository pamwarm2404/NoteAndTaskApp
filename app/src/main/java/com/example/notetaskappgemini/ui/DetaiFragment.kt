package com.example.notetaskappgemini.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetaskappgemini.NoteApplication
import com.example.notetaskappgemini.data.entity.NoteTask
import com.example.notetaskappgemini.databinding.FragmentDetailBinding
import com.example.notetaskappgemini.viewmodel.NoteViewModel
import com.example.notetaskappgemini.viewmodel.NoteViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((requireActivity().application as NoteApplication).repository)
    }

    private val args: DetailFragmentArgs by navArgs()
    private var currentNote: NoteTask? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Cài đặt Spinner (Thêm "Uncategorized" mặc định)
        setupCategorySpinner()

        // 2. Kiểm tra nếu là Sửa Ghi chú cũ
        if (args.noteId != -1) {
            viewModel.getNoteById(args.noteId).observe(viewLifecycleOwner) { note ->
                note?.let {
                    currentNote = it
                    binding.etTitle.setText(it.title)
                    binding.etContent.setText(it.content)
                    // Lưu ý: Việc setSelection cho spinner sẽ được xử lý bên trong hàm setupCategorySpinner
                    // khi danh sách category tải xong.
                }
            }
        }

        // 3. Sự kiện bấm nút Lưu
        binding.fabSave.setOnClickListener {
            saveNote()
        }
    }

    private fun setupCategorySpinner() {
        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            // Tạo danh sách tên Category
            val categoryNames = mutableListOf<String>()

            // LUÔN LUÔN thêm "Uncategorized" vào đầu tiên
            categoryNames.add("Uncategorized")

            // Sau đó mới thêm các danh mục từ Database
            categoryNames.addAll(categories.map { it.name })

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            // Logic chọn lại đúng Category cũ (nếu đang sửa)
            if (currentNote != null) {
                val position = categoryNames.indexOf(currentNote!!.type)
                if (position >= 0) {
                    binding.spinnerCategory.setSelection(position)
                } else {
                    // Nếu không tìm thấy (hoặc là note cũ chưa có category), chọn mặc định là 0 (Uncategorized)
                    binding.spinnerCategory.setSelection(0)
                }
            }
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()

        // Lấy giá trị từ Spinner, nếu lỗi thì mặc định là "Uncategorized"
        val selectedCategory = binding.spinnerCategory.selectedItem?.toString() ?: "Uncategorized"

        if (title.isEmpty()) {
            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        // Sử dụng thời gian thực hệ thống (kiểu Long) để khớp với Database
        val currentDate = System.currentTimeMillis()

        if (currentNote == null) {
            // Thêm mới
            val newNote = NoteTask(
                id = 0,
                title = title,
                content = content,
                date = currentDate,
                type = selectedCategory // Lưu loại vào
            )
            viewModel.insertNote(newNote)
            Toast.makeText(context, "Note Saved!", Toast.LENGTH_SHORT).show()
        } else {
            // Cập nhật
            val updateNote = currentNote!!.copy(
                title = title,
                content = content,
                date = currentDate,
                type = selectedCategory // Cập nhật loại mới
            )
            viewModel.updateNote(updateNote)
            Toast.makeText(context, "Note Updated!", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}