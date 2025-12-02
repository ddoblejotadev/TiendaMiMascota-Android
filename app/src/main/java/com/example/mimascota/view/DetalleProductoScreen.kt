package com.example.mimascota.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimascota.model.Producto
import com.example.mimascota.util.addIVA
import com.example.mimascota.util.formatCurrencyCLP

@Composable
fun DetalleProductoScreen(
    navController: NavController,
    producto: Producto,
    onAddToCart: (Producto, Int) -> Unit
) {
    var cantidad by remember { mutableStateOf(1) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Nombre del producto
        Text(
            text = producto.name,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Descripción
        producto.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Precio
        val precioConIva = addIVA(producto.price)
        Text(
            text = formatCurrencyCLP(precioConIva),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Precio anterior (si existe)
        producto.precioAnterior?.let {
            val precioAnteriorConIva = addIVA(it)
            Text(
                text = formatCurrencyCLP(precioAnteriorConIva),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = TextDecoration.LineThrough,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de cantidad
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { if (cantidad > 1) cantidad-- }) {
                Text("-")
            }
            Text(
                text = "$cantidad",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(onClick = { cantidad++ }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtotal
        val subtotal = addIVA(producto.price) * cantidad
        Text(
            text = "Subtotal: ${formatCurrencyCLP(subtotal)}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Botón agregar al carrito
        Button(
            onClick = {
                onAddToCart(producto, cantidad)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar al carrito")
        }
    }
}
