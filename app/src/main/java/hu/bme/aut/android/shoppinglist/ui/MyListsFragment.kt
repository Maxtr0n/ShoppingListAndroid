package hu.bme.aut.android.shoppinglist.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.adapters.MyListsAdapter
import hu.bme.aut.android.shoppinglist.adapters.MyListsClickListener
import hu.bme.aut.android.shoppinglist.adapters.MyListsLongClickListener
import hu.bme.aut.android.shoppinglist.databinding.FragmentMyListsBinding
import hu.bme.aut.android.shoppinglist.models.ShoppingList
import hu.bme.aut.android.shoppinglist.viewModels.MainViewModel


class MyListsFragment : Fragment() {
    companion object {
        const val TAG = "MyListsFragment"
    }

    private lateinit var fragmentContext: Context
    private lateinit var binding: FragmentMyListsBinding
    private lateinit var navController: NavController
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_lists, container, false)
        navController = findNavController()
        fragmentContext = requireContext()
        auth = FirebaseAuth.getInstance()
        val application = requireActivity().application

        if(auth.currentUser == null) {
            navController.navigate(MyListsFragmentDirections.actionMyListsFragmentToWelcomeFragment())
        } else {
            mainViewModel.listenToUser(auth.currentUser!!)
        }

        mainViewModel.currentUser.observe(viewLifecycleOwner, { user ->
            if(user != null){
                if(user.uid.isNotEmpty() and user.listIds.isNotEmpty()){
                    mainViewModel.listenToShoppingLists()
                }
            }
        })

        val rvMyLists = binding.rvShoppingLists
        val myListsAdapter = MyListsAdapter(MyListsClickListener { list ->
            navController.navigate(MyListsFragmentDirections.actionMyListsFragmentToListFragment(list.id))
        },
        MyListsLongClickListener { list ->
            onListLongPressed(list)
        })

        rvMyLists.adapter = myListsAdapter
        rvMyLists.layoutManager = LinearLayoutManager(fragmentContext)

        mainViewModel.lists.observe(viewLifecycleOwner, Observer { lists ->
            myListsAdapter.submitList(lists)
        })

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = mainViewModel
        }
    }

    private fun onListLongPressed(list: ShoppingList) {
        val options = listOf("Azonosító másolása", "Leiratkozás")
        val dialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
        dialog.show {
            listItems(items = options) { dialog, index, text ->
                when(index) {
                    0 -> copyIdToClipboard(list)
                }
            }
        }
    }

    private fun copyIdToClipboard(list: ShoppingList) {
        //val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
       // val clip: ClipData = ClipData.newPlainText()
    }
}