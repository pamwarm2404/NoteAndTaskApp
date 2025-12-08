package com.example.notetaskappgemini.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notetaskappgemini.NoteApplication
import com.example.notetaskappgemini.data.entity.User
import com.example.notetaskappgemini.databinding.FragmentUserProfileBinding
import com.example.notetaskappgemini.viewmodel.NoteViewModel
import com.example.notetaskappgemini.viewmodel.NoteViewModelFactory

// QUAN TRỌNG: Tên class phải là UserProfileFragment
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private var currentUser: User? = User(id = 1, username = "", email = "")

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((requireActivity().application as NoteApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                currentUser = user
                if (binding.etUsername.text.isNullOrBlank() && !binding.etUsername.hasFocus()) {
                    binding.etUsername.setText(user.username)
                }
                if (binding.etEmail.text.isNullOrBlank() && !binding.etEmail.hasFocus()) {
                    binding.etEmail.setText(user.email)
                }
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            saveAndResetProfile()
        }
    }

    private fun saveAndResetProfile() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (username.isBlank() || email.isBlank()) {
            Toast.makeText(context, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val userToSave = currentUser!!.copy(username = username, email = email)
        viewModel.insertUser(userToSave)

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        // Reset Form
        binding.etUsername.text?.clear()
        binding.etEmail.text?.clear()
        binding.etUsername.clearFocus()
        binding.etEmail.clearFocus()

        Toast.makeText(context, "✅ Đã lưu! Form đã được làm mới.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}