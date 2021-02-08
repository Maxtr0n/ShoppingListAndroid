package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import kotlinx.coroutines.*

class ShoppingListViewModel(
        application: Application, listId: String
) : AndroidViewModel(application) {

    private val firebaseDb = Firebase.firestore
    private val collectionReference = firebaseDb.collection("lists").document(listId).collection("items")

    private val TAG = "ShoppingListViewModel"
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var items: MutableLiveData<List<ShoppingItem>> = MutableLiveData()

    init {
        listenToShoppingItems()
    }

    private fun listenToShoppingItems() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference
                        .orderBy("name")
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                items.value = null
                                return@addSnapshotListener
                            }

                            if (value != null) {
                                val itemList: MutableList<ShoppingItem> = mutableListOf()
                                val documents = value.documents
                                documents.forEach {
                                    val item = it.toObject<ShoppingItem>()
                                    if (item != null) {
                                        item.id = it.id
                                        itemList.add(item)
                                    }
                                }
                                items.value = itemList
                            }
                        }
            }
        }
    }

    fun onAddItem(item: ShoppingItem) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference.add(item)
            }
        }
    }

    fun onDeleteItem(item: ShoppingItem) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference.document(item.id).delete()
            }
        }
    }

    fun onUpdateItem(item: ShoppingItem) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference.document(item.id).set(item)
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}