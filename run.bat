@echo off
chcp 65001 >nul

rem Script pour lancer le jeu

rem Arguments possibles (séparés par des espaces) :
rem   "console" pour vue console
rem   "dev" pour mode développeur
rem
rem Lancer avec run.bat argument1 argument2 ...

cls
echo ======================= Game start ========================
echo The game is starting, please wait...

cd gradleComponent\

if "%~1"=="" (
    call gradlew run --quiet --console=plain
) else (
    call gradlew run --quiet --console=plain --args="%*"
)

cd ..

echo ======================== End game =========================
