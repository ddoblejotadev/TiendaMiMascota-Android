package com.example.mimascota

import android.app.Application
import android.util.Log
import com.example.mimascota.model.Usuario
import com.example.mimascota.viewModel.AuthViewModel
import com.example.mimascota.viewModel.LoginState
import com.example.mimascota.repository.AuthRepository
import com.example.mimascota.util.TokenManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockApp: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockApp = mockk(relaxed = true)

        // Mockear TokenManager (objeto) para evitar acceso real a prefs
        mockkObject(TokenManager)
        every { TokenManager.getUsuario() } returns null
        every { TokenManager.getUserRole() } returns ""

        // Mockear Log para pruebas JVM (usar any<String>() para parámetros)
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    //TEST: refrescarUsuarioDesdeToken actualiza estado
    @Test
    fun refrescarUsuarioDesdeTokenTest() = runTest {
        val usuario = Usuario(usuarioId = 42, nombre = "Juan", email = "j@u.com", fotoUrl = null)
        every { TokenManager.getUsuario() } returns usuario

        val vm = AuthViewModel(mockApp)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Juan", vm.usuarioActual.value)
        assertEquals(42, vm.usuarioActualId.value)
        assertNull(vm.fotoPerfil.value)
    }

    //TEST: cerrarSesion actualiza estado
    @Test
    fun cerrarSesionTest() = runTest {
        mockkConstructor(AuthRepository::class)
        coEvery { anyConstructed<AuthRepository>().logout() } returns Result.success(Unit)

        every { TokenManager.getUsuario() } returns null

        val vm = AuthViewModel(mockApp)

        testDispatcher.scheduler.advanceUntilIdle()

        vm.usuarioActual.value = "Pedro"
        vm.usuarioActualId.value = 7
        vm.fotoPerfil.value = null
        vm.loginState.value = LoginState.Error("x")
        vm.registroState.value = "algo"

        vm.cerrarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(vm.usuarioActual.value)
        assertNull(vm.usuarioActualId.value)
        assertNull(vm.fotoPerfil.value)
        assertTrue(vm.loginState.value is LoginState.Idle)
        assertEquals("", vm.registroState.value)

        coVerify { anyConstructed<AuthRepository>().logout() }
    }

    //TEST: esAdmin devuelve true
    @Test
    fun esAdminTest() = runTest {
        every { TokenManager.getUserRole() } returns "admin"

        val vm = AuthViewModel(mockApp)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.esAdmin())
    }
}