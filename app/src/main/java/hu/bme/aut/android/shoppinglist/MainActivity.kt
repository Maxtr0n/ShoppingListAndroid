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
import com.google.firebase.analytics.FirebaseAnalytics
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListAdapter
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListListener
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import hu.bme.aut.android.shoppinglist.databinding.ActivityMainBinding
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModel
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var analytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        analytics = FirebaseAnalytics.getInstance(this)

        val application = requireNotNull(this).application
        val shoppingListViewModelFactory = ShoppingListViewModelFactory(application)
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

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                shoppingListViewModel.onDeleteItem(shoppingListAdapter.getItemAt(viewHolder.adapterPosition))
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvShoppingItem)


        initFab(shoppingListViewModel)
        initBuyButton(shoppingListViewModel)
    }

    private fun initBuyButton(shoppingListViewModel: ShoppingListViewModel) {
        val btnBuy = binding.btnBuy
        btnBuy.setOnClickListener {
            val dialog = MaterialDialog(this).show {
                message(R.string.are_you_sure)
                positiveButton(R.string.yes) {
                    shoppingListViewModel.deleteCheckedItems()
                }
                negativeButton(R.string.no)
            }
        }
    }

    private fun initFab(shoppingListViewModel: ShoppingListViewModel) {
        val fab = binding.fabAdd
        fab.setOnClickListener {
            val dialog = MaterialDialog(this)
            dialog.show {
                input(hintRes = R.string.rucikk_neve) { _, text ->
                    analytics.logEvent("item_added", null)
                    shoppingListViewModel.onAddItem(ShoppingItem(name = text.toString()))
                }.getInputField().setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                    if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                        shoppingListViewModel.onAddItem(ShoppingItem(name = getInputField().text.toString()))
                        dialog.dismiss()
                        return@OnKeyListener true
                    }
                    false
                })

                positiveButton(R.string.add_item)
                negativeButton(R.string.cancel)
            }
        }
    }

}