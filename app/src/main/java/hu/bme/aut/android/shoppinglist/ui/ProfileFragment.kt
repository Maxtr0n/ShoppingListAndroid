package hu.bme.aut.android.shoppinglist.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()

        binding.tvUsernameText.text = auth.currentUser?.displayName ?: ""
        binding.tvEmailText.text = auth.currentUser?.email ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val navController = findNavController()
                            navController.navigate(ProfileFragmentDirections.actionProfileFragmentToWelcomeFragment())
                        } else {
                            Snackbar.make(view, getString(R.string.kijelentkezes_sikertelebn), Snackbar.LENGTH_LONG).show()
                        }
                    }
        }
    }
}