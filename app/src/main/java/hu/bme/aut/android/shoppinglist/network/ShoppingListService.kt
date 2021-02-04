package hu.bme.aut.android.shoppinglist.network

import android.util.Log
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.database.ShoppingItem

class ShoppingListService {

    private val firebaseDb = Firebase.firestore
    private val collectionReference = firebaseDb.collection("items")
    /*private val settings = firestoreSettings {
        isPersistenceEnabled = false
    }*/
    private val TAG = "ShoppingListService"

    /*init {
        firebaseDb.firestoreSettings = settings
    }*/

    /*fun getItems(): List<ShoppingItem> {
        val list = mutableListOf<ShoppingItem>()
        var shoppingItem: ShoppingItem

        collectionReference
                .get()
                .addOnSuccessListener { result ->
                    for(document in result) {
                        networkShoppingItem = document.toObject<NetworkShoppingItem>()
                        shoppingItem = ShoppingItem(id = document.id, name = networkShoppingItem.name, acquired = networkShoppingItem.acquired)
                        list.add(shoppingItem)
                    }
                }

        return list
    }*/



    fun addItem(item: ShoppingItem) {
        collectionReference.add(item)
                .addOnSuccessListener {
                    Log.d(TAG, "Uploaded item to server: "+ item.name)

                }
    }

    fun deleteItem(item: ShoppingItem) {
        collectionReference.document(item.id).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Deleted item from server: "+ item.name)
                }
    }

    fun updateItem(item: ShoppingItem) {
        collectionReference.document(item.id).set(item)
                .addOnSuccessListener {
                    Log.d(TAG, "Updated item on server: "+ item.name)

                }
    }
}