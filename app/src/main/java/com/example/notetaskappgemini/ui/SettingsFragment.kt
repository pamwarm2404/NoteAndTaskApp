package com.example.notetaskappgemini.ui

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
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val WORK_NAME = "NoteTaskReminderWork"
    private val PREFS_NAME = "NoteTaskSettings"
    private val KEY_REMINDER = "is_reminder_enabled"

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
        setupReminderSwitch()
    }

    private fun setupThemeSwitch() {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        binding.switchTheme.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupReminderSwitch() {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isReminderOn = sharedPreferences.getBoolean(KEY_REMINDER, false)
        binding.switchReminder.isChecked = isReminderOn

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_REMINDER, isChecked).apply()

            if (isChecked) {
                val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES).build()
                WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
                Toast.makeText(context, "Đã BẬT thông báo nhắc nhở", Toast.LENGTH_SHORT).show()
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork(WORK_NAME)
                Toast.makeText(context, "Đã TẮT thông báo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}