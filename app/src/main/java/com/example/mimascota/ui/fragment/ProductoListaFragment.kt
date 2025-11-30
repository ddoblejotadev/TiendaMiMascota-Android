package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.R
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.databinding.FragmentProductoListaBinding
import com.example.mimascota.model.Producto
import com.example.mimascota.ui.adapter.CategoryAdapter
import com.example.mimascota.viewModel.SharedViewModel
import com.example.mimascota.viewModel.SharedViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ProductoListaFragment : Fragment() {

    private var _binding: FragmentProductoListaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels { 
        SharedViewModelFactory(requireContext().applicationContext) 
    }
    private lateinit var categoryAdapter: CategoryAdapter

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

        setupMenu()
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        viewModel.cargarProductos()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_producto_lista, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? SearchView ?: return

                searchView.queryHint = "Buscar productos..."
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        viewModel.buscarProductos(query ?: "")
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.buscarProductos(newText ?: "")
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_cart -> {
                        // TODO: navegar al carrito
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
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onProductoClick = { producto -> mostrarDetalleProducto(producto) },
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
            adapter = categoryAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarProductos()
        }
    }

    private fun setupObservers() {
        viewModel.productosAgrupados.observe(viewLifecycleOwner) { categorias ->
            categoryAdapter.submitList(categorias)
            binding.tvEmptyState.visibility = if (categorias.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.rvProductos.visibility = if (categorias.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            if (!isLoading) {
                binding.progressBar.visibility = View.GONE
            }
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
