package com.example.mimascota.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

        // Ajustar padding superior para el toolbar (botón atrás accesible)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = sysBarInsets.top)
            insets
        }

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
        // Intentar cargar órdenes; si TokenManager no tiene userId, ViewModel intentará recuperar perfil.
        viewModel.cargarMisOrdenes()

        // Añadir acción para sincronizar perfil desde la UI si está vacío
        binding.tvSinOrdenes.setOnClickListener {
            // al tocar el área vacía forzamos recarga
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
            // Click en orden - puedes navegar a detalle o mostrar más info
            Toast.makeText(this, "Orden #${orden.numeroOrden}", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a pantalla de detalle de orden
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
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Mis Pedidos"
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Observar datos del ViewModel
     */
    private fun observeData() {
        // Observar órdenes
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

        // Observar loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        // Observar errores
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                // Limpiar error
                viewModel.limpiarError()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refrescar órdenes cada vez que la actividad vuelve a primer plano
        viewModel.cargarMisOrdenes()
    }
}
