package com.route.todoc41.ui.home.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.route.todoc41.Helper
import com.route.todoc41.R
import com.route.todoc41.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLanguageSelector()
        setupModeSelector()

    }

    private fun setupLanguageSelector() {
        val languages = listOf(getString(R.string.english), getString(R.string.arabic))
        val languageAdapter = ArrayAdapter(
            requireContext(),
            R.layout.drop_down_item,
            languages
        )
        binding.autoCompleteTVLanguages.setAdapter(languageAdapter)

        binding.autoCompleteTVLanguages.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguage = if (position == 0) "en" else "ar"
            Helper.setLocale(requireContext(), selectedLanguage)
            requireActivity().recreate()
        }
    }
    private fun setupModeSelector() {
        val modes = resources.getStringArray(R.array.modes)
        val modeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            modes
        )
        binding.autoCompleteTVModes.setAdapter(modeAdapter)




        binding.autoCompleteTVModes.setOnItemClickListener { _, _, position, _ ->
            val selectedMode = modes[position]

            when (position) {
                0 -> {
                    requireActivity().setTheme(R.style.Base_Theme_ToDo)
                }
                1 -> {

                    requireActivity().setTheme(R.style.Base_Theme_ToDo_dark)


                    requireActivity().recreate()
                }}}}}






