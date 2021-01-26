package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import hu.bme.aut.android.shoppinglist.database.ItemDao
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import kotlinx.coroutines.*

class ShoppingListViewModel(
        val database: ItemDao,
        application: Application
) : AndroidViewModel(application) {


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val items = database.getAllItems()

    fun onAddItem(newItem: ShoppingItem) {
        uiScope.launch {
            insert(newItem)
        }
    }

    private suspend fun insert(newItem: ShoppingItem){
        withContext(Dispatchers.IO){
            database.insert(newItem)
        }
    }

    fun onDeleteItem(item: ShoppingItem){
        uiScope.launch {
            delete(item)
        }
    }

   private suspend fun delete(item: ShoppingItem){
        withContext(Dispatchers.IO){
            database.delete(item)
        }
    }

    fun onUpdateItem(item: ShoppingItem){
        uiScope.launch {
            update(item)
        }
    }

    private suspend fun update(item: ShoppingItem){
        withContext(Dispatchers.IO){
            database.update(item)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}