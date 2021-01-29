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
        application: Application
) : AndroidViewModel(application) {

    private val firebaseDb = Firebase.firestore
    private val collectionReference = firebaseDb.collection("items")

    private val TAG = "ViewModel"
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var items : MutableLiveData<List<ShoppingItem>> = MutableLiveData()

    init {
        listenToShoppingItems()
    }

    fun onAddItem(item: ShoppingItem) {
        collectionReference.add(item)
                .addOnSuccessListener {
                    Log.d(TAG, "Uploaded item to server: "+ item.name)
                }
    }

   private fun listenToShoppingItems(){
       collectionReference.addSnapshotListener { value, error ->
           if(error != null) {
               Log.w(TAG, "Listen failed.", error)
               items.value = null
               return@addSnapshotListener
           }

           if(value != null) {
               val itemList : MutableList<ShoppingItem> = mutableListOf()
               val documents = value.documents
               documents.forEach {
                   val item = it.toObject<ShoppingItem>()
                   if(item != null) {
                       item.id = it.id
                       itemList.add(item)
                   }
               }
                items.value = itemList
           }
       }
   }

    fun onDeleteItem(item: ShoppingItem) {
        collectionReference.document(item.id).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Deleted item from server: "+ item.name)
                }
    }

    fun onUpdateItem(item: ShoppingItem) {
        collectionReference.document(item.id).set(item)
                .addOnSuccessListener {
                    Log.d(TAG, "Updated item on server: "+ item.name)
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