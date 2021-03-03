package hu.bme.aut.android.shoppinglist.ui

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BasicGridItem
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.gridItems
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.google.android.material.transition.platform.MaterialFade
import com.google.firebase.analytics.FirebaseAnalytics
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.adapters.ShoppingItemListAdapter
import hu.bme.aut.android.shoppinglist.adapters.ShoppingListListener
import hu.bme.aut.android.shoppinglist.models.ShoppingItem
import hu.bme.aut.android.shoppinglist.databinding.FragmentListBinding
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModel
import hu.bme.aut.android.shoppinglist.viewModels.ShoppingListViewModelFactory

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var fragmentContext: Context
    private lateinit var shoppingListViewModel: ShoppingListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        val shoppingListAdapter = ShoppingItemListAdapter(ShoppingListListener { item ->
            val newItem = ShoppingItem(id = item.id, name = item.name, acquired = !item.acquired)
            shoppingListViewModel.onUpdateItem(newItem)
        }).apply {
            onItemLongPress = { shoppingItem ->
                onShoppingItemLongPressed(shoppingItem)
            }
        }
        rvShoppingItem.adapter = shoppingListAdapter
        rvShoppingItem.layoutManager = LinearLayoutManager(fragmentContext)

        shoppingListViewModel.items.observe(viewLifecycleOwner, Observer { items ->
            //update UI
            if(items.isEmpty()){
                val materialFade = MaterialFade().apply {
                    duration = 150L
                }
                TransitionManager.beginDelayedTransition(container, materialFade)
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvShoppingItems.visibility = View.INVISIBLE
            } else {
                val materialFade = MaterialFade().apply {
                    duration = 84L
                }
                TransitionManager.beginDelayedTransition(container, materialFade)
                binding.tvEmpty.visibility = View.INVISIBLE
                binding.rvShoppingItems.visibility = View.VISIBLE
            }
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
                input(hintRes = R.string.rucikk_neve) { _, text ->
                    shoppingListViewModel.onAddItem(ShoppingItem(name = text.toString()))
                }

                positiveButton(R.string.add_item)
                negativeButton(R.string.cancel)
            }
        }
    }

    private fun onShoppingItemLongPressed(shoppingItem: ShoppingItem) {
        val items = listOf(
                BasicGridItem(R.drawable.ic_baseline_edit_24, "Szerkesztés"),
                BasicGridItem(R.drawable.ic_baseline_delete_24, "Törlés")
        )
        val dialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
        dialog.show {
            gridItems(items) { _, index, item ->
                when(index) {
                    0 -> ShowEditDialog()
                    1 -> shoppingListViewModel.onDeleteItem(shoppingItem)
                }
            }
        }
    }

    private fun ShowEditDialog() {
        TODO("Not yet implemented")
    }
}