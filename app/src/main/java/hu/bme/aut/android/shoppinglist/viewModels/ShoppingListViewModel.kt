package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.models.FirestoreShoppingItem
import hu.bme.aut.android.shoppinglist.models.FirestoreShoppingList
import hu.bme.aut.android.shoppinglist.models.ShoppingItem
import kotlinx.coroutines.*

class ShoppingListViewModel(
        application: Application, listId: String
) : AndroidViewModel(application) {

    private val firebaseDb = Firebase.firestore
    private val itemsCollectionReference = firebaseDb.collection("lists").document(listId).collection("items")

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
                itemsCollectionReference
                        .orderBy("name")
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                items.value = null
                                return@addSnapshotListener
                            }

                            if (value != null) {
                                val itemList: MutableList<ShoppingItem> = mutableListOf()

                                for(doc in value) {
                                    val item = doc.toObject<ShoppingItem>()
                                    item.id = doc.id
                                    itemList.add(item)
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
                val firestoreItem = FirestoreShoppingItem(item.name, item.acquired)
                itemsCollectionReference.add(firestoreItem)
            }
        }
    }

    fun onDeleteItem(item: ShoppingItem) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                itemsCollectionReference.document(item.id).delete()
            }
        }
    }

    fun onUpdateItem(item: ShoppingItem) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val firestoreItem = FirestoreShoppingItem(item.name, item.acquired)
                itemsCollectionReference.document(item.id).set(firestoreItem)
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