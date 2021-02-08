package hu.bme.aut.android.shoppinglist.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import hu.bme.aut.android.shoppinglist.network.FirebaseUserLiveData
import kotlin.random.Random

class LoginViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}
