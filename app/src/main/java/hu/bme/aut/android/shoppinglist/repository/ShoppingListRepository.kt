package hu.bme.aut.android.shoppinglist.repository

import android.util.Log
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.database.ItemDao
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import hu.bme.aut.android.shoppinglist.database.ShoppingListDatabase
import hu.bme.aut.android.shoppinglist.network.ShoppingListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShoppingListRepository(private val database: ItemDao) {

    private val firebaseDb = Firebase.firestore
    private val collectionReference = firebaseDb.collection("items")

    private val TAG = "Repository"
    private val shoppingListService = ShoppingListService()
    val items = database.getAllItems()


    suspend fun refreshShoppingList() {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "refreshShoppingList() called")
            val list = mutableListOf<ShoppingItem>()
            var shoppingItem: ShoppingItem

            collectionReference.get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            shoppingItem = document.toObject()
                            list.add(shoppingItem)
                        }
                        database.insertAll(list.toList())
                    }
        }
    }

    suspend fun addItem(item: ShoppingItem) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "addItem() called")
            //database.insert(item)
            shoppingListService.addItem(item)
        }
        refreshShoppingList()
    }

    suspend fun deleteItem(item: ShoppingItem) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "deleteItem() called")
            //database.delete(item)
            shoppingListService.deleteItem(item)
        }
        refreshShoppingList()
    }

    suspend fun updateItem(item: ShoppingItem) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "update() called")
            //database.update(item)
            shoppingListService.updateItem(item)
        }
        refreshShoppingList()
    }

    private fun listenToShoppingItems() {
        collectionReference.addSnapshotListener { snapshot, error ->
            if(error != null) {
                Log.w(TAG, "Listen Failed", error)
                return@addSnapshotListener
            }

            if(snapshot != null) {
                val items = ArrayList<ShoppingItem>()
                val documents = snapshot.documents
                documents.forEach {
                    val networkShoppingItem = it.toObject<NetworkShoppingItem>()
                    if(networkShoppingItem != null) {
                        val shoppingItem = ShoppingItem(id = it.id, name = networkShoppingItem.name, acquired = networkShoppingItem.acquired)
                        items.add(shoppingItem)
                    }
                }

            }
        }
    }
}