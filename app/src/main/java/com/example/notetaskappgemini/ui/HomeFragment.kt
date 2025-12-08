package com.example.notetaskappgemini.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetaskappgemini.NoteApplication
import com.example.notetaskappgemini.R
import com.example.notetaskappgemini.databinding.FragmentHomeBinding
import com.example.notetaskappgemini.ui.adapter.NoteAdapter
import com.example.notetaskappgemini.viewmodel.NoteViewModel
import com.example.notetaskappgemini.viewmodel.NoteViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((requireActivity().application as NoteApplication).repository)
    }

    // Dùng nullable để an toàn tuyệt đối
    private var noteAdapter: NoteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        setupObservers()
        setupEvents()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
                        true
                    }
                    R.id.menu_categories -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoryListFragment())
                        true
                    }
                    R.id.menu_profile -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onItemClick = { note ->
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(noteId = note.id, type = note.type)
                findNavController().navigate(action)
            },
            onDeleteClick = { note ->
                viewModel.deleteNote(note)
                Toast.makeText(context, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerView.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupObservers() {
        viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            noteAdapter?.submitList(notes)
        }
    }

    private fun setupEvents() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(noteId = -1))
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (noteAdapter != null) {
                    if (query.isNotEmpty()) {
                        viewModel.searchNotes(query).observe(viewLifecycleOwner) { list -> noteAdapter?.submitList(list) }
                    } else {
                        viewModel.allNotes.observe(viewLifecycleOwner) { list -> noteAdapter?.submitList(list) }
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        noteAdapter = null
        _binding = null
    }
}