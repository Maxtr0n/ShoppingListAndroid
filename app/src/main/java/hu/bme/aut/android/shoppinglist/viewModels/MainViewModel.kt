package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.models.ShoppingList
import hu.bme.aut.android.shoppinglist.models.User
import kotlinx.coroutines.*


class MainViewModel(
        application: Application
) : AndroidViewModel(application) {

    private val firebaseDb = Firebase.firestore
    private val collectionReference = firebaseDb.collection("lists")
    private val auth: FirebaseAuth

    private val TAG = "MainViewModel"
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var authenticatedUserLiveData: MutableLiveData<User> = MutableLiveData()
    var lists: MutableLiveData<List<ShoppingList>> = MutableLiveData()


    init {
        listenToShoppingItems()
        auth = FirebaseAuth.getInstance()
    }

    private fun listenToShoppingItems() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference
                        .orderBy("name")
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                lists.value = null
                                return@addSnapshotListener
                            }

                            if (value != null) {
                                val listOfShoppingLists: MutableList<ShoppingList> = mutableListOf()
                                val documents = value.documents
                                documents.forEach {
                                    val list = it.toObject<ShoppingList>()
                                    if (list != null) {
                                        list.id = it.id
                                        listOfShoppingLists.add(list)
                                    }
                                }
                                lists.value = listOfShoppingLists
                            }
                        }
            }
        }
    }

    fun onAddItem(list: ShoppingList) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference.add(list)
            }
        }
    }

    fun onDeleteItem(list: ShoppingList) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference.document(list.id).delete()
            }
        }
    }

    fun onUpdateItem(list: ShoppingList) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                collectionReference.document(list.id).set(list)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}