@echo off
REM Script de verificación rápida para TiendaMiMascota Android
REM Verifica que todo esté configurado correctamente

echo ============================================
echo  Verificacion de Configuracion
echo  TiendaMiMascota Android
echo ============================================
echo.

echo [1/5] Verificando Gradle...
where gradle >nul 2>nul
if %errorlevel% equ 0 (
    echo [OK] Gradle encontrado
) else (
    echo [ADVERTENCIA] Gradle no encontrado en PATH
)
echo.

echo [2/5] Verificando proyecto...
if exist "app\build.gradle.kts" (
    echo [OK] Proyecto encontrado
) else (
    echo [ERROR] No se encontro el proyecto
    pause
    exit /b 1
)
echo.

echo [3/5] Verificando archivos de configuracion...
if exist "app\src\main\java\com\example\mimascota\util\AppConfig.kt" (
    echo [OK] AppConfig.kt encontrado
) else (
    echo [ERROR] AppConfig.kt no encontrado
)

if exist "app\src\main\java\com\example\mimascota\config\ApiConfig.kt" (
    echo [OK] ApiConfig.kt encontrado
) else (
    echo [ERROR] ApiConfig.kt no encontrado
)

if exist "app\src\main\java\com\example\mimascota\util\ConnectionTester.kt" (
    echo [OK] ConnectionTester.kt encontrado
) else (
    echo [ERROR] ConnectionTester.kt no encontrado
)
echo.

echo [4/5] Verificando backend local...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/productos > temp_status.txt 2>nul
set /p STATUS=<temp_status.txt
del temp_status.txt 2>nul

if "%STATUS%"=="200" (
    echo [OK] Backend local respondiendo en puerto 8080
) else if "%STATUS%"=="000" (
    echo [ADVERTENCIA] Backend local no responde
    echo              Inicia Spring Boot antes de probar la app
) else (
    echo [ADVERTENCIA] Backend respondio con codigo: %STATUS%
)
echo.

echo [5/5] Compilando proyecto...
echo Esto puede tardar unos minutos...
call gradlew assembleDebug --console=plain > build_output.txt 2>&1
if %errorlevel% equ 0 (
    echo [OK] Compilacion exitosa
) else (
    echo [ERROR] Falló la compilacion. Revisa build_output.txt
)
echo.

echo ============================================
echo  Resumen
echo ============================================
echo.
echo URLs configuradas:
echo   - Debug:   http://10.0.2.2:8080/api/
echo   - Release: https://tiendamimascotabackends.onrender.com/api/
echo.
echo Documentacion disponible:
echo   - INICIO_RAPIDO.md
echo   - CONFIGURACION_URLS.md
echo   - GUIA_PRUEBAS.md
echo.
echo Proximos pasos:
echo   1. Inicia Spring Boot (si no esta corriendo)
echo   2. Abre Android Studio
echo   3. Run 'app'
echo   4. Revisa Logcat
echo.
echo ============================================
pause

