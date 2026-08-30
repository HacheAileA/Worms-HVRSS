#!/bin/bash

# Script pour exécuter les tests, checkstyle et la Javadoc

clear
echo "========================="
echo "Running tests..."
./gradleComponent/gradlew -p gradleComponent test

echo "========================="
echo "Running Checkstyle..."
./gradleComponent/gradlew -p gradleComponent checkstyleMain

echo "========================="
echo "Cleaning build..."
./gradleComponent/gradlew -p gradleComponent clean

echo "========================="
echo "Generating Javadoc..."
./gradleComponent/gradlew -p gradleComponent javadoc

echo "========================="
echo "Done"
