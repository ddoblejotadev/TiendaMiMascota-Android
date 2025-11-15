package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mimascota.R
import com.example.mimascota.ViewModel.JwtAuthViewModel
import com.example.mimascota.databinding.FragmentRegistroBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * RegistroFragment: Pantalla de registro de nuevos usuarios
 *
 * Características:
 * - Campos: email, password, nombre, telefono, direccion, run
 * - Validaciones: email válido, password >= 6 caracteres, nombre obligatorio
 * - Loading state mientras registra
 * - Manejo de errores
 * - Navegación a ProductoListaFragment tras registro exitoso
 */
class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: JwtAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = JwtAuthViewModel(requireContext())

        setupListeners()
        setupObservers()
    }

    /**
     * Configurar listeners de botones
     */
    private fun setupListeners() {
        binding.btnRegistrar.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val nombre = binding.etNombre.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim().takeIf { it.isNotEmpty() }
            val direccion = binding.etDireccion.text.toString().trim().takeIf { it.isNotEmpty() }
            val run = binding.etRun.text.toString().trim().takeIf { it.isNotEmpty() }

            authViewModel.registro(
                email = email,
                password = password,
                nombre = nombre,
                telefono = telefono,
                direccion = direccion,
                run = run
            )
        }

        binding.btnYaTienesCuenta.setOnClickListener {
            // Volver a LoginFragment
            parentFragmentManager.popBackStack()
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
                    binding.btnRegistrar.isEnabled = false
                    binding.btnYaTienesCuenta.isEnabled = false
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegistrar.isEnabled = true
                    binding.btnYaTienesCuenta.isEnabled = true
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

        // Observar registro exitoso
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

