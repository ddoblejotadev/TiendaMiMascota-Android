package com.example.mimascota.ui.activity

import android.content.Intent
import android.os.Bundle
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.example.mimascota.databinding.ActivityOrdenExitosaBinding
import java.text.NumberFormat
import java.util.Locale

/**
 * OrdenExitosaActivity: Pantalla de confirmación después de realizar un pedido
 */
class OrdenExitosaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdenExitosaBinding

    private var ordenId: Int = 0
    private var numeroOrden: String = ""
    private var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdenExitosaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos de la orden desde el Intent
        ordenId = intent.getIntExtra("ORDEN_ID", 0)
        numeroOrden = intent.getStringExtra("NUMERO_ORDEN") ?: ""
        total = intent.getIntExtra("TOTAL", 0)

        mostrarDatosOrden()
        setupListeners()
    }

    private fun mostrarDatosOrden() {
        binding.tvNumeroOrden.text = numeroOrden

        val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
        binding.tvTotalPagado.text = formato.format(total)
    }

    private fun setupListeners() {
        binding.btnVerMisPedidos.setOnClickListener {
            // TODO: Navegar a pantalla de historial de pedidos
            // val intent = Intent(this, MisPedidosActivity::class.java)
            // startActivity(intent)
            finish()
        }

        binding.btnVolverInicio.setOnClickListener {
            // Volver al inicio y limpiar el back stack
            val intent = Intent(this, com.example.mimascota.MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    // Deshabilitar el botón de back para evitar volver al checkout
    @Deprecated("Deprecated in Java")
    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        // Mantener comportamiento personalizado y llamar a super para no romper expectativas de ciclo de vida
        val intent = Intent(this, com.example.mimascota.MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        super.onBackPressed()
        finish()
    }
}
