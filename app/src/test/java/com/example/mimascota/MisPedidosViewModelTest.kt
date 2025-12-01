package com.example.mimascota

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.repository.OrdenRepository
import com.example.mimascota.util.TokenManager
import com.example.mimascota.viewModel.MisPedidosViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Field
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class MisPedidosViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Scheduler/Dispatcher para controlar la ejecución de coroutines
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var tokenManager: TokenManager
    private lateinit var repoMock: OrdenRepository
    private lateinit var authRepoMock: AuthRepository
    private lateinit var viewModel: MisPedidosViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        tokenManager = mockk(relaxed = true)
        repoMock = mockk(relaxed = true)
        authRepoMock = mockk(relaxed = true)

        // Instanciamos el ViewModel con el TokenManager (constructor real)
        viewModel = MisPedidosViewModel(tokenManager)

        // Inyectamos por reflexión los repos mocks en las propiedades privadas
        injectPrivateField(viewModel, "repository", repoMock)
        injectPrivateField(viewModel, "authRepository", authRepoMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    //TEST: cargarOrdenes ordena por fecha descendente
    @Test
    fun cargarOrdenesTests() = runTest(testScheduler) {
        val ordOld = mockk<OrdenHistorial>(relaxed = true)
        val ordNew = mockk<OrdenHistorial>(relaxed = true)

        every { ordOld.fecha } returns "2025-01-01"
        every { ordNew.fecha } returns "2025-02-01"
        every { ordOld.usuarioId } returns 1L
        every { ordNew.usuarioId } returns 1L

        coEvery { repoMock.obtenerMisOrdenes(1L) } returns Result.success(listOf(ordOld, ordNew))

        viewModel.cargarOrdenes(1L)
        testScheduler.advanceUntilIdle()

        val fechas = viewModel.misOrdenes.getOrAwaitValue().map { it.fecha }
        Assert.assertEquals(listOf("2025-02-01", "2025-01-01"), fechas)
        Assert.assertEquals(false, viewModel.isLoading.getOrAwaitValue())
        Assert.assertEquals(null, viewModel.error.getOrAwaitValue())
    }

    //TEST: cargar detalle
    @Test
    fun cargarDetalleOrdenTest() = runTest(testScheduler) {
        val orden = mockk<OrdenHistorial>(relaxed = true)
        coEvery { repoMock.obtenerDetalleOrden(10L) } returns Result.success(orden)

        viewModel.cargarDetalleOrden(10L)
        testScheduler.advanceUntilIdle()

        Assert.assertEquals(orden, viewModel.ordenSeleccionada.getOrAwaitValue())
        Assert.assertEquals(false, viewModel.isLoading.getOrAwaitValue())
    }

    //TEST: cancelar orden
    @Test
    fun cancelarOrdenTest() = runTest(testScheduler) {
        val ord = mockk<OrdenHistorial>(relaxed = true)
        every { ord.usuarioId } returns 5L
        every { ord.fecha } returns "2025-01-01"

        coEvery { repoMock.cancelarOrden(99L) } returns Result.success(ord)
        coEvery { repoMock.obtenerMisOrdenes(5L) } returns Result.success(listOf(ord))

        viewModel.cargarOrdenes(5L)
        testScheduler.advanceUntilIdle()

        viewModel.cancelarOrden(99L)
        testScheduler.advanceUntilIdle()

        val mensaje = viewModel.error.getOrAwaitValue()
        Assert.assertTrue(mensaje == null || mensaje == "Orden cancelada exitosamente")
        val lista = viewModel.misOrdenes.getOrAwaitValue()
        Assert.assertEquals(1, lista.size)
        Assert.assertEquals(5L, lista.first().usuarioId)
    }
    //TEST: cargarMisOrdenes usa TokenManager
    @Test
    fun cargarMisOrdenesUserIdTest() = runTest(testScheduler) {
        every { tokenManager.getUserId() } returns 42L
        coEvery { repoMock.obtenerMisOrdenes(42L) } returns Result.success(emptyList())

        viewModel.cargarMisOrdenes()
        testScheduler.advanceUntilIdle()

        coVerify { repoMock.obtenerMisOrdenes(42L) }
        Assert.assertEquals(false, viewModel.isLoading.getOrAwaitValue())
    }

    //Helpers
    private fun injectPrivateField(target: Any, fieldName: String, value: Any) {
        val field: Field = target::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(target, value)
    }

    //Extensión para obtener valor de LiveData en tests (bloqueante, timeout)
    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        if (!latch.await(time, timeUnit)) {
            this.removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}