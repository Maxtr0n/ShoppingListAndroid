package hu.bme.aut.android.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.shoppinglist.databinding.FragmentMyListsBinding


class MyListsFragment : Fragment() {

    companion object {
        const val TAG = "MyListsFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private lateinit var binding: FragmentMyListsBinding
    private lateinit var auth: FirebaseAuth

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
        auth = FirebaseAuth.getInstance()
        

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}