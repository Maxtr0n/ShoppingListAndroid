package hu.bme.aut.android.shoppinglist.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items_table")
data class ShoppingItem(
        @PrimaryKey(autoGenerate = false)
        var id: String = "",

        var name: String? = null,

        var acquired: Boolean = false
) {

}