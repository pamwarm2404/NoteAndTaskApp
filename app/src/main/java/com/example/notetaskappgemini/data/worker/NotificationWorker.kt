package com.example.notetaskappgemini.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.notetaskappgemini.utils.sendNotification

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Kiểm tra xem người dùng có đang BẬT thông báo không
        val sharedPreferences = applicationContext.getSharedPreferences("NoteTaskSettings", Context.MODE_PRIVATE)
        val isReminderOn = sharedPreferences.getBoolean("is_reminder_enabled", false)

        // Nếu đã TẮT -> Dừng ngay, không gửi gì cả
        if (!isReminderOn) {
            return Result.success()
        }

        // Nếu đang BẬT -> Gửi thông báo
        val title = "Note Task Reminder"
        val message = "Đừng quên kiểm tra các công việc hôm nay nhé!"

        sendNotification(applicationContext, title, message)

        return Result.success()
    }
}