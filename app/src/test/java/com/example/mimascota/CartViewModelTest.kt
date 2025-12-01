package com.example.mimascota

import android.util.Log
import com.example.mimascota.repository.CartSyncRepository
import com.example.mimascota.viewModel.CartViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkObject(com.example.mimascota.util.TokenManager)
        every { com.example.mimascota.util.TokenManager.isLoggedIn() } returns false
        every { com.example.mimascota.util.TokenManager.getUserId() } returns 0L

        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    //TEST: agregarAlCarrito actualiza total correctamente
    @Test
    fun agregarAlCarritoTest() = runTest {
        val vm = CartViewModel()

        val producto = mockk<com.example.mimascota.model.Producto>(relaxed = true)
        every { producto.producto_id } returns 5
        every { producto.producto_nombre } returns "ProductoX"
        every { producto.price } returns 12.5


        vm.agregarAlCarrito(producto)
        vm.agregarAlCarrito(producto)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, vm.items.value.size)
        assertEquals(2, vm.cantidadParaProducto(5))
        assertEquals(25.0, vm.total.value, 0.001)
    }

    //TEST: actualizarCantidad actualiza total correctamente con 0 cantidad y vacia el carrito
    @Test
    fun actualizarCantidadTest() = runTest {
        val vm = CartViewModel()

        val producto = mockk<com.example.mimascota.model.Producto>(relaxed = true)
        every { producto.producto_id } returns 3
        every { producto.producto_nombre } returns "P"
        every { producto.price } returns 5.0

        vm.agregarAlCarrito(producto)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, vm.items.value.size)

        vm.actualizarCantidad(producto, 0)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, vm.items.value.size)
        assertEquals(0, vm.cantidadParaProducto(3))
        assertEquals(0.0, vm.total.value, 0.001)
    }

    //TEST: vaciarCarrito actualiza total correctamente y vacia el carrito con sincronización
    @Test
    fun vaciarCarritoTest() = runTest {

        every { com.example.mimascota.util.TokenManager.isLoggedIn() } returns true
        every { com.example.mimascota.util.TokenManager.getUserId() } returns 7L

        mockkConstructor(CartSyncRepository::class)
        coEvery { anyConstructed<CartSyncRepository>().sincronizarCarritoConReact(any(), any()) } returns mockk(relaxed = true)

        val vm = CartViewModel()

        val producto = mockk<com.example.mimascota.model.Producto>(relaxed = true)
        every { producto.producto_id } returns 9
        every { producto.producto_nombre } returns "Px"
        every { producto.price } returns 2.0

        vm.agregarAlCarrito(producto)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, vm.items.value.size)

        vm.vaciarCarrito()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, vm.items.value.size)
        assertEquals(0.0, vm.total.value, 0.001)

        coVerify(atLeast = 1) { anyConstructed<CartSyncRepository>().sincronizarCarritoConReact(any(), any()) }
    }

    //TEST: agregarAlCarritoSync actualiza total correctamente con sincronización si está logueado
    @Test
    fun agregarAlCarritoSyncTest() = runTest {
        every { com.example.mimascota.util.TokenManager.isLoggedIn() } returns true
        every { com.example.mimascota.util.TokenManager.getUserId() } returns 11L

        mockkConstructor(CartSyncRepository::class)
        coEvery { anyConstructed<CartSyncRepository>().sincronizarCarritoConReact(any(), any()) } returns mockk(relaxed = true)

        val vm = CartViewModel()

        val producto = mockk<com.example.mimascota.model.Producto>(relaxed = true)
        every { producto.producto_id } returns 21
        every { producto.producto_nombre } returns "Psync"
        every { producto.price } returns 1.5

        vm.agregarAlCarrito(producto)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { anyConstructed<CartSyncRepository>().sincronizarCarritoConReact(any(), any()) }
    }
}