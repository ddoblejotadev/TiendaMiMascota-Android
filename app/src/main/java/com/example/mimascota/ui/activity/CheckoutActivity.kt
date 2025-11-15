package com.example.mimascota.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.Model.*
import com.example.mimascota.ViewModel.CheckoutViewModel
import com.example.mimascota.ViewModel.SharedViewModel
import com.example.mimascota.client.RetrofitClient // agregado
import com.example.mimascota.databinding.ActivityCheckoutBinding
import com.example.mimascota.ui.adapter.CheckoutProductoAdapter
import com.example.mimascota.util.TokenManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * CheckoutActivity: Pantalla para finalizar la compra
 *
 * Flujo:
 * 1. Muestra formulario de envío (auto-completa datos del usuario)
 * 2. Muestra resumen del carrito
 * 3. Verifica stock antes de crear la orden
 * 4. Crea la orden en el backend
 * 5. Navega a OrdenExitosaActivity
 */
class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var cartViewModel: SharedViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: CheckoutProductoAdapter

    private var carritoItems: List<CartItem> = emptyList()
    private var totalPagar: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModels y TokenManager
        checkoutViewModel = CheckoutViewModel()
        cartViewModel = SharedViewModel(this)
        RetrofitClient.init(applicationContext)
        tokenManager = TokenManager // uso del object

        setupToolbar()
        setupRecyclerView()
        cargarDatosUsuario()
        observarCarrito()
        setupListeners()
        observarViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = CheckoutProductoAdapter()
        binding.rvProductos.layoutManager = LinearLayoutManager(this)
        binding.rvProductos.adapter = adapter
    }

    /**
     * Auto-completa los datos del usuario logueado
     */
    private fun cargarDatosUsuario() {
        if (tokenManager.isLoggedIn()) {
            binding.etNombreCompleto.setText(tokenManager.getUserName() ?: "")
            binding.etEmail.setText(tokenManager.getUserEmail() ?: "")
            binding.etTelefono.setText(tokenManager.getUserTelefono() ?: "")
            binding.etDireccion.setText(tokenManager.getUserDireccion() ?: "")
        }
    }

    /**
     * Observa el carrito para mostrar los productos
     */
    private fun observarCarrito() {
        cartViewModel.carrito.observe(this) { items ->
            carritoItems = items
            adapter.submitList(items)
            calcularTotal(items)
        }
    }

    /**
     * Calcula el total a pagar
     */
    private fun calcularTotal(items: List<CartItem>) {
        totalPagar = items.sumOf { it.subtotal }
        val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
        binding.tvTotal.text = formato.format(totalPagar)
    }

    private fun setupListeners() {
        binding.btnRealizarPedido.setOnClickListener {
            if (validarFormulario()) {
                verificarStockYProcesar()
            }
        }
    }

    /**
     * Valida que todos los campos obligatorios estén llenos
     */
    private fun validarFormulario(): Boolean {
        var isValid = true

        // Validar nombre
        if (binding.etNombreCompleto.text.isNullOrBlank()) {
            binding.tilNombreCompleto.error = "Nombre es obligatorio"
            isValid = false
        } else {
            binding.tilNombreCompleto.error = null
        }

        // Validar email
        val email = binding.etEmail.text.toString()
        if (email.isBlank()) {
            binding.tilEmail.error = "Email es obligatorio"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email inválido"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Validar teléfono
        if (binding.etTelefono.text.isNullOrBlank()) {
            binding.tilTelefono.error = "Teléfono es obligatorio"
            isValid = false
        } else {
            binding.tilTelefono.error = null
        }

        // Validar dirección
        if (binding.etDireccion.text.isNullOrBlank()) {
            binding.tilDireccion.error = "Dirección es obligatoria"
            isValid = false
        } else {
            binding.tilDireccion.error = null
        }

        // Validar ciudad
        if (binding.etCiudad.text.isNullOrBlank()) {
            binding.tilCiudad.error = "Ciudad es obligatoria"
            isValid = false
        } else {
            binding.tilCiudad.error = null
        }

        // Validar región
        if (binding.etRegion.text.isNullOrBlank()) {
            binding.tilRegion.error = "Región es obligatoria"
            isValid = false
        } else {
            binding.tilRegion.error = null
        }

        // Validar que haya productos en el carrito
        if (carritoItems.isEmpty()) {
            Snackbar.make(binding.root, "El carrito está vacío", Snackbar.LENGTH_LONG).show()
            isValid = false
        }

        return isValid
    }

    /**
     * Verifica el stock antes de procesar el pedido
     */
    private fun verificarStockYProcesar() {
        val stockItems = carritoItems.map {
            StockItem(
                productoId = it.producto.producto_id,
                cantidad = it.cantidad
            )
        }

        checkoutViewModel.verificarStock(stockItems)
    }

    /**
     * Crea la orden en el backend
     */
    private fun crearOrden() {
        val subtotal = carritoItems.sumOf { it.subtotal }
        val total = subtotal // sumar envío si corresponde
        val datosEnvio = DatosEnvio(
            nombreCompleto = binding.etNombreCompleto.text.toString(),
            email = binding.etEmail.text.toString(),
            telefono = binding.etTelefono.text.toString(),
            direccion = binding.etDireccion.text.toString(),
            ciudad = binding.etCiudad.text.toString(),
            region = binding.etRegion.text.toString(),
            codigoPostal = binding.etCodigoPostal.text.toString(), // no nullable
            metodoPago = "tarjeta",
            pais = "Chile"
        )
        val items = carritoItems.map { ci ->
            ItemOrden(
                productoId = ci.producto.producto_id,
                cantidad = ci.cantidad,
                precioUnitario = ci.producto.price
            )
        }
        val usuarioIdLong = tokenManager.getUserId().toLong()
        checkoutViewModel.crearOrden(
            usuarioId = usuarioIdLong,
            esInvitado = false,
            datosEnvio = datosEnvio,
            items = items,
            subtotal = subtotal,
            total = total
        )
    }

    /**
     * Observa los estados del ViewModel
     */
    private fun observarViewModel() {
        // Observar loading
        lifecycleScope.launch {
            checkoutViewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnRealizarPedido.isEnabled = !isLoading
            }
        }

        // Observar verificación de stock
        lifecycleScope.launch {
            checkoutViewModel.stockVerificado.collect { response ->
                response?.let {
                    if (it.disponible) {
                        // Stock disponible, crear orden
                        crearOrden()
                    } else {
                        // Stock no disponible, mostrar diálogo
                        mostrarDialogoStockInsuficiente(it.productosAgotados ?: emptyList())
                    }
                }
            }
        }

        // Observar orden creada
        lifecycleScope.launch {
            checkoutViewModel.ordenCreada.collect { response ->
                response?.let {
                    cartViewModel.vaciarCarrito()
                    val intent = Intent(this@CheckoutActivity, OrdenExitosaActivity::class.java)
                    intent.putExtra("ORDEN_ID", it.id) // usar id
                    intent.putExtra("NUMERO_ORDEN", it.numeroOrden)
                    intent.putExtra("TOTAL", it.total)
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Observar errores
        lifecycleScope.launch {
            checkoutViewModel.error.collect { error ->
                error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    checkoutViewModel.limpiarError() // nombre correcto
                }
            }
        }
    }

    /**
     * Muestra diálogo cuando hay productos sin stock
     */
    private fun mostrarDialogoStockInsuficiente(productosAgotados: List<ProductoAgotado>) {
        val mensaje = buildString {
            append("Los siguientes productos no tienen stock suficiente:\n\n")
            productosAgotados.forEach { producto ->
                append("• ${producto.productoNombre}\n")
                append("  Solicitado: ${producto.cantidadSolicitada} | Disponible: ${producto.stockDisponible}\n\n")
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("⚠️ Stock Insuficiente")
            .setMessage(mensaje)
            .setPositiveButton("Eliminar y Continuar") { _, _ ->
                eliminarProductosAgotadosYContinuar(productosAgotados)
            }
            .setNegativeButton("Volver al Carrito") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Elimina productos agotados del carrito y continúa
     */
    private fun eliminarProductosAgotadosYContinuar(productosAgotados: List<ProductoAgotado>) {
        productosAgotados.forEach { productoAgotado ->
            val producto = carritoItems.find { it.producto.producto_id == productoAgotado.productoId }?.producto
            producto?.let {
                cartViewModel.eliminarDelCarrito(it)
            }
        }

        Snackbar.make(
            binding.root,
            "Productos sin stock eliminados. Verificando nuevamente...",
            Snackbar.LENGTH_SHORT
        ).show()

        // Volver a verificar stock después de eliminar
        verificarStockYProcesar()
    }
}
