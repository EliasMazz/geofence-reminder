package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.authentication.FirebaseAuthWrapper.AuthenticationState
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.authentication.FakeAuthentication
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.REMINDERS_NOT_FOUND_ERROR
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource

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

    private val reminders: MutableList<ReminderDTO> = mutableListOf(remidender1, remidender2)

    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource(reminders)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource,
            FakeAuthentication()
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `when logout user is called call firebase auth logout user`() =
        with(reminderListViewModel)
        {
            assertEquals(isAuthenticated.getOrAwaitValue(), AuthenticationState.AUTHENTICATED)
            logoutUser()
            assertEquals(isAuthenticated.getOrAwaitValue(), AuthenticationState.UNAUTHENTICATED)
        }

    @Test
    fun `when loadReminders is succesfuly correct loading indicator is set`() =
        with(reminderListViewModel)
        {
            mainCoroutineRule.pauseDispatcher()
            loadReminders()
            assertEquals(showLoading.getOrAwaitValue(), true)

            mainCoroutineRule.resumeDispatcher()
            assertEquals(showLoading.getOrAwaitValue(), false)
        }

    @Test
    fun `when loadReminders is succesfuly correct remindersList is set correct`() =
        with(reminderListViewModel)
        {
            loadReminders()
            val result = remindersList.getOrAwaitValue()

            result.forEachIndexed { index, reminderDataItem ->
                assertEquals(reminderDataItem.title, reminders[index].title)
                assertEquals(reminderDataItem.description, reminders[index].description)
                assertEquals(reminderDataItem.longitude, reminders[index].longitude)
                assertEquals(reminderDataItem.latitude, reminders[index].latitude)
                assertEquals(reminderDataItem.location, reminders[index].location)
                assertEquals(reminderDataItem.id, reminders[index].id)
            }
        }

    @Test
    fun `when loadReminders fails set correct snackbar message`() =
        with(reminderListViewModel)
        {
            fakeDataSource.reminders = null

            loadReminders()

            assertEquals(showSnackBar.getOrAwaitValue(), REMINDERS_NOT_FOUND_ERROR)
        }

    @Test
    fun `when loadReminders is called without reminders set show no data true`() =
        with(reminderListViewModel)
        {
            fakeDataSource.reminders = mutableListOf()
            loadReminders()

            assertEquals(showNoData.getOrAwaitValue(), true)
        }

    @Test
    fun `when loadReminders is called with reminders null set show no data true`() =
        with(reminderListViewModel)
        {
            fakeDataSource.reminders = mutableListOf()
            loadReminders()

            assertEquals(showNoData.getOrAwaitValue(), true)
        }

    @Test
    fun `when loadReminders is called with reminders set show no data false`() =
        with(reminderListViewModel)
        {
            loadReminders()

            assertEquals(showNoData.getOrAwaitValue(), false)
        }
}
