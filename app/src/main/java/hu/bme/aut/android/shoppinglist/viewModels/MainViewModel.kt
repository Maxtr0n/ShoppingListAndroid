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
import hu.bme.aut.android.shoppinglist.models.FirestoreShoppingList
import hu.bme.aut.android.shoppinglist.models.ShoppingList
import hu.bme.aut.android.shoppinglist.models.User
import kotlinx.coroutines.*


class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val firebaseDb = Firebase.firestore
    private val listsCollectionReference = firebaseDb.collection("lists")
    private val usersCollectionReference = firebaseDb.collection("users")

    private val TAG = "MainViewModel"
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var lists: MutableLiveData<List<ShoppingList>> = MutableLiveData()
    var currentUser: MutableLiveData<User> = MutableLiveData()


    private fun createUserObject(firebaseUser: FirebaseUser): User {
        val user = User(firebaseUser.uid)
        if (firebaseUser.email != null) {
            user.email = firebaseUser.email!!
        }
        if (firebaseUser.displayName != null) {
            user.name = firebaseUser.displayName!!
        }
        return user
    }

    fun addUserToFirestore(newUser: FirebaseUser) {
        usersCollectionReference.document(newUser.uid).set(createUserObject(newUser))
    }

    fun getUserFromFireStore(firebaseUser: FirebaseUser) {
        usersCollectionReference.document(firebaseUser.uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject<User>()

                currentUser.value = user;
            }

            .addOnFailureListener { exception ->
                Log.d(TAG, "Request failed ", exception)
            }

    }

    fun getShoppingLists() {
        for (listId in currentUser.value?.listIds!!) {
            listsCollectionReference.document(listId).get()
                .addOnSuccessListener { document ->
                    val shoppingList = document.toObject<ShoppingList>()
                    if (shoppingList != null) {
                        shoppingList.id = document.id
                        val newList = lists.value?.toMutableList()
                        if (newList != null) {
                            newList.add(shoppingList)
                            lists.value = newList.toList()
                        } else {
                            lists.value = listOf(shoppingList)
                        }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Request failed ", exception)
                }
        }
    }

    fun addListAndSubscribe(listName: String) {
        val firestoreList = FirestoreShoppingList(listName)
        listsCollectionReference.add(firestoreList).addOnSuccessListener { documentReference ->
            subscribeToList(documentReference.id)
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
                val firestoreList = FirestoreShoppingList(list.name)
                listsCollectionReference.document(list.id).set(firestoreList)
            }
        }
    }

    fun subscribeToList(listId: String) {
        val user = currentUser.value
        if (user != null) {
            user.listIds.add(listId)
            usersCollectionReference.document(user.uid).set(user)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}