package hu.bme.aut.android.shoppinglist.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM shopping_items_table")
    fun getAllItems(): LiveData<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(shoppingItem: ShoppingItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(shoppingItems: List<ShoppingItem>)

    @Delete
    fun delete(shoppingItem: ShoppingItem)

    @Update
    fun update(shoppingItem: ShoppingItem)
}