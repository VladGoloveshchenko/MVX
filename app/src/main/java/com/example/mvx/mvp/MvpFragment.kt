package com.example.mvx.mvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mvx.databinding.FragmentMvpBinding
import com.example.mvx.model.Entity
import com.example.mvx.model.ServiceProvider

class MvpFragment : Fragment(), MvpView {

    private var _binding: FragmentMvpBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val presenter: MvpPresenter by lazy {
        MvpPresenter(ServiceProvider.provideDataStore())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMvpBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        binding.button.setOnClickListener {
            presenter.onButtonClicked()
        }
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detachView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun showToast() {
        Toast.makeText(requireContext(), "worked", Toast.LENGTH_SHORT).show()
    }

    override fun showItems(items: List<Entity>) {
        binding.textResult.text = null
        items.forEach {
            binding.textResult.append(it.toString())
            binding.textResult.append("\n")
        }
    }
}