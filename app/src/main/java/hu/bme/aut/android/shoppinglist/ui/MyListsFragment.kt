package hu.bme.aut.android.shoppinglist.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.databinding.FragmentMyListsBinding
import hu.bme.aut.android.shoppinglist.viewModels.LoginViewModel


class MyListsFragment : Fragment() {

    companion object {
        const val TAG = "MyListsFragment"
    }

    private lateinit var binding: FragmentMyListsBinding

    private lateinit var navController: NavController
    private val loginViewModel by viewModels<LoginViewModel>()

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

        binding.tvText.setOnClickListener(
                Navigation.createNavigateOnClickListener(MyListsFragmentDirections.actionMyListsFragmentToListFragment("items"))
        )


        return binding.root
    }



}