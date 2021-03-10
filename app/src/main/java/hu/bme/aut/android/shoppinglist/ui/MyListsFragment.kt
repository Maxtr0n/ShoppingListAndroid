package hu.bme.aut.android.shoppinglist.ui

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialFade
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.adapters.MyListsAdapter
import hu.bme.aut.android.shoppinglist.adapters.MyListsListener
import hu.bme.aut.android.shoppinglist.databinding.FragmentMyListsBinding
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
            if(user.uid.isNotEmpty() and user.listIds.isNotEmpty()){
                mainViewModel.listenToShoppingLists()
            }
        })

        val rvMyLists = binding.rvShoppingLists
        val myListsAdapter = MyListsAdapter(MyListsListener { list ->
            navController.navigate(MyListsFragmentDirections.actionMyListsFragmentToListFragment(list.id))
        })

        rvMyLists.adapter = myListsAdapter
        rvMyLists.layoutManager = LinearLayoutManager(fragmentContext)

        mainViewModel.lists.observe(viewLifecycleOwner, Observer { lists ->
            if(lists.isEmpty()) {
                val materialFade = MaterialFade().apply {
                    duration = 150L
                }
                TransitionManager.beginDelayedTransition(container, materialFade)
                binding.tvEmpty.visibility = View.INVISIBLE
                binding.rvShoppingLists.visibility = View.INVISIBLE
            } else {
                val materialFade = MaterialFade().apply {
                    duration = 84L
                }
                TransitionManager.beginDelayedTransition(container, materialFade)
                binding.tvEmpty.visibility = View.INVISIBLE
                binding.rvShoppingLists.visibility = View.VISIBLE
            }
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
}