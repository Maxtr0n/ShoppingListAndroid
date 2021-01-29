package hu.bme.aut.android.shoppinglist.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items_table")
data class ShoppingItem(
        @PrimaryKey(autoGenerate = false)
        val id: String = "",

        val name: String? = null,

        val acquired: Boolean = false
) {

}