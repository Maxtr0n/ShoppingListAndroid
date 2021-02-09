package hu.bme.aut.android.shoppinglist.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.material.transition.MaterialFadeThrough
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import hu.bme.aut.android.shoppinglist.databinding.FragmentNewListBinding


class NewListFragment : Fragment() {

    private lateinit var binding: FragmentNewListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_list, container, false)
        binding.btnAdd.setOnClickListener {
            val dialog = MaterialDialog(requireContext())
            dialog.show {
                input(hintRes = R.string.uj_lista_neve) { _, text ->
                    //shoppingListViewModel.onAddItem(ShoppingItem(name = text.toString()))
                }.getInputField().setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                    if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                        //shoppingListViewModel.onAddItem(ShoppingItem(name = getInputField().text.toString()))
                        dialog.dismiss()
                        return@OnKeyListener true
                    }
                    false
                })

                positiveButton(R.string.add_item)
                negativeButton(R.string.cancel)
            }
        }

        return binding.root
    }



}