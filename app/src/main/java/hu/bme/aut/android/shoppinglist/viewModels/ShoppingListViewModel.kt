package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.database.ItemDao
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import hu.bme.aut.android.shoppinglist.database.ShoppingListDatabase
import hu.bme.aut.android.shoppinglist.repository.ShoppingListRepository
import kotlinx.coroutines.*
import java.io.IOException

class ShoppingListViewModel(
        application: Application
) : AndroidViewModel(application) {

    val database = ShoppingListDatabase.getInstance(application).itemDao
    private val shoppingListRepository = ShoppingListRepository(database)
    val items = shoppingListRepository.items
    private val TAG = "ViewModel"
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)




    init {
        refreshDataFromRepository()
    }

    fun onAddItem(item: ShoppingItem) {
        uiScope.launch {
            try {
                shoppingListRepository.addItem(item)
            }
            catch (networkError: IOException) {
                Log.e(TAG, "Network Error in onAddItem()!")
            }
        }
    }


    fun onDeleteItem(item: ShoppingItem) {
        uiScope.launch {
            try {
                shoppingListRepository.deleteItem(item)
            }
            catch (networkError: IOException) {
                Log.e(TAG, "Network Error in onDeleteItem()!")
            }
        }
    }


    fun onUpdateItem(item: ShoppingItem) {
        uiScope.launch {
            try {
                shoppingListRepository.updateItem(item)
            }
            catch (networkError: IOException) {
                Log.e(TAG, "Network Error in onUpdateItem()!")
            }
        }
    }

    fun deleteCheckedItems() {
        for (item in items.value!!) {
            when (item.acquired) {
                true -> onDeleteItem(item)
            }
        }
    }

    private fun refreshDataFromRepository() {
        uiScope.launch {
            try {
                shoppingListRepository.refreshShoppingList()
            }
            catch (networkError: IOException) {
                if(items.value.isNullOrEmpty())
                    Log.e(TAG, "Network Error in refreshDataFromRepository()!")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}