package hu.bme.aut.android.shoppinglist.models

import com.google.firebase.firestore.Exclude

data class User(
        var uid: String? = null,
        var name: String? = null,
        var email: String? = null,
        var listIds: ArrayList<String> = ArrayList()

){

}