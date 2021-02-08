package hu.bme.aut.android.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.firebase.ui.auth.AuthUI
import hu.bme.aut.android.shoppinglist.databinding.FragmentNewListBinding
import hu.bme.aut.android.shoppinglist.viewModels.LoginViewModel


class NewListFragment : Fragment() {

    private lateinit var binding: FragmentNewListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_list, container, false)

        return binding.root
    }



}