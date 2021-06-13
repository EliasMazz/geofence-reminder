package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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

    private val listOfReminders = mutableListOf(remidender1, remidender2)

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        database.reminderDao().saveReminder(remidender1)

        val result = database.reminderDao().getReminderById(remidender1.id)

        assertNotNull(result)
        assertEquals(result?.id, remidender1.id)
        assertEquals(result?.description, remidender1.description)
        assertEquals(result?.location, remidender1.location)
        assertEquals(result?.latitude, remidender1.latitude)
    }

    @Test
    fun saveReminderAndGetByIdError() = runBlockingTest {
        database.reminderDao().saveReminder(remidender1)

        val result = database.reminderDao().getReminderById(remidender2.id)

        assertNull(result)
    }

    @Test
    fun saveRemindersAndDeleteAll() = runBlockingTest {
        database.reminderDao().saveReminder(remidender1)
        database.reminderDao().saveReminder(remidender2)

        database.reminderDao().deleteAllReminders()

        val result = database.reminderDao().getReminders()

        assertEquals(result, emptyList<ReminderDTO>())
    }
}
