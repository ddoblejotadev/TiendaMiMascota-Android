package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.Model.Producto
import com.example.mimascota.R
import com.example.mimascota.ViewModel.SharedViewModel
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.databinding.FragmentProductoListaBinding
import com.example.mimascota.ui.adapter.ProductoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ProductoListaFragment : Fragment() {

    private var _binding: FragmentProductoListaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var productoAdapter: ProductoAdapter

    private val tokenManager by lazy { RetrofitClient.getTokenManager() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_producto_lista, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? SearchView ?: return

                searchView.queryHint = "Buscar productos..."
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { viewModel.buscarProductos(it) }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { viewModel.buscarProductos(it) }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_cart -> {
                        // TODO: navegar al carrito si corresponde
                        true
                    }
                    R.id.action_refresh -> {
                        viewModel.cargarProductos()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupRecyclerView()
        setupSpinner()
        setupObservers()
        setupSwipeRefresh()

        viewModel.cargarProductos()
    }

    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter(
            onItemClick = { producto -> mostrarDetalleProducto(producto) },
            onAddToCartClick = { producto ->
                if (tokenManager.isLoggedIn()) {
                    viewModel.agregarAlCarrito(producto)
                } else {
                    mostrarDialogoIniciarSesion()
                }
            }
        )

        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productoAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSpinner() {
        val categorias = viewModel.obtenerCategorias()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categorias
        )
        binding.spinnerCategoria.adapter = adapter

        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val categoriaSeleccionada = categorias.getOrNull(position) ?: return
                viewModel.cargarProductosPorCategoria(categoriaSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarProductos()
        }
    }

    private fun setupObservers() {
        viewModel.productosFiltrados.observe(viewLifecycleOwner) { productos ->
            productoAdapter.submitList(productos)
            if (productos.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvProductos.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvProductos.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Reintentar") { viewModel.cargarProductos() }
                    .show()
                viewModel.limpiarError()
            }
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.limpiarMensaje()
            }
        }

        viewModel.cantidadItems.observe(viewLifecycleOwner) {
            // Si usas badge en toolbar, invalida el menú del host
            (requireActivity() as MenuHost).invalidateMenu()
        }
    }

    private fun mostrarDetalleProducto(producto: Producto) {
        if (!tokenManager.isLoggedIn()) {
            mostrarDialogoIniciarSesion()
            return
        }
        viewModel.seleccionarProducto(producto)
        val bottomSheet = ProductoDetalleBottomSheet()
        bottomSheet.show(parentFragmentManager, "ProductoDetalleBottomSheet")
    }

    private fun mostrarDialogoIniciarSesion() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Iniciar Sesión")
            .setMessage("Debes iniciar sesión para agregar productos al carrito")
            .setPositiveButton("Iniciar Sesión") { _, _ ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
