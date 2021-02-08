package hu.bme.aut.android.shoppinglist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_lists, container, false)

        binding.tvText.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_myListsFragment_to_listFragment)
        )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


    }


}