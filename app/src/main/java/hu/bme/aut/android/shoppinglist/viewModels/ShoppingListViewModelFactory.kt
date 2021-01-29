package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.bme.aut.android.shoppinglist.database.ItemDao

class ShoppingListViewModelFactory(
        private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        if(modelClass.isAssignableFrom(ShoppingListViewModel::class.java)){
            return ShoppingListViewModel( application ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}