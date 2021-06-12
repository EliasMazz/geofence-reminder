package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.Before

class RemindersLocalRepositoryTest {
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

    private val remidender3 = ReminderDTO(
        title = "title3",
        description = "description3",
        location = "location3",
        latitude = 3.0,
        longitude = 3.0,
        id = "3"
    )

    private lateinit var dao: RemindersDao

    @Before
    fun setup() {
        //dao = Fa

        RemindersLocalRepository(
            dao
        )
    }

}
