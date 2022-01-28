package com.example.mvx.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.mvx.databinding.FragmentMvvmBinding
import com.example.mvx.model.LceState
import com.example.mvx.model.ServiceProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MVVMFragment : Fragment() {

    private var _binding: FragmentMvvmBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: MVVMViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MVVMViewModel(ServiceProvider.provideDataStore()) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMvvmBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lceFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { lce ->
                binding.progress.isVisible = lce == LceState.Loading
                when (lce) {
                    is LceState.Content -> {
                        binding.textResult.text = null
                        lce.items.forEach {
                            binding.textResult.append(it.toString())
                            binding.textResult.append("\n")
                        }
                    }
                    is LceState.Error -> {
                        Snackbar.make(view, lce.throwable.message ?: "", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.sharedFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                Toast.makeText(requireContext(), "worked", Toast.LENGTH_SHORT).show()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.button.setOnClickListener {
            viewModel.onButtonClicked()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}