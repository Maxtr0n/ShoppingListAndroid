package hu.bme.aut.android.shoppinglist.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.adapters.GlideApp
import hu.bme.aut.android.shoppinglist.databinding.FragmentProfileBinding
import hu.bme.aut.android.shoppinglist.viewModels.MainViewModel
import kotlinx.coroutines.currentCoroutineContext

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val mainViewModel: MainViewModel by activityViewModels()
    private val storageReference = Firebase.storage.reference
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if(uri != null) {
            uploadImageToFirebase(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = mainViewModel
            tvUsernameText.text = auth.currentUser?.displayName ?: ""
            tvEmailText.text = auth.currentUser?.email ?: ""
        }

        binding.btnLogout.setOnClickListener {
            mainViewModel.signOutUser()
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

        binding.btnSettings.setOnClickListener {
            val navController = findNavController()
            navController.navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
        }

        binding.ivProfilePicture.setOnClickListener {
            getContent.launch("image/*")
        }

        mainViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if(user != null){
                if(user.hasProfilePicture){
                    val pfpReference = storageReference.child(user.uid + ".jpg")
                    GlideApp.with(this).load(pfpReference).placeholder(R.drawable.ic_avatar_placeholder).into(binding.ivProfilePicture)
                }
                else{
                    binding.ivProfilePicture.setImageResource(R.drawable.ic_avatar_placeholder)
                }
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        inputStream?.let {
            val fileName = mainViewModel.currentUser.value!!.uid + ".jpg"
            val pfpRef = storageReference.child(fileName)
            val uploadTask = pfpRef.putStream(inputStream)
            uploadTask.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "A képet nem sikerült feltölteni",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener {
                Toast.makeText(requireContext(), "Sikeres feltöltés", Toast.LENGTH_SHORT).show()
                mainViewModel.setUserProfilePicture()
            }

        }
    }
}