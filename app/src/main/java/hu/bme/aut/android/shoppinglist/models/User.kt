package hu.bme.aut.android.shoppinglist.models

import com.google.firebase.firestore.Exclude

data class User(
        var uid: String = "",
        var name: String = "",
        var email: String = "",
        var hasProfilePicture: Boolean = false,
        var listIds: ArrayList<String> = ArrayList()

){

}