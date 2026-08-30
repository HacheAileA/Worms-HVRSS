#!/bin/bash

# Script pour lancer le jeu

# Arguments possibles (séparés par des espaces) :
#   "console" pour vue console
#   "dev" pour mode développeur
#
# Lancer avec ./run.sh argument1 argument2 ...

export LANG=en_US.UTF-8

clear
echo "======================= Game start ========================"
echo "The game is starting, please wait..."

if [ $# -eq 0 ]; then
    args="empty"
else
    args="$@"
fi

cd gradleComponent/
./gradlew run --quiet --console=plain --args="$args"
echo "======================== End game ========================="
