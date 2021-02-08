package hu.bme.aut.android.shoppinglist.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory (
        private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel( application ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}