package com.example.mimascota.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mimascota.R
import com.example.mimascota.ui.fragment.ProductoListaFragment

class ProductoListaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_lista)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProductoListaFragment())
                .commit()
        }
    }
}
