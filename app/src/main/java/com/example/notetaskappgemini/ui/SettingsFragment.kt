package com.example.notetaskappgemini.ui

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notetaskappgemini.databinding.FragmentSettingsBinding
import com.example.notetaskappgemini.data.worker.NotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val WORK_NAME = "NoteTaskDailyReminder" // Đổi tên để tránh xung đột cũ

    private val PREFS_NAME = "NoteTaskSettings"
    private val KEY_REMINDER = "is_reminder_enabled"
    private val KEY_HOUR = "reminder_hour"
    private val KEY_MINUTE = "reminder_minute"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupThemeSwitch()
        setupReminderSettings()
    }

    private fun setupThemeSwitch() {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        binding.switchTheme.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun setupReminderSettings() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(KEY_REMINDER, false)

        // Mặc định là 8:00 sáng nếu chưa chỉnh
        val savedHour = prefs.getInt(KEY_HOUR, 8)
        val savedMinute = prefs.getInt(KEY_MINUTE, 0)

        // Cập nhật UI
        binding.switchReminder.isChecked = isEnabled
        binding.layoutTimePicker.visibility = if (isEnabled) View.VISIBLE else View.GONE
        binding.tvTimeSelected.text = String.format("%02d:%02d", savedHour, savedMinute)

        // Sự kiện Bật/Tắt Switch
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_REMINDER, isChecked).apply()

            if (isChecked) {
                binding.layoutTimePicker.visibility = View.VISIBLE
                scheduleNotification(savedHour, savedMinute) // Lên lịch ngay
                Toast.makeText(context, "Đã bật nhắc nhở", Toast.LENGTH_SHORT).show()
            } else {
                binding.layoutTimePicker.visibility = View.GONE
                cancelNotification()
                Toast.makeText(context, "Đã tắt nhắc nhở", Toast.LENGTH_SHORT).show()
            }
        }

        // Sự kiện Bấm vào giờ để chỉnh
        binding.tvTimeSelected.setOnClickListener {
            val currentHour = prefs.getInt(KEY_HOUR, 8)
            val currentMinute = prefs.getInt(KEY_MINUTE, 0)

            TimePickerDialog(context, { _, hourOfDay, minute ->
                // 1. Lưu giờ mới vào bộ nhớ
                prefs.edit().putInt(KEY_HOUR, hourOfDay).putInt(KEY_MINUTE, minute).apply()

                // 2. Cập nhật giao diện
                binding.tvTimeSelected.text = String.format("%02d:%02d", hourOfDay, minute)

                // 3. Lên lịch lại với giờ mới
                scheduleNotification(hourOfDay, minute)
                Toast.makeText(context, "Đã cập nhật giờ nhắc: ${binding.tvTimeSelected.text}", Toast.LENGTH_SHORT).show()

            }, currentHour, currentMinute, true).show()
        }
    }

    private fun scheduleNotification(hour: Int, minute: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Nếu giờ chọn đã qua (ví dụ: bây giờ 10h, chọn 8h), thì đặt cho ngày mai
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Tính toán độ trễ (Delay)
        val initialDelay = target.timeInMillis - now.timeInMillis

        // Tạo Worker lặp lại mỗi 24 giờ
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // Thay thế lịch cũ bằng lịch mới
            workRequest
        )
    }

    private fun cancelNotification() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork(WORK_NAME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}