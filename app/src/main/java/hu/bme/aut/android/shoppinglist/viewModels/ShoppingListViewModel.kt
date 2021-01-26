package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    private var  firebaseDb = Firebase.firestore
    private lateinit var itemsCollectionReference: CollectionReference

    init {
        itemsCollectionReference = firebaseDb.collection("items")
    }

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

    fun deleteCheckedItems(){
        for(item in items.value!!){
            when(item.acquired){
                true -> onDeleteItem(item)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}