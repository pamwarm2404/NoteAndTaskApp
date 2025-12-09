package com.example.notetaskappgemini.ui

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notetaskappgemini.NoteApplication
import com.example.notetaskappgemini.databinding.FragmentUserProfileBinding
import com.example.notetaskappgemini.data.entity.User
import com.example.notetaskappgemini.viewmodel.NoteViewModel
import com.example.notetaskappgemini.viewmodel.NoteViewModelFactory

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private var currentUser: User? = User(id = 1, username = "", email = "")

    // BIẾN CỜ: Đánh dấu xem có phải vừa bấm nút Lưu không
    private var isJustSaved = false

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((requireActivity().application as NoteApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Quan sát dữ liệu
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                currentUser = user

                // LOGIC QUAN TRỌNG:
                // Nếu vừa bấm Lưu xong (isJustSaved = true) thì BỎ QUA, không điền lại dữ liệu
                if (isJustSaved) {
                    isJustSaved = false // Reset cờ để lần sau hoạt động bình thường
                    return@observe
                }

                // Chỉ tự động điền khi mở màn hình lần đầu (lúc ô đang trống)
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

        // 1. Kiểm tra rỗng
        if (username.isBlank() || email.isBlank()) {
            Toast.makeText(context, "Vui lòng nhập đủ Tên và Email!", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Kiểm tra định dạng Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email không hợp lệ"
            binding.etEmail.requestFocus()
            return
        }

        // 3. Đánh dấu là đang Lưu (để chặn Observer tự điền lại)
        isJustSaved = true

        // 4. Lưu vào Database
        val userToSave = currentUser!!.copy(username = username, email = email)
        viewModel.insertUser(userToSave)

        // 5. Ẩn bàn phím
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        // 6. Xóa trắng form ngay lập tức
        binding.etUsername.text?.clear()
        binding.etEmail.text?.clear()

        binding.etUsername.clearFocus()
        binding.etEmail.clearFocus()
        binding.etEmail.error = null

        Toast.makeText(context, "✅ Đã lưu hồ sơ thành công!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}