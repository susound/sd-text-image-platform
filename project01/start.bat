@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion
title Nebula Studio - Start All

set "ROOT=%~dp0"

echo.
echo   =============================================
echo     Nebula Studio - Start All Services
echo   =============================================
echo.

:: ============================================================
:: Phase 1 - Cleanup
:: ============================================================
echo   [1/4] Stopping old instances...

call :kill_port 5000
call :kill_port 8080
call :kill_port 3000
call :kill_port 3001
del /f "%ROOT%backend\data\nebula_studio.lock.db" 2>nul
echo         Done.

:: ============================================================
:: Phase 2 - Inference Service (5000)
:: ============================================================
echo.
echo   [2/4] Inference Service ^(5000^)

if not exist "%ROOT%inference-service\main.py" (
    echo         SKIP: inference-service\main.py not found
    goto :backend
)

cd /d "%ROOT%inference-service"

:: Check if torch is already installed (skip if yes)
python -c "import torch; exit(0)" >nul 2>&1
if !errorlevel! neq 0 (
    echo         Installing PyTorch ^(CUDA, ~2.5GB - may take a while^)...
    pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu124
    if !errorlevel! neq 0 (
        echo         WARN: CUDA torch install failed, falling back to CPU...
        pip install torch torchvision torchaudio
    )
) else (
    echo         PyTorch already installed
)
pip install -r requirements.txt >nul 2>&1
echo         Dependencies ready

echo         Launching...
start "InferenceService" /D "%ROOT%inference-service" cmd /c "set CUDA_LAUNCH_BLOCKING=1 && python main.py"

echo         Loading model to GPU (may take 2-5 min on first run)...
echo         (check progress at http://127.0.0.1:5000/health)
echo         Waiting up to 300s, first check after 15s...
ping -n 16 127.0.0.1 >nul
call :wait "http://127.0.0.1:5000/health" 285
if !errorlevel! equ 0 (
    echo         READY
) else (
    echo         WARN: Not ready yet ^(can be slow on first load^)
    echo         Run "stop.bat" if it fails, then start again
)

:: ============================================================
:: Phase 3 - Backend (8080)
:: ============================================================
:backend
echo.
echo   [3/4] Backend ^(8080^)

if not exist "%ROOT%backend\pom.xml" (
    echo         SKIP: backend\pom.xml not found
    goto :frontend
)

cd /d "%ROOT%backend"

set "JAR="
for %%f in (target\*.jar) do if not "%%~nxf"=="original-*" set "JAR=%%f"

if defined JAR (
    echo         Starting from JAR...
    start "Nebula-Backend" /D "%ROOT%backend" java -jar "%ROOT%backend\!JAR!" --spring.profiles.active=h2
) else (
    echo         Building and starting from Maven...
    start "Nebula-Backend" /D "%ROOT%backend" cmd /c "mvn spring-boot:run -Dspring-boot.run.profiles=h2"
)

echo         Waiting (Spring Boot startup, ~30-90s)...
call :wait "http://localhost:8080" 120
if !errorlevel! equ 0 (
    echo         READY
) else (
    echo         WARN: Backend not yet ready
)

:: ============================================================
:: Phase 4 - Frontend (3000)
:: ============================================================
:frontend
echo.
echo   [4/4] Frontend ^(3000^)

if not exist "%ROOT%frontend\package.json" (
    echo         SKIP: frontend\package.json not found
    goto :done
)

if not exist "%ROOT%frontend\node_modules\" (
    echo         Installing npm dependencies...
    cd /d "%ROOT%frontend"
    call npm install
)

echo         Launching...
start "Nebula-Frontend" /D "%ROOT%frontend" cmd /c "npm run dev"

echo         Waiting...
call :wait "http://localhost:3000" 45
if !errorlevel! equ 0 (
    echo         READY
) else (
    echo         WARN: Frontend not yet available
)

:: ============================================================
:: Summary
:: ============================================================
:done
echo.
echo   =============================================
echo     All services started
echo   =============================================
echo     Inference      http://127.0.0.1:5000/health
echo     Backend API    http://localhost:8080
echo     Frontend       http://localhost:3000
echo.
echo     Login:         admin@nebula.com / admin123
echo   =============================================
echo.
echo  Press any key to close this window (services keep running).
echo  Or close all services with: stop.bat
echo.
pause
exit /b 0

:: ============================================================
:: Helper: Wait for URL
:: ============================================================
:wait
setlocal
set "url=%~1"
set "max=%~2"
set "elapsed=0"
:wait_poll
curl -s --connect-timeout 3 --max-time 5 "%url%" >"%TEMP%\hc.txt" 2>&1
if !errorlevel! equ 0 (
    del "%TEMP%\hc.txt" 2>nul
    exit /b 0
)
del "%TEMP%\hc.txt" 2>nul
ping -n 4 127.0.0.1 >nul
set /a elapsed+=3
if !elapsed! lss !max! goto wait_poll
exit /b 1

:: ============================================================
:: Helper: Kill by port
:: ============================================================
:kill_port
setlocal
set "port=%~1"
for /f "tokens=5" %%a in ('netstat -ano ^| findstr /c:":%port% "') do (
    taskkill /f /pid %%a >nul 2>&1
)
exit /b 0
