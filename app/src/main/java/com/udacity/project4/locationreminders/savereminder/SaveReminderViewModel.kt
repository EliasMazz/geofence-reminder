package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.authentication.IFirebaseAuth
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(
    private val app: Application,
    private val dataSource: ReminderDataSource,
    firebaseAuth: IFirebaseAuth
) : BaseViewModel(app, firebaseAuth) {
    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    val selectedPOI = MutableLiveData<PointOfInterest>()

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        selectedPOI.value = null
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(reminderData: ReminderDataItem) =
        if (validateEnteredData(reminderData)) {
            saveReminder(reminderData)
            true
        } else {
            false
        }

    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    fun showSnackBarpermissionDenied() {
        showSnackBar.value = app.getString(R.string.permission_denied_explanation)
    }

    fun showToastGeoFenceNotAdded() {
        showToast.value = app.getString(R.string.geofences_not_added)
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBar.value = app.getString(R.string.err_enter_title)
            return false
        }

        if (reminderData.location.isNullOrEmpty() ||
            reminderData.latitude == null ||
            reminderData.longitude == null
        ) {
            showSnackBar.value = app.getString(R.string.err_select_location)
            return false
        }
        return true
    }
}
