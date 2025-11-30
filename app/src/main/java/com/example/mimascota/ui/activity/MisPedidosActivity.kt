package com.example.mimascota.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.databinding.ActivityMisPedidosBinding
import com.example.mimascota.ui.adapter.OrdenesAdapter
import com.example.mimascota.util.TokenManager
import com.example.mimascota.viewModel.MisPedidosViewModel
import com.example.mimascota.viewModel.MisPedidosViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * MisPedidosActivity: Pantalla para ver el historial de órdenes del usuario
 */
class MisPedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisPedidosBinding
    private lateinit var viewModel: MisPedidosViewModel
    private lateinit var adapter: OrdenesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Usar factory por compatibilidad pero sin tokenManager obligatorio
        val factory = MisPedidosViewModelFactory(TokenManager)
        viewModel = ViewModelProvider(this, factory)[MisPedidosViewModel::class.java]

        // Configurar RecyclerView
        setupRecyclerView()

        // Configurar toolbar
        setupToolbar()

        // Observar datos del ViewModel
        observeData()

        // Cargar órdenes
        viewModel.cargarMisOrdenes()

        // Añadir acción para sincronizar perfil desde la UI si está vacío
        binding.tvSinOrdenes.setOnClickListener {
            viewModel.cargarMisOrdenes()
        }

        // Observadores adicionales
        viewModel.error.observe(this) { err ->
            err?.let {
                if (it.contains("Usuario no autenticado") || it.contains("No se pudo recuperar perfil")) {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Sincronizar perfil") {
                            lifecycleScope.launch {
                                viewModel.cargarMisOrdenes()
                            }
                        }.show()
                } else {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
                viewModel.limpiarError()
            }
        }
    }

    /**
     * Configurar RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = OrdenesAdapter { orden ->
            Toast.makeText(this, "Orden #${orden.numeroOrden}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewOrdenes.apply {
            layoutManager = LinearLayoutManager(this@MisPedidosActivity)
            adapter = this@MisPedidosActivity.adapter
        }
    }

    /**
     * Configurar toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Observar datos del ViewModel
     */
    private fun observeData() {
        viewModel.misOrdenes.observe(this) { ordenes ->
            if (ordenes.isNullOrEmpty()) {
                binding.recyclerViewOrdenes.visibility = View.GONE
                binding.tvSinOrdenes.visibility = View.VISIBLE
            } else {
                binding.recyclerViewOrdenes.visibility = View.VISIBLE
                binding.tvSinOrdenes.visibility = View.GONE
                adapter.submitList(ordenes)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.limpiarError()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarMisOrdenes()
    }
}
