package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private val listsCollectionReference = firebaseDb.collection("lists")
    private val usersCollectionReference = firebaseDb.collection("users")
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val TAG = "MainViewModel"
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var lists: MutableLiveData<List<ShoppingList>> = MutableLiveData()
    var currentUser: MutableLiveData<User> = MutableLiveData()



    init {
        listenToShoppingItems()
    }

    fun createUserObject(firebaseUser: FirebaseUser): User{

        return User(firebaseUser.uid, firebaseUser.displayName, firebaseUser.email)
    }

    fun addUserToFirestore(newUser: FirebaseUser) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                usersCollectionReference.document(newUser.uid).set(createUserObject(newUser))
            }
        }
    }

    fun listenToUser(user: FirebaseUser) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                usersCollectionReference.document(user.uid)
                        .addSnapshotListener { value, e ->
                            if(e != null) {
                                currentUser.value = null
                            }

                            if(value != null) {

                            }
                        }
            }
        }
    }

    private fun listenToShoppingItems() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                listsCollectionReference
                        .orderBy("name")
                        .addSnapshotListener { value, e ->
                            if (e != null) {
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
                listsCollectionReference.add(list)
            }
        }
    }

    fun onDeleteItem(list: ShoppingList) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                listsCollectionReference.document(list.id).delete()
            }
        }
    }

    fun onUpdateItem(list: ShoppingList) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                listsCollectionReference.document(list.id).set(list)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}