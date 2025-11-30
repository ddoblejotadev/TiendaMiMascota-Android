package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mimascota.R
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.databinding.FragmentLoginBinding
import com.example.mimascota.viewModel.JwtAuthViewModel
import com.example.mimascota.viewModel.JwtAuthViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: JwtAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Corregido: Usar la factory para instanciar el ViewModel
        val tokenManager = RetrofitClient.getTokenManager()
        val factory = JwtAuthViewModelFactory(tokenManager)
        authViewModel = ViewModelProvider(this, factory)[JwtAuthViewModel::class.java]

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            authViewModel.login(email, password)
        }

        binding.btnRegistrarse.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistroFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    /**
     * Corregido: Observar el nuevo authState
     */
    private fun setupObservers() {
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is JwtAuthViewModel.AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnLogin.isEnabled = false
                        binding.btnRegistrarse.isEnabled = false
                    }
                    is JwtAuthViewModel.AuthState.Authenticated -> {
                        binding.progressBar.visibility = View.GONE
                        // Navegar a ProductoListaFragment
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ProductoListaFragment())
                            .commit()
                    }
                    is JwtAuthViewModel.AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnRegistrarse.isEnabled = true
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    is JwtAuthViewModel.AuthState.Unauthenticated -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnRegistrarse.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
