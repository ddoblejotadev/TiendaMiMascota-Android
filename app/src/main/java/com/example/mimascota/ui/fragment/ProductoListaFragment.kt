package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.Model.Producto
import com.example.mimascota.R
import com.example.mimascota.ViewModel.SharedViewModel
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.databinding.FragmentProductoListaBinding
import com.example.mimascota.ui.adapter.ProductoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * ProductoListaFragment: Fragment principal con lista de productos
 *
 * Características:
 * - RecyclerView con lista de productos
 * - SearchView para buscar
 * - Spinner para filtrar por categoría
 * - Bottom Sheet para agregar al carrito
 * - Validación de autenticación JWT
 * - Observadores LiveData
 */
class ProductoListaFragment : Fragment() {

    private var _binding: FragmentProductoListaBinding? = null
    private val binding get() = _binding!!

    // SharedViewModel compartido entre fragments
    private val viewModel: SharedViewModel by activityViewModels()

    private lateinit var productoAdapter: ProductoAdapter

    // TokenManager para validar login
    private val tokenManager by lazy { RetrofitClient.getTokenManager() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoListaBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSpinner()
        setupObservers()
        setupSwipeRefresh()

        // Cargar productos al iniciar
        viewModel.cargarProductos()
    }

    /**
     * Configuración del RecyclerView
     */
    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter(
            onItemClick = { producto ->
                // Mostrar detalles en bottom sheet
                mostrarDetalleProducto(producto)
            },
            onAddToCartClick = { producto ->
                // Validar que esté logueado antes de agregar
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

    /**
     * Configuración del Spinner de categorías
     */
    private fun setupSpinner() {
        val categorias = viewModel.obtenerCategorias()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categorias
        )

        binding.spinnerCategoria.adapter = adapter

        binding.spinnerCategoria.setOnItemSelectedListener(
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val categoriaSeleccionada = categorias[position]
                    viewModel.cargarProductosPorCategoria(categoriaSeleccionada)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        )
    }

    /**
     * Configuración de SwipeRefresh para actualizar
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarProductos()
        }
    }

    /**
     * Observadores de LiveData
     */
    private fun setupObservers() {
        // Observar productos filtrados
        viewModel.productosFiltrados.observe(viewLifecycleOwner) { productos ->
            productoAdapter.submitList(productos)

            // Mostrar mensaje si no hay productos
            if (productos.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvProductos.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvProductos.visibility = View.VISIBLE
            }
        }

        // Observar loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading

            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Observar errores
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Reintentar") {
                        viewModel.cargarProductos()
                    }
                    .show()
                viewModel.limpiarError()
            }
        }

        // Observar mensajes
        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.limpiarMensaje()
            }
        }

        // Observar cantidad de items en carrito
        viewModel.cantidadItems.observe(viewLifecycleOwner) { cantidad ->
            // Actualizar badge del carrito
            updateCartBadge(cantidad)
        }
    }

    /**
     * Muestra el detalle del producto en un Bottom Sheet
     */
    private fun mostrarDetalleProducto(producto: Producto) {
        // Validar que esté logueado antes de mostrar detalles
        if (!tokenManager.isLoggedIn()) {
            mostrarDialogoIniciarSesion()
            return
        }

        viewModel.seleccionarProducto(producto)

        val bottomSheet = ProductoDetalleBottomSheet()
        bottomSheet.show(parentFragmentManager, "ProductoDetalleBottomSheet")
    }

    /**
     * Muestra diálogo para iniciar sesión
     */
    private fun mostrarDialogoIniciarSesion() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Iniciar Sesión")
            .setMessage("Debes iniciar sesión para agregar productos al carrito")
            .setPositiveButton("Iniciar Sesión") { _, _ ->
                // Navegar a LoginFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Actualiza el badge del carrito en el toolbar
     */
    private fun updateCartBadge(cantidad: Int) {
        // Implementar badge en el menú
        activity?.invalidateOptionsMenu()
    }

    /**
     * Crear menú con SearchView
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_producto_lista, menu)

        // Configurar SearchView
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

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

        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Manejar clicks en el menú
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                // Navegar al carrito
                // findNavController().navigate(R.id.action_to_cart)
                true
            }
            R.id.action_refresh -> {
                viewModel.cargarProductos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

