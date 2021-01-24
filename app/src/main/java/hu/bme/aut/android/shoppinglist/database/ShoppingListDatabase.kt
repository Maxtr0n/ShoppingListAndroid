package hu.bme.aut.android.shoppinglist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShoppingItem::class], version = 1)
abstract class ShoppingListDatabase : RoomDatabase() {

    abstract val itemDao: ItemDao

    companion object {

        @Volatile
        private var INSTANCE: ShoppingListDatabase? = null

        fun getInstance(context: Context): ShoppingListDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ShoppingListDatabase::class.java,
                        "shopping_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}