package hu.bme.aut.android.shoppinglist.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListRepository {

    private val firebaseDb = Firebase.firestore
    private val collectionReference = firebaseDb.collection("items")

    var items: MutableLiveData<List<ShoppingItem>> = MutableLiveData()

    init {
        listenToShoppingItems()
    }

     private fun listenToShoppingItems() {
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

    fun onAddItem(item: ShoppingItem) {
                collectionReference.add(item)
    }

    fun onDeleteItem(item: ShoppingItem) {
                collectionReference.document(item.id).delete()
    }

    fun onUpdateItem(item: ShoppingItem) {
                collectionReference.document(item.id).set(item)
    }

}