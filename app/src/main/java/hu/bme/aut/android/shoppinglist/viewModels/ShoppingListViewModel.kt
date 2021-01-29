package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
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


    fun onAddItem(item: ShoppingItem) {
        uiScope.launch {

        }
    }

    fun deleteCheckedItems() {
        TODO("Not yet implemented")
    }

    fun onDeleteItem(item: ShoppingItem) {
        TODO("Not yet implemented")
    }

    fun onUpdateItem(item: ShoppingItem) {
        TODO("Not yet implemented")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}