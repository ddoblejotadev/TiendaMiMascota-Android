package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mimascota.R
import com.example.mimascota.ViewModel.JwtAuthViewModel
import com.example.mimascota.databinding.FragmentRegistroBinding
import com.example.mimascota.util.RutValidator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * RegistroFragment: Pantalla de registro de nuevos usuarios
 *
 * Características:
 * - Campos: email, password, nombre, telefono, direccion, run
 * - Validaciones: email válido, password >= 6 caracteres, nombre obligatorio, RUT válido (si no está vacío)
 * - Validación en tiempo real del RUT
 * - Formateo automático del RUT
 * - Loading state mientras registra
 * - Manejo de errores
 * - Navegación a ProductoListaFragment tras registro exitoso
 */
class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: JwtAuthViewModel
    private var registrationRequested = false

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
        authViewModel = androidx.lifecycle.ViewModelProvider(this)[JwtAuthViewModel::class.java]

        setupRutValidation()
        setupListeners()
        setupObservers()
    }

    /**
     * Configurar validación en tiempo real del RUT
     */
    private fun setupRutValidation() {
        // Validación en tiempo real mientras el usuario escribe
        binding.etRun.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val rut = s?.toString()?.trim()

                // Si el RUT está vacío, limpiar error (es opcional)
                if (rut.isNullOrEmpty()) {
                    binding.tilRun.error = null
                    return
                }

                // Si el RUT no está vacío, validar
                if (!RutValidator.esValido(rut)) {
                    binding.tilRun.error = getString(R.string.rut_invalido)
                } else {
                    binding.tilRun.error = null
                }
            }
        })

        // Formatear automáticamente cuando el usuario termina de editar
        binding.etRun.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val rut = binding.etRun.text?.toString()?.trim()
                if (!rut.isNullOrEmpty() && RutValidator.esValido(rut)) {
                    // Formatear a XX.XXX.XXX-X
                    val rutFormateado = RutValidator.formatear(rut)
                    binding.etRun.setText(rutFormateado)
                    // Mover cursor al final
                    binding.etRun.setSelection(rutFormateado.length)
                }
            }
        }
    }

    /**
     * Configurar listeners de botones
     */
    private fun setupListeners() {
        binding.btnRegistrar.setOnClickListener {
            // Limpiar errores anteriores
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilNombre.error = null
            binding.tilRun.error = null

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val nombre = binding.etNombre.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim().takeIf { it.isNotEmpty() }
            val direccion = binding.etDireccion.text.toString().trim().takeIf { it.isNotEmpty() }
            val run = binding.etRun.text.toString().trim()

            var hasError = false

            // Email obligatorio y válido
            if (email.isEmpty()) {
                binding.tilEmail.error = getString(R.string.email_obligatorio)
                hasError = true
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = getString(R.string.email_invalido)
                hasError = true
            } else {
                binding.tilEmail.error = null
            }

            // Password obligatorio y longitud mínima
            if (password.isEmpty()) {
                binding.tilPassword.error = getString(R.string.password_obligatorio)
                hasError = true
            } else if (password.length < 6) {
                binding.tilPassword.error = getString(R.string.password_minimo)
                hasError = true
            } else {
                binding.tilPassword.error = null
            }

            // Nombre obligatorio
            if (nombre.isEmpty()) {
                binding.tilNombre.error = getString(R.string.nombre_obligatorio)
                hasError = true
            } else {
                binding.tilNombre.error = null
            }

            // RUT obligatorio y válido
            if (run.isEmpty()) {
                binding.tilRun.error = "El RUT es obligatorio"
                hasError = true
            } else if (!RutValidator.esValido(run)) {
                binding.tilRun.error = getString(R.string.rut_invalido)
                hasError = true
            } else {
                binding.tilRun.error = null
            }

            if (hasError) {
                Snackbar.make(binding.root, getString(R.string.complete_campos_obligatorios), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Si todo está OK, enviar al ViewModel
            registrationRequested = true
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
                if (isLoggedIn && registrationRequested) {
                    // Solo navegar si esta pantalla inició el registro
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProductoListaFragment())
                        .commit()
                    registrationRequested = false
                }
             }
         }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
