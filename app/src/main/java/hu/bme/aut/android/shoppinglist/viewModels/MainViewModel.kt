package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
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

    private var _lists: MutableLiveData<List<ShoppingList>> = MutableLiveData()
    var lists: LiveData<List<ShoppingList>> = _lists

    private var _currentUser: MutableLiveData<User> = MutableLiveData()
    var currentUser: LiveData<User> = _currentUser

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

    fun listenToUser(firebaseUser: FirebaseUser) {
        usersCollectionReference.document(firebaseUser.uid)
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        return@addSnapshotListener
                    }

                    if(value != null && value.exists()) {
                        val user = value.toObject<User>()

                        _currentUser.value = user
                    }
                }
    }

    fun listenToShoppingLists() {
        listsCollectionReference.whereIn(FieldPath.documentId(), currentUser.value?.listIds!!)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        _lists.value = null
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val listOfShoppingLists: MutableList<ShoppingList> = mutableListOf()

                        for (doc in value) {
                            val list = doc.toObject<ShoppingList>()
                            list.id = doc.id
                            listOfShoppingLists.add(list)
                        }

                        _lists.value = listOfShoppingLists
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
        listsCollectionReference.document(list.id).delete()
    }

    fun onUpdateItem(list: ShoppingList) {
        val firestoreList = FirestoreShoppingList(list.name)
        listsCollectionReference.document(list.id).set(firestoreList)
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