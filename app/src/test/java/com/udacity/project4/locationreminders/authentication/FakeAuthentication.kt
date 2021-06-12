package com.udacity.project4.locationreminders.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.authentication.FirebaseAuthWrapper
import com.udacity.project4.authentication.IFirebaseAuth

class FakeAuthentication : IFirebaseAuth {
    override fun logoutUser() {
        authenticated.value = FirebaseAuthWrapper.AuthenticationState.UNAUTHENTICATED
    }

    private val authenticated = MutableLiveData<FirebaseAuthWrapper.AuthenticationState>()

    override val isAuthenticated: LiveData<FirebaseAuthWrapper.AuthenticationState>
        get() {
            authenticated.value = FirebaseAuthWrapper.AuthenticationState.AUTHENTICATED
            return authenticated
        }
}
