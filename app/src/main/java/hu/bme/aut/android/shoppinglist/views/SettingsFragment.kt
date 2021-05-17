package hu.bme.aut.android.shoppinglist.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import hu.bme.aut.android.shoppinglist.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val themePref = findPreference<ListPreference>("theme")

        themePref?.entryValues = arrayOf("light", "dark", "default")
        themePref?.entries = arrayOf("Világos", "Sötét", "Alapértelmezett (Rendszer)")

        themePref?.setOnPreferenceChangeListener { _, newValue ->
            when(newValue){
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true
        }
    }

}
