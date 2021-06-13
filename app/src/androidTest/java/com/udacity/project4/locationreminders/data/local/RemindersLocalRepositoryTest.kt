package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var database: RemindersDatabase

    private val remidender1 = ReminderDTO(
        title = "title1",
        description = "description1",
        location = "location1",
        latitude = 1.0,
        longitude = 1.0,
        id = "1"
    )

    private val remidender2 = ReminderDTO(
        title = "title2",
        description = "description2",
        location = "location2",
        latitude = 2.0,
        longitude = 2.0,
        id = "2"
    )

    @Before
    fun set() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        reminderDataSource = RemindersLocalRepository(
            database.reminderDao(),
            mainCoroutineRule.dispatcher
        )
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun saveReminderAndGetById() = mainCoroutineRule.runBlockingTest {
        reminderDataSource.saveReminder(remidender1)

        val result = reminderDataSource.getReminder(remidender1.id)

        assertNotNull(result)
        assertTrue(result is Result.Success)

        val reminders = result as Result.Success
        assertEquals(reminders.data.id, remidender1.id)
        assertEquals(reminders.data.title, remidender1.title)
        assertEquals(reminders.data.description, remidender1.description)
        assertEquals(reminders.data.location, remidender1.location)
        assertEquals(reminders.data.latitude, remidender1.latitude)
        assertEquals(reminders.data.longitude, remidender1.longitude)
    }

    @Test
    fun saveReminderAndGetByIdError() = mainCoroutineRule.runBlockingTest {
        reminderDataSource.saveReminder(remidender1)

        val result = reminderDataSource.getReminder(remidender2.id)

        assertNotNull(result)
        assertTrue(result is Result.Error)

        val errorMessage = result as Result.Error
        assertEquals(errorMessage.message, REMINDER_NOT_FOUND_ERROR)
    }


    @Test
    fun saveRemindersAndDeleteAll() = mainCoroutineRule.runBlockingTest {
        reminderDataSource.saveReminder(remidender1)
        reminderDataSource.saveReminder(remidender2)

        reminderDataSource.deleteAllReminders()

        val result = reminderDataSource.getReminders()

        assertNotNull(result)
        assertTrue(result is Result.Success)

        val reminders = result as Result.Success
        assertEquals(reminders.data, emptyList<ReminderDTO>())
    }
}
