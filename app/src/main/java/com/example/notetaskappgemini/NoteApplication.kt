package com.example.notetaskappgemini

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notetaskappgemini.data.local.NoteDatabase
import com.example.notetaskappgemini.data.repository.NoteRepository
import com.example.notetaskappgemini.data.worker.NotificationWorker
import java.util.concurrent.TimeUnit

class NoteApplication : Application() {

    // Sử dụng lazy để chỉ khởi tạo khi thực sự cần dùng
    val database by lazy { NoteDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }

    private val WORK_NAME = "NoteTaskReminderWork"

    override fun onCreate() {
        super.onCreate()

        // GỌI HÀM LÊN LỊCH TÁC VỤ KHI ỨNG DỤNG KHỞI ĐỘNG
        scheduleNotificationWorker()
    }

    private fun scheduleNotificationWorker() {
        // 1. Định nghĩa yêu cầu công việc lặp lại (ví dụ: mỗi 15 phút)
        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15,
            TimeUnit.MINUTES // Tối thiểu là 15 phút theo quy tắc Android
        )
            .build()

        // 2. Lên lịch tác vụ
        // enqueueUniquePeriodicWork đảm bảo chỉ có MỘT tác vụ này được lên lịch
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Giữ lại phiên bản cũ nếu đã tồn tại
            notificationWorkRequest
        )
    }
}