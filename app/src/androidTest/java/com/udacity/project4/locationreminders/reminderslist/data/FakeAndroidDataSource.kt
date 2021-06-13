package com.udacity.project4.locationreminders.reminderslist.data

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking

const val REMINDERS_NOT_FOUND_ERROR = "reminders not found"
const val REMINDER_NOT_FOUND_ERROR = "reminder not found"

class FakeAndroidDataSource(
    var reminders: MutableList<ReminderDTO>? = mutableListOf()
) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        runBlocking {
            reminders?.let {
                return@runBlocking Result.Success(ArrayList(it))
            } ?: Result.Error(REMINDERS_NOT_FOUND_ERROR)
        }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        runBlocking { reminders?.add(reminder) }
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        runBlocking {
            reminders?.firstOrNull { it.id == id }.let {
                return@runBlocking if (it != null) {
                    Result.Success(it)
                } else {
                    Result.Error("reminder not found")
                }
            }
        }

    override suspend fun deleteAllReminders() {
        runBlocking { reminders?.clear() }
    }
}
