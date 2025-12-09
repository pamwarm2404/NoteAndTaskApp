package com.example.notetaskappgemini.ui

import android.app.AlertDialog
import android.graphics.Color
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

        // 1. Cài đặt Editor
        setupRichEditor()

        // 2. Cài đặt Spinner Category
        setupCategorySpinner()

        // 3. Load dữ liệu cũ (nếu có)
        if (args.noteId != -1) {
            viewModel.getNoteById(args.noteId).observe(viewLifecycleOwner) { note ->
                note?.let {
                    currentNote = it
                    binding.etTitle.setText(it.title)
                    binding.cbPin.isChecked = it.isPinned

                    // QUAN TRỌNG: Load nội dung HTML vào Editor
                    binding.editor.html = it.content
                }
            }
        }

        binding.fabSave.setOnClickListener {
            saveNote()
        }
    }

    private fun setupRichEditor() {
        binding.editor.setPlaceholder("Type something here...")
        binding.editor.setPadding(10, 10, 10, 10)
        binding.editor.setEditorFontSize(18)

        // Sự kiện các nút công cụ
        binding.actionBold.setOnClickListener { binding.editor.setBold() }
        binding.actionItalic.setOnClickListener { binding.editor.setItalic() }

        // Chọn màu chữ
        binding.actionColor.setOnClickListener {
            showColorPickerDialog { color -> binding.editor.setTextColor(color) }
        }

        // Chọn màu Highlight (Nền chữ)
        binding.actionHighlight.setOnClickListener {
            showColorPickerDialog { color -> binding.editor.setTextBackgroundColor(color) }
        }
    }

    // Hàm hiển thị hộp thoại chọn màu đơn giản
    private fun showColorPickerDialog(onColorSelected: (Int) -> Unit) {
        val colors = arrayOf("Black", "Red", "Blue", "Green", "Yellow", "Cyan")
        val colorValues = intArrayOf(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN)

        AlertDialog.Builder(context)
            .setTitle("Choose Color")
            .setItems(colors) { _, which ->
                onColorSelected(colorValues[which])
            }
            .show()
    }

    private fun setupCategorySpinner() {
        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = mutableListOf<String>()
            categoryNames.add("Uncategorized")
            categoryNames.addAll(categories.map { it.name })

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            if (currentNote != null) {
                val position = categoryNames.indexOf(currentNote!!.type)
                binding.spinnerCategory.setSelection(if (position >= 0) position else 0)
            }
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()

        // QUAN TRỌNG: Lấy nội dung HTML từ Editor
        val content = binding.editor.html ?: ""

        val selectedCategory = binding.spinnerCategory.selectedItem?.toString() ?: "Uncategorized"
        val isPinned = binding.cbPin.isChecked

        if (title.isEmpty()) {
            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = System.currentTimeMillis()

        if (currentNote == null) {
            val newNote = NoteTask(0, title, content, currentDate, selectedCategory, isPinned, false)
            viewModel.insertNote(newNote)
        } else {
            val updateNote = currentNote!!.copy(title = title, content = content, date = currentDate, type = selectedCategory, isPinned = isPinned)
            viewModel.updateNote(updateNote)
        }
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}