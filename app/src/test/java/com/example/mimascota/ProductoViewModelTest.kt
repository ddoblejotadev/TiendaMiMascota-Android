package com.example.mimascota

import com.example.mimascota.model.Producto
import com.example.mimascota.repository.ProductoRepository
import com.example.mimascota.viewModel.ProductoViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    //TEST: getAllProductos carga lista de productos
    @Test
    fun getAllProductosTest() = runTest {
        mockkConstructor(ProductoRepository::class)
        val productoMock = mockk<Producto>(relaxed = true)
        val lista = listOf(productoMock)
        coEvery { anyConstructed<ProductoRepository>().getAllProductos() } returns ProductoRepository.ProductoResult.Success(lista)

        val vm = ProductoViewModel()
        vm.getAllProductos()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(lista, vm.productos.value)
    }

    //TEST: getProductoById carga producto por id
    @Test
    fun getProductoByIdTest() = runTest {
        mockkConstructor(ProductoRepository::class)
        val productoMock = mockk<Producto>(relaxed = true)
        val id = 7
        coEvery { anyConstructed<ProductoRepository>().getProductoById(id) } returns ProductoRepository.ProductoResult.Success(productoMock)

        val vm = ProductoViewModel()
        vm.getProductoById(id)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(productoMock, vm.producto.value)
    }

    //TEST: whenRepositoryReturnsFailure carga lista vacía en caso de error
    @Test
    fun whenRepositoryReturnsFailureTest() = runTest {
        mockkConstructor(ProductoRepository::class)
        coEvery { anyConstructed<ProductoRepository>().getAllProductos() } returns
                mockk<ProductoRepository.ProductoResult<List<Producto>>>(relaxed = true)
        val vm = ProductoViewModel()
        vm.getAllProductos()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(emptyList<Producto>(), vm.productos.value)
        assertNull(vm.producto.value)
    }
}