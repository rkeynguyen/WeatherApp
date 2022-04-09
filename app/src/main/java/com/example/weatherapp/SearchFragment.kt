package com.example.weatherapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.databinding.FragmentCurrentConditionsBinding
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.dataclass.CurrentConditions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    @Inject lateinit var viewModel : SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = "Search"
        binding = FragmentSearchBinding.inflate(inflater)

        viewModel.enableButton.observe(viewLifecycleOwner){enable ->
            binding.submit.isEnabled = enable
        }

        viewModel.showErrorDialog.observe(viewLifecycleOwner){ showError ->
            if(showError){

                ErrorDialogFragment().show(childFragmentManager, ErrorDialogFragment.TAG)
            }
        }

        binding.zipCode.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.toString()?.let { viewModel.updateZipCode(it)}
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submit.setOnClickListener {
            viewModel.submitButtonClicked()
            viewModel.showErrorDialog.value?.let {
                if (!it) {
                     viewModel.currentConditions.observe(viewLifecycleOwner){ current ->
                         val conditions: CurrentConditions = current
                         viewModel.zipCode?.let { zip ->
                             val action = SearchFragmentDirections.searchToCurrent(conditions, zip)
                             findNavController().navigate(action) }
                     }

                }
            }
        }
    }
}