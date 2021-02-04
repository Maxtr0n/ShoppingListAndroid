package hu.bme.aut.android.shoppinglist.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


data class ShoppingItem(
        var id: String = "",

        var name: String = "",

        var acquired: Boolean = false
) {

}