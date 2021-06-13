package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.FirebaseAuthWrapper
import com.udacity.project4.authentication.IFirebaseAuth
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.data.FakeAndroidDataSource
import com.udacity.project4.locationreminders.reminderslist.data.REMINDERS_NOT_FOUND_ERROR
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {
    private lateinit var fakeRepository: FakeAndroidDataSource
    private lateinit var firebaseAuthInstance: FirebaseAuth
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        firebaseAuthInstance = FirebaseAuth.getInstance()
        fakeRepository = FakeAndroidDataSource()

        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    fakeRepository,
                    get() as IFirebaseAuth
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    fakeRepository,
                    get() as IFirebaseAuth
                )
            }
            single {
                FirebaseAuthWrapper(
                    appContext,
                    firebaseAuthInstance
                ) as IFirebaseAuth
            }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //clear the data to start fresh
        runBlocking {
            fakeRepository.deleteAllReminders()
        }
    }

    @Test
    fun reminderItemList_reminderCorrectDisplayed() = runBlockingTest {
        val remidender1 = ReminderDTO(
            title = "title1",
            description = "description1",
            location = "location1",
            latitude = 1.0,
            longitude = 1.0,
            id = "1"
        )

        fakeRepository.saveReminder(remidender1)
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withText(remidender1.title)).check(matches(isDisplayed()))
        onView(withText(remidender1.description)).check(matches(isDisplayed()))
    }

    @Test
    fun reminderItemListEmpty_noDataIsDisplayed() {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withText(R.string.no_data)).check(matches(isDisplayed()))
    }

    @Test
    fun listReturnError_errorMessageIsShown() = runBlockingTest {
        fakeRepository.reminders = null

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withText(REMINDERS_NOT_FOUND_ERROR)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCreateReminder_navigateToSaveReminder() = runBlockingTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
}
