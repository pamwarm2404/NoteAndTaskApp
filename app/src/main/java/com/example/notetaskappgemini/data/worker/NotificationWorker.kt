package com.example.notetaskappgemini.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.notetaskappgemini.utils.sendNotification

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        sendNotification(
            applicationContext,
            "Note Task Reminder",
            "Đã đến lúc kiểm tra các ghi chú và tác vụ của bạn!"
        )
        return Result.success()
    }
}