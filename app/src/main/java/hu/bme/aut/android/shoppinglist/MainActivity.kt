package hu.bme.aut.android.shoppinglist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListAdapter
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListListener
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import hu.bme.aut.android.shoppinglist.database.ShoppingListDatabase
import hu.bme.aut.android.shoppinglist.databinding.ActivityMainBinding
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModel
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var analytics: FirebaseAnalytics
    private var  firebaseDb = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        analytics = FirebaseAnalytics.getInstance(this)

        val application = requireNotNull(this).application

        val dataSource = ShoppingListDatabase.getInstance(application).itemDao
        val shoppingListViewModelFactory = ShoppingListViewModelFactory(dataSource, application)

        //val shoppingListViewModel: ShoppingListViewModel by viewModels { shoppingListViewModelFactory }
        val shoppingListViewModel = ViewModelProvider(this, shoppingListViewModelFactory).get(ShoppingListViewModel::class.java)

        binding.shoppingListViewModel = shoppingListViewModel

        val rvShoppingItem = binding.rvShoppingItems
        val shoppingListAdapter = ShoppingListAdapter(ShoppingListListener { item ->
            val newItem = ShoppingItem(id = item.id, name = item.name, acquired = !item.acquired)
            shoppingListViewModel.onUpdateItem(newItem)
        })
        rvShoppingItem.adapter = shoppingListAdapter
        rvShoppingItem.layoutManager = LinearLayoutManager(this)

        shoppingListViewModel.items.observe(this, Observer { items ->
            //update UI
            shoppingListAdapter.submitList(items)
        })


        val fab = binding.fabAdd
        fab.setOnClickListener {
            val dialog = MaterialDialog(this)
            dialog.show {
                input(hintRes = R.string.rucikk_neve){_, text ->
                    analytics.logEvent("item_added", null)
                    shoppingListViewModel.onAddItem(ShoppingItem( name = text.toString()))
                }
                positiveButton(R.string.add_item)
                negativeButton(R.string.cancel)
            }
        }

        val btnBuy = binding.btnBuy
        btnBuy.setOnClickListener {
           val dialog = MaterialDialog(this).show {
               message(R.string.are_you_sure)
               positiveButton(R.string.yes){
                   for(item in shoppingListViewModel.items.value!!){
                       when (item.acquired){
                           true -> shoppingListViewModel.onDeleteItem(item)
                       }
                   }
               }
                negativeButton (R.string.no){
                    cancel()
               }
           }
        }
    }
}