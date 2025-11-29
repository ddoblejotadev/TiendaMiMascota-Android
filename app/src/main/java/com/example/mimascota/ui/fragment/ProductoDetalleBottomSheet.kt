package com.example.mimascota.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.mimascota.R
import com.example.mimascota.databinding.BottomSheetProductoDetalleBinding
import com.example.mimascota.model.Producto
import com.example.mimascota.util.AppConfig
import com.example.mimascota.viewModel.SharedViewModel
import com.example.mimascota.viewModel.SharedViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProductoDetalleBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProductoDetalleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels { 
        SharedViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProductoDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.productoSeleccionado.observe(viewLifecycleOwner) { producto ->
            producto?.let { bindProducto(it) }
        }
    }

    private fun bindProducto(producto: Producto) {
        binding.tvNombre.text = producto.producto_nombre
        binding.tvDescripcion.text = producto.description
        binding.tvPrecio.text = String.format("$%.2f", producto.price)

        val fullUrl = AppConfig.toAbsoluteImageUrl(producto.imageUrl)
        if (!fullUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.placeholder_product)
                .into(binding.ivProducto)
        } else {
            binding.ivProducto.setImageResource(R.drawable.placeholder_product)
        }

        var cantidad = 1
        binding.tvCantidad.text = cantidad.toString()

        binding.btnAumentar.setOnClickListener {
            cantidad++
            binding.tvCantidad.text = cantidad.toString()
        }

        binding.btnDisminuir.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                binding.tvCantidad.text = cantidad.toString()
            }
        }

        binding.btnAgregarCarrito.setOnClickListener {
            viewModel.agregarAlCarrito(producto, cantidad)
            dismiss()
        }

        binding.btnCerrar.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.limpiarSeleccion()
        _binding = null
    }
}
