
package com.example.mimascota.view

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mimascota.model.Producto
import com.example.mimascota.util.CurrencyUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoCard(producto: Producto, onProductoClick: () -> Unit, onAddToCart: () -> Unit, cartQuantity: Int = 0) {
    val context = LocalContext.current
    val agregadoMsg = stringResource(id = com.example.mimascota.R.string.producto_agregado)
    val sinStockMsg = stringResource(id = com.example.mimascota.R.string.stock_insuficiente)
    val stockVal = producto.stock
    val estaAgotado = stockVal != null && stockVal <= 0

    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed.value) 0.96f else 1f, label = "")

    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(8.dp),
        onClick = onProductoClick
    ) {
        Column(modifier = Modifier.padding(8.dp).scale(scale)) {
            ProductImage(
                imageUrl = producto.imageUrl,
                contentDescription = producto.producto_nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp), // Set a fixed height for the image
                contentScale = ContentScale.Crop // Crop the image to fill the bounds
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(producto.producto_nombre, style = MaterialTheme.typography.titleMedium)

            if (stockVal == null) {
                Text(stringResource(id = com.example.mimascota.R.string.no_products), style = MaterialTheme.typography.bodySmall)
            } else if (stockVal <= 0) {
                Text(stringResource(id = com.example.mimascota.R.string.stock_insuficiente), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            } else {
                Text(String.format(stringResource(id = com.example.mimascota.R.string.product_stock), stockVal), style = MaterialTheme.typography.bodySmall)
            }

            Text(CurrencyUtils.formatAsCLP(producto.price), style = MaterialTheme.typography.bodyMedium)

            if (cartQuantity > 0) {
                Text(stringResource(id = com.example.mimascota.R.string.en_carrito, cartQuantity), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    if (estaAgotado) {
                        Toast.makeText(context, sinStockMsg, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    pressed.value = true
                    onAddToCart()

                    coroutineScope.launch {
                        kotlinx.coroutines.delay(180)
                        pressed.value = false
                    }
                    Toast.makeText(context, agregadoMsg, Toast.LENGTH_SHORT).show()
                },
                enabled = !estaAgotado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = com.example.mimascota.R.string.add_to_cart))
            }
        }
    }
}
