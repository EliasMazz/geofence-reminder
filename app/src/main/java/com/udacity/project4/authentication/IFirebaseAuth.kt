package com.udacity.project4.authentication

import androidx.lifecycle.LiveData

interface IFirebaseAuth {
    fun logoutUser()
    val isAuthenticated: LiveData<FirebaseAuthWrapper.AuthenticationState>
}
