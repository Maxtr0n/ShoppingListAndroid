package hu.bme.aut.android.shoppinglist

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import hu.bme.aut.android.shoppinglist.databinding.FragmentListBinding
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModel
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModelFactory

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var fragmentContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        analytics = FirebaseAnalytics.getInstance(requireContext())
        fragmentContext = requireContext()

        val application = requireActivity().application
        val shoppingListViewModelFactory = ShoppingListViewModelFactory(application)
        val shoppingListViewModel = ViewModelProvider(this, shoppingListViewModelFactory).get(ShoppingListViewModel::class.java)
        binding.shoppingListViewModel = shoppingListViewModel

        val rvShoppingItem = binding.rvShoppingItems
        val shoppingListAdapter = ShoppingListAdapter(ShoppingListListener { item ->
            val newItem = ShoppingItem(id = item.id, name = item.name, acquired = !item.acquired)
            shoppingListViewModel.onUpdateItem(newItem)
        })
        rvShoppingItem.adapter = shoppingListAdapter
        rvShoppingItem.layoutManager = LinearLayoutManager(fragmentContext)

        shoppingListViewModel.items.observe(viewLifecycleOwner, Observer { items ->
            //update UI
            if(items.isEmpty()){
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvShoppingItems.visibility = View.INVISIBLE
            }
            else {
                binding.tvEmpty.visibility = View.INVISIBLE
                binding.rvShoppingItems.visibility = View.VISIBLE
            }
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

        return binding.root
    }

    private fun initBuyButton(shoppingListViewModel: ShoppingListViewModel) {
        val btnBuy = binding.btnBuy
        btnBuy.setOnClickListener {
            val dialog = MaterialDialog(requireContext()).show {
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
            val dialog = MaterialDialog(requireContext())
            dialog.show {
                input(hintRes = R.string.rucikk_neve) { _, text ->
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