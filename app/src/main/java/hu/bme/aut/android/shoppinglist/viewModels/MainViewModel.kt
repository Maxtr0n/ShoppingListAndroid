package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
        listenToShoppingLists()
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

    fun getUserFromFireStore(firebaseUser: FirebaseUser) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                usersCollectionReference.document(firebaseUser.uid).collection("listIds").get()
                        .addOnSuccessListener { documents ->
                            val user = createUserObject(firebaseUser)
                            for (document in documents) {
                                user.listIds.add(document.id)
                            }
                            currentUser.value = user;
                        }

                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Request failed ", exception)
                        }
            }
        }
    }

    private fun listenToShoppingLists() {
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