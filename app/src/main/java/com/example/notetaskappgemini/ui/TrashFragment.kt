package com.example.notetaskappgemini.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetaskappgemini.NoteApplication
import com.example.notetaskappgemini.databinding.FragmentTrashBinding
import com.example.notetaskappgemini.ui.adapter.NoteAdapter
import com.example.notetaskappgemini.viewmodel.NoteViewModel
import com.example.notetaskappgemini.viewmodel.NoteViewModelFactory

class TrashFragment : Fragment() {
    private var _binding: FragmentTrashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((requireActivity().application as NoteApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NoteAdapter(
            onItemClick = { note ->
                // Bấm vào thì hỏi Khôi phục
                AlertDialog.Builder(context)
                    .setTitle("Khôi phục ghi chú?")
                    .setMessage("Bạn có muốn khôi phục ghi chú này về màn hình chính không?")
                    .setPositiveButton("Khôi phục") { _, _ ->
                        viewModel.restoreNote(note)
                        Toast.makeText(context, "Đã khôi phục", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            },
            onDeleteClick = { note ->
                // Bấm nút xóa thì hỏi Xóa vĩnh viễn
                AlertDialog.Builder(context)
                    .setTitle("Xóa vĩnh viễn?")
                    .setMessage("Hành động này không thể hoàn tác.")
                    .setPositiveButton("Xóa luôn") { _, _ ->
                        viewModel.deleteNote(note) // Xóa thật khỏi DB
                        Toast.makeText(context, "Đã xóa vĩnh viễn", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            }
        )

        binding.recyclerViewTrash.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.trashNotes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}