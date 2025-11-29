package com.example.mimascota.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.databinding.ActivityMisPedidosBinding
import com.example.mimascota.ui.adapter.OrdenesAdapter
import com.example.mimascota.viewModel.MisPedidosViewModel
import com.example.mimascota.viewModel.MisPedidosViewModelFactory

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

        // Corregido: Usar la factory para instanciar el ViewModel
        val tokenManager = RetrofitClient.getTokenManager()
        val factory = MisPedidosViewModelFactory(tokenManager)
        viewModel = ViewModelProvider(this, factory)[MisPedidosViewModel::class.java]

        // Configurar RecyclerView
        setupRecyclerView()

        // Configurar toolbar
        setupToolbar()

        // Observar datos del ViewModel
        observeData()

        // Cargar órdenes
        viewModel.cargarMisOrdenes()
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
        // Corregido: observar 'misOrdenes'
        viewModel.misOrdenes.observe(this) { ordenes ->
            if (ordenes.isEmpty()) {
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
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar errores
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                // Corregido: llamar a 'limpiarError()'
                viewModel.limpiarError()
            }
        }
    }
}
