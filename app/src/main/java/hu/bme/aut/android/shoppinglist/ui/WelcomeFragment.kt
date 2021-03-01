package hu.bme.aut.android.shoppinglist.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.databinding.FragmentWelcomeBinding
import hu.bme.aut.android.shoppinglist.viewModels.MainViewModel
import hu.bme.aut.android.shoppinglist.viewModels.MainViewModelFactory


class WelcomeFragment : Fragment() {

    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        val application = requireActivity().application
        val mainViewModelFactory = MainViewModelFactory(application)
        mainViewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

        binding.btnLogin.setOnClickListener {
            launchSignInFlow()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK) {
                if (response != null) {
                    if(response.isNewUser) {
                        //viewmodel -> tegyen be uj usert a firestoreba
                        auth.currentUser?.let { mainViewModel.addUserToFirestore(it) }
                    }
                    //viewmodel -> szedje le a user adatait firestore-bol
                    auth.currentUser?.let { mainViewModel.getUserFromFireStore(it) }
                }
                navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToMyListsFragment())
            } else {
                if(response == null) {
                    showSnackbar(R.string.sikertelen_bejelentkezes)
                    return
                }
                if(response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.nincs_internet)
                    return
                }

                showSnackbar(R.string.unkwown_error)
            }
        }
    }

    private fun showSnackbar(messageRes: Int) {
        Snackbar.make(requireView(), messageRes, Snackbar.LENGTH_LONG).show()
    }
}