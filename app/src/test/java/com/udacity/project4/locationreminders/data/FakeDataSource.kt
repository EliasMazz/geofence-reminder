package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking

const val REMINDERS_NOT_FOUND_ERROR = "reminders not found"
const val REMINDER_NOT_FOUND_ERROR = "reminder not found"
const val REMINDER_TEST_EXCEPTION_ERROR = "Test exception"

class FakeDataSource(
    var reminders: MutableList<ReminderDTO>? = mutableListOf()
) : ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = true
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        runBlocking {
            if (shouldReturnError) {
                val exception = Exception(REMINDER_TEST_EXCEPTION_ERROR)
                return@runBlocking Result.Error(exception.localizedMessage)
            }
            reminders?.let {
                return@runBlocking Result.Success(ArrayList(it))
            } ?: Result.Error(REMINDERS_NOT_FOUND_ERROR)
        }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        runBlocking { reminders?.add(reminder) }
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        runBlocking {
            if (shouldReturnError) {
                val exception = Exception(REMINDER_TEST_EXCEPTION_ERROR)
                return@runBlocking Result.Error(exception.localizedMessage)
            }
            reminders?.firstOrNull { it.id == id }.let {
                return@runBlocking if (it != null) {
                    Result.Success(it)
                } else {
                    Result.Error(REMINDER_NOT_FOUND_ERROR)
                }
            }
        }

    override suspend fun deleteAllReminders() {
        runBlocking { reminders?.clear() }
    }
}
