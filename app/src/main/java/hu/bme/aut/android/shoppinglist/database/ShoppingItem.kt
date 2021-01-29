package hu.bme.aut.android.shoppinglist.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


data class ShoppingItem(
        val name: String? = null,

        val acquired: Boolean = false
) {

}