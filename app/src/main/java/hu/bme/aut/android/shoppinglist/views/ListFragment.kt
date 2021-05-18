package hu.bme.aut.android.shoppinglist.views

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.google.firebase.analytics.FirebaseAnalytics
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.adapters.ShoppingItemListAdapter
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListItemClickListener
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListItemLongClickListener
import hu.bme.aut.android.shoppinglist.models.ShoppingItem
import hu.bme.aut.android.shoppinglist.databinding.FragmentListBinding
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModel
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModelFactory

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var fragmentContext: Context
    private lateinit var shoppingListViewModel: ShoppingListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        fragmentContext = requireContext()
        analytics = FirebaseAnalytics.getInstance(fragmentContext)
        val args : ListFragmentArgs by navArgs()

        val application = requireActivity().application
        val shoppingListViewModelFactory = ShoppingListViewModelFactory(application, args.listId)
        shoppingListViewModel = ViewModelProvider(this, shoppingListViewModelFactory).get(ShoppingListViewModel::class.java)
        binding.shoppingListViewModel = shoppingListViewModel

        val rvShoppingItem = binding.rvShoppingItems
        val shoppingListAdapter = ShoppingItemListAdapter(ShoppingListItemClickListener { item ->
            val newItem = ShoppingItem(id = item.id, name = item.name, acquired = !item.acquired)
            shoppingListViewModel.updateItem(newItem)
        },
        ShoppingListItemLongClickListener { item ->
            onShoppingItemLongPressed(item)
        })
        rvShoppingItem.adapter = shoppingListAdapter
        rvShoppingItem.layoutManager = LinearLayoutManager(fragmentContext)

        shoppingListViewModel.items.observe(viewLifecycleOwner, Observer { items ->
            shoppingListAdapter.submitList(items)
        })

        initFab()
        initBuyButton()

        return binding.root
    }

    private fun initBuyButton() {
        val btnBuy = binding.btnBuy
        btnBuy.setOnClickListener {
            MaterialDialog(requireContext()).show {
                message(R.string.are_you_sure)
                positiveButton(R.string.yes) {
                    shoppingListViewModel.deleteCheckedItems()
                }
                negativeButton(R.string.no)
            }
        }
    }

    private fun initFab() {
        val fab = binding.fabAdd
        fab.setOnClickListener {
            val dialog = MaterialDialog(requireContext())
            dialog.show {
                input(hintRes = R.string.item_name) { _, text ->
                    shoppingListViewModel.addItem(ShoppingItem(name = text.toString()))
                }

                positiveButton(R.string.add_item)
                negativeButton(R.string.cancel)
            }
        }
    }

    private fun onShoppingItemLongPressed(shoppingItem: ShoppingItem) {
        val options = listOf("Szerkesztés", "Törlés")
        val dialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
        dialog.show {
            title(text = shoppingItem.name)
            listItems(items = options) { dialog, index, text ->
                when (index) {
                    0 -> showEditDialog(shoppingItem)
                    1 -> shoppingListViewModel.deleteItem(shoppingItem)
                }
            }
        }
    }

    private fun showEditDialog(shoppingItem: ShoppingItem) {
        MaterialDialog(requireContext()).show {
            input(hintRes = R.string.item_name, prefill = shoppingItem.name) { _, text ->
                if(shoppingItem.name != text.toString())
                {
                    shoppingItem.name = text.toString()
                    shoppingListViewModel.updateItem(shoppingItem)
                }
            }
            positiveButton(R.string.rename)
            negativeButton(R.string.cancel)
        }

    }
}