@echo off
chcp 65001 >nul

rem Script pour exécuter les tests, checkstyle et la Javadoc

cls

echo =========================
echo Running tests...
call  .\gradleComponent\gradlew -p gradleComponent test

echo =========================
echo Running Checkstyle...
call  .\gradleComponent\gradlew -p gradleComponent checkstyleMain

echo =========================
echo Cleaning build...
call .\gradleComponent\gradlew -p gradleComponent clean

echo =========================
echo Generating Javadoc...
call  .\gradleComponent\gradlew -p gradleComponent javadoc

echo =========================
echo Done
