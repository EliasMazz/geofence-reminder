package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.model.PointOfInterest

@BindingAdapter("saveButtonVisibility")
fun bindSaveButton(view: View, pointOfInterest: PointOfInterest?) {
    pointOfInterest?.let {
        view.visibility = View.VISIBLE
    }
}
