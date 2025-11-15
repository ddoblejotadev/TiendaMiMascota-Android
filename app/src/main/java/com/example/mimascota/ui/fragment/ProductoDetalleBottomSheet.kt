package com.example.mimascota.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.mimascota.Model.Producto
import com.example.mimascota.R
import com.example.mimascota.ViewModel.SharedViewModel
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.databinding.BottomSheetProductoDetalleBinding
import com.example.mimascota.util.TokenManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

/**
 * ProductoDetalleBottomSheet: Bottom Sheet para mostrar detalles del producto
 *
 * Características:
 * - Detalles completos del producto
 * - Selector de cantidad
 * - Botón para agregar al carrito
 * - Validación de stock
 * - Validación de autenticación JWT
 */
class ProductoDetalleBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProductoDetalleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    private var cantidadSeleccionada = 1
    private var productoActual: Producto? = null

    // TokenManager inicializado en onCreateView
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProductoDetalleBinding.inflate(inflater, container, false)
        tokenManager = RetrofitClient.getTokenManager()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    /**
     * Observar el producto seleccionado
     */
    private fun setupObservers() {
        viewModel.productoSeleccionado.observe(viewLifecycleOwner) { producto ->
            producto?.let {
                productoActual = it
                mostrarDetalles(it)
            }
        }
    }

    /**
     * Configurar listeners de los botones
     */
    private fun setupListeners() {
        // Botón disminuir cantidad
        binding.btnDisminuir.setOnClickListener {
            if (cantidadSeleccionada > 1) {
                cantidadSeleccionada--
                actualizarCantidad()
            }
        }

        // Botón aumentar cantidad
        binding.btnAumentar.setOnClickListener {
            productoActual?.let { producto ->
                if (cantidadSeleccionada < producto.stock) {
                    cantidadSeleccionada++
                    actualizarCantidad()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Stock máximo alcanzado",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Botón agregar al carrito
        binding.btnAgregarCarrito.setOnClickListener {
            // Validar que esté logueado
            if (!tokenManager.isLoggedIn()) {
                mostrarDialogoIniciarSesion()
                return@setOnClickListener
            }

            productoActual?.let { producto ->
                val exitoso = viewModel.agregarAlCarrito(producto, cantidadSeleccionada)
                if (exitoso) {
                    dismiss()
                }
            }
        }

        // Botón cerrar
        binding.btnCerrar.setOnClickListener {
            dismiss()
        }
    }

    /**
     * Muestra los detalles del producto
     */
    private fun mostrarDetalles(producto: Producto) {
        with(binding) {
            // Nombre
            tvNombre.text = producto.name

            // Descripción
            tvDescripcion.text = producto.description ?: "Sin descripción"

            // Precio
            val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            tvPrecio.text = formato.format(producto.price)

            // Precio anterior si existe
            producto.precioAnterior?.let { precioAnterior ->
                tvPrecioAnterior.text = formato.format(precioAnterior)
                tvPrecioAnterior.paintFlags = tvPrecioAnterior.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvPrecioAnterior.visibility = View.VISIBLE
                chipDescuento.visibility = View.VISIBLE

                val descuento = ((precioAnterior - producto.price) * 100 / precioAnterior)
                chipDescuento.text = "-$descuento%"
            } ?: run {
                tvPrecioAnterior.visibility = View.GONE
                chipDescuento.visibility = View.GONE
            }

            // Stock
            tvStock.text = "Stock disponible: ${producto.stock}"

            // Categoría
            chipCategoria.text = producto.category

            // Valoración
            producto.valoracion?.let { rating ->
                ratingBar.rating = rating.toFloat()
                tvRating.text = String.format("%.1f", rating)
                ratingBar.visibility = View.VISIBLE
                tvRating.visibility = View.VISIBLE
            } ?: run {
                ratingBar.visibility = View.GONE
                tvRating.visibility = View.GONE
            }

            // Destacado
            if (producto.destacado) {
                chipDestacado.visibility = View.VISIBLE
            } else {
                chipDestacado.visibility = View.GONE
            }

            // Detalles específicos por categoría
            mostrarDetallesCategoria(producto)

            // Imagen
            Glide.with(requireContext())
                .load(producto.imageUrl)
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.ic_error_image)
                .centerCrop()
                .into(ivProducto)

            // Habilitar/deshabilitar botón según stock
            btnAgregarCarrito.isEnabled = producto.stock > 0

            // Resetear cantidad
            cantidadSeleccionada = 1
            actualizarCantidad()
        }
    }

    /**
     * Muestra detalles específicos según la categoría
     */
    private fun mostrarDetallesCategoria(producto: Producto) {
        val detalles = mutableListOf<String>()

        producto.marca?.let { detalles.add("Marca: $it") }
        producto.material?.let { detalles.add("Material: $it") }
        producto.tamano?.let { detalles.add("Tamaño: $it") }
        producto.peso?.let { detalles.add("Peso: $it") }
        producto.tipo?.let { detalles.add("Tipo: $it") }
        producto.tipoHigiene?.let { detalles.add("Tipo: $it") }
        producto.fragancia?.let { detalles.add("Fragancia: $it") }
        producto.tipoAccesorio?.let { detalles.add("Tipo: $it") }

        if (detalles.isNotEmpty()) {
            binding.tvDetallesAdicionales.text = detalles.joinToString("\n")
            binding.tvDetallesAdicionales.visibility = View.VISIBLE
        } else {
            binding.tvDetallesAdicionales.visibility = View.GONE
        }
    }

    /**
     * Actualiza la UI de cantidad
     */
    private fun actualizarCantidad() {
        binding.tvCantidad.text = cantidadSeleccionada.toString()

        productoActual?.let { producto ->
            val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            val subtotal = producto.price * cantidadSeleccionada
            binding.tvSubtotal.text = "Subtotal: ${formato.format(subtotal)}"
        }
    }

    /**
     * Muestra diálogo para iniciar sesión
     */
    private fun mostrarDialogoIniciarSesion() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Iniciar Sesión")
            .setMessage("Debes iniciar sesión para agregar productos al carrito")
            .setPositiveButton("Iniciar Sesión") { _, _ ->
                dismiss()
                // Navegar a LoginFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.limpiarSeleccion()
        _binding = null
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }
}

