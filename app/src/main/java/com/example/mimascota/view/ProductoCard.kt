package com.example.mimascota.view

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mimascota.model.Producto
import com.example.mimascota.util.AppConfig
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
            val imageUrl = AppConfig.toAbsoluteImageUrl(producto.imageUrl)
            ProductImage(
                imageUrl = imageUrl,
                contentDescription = producto.producto_nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
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