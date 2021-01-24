package hu.bme.aut.android.shoppinglist.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items_table")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String?,

    val acquired: Boolean = false
) {

}