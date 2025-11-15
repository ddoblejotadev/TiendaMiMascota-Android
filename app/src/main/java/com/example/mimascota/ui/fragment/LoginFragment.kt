package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mimascota.R
import com.example.mimascota.ViewModel.JwtAuthViewModel
import com.example.mimascota.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * LoginFragment: Pantalla de inicio de sesión con JWT
 *
 * Características:
 * - Campos: email, password
 * - Validaciones: email válido, password >= 6 caracteres
 * - Loading state mientras autentica
 * - Manejo de errores
 * - Navegación a ProductoListaFragment tras login exitoso
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: JwtAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = androidx.lifecycle.ViewModelProvider(this)[com.example.mimascota.ViewModel.JwtAuthViewModel::class.java]

        setupListeners()
        setupObservers()
    }

    /**
     * Configurar listeners de botones
     */
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            authViewModel.login(email, password)
        }

        binding.btnRegistrarse.setOnClickListener {
            // Navegar a RegistroFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistroFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    /**
     * Observar estados del ViewModel
     */
    private fun setupObservers() {
        // Observar loading
        lifecycleScope.launch {
            authViewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    binding.btnRegistrarse.isEnabled = false
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.btnRegistrarse.isEnabled = true
                }
            }
        }

        // Observar errores
        lifecycleScope.launch {
            authViewModel.error.collect { error ->
                error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    authViewModel.clearError()
                }
            }
        }

        // Observar login exitoso
        lifecycleScope.launch {
            authViewModel.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    // Navegar a ProductoListaFragment
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProductoListaFragment())
                        .commit()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
