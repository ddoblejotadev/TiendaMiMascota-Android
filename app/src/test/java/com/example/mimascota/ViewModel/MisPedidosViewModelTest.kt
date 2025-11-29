package com.example.mimascota.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mimascota.model.Orden
import com.example.mimascota.repository.OrdenRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class MisPedidosViewModelTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MisPedidosViewModel
    private lateinit var repository: OrdenRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(OrdenRepository::class.java)
        viewModel = MisPedidosViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TEST cargarOrdenes
    @Test
    fun cargarOrdenesTest() = runTest {
        val orden1 = Orden(1, 10, fecha = 1000L)
        val orden2 = Orden(2, 10, fecha = 2000L)

        val lista = listOf(orden1, orden2)

        `when`(repository.obtenerMisOrdenes(10))
            .thenReturn(Result.success(lista))

        viewModel.cargarOrdenes(10)
        testScheduler.advanceUntilIdle()

        Assert.assertEquals(listOf(orden2, orden1), viewModel.ordenes.value)
        Assert.assertEquals(false, viewModel.isLoading.value)
        Assert.assertEquals(null, viewModel.error.value)
    }

    // TEST cargarOrdenes (ERROR)
    @Test
    fun manejaErroresTest() = runTest {
        `when`(repository.obtenerMisOrdenes(10))
            .thenReturn(Result.failure(Exception("Error de red")))

        viewModel.cargarOrdenes(10)
        testScheduler.advanceUntilIdle()

        Assert.assertEquals(emptyList<Orden>(), viewModel.ordenes.value)
        Assert.assertEquals("Error de red", viewModel.error.value)
        Assert.assertEquals(false, viewModel.isLoading.value)
    }

    // TEST cargarDetalleOrden
    @Test
    fun ActualizarOrdenSeleccionadaTest() = runTest {
        val orden = Orden(5, 10, fecha = 3000)

        `when`(repository.obtenerDetalleOrden(5))
            .thenReturn(Result.success(orden))

        viewModel.cargarDetalleOrden(5)
        testScheduler.advanceUntilIdle()

        Assert.assertEquals(orden, viewModel.ordenSeleccionada.value)
        Assert.assertEquals(null, viewModel.error.value)
    }

    // TEST cargarDetalleOrden (ERROR)
    @Test
    fun manejarErroresTest() = runTest {
        `when`(repository.obtenerDetalleOrden(5))
            .thenReturn(Result.failure(Exception("No existe")))

        viewModel.cargarDetalleOrden(5)
        testScheduler.advanceUntilIdle()

        Assert.assertEquals("No existe", viewModel.error.value)
        Assert.assertEquals(null, viewModel.ordenSeleccionada.value)
    }

    // TEST cancelarOrden
    @Test
    fun mostrarMensajeExito() = runTest {
        val orden = Orden(1, usuarioId = 10, fecha = 1000)
        `when`(repository.cancelarOrden(1)).thenReturn(Result.success(Unit))
        `when`(repository.obtenerMisOrdenes(10)).thenReturn(Result.success(listOf(orden)))

        // Pre-cargar órdenes
        viewModel.cargarOrdenes(10)
        testScheduler.advanceUntilIdle()

        viewModel.cancelarOrden(1)
        testScheduler.advanceUntilIdle()

        Assert.assertEquals("Orden cancelada exitosamente", viewModel.error.value)
    }

    // TEST cancelarOrden (ERROR)
    @Test
    fun cManejarErroresTest() = runTest {
        `when`(repository.cancelarOrden(1))
            .thenReturn(Result.failure(Exception("Fallo al cancelar")))

        viewModel.cancelarOrden(1)
        testScheduler.advanceUntilIdle()

        Assert.assertEquals("Fallo al cancelar", viewModel.error.value)
    }

    // TEST clearError()
    @Test
    fun limpiarMensajeError() {
        viewModel.clearError()
        Assert.assertEquals(null, viewModel.error.value)
    }
}