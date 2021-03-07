package hu.bme.aut.android.shoppinglist.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.transition.MaterialFadeThrough
import hu.bme.aut.android.shoppinglist.R
import hu.bme.aut.android.shoppinglist.databinding.FragmentNewListBinding
import hu.bme.aut.android.shoppinglist.viewModels.MainViewModel
import hu.bme.aut.android.shoppinglist.viewModels.MainViewModelFactory


class NewListFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentNewListBinding
    private lateinit var fragmentContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_list, container, false)
        fragmentContext = requireContext()
        val application = requireActivity().application



        binding.btnAdd.setOnClickListener {
            val dialog = MaterialDialog(requireContext())
            dialog.show {
                input(hintRes = R.string.uj_lista_neve) { _, text ->
                    mainViewModel.addListAndSubscribe(text.toString())
                }

                positiveButton(R.string.add_item)
                negativeButton(R.string.cancel)
            }
        }

        binding.btnSubscribe.setOnClickListener {
            val dialog = MaterialDialog(requireContext())
            dialog.show {
                input(hintRes = R.string.lista_azonositoja) { _, text ->
                    mainViewModel.subscribeToList(text.toString())
                }

                positiveButton(R.string.add_item)
                negativeButton(R.string.cancel)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = mainViewModel
        }
    }

}