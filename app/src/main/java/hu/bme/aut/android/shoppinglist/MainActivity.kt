package hu.bme.aut.android.shoppinglist

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListAdapter
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListListener
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import hu.bme.aut.android.shoppinglist.databinding.ActivityMainBinding
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModel
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    }

}