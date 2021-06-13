package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.authentication.FakeAuthentication
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
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
class SaveReminderViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val reminderDataItem = ReminderDataItem(
        title = "title1",
        description = "description1",
        location = "location1",
        latitude = 1.0,
        longitude = 1.0,
        id = "1"
    )
    private val applicationContext: Application = ApplicationProvider.getApplicationContext()
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setupViewModel() {
        saveReminderViewModel = SaveReminderViewModel(
            applicationContext,
            FakeDataSource(),
            FakeAuthentication()
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `when clear reminder is called clear the live data objects`() =
        with(saveReminderViewModel) {
            reminderTitle.value = "title"
            reminderDescription.value = "description"
            selectedPOI.value = PointOfInterest(LatLng(0.0, 0.0), "P1", "P2")

            onClear()

            assertThat(reminderTitle.getOrAwaitValue(), nullValue())
            assertThat(reminderDescription.getOrAwaitValue(), nullValue())
            assertThat(selectedPOI.getOrAwaitValue(), nullValue())
        }

    @Test
    fun `when show snack bar permission denied is called set correct string`() =
        with(saveReminderViewModel)
        {
            showSnackBarpermissionDenied()

            val expected = applicationContext.getString(R.string.permission_denied_explanation)

            assertEquals(showSnackBar.getOrAwaitValue(), expected)
        }

    @Test
    fun `when show snack bar geo fence not added is called set correct string`() =
        with(saveReminderViewModel)
        {
            showToastGeoFenceNotAdded()

            val expected = applicationContext.getString(R.string.geofences_not_added)

            assertEquals(showToast.getOrAwaitValue(), expected)
        }

    @Test
    fun `when validate and save reminder is called correct loading indicator is set`() =
        with(saveReminderViewModel) {
            mainCoroutineRule.pauseDispatcher()
            validateAndSaveReminder(reminderDataItem)
            assertEquals(showLoading.getOrAwaitValue(), true)

            mainCoroutineRule.resumeDispatcher()
            assertEquals(showLoading.getOrAwaitValue(), false)
        }


    @Test
    fun `when validate and save reminder is called correct toast message and navigation command are set`() =
        with(saveReminderViewModel) {
            val isValidated = validateAndSaveReminder(reminderDataItem)

            val expected = applicationContext.getString(R.string.reminder_saved)

            assertEquals(showToast.getOrAwaitValue(), expected)
            assertEquals(navigationCommand.getOrAwaitValue(), NavigationCommand.Back)
            assertEquals(isValidated, true)
        }

    @Test
    fun `when validate and save reminder with title null correct snackbar message is set`() =
        with(saveReminderViewModel) {
            val isValidated = validateAndSaveReminder(reminderDataItem.copy(title = null))

            val expected = applicationContext.getString(R.string.err_enter_title)

            assertEquals(showSnackBar.getOrAwaitValue(), expected)
            assertEquals(isValidated, false)
        }

    @Test
    fun `when validate and save reminder with title empty correct snackbar message is set`() =
        with(saveReminderViewModel) {
            val isValidated = validateAndSaveReminder(reminderDataItem.copy(title = ""))

            val expected = applicationContext.getString(R.string.err_enter_title)

            assertEquals(showSnackBar.getOrAwaitValue(), expected)
            assertEquals(isValidated, false)
        }

    @Test
    fun `when validate and save reminder with location null correct snackbar message is set`() =
        with(saveReminderViewModel) {
            val isValidated = validateAndSaveReminder(reminderDataItem.copy(location = null))

            val expected = applicationContext.getString(R.string.err_select_location)

            assertEquals(showSnackBar.getOrAwaitValue(), expected)
            assertEquals(isValidated, false)
        }

    @Test
    fun `when validate and save reminder with location empty correct snackbar message is set`() =
        with(saveReminderViewModel) {
            val isValidated = validateAndSaveReminder(reminderDataItem.copy(location = ""))

            val expected = applicationContext.getString(R.string.err_select_location)

            assertEquals(showSnackBar.getOrAwaitValue(), expected)
            assertEquals(isValidated, false)
        }

    @Test
    fun `when validate and save reminder with latitude null correct snackbar message is set`() =
        with(saveReminderViewModel) {
            val isValidated = validateAndSaveReminder(reminderDataItem.copy(latitude = null))

            val expected = applicationContext.getString(R.string.err_select_location)

            assertEquals(showSnackBar.getOrAwaitValue(), expected)
            assertEquals(isValidated, false)
        }

    @Test
    fun `when validate and save reminder with longitude null correct snackbar message is set`() =
        with(saveReminderViewModel) {
            val isValidated = validateAndSaveReminder(reminderDataItem.copy(longitude = null))

            val expected = applicationContext.getString(R.string.err_select_location)

            assertEquals(showSnackBar.getOrAwaitValue(), expected)
            assertEquals(isValidated, false)
        }
}


