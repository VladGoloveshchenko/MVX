package com.example.mvx.mvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mvx.databinding.FragmentMviBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MVIFragment : Fragment() {

    private var _binding: FragmentMviBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<MVIViewModel>()

    private val suggestionsAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, emptyArray())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMviBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    private val actions = MutableSharedFlow<UiAction>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            editText.setOnEditorActionListener { _, keyCode, _ ->
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    actions.tryEmit(UiAction.QueryConfirmed(editText.text.toString()))
                    viewModel.handleAction(UiAction.QueryConfirmed(editText.text.toString()))
                    true
                } else false
            }
            editText.addTextChangedListener {
                viewModel.handleAction(UiAction.QueryChanged(editText.text.toString()))
            }
            editText.setAdapter(suggestionsAdapter)
        }

        viewModel
            .state
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(state: UiState) {
        with(binding) {
            progress.isVisible = state.isLoading
            textResult.text = state.items.joinToString(separator = "\n") { it.value }
            state.throwable?.let {
                Snackbar.make(root, it.localizedMessage ?: "", Snackbar.LENGTH_SHORT).show()
            }
            suggestionsAdapter.clear()
            suggestionsAdapter.addAll(state.suggestions)
            suggestionsAdapter.filter.filter(editText.text, editText)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}