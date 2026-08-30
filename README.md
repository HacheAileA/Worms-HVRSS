# Projet Worms — Université Paris Cité (L2 Informatique 2025–2026) - G13-E7

## Table des matières
- [Description du projet](#description-du-projet)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Compilation et exécution](#compilation-et-exécution)
- [Tests](#tests)
- [Outils](#outils)
- [Auteurs et remerciements](#auteurs-et-remerciements)

---

## Description du projet

Ce projet est un jeu d’artillerie 2D inspiré de Worms, développé en Java dans le cadre des UE POO et Conduite de Projet à l’Université Paris Cité.  
Dans ce jeu tour par tour, deux équipes de vers s’affrontent sur un terrain 2D avec des armes.

Le projet suit une méthodologie Agile (Scrum) et est développé en plusieurs itérations, avec :
- une version **textuelle** (console) pour les tests du modèle
- une version **graphique** réalisée avec les modules Swing et AWT

---

## Architecture

Nous suivons une architecture MVC (Modèle – Vue – Contrôleur) :  

- **Modèle** : contient la logique du jeu (`GameModel`, `Worm`, `Team`,  `Item`, `Guns`, `Tools`, etc.)
- **Vue** : gère l’affichage en vue texte et graphique Swing (`GameView`, `ConsoleView`, `GuiView`, etc.)
- **Contrôleur** : coordonne les entrées clavier/souris et le déroulement des tours (`GameController`, `ConsoleController`, `GuiController`, `BotController` etc.)

Organisation des dossiers :

```
src/
└── main/
    ├── java/
    │   ├── app/
    │   ├── controller/
    │   │   ├── bot/
    │   │   ├── console/
    │   │   └── gui/
    │   ├── model/
    │   │   ├── items/
    │   │   │   ├── crates/
    │   │   │   ├── guns/
    │   │   │   └── tools/
    │   │   ├── persistence/
    │   │   ├── physics/
    │   │   └── players/
    │   └── view/
    │       ├── console/
    │       └── gui/
    │
    └── resources/
        ├── assets/
        │   ├── items/
        │   ├── tiles/
        │   │   ├── bridge/
        │   │   ├── cave/
        │   │   ├── decorations/
        │   │   └── island/
        │   └── worms/
        └── sounds/
            ├── ambiance/
            └── sounds-effects/
```
---

## Prérequis

- **Java 17 ou supérieur (pas 25 à moins qu'on ne fasse qu'exécuter le .jar)**
- **Gradle** (gestion du build et des dépendances)
- Compatible avec Linux, macOS et Windows

---

## Compilation et exécution

Pour compiler et exécuter le programme, placez-vous à la racine du projet pour utiliser les scripts fournis ou les commandes Gradle directement.

### Avec les scripts (Linux/macOS/Windows)

-   **Compiler et lancer les tests :**
    - Sur Linux/macOS :
        ```bash
        ./runtest.sh
        ```
    
    - Sur Windows :
        ```bat
        runtest.bat
        ```
    
    Ce script simule la pipeline d'intégration continue (CI/CD) en exécutant un ensemble de tâches : nettoyage du projet, lancement des tests, analyse du style de code (checkstyle) et génération de la documentation (javadoc).

-   **Lancer le jeu :**
    - Sur Linux/macOS :
        ```bash
        ./run.sh
        ```
    
    - Sur Windows :
        ```bat
        run.bat
        ```

Ce jeu peut être lancé avec des arguments. Ils peuvent être entrés dans l'ordre souhaité, mais ils doivent être séparés par des espaces et respecter la casse :
- `"console"` -> permet de jouer en vue console (pas d'affichage graphique). Si pas spécifié, lance le jeu en vue graphique.
- `"dev"` -> permet de jouer en mode développeur (pas d'animations, jeu pré-paramétré). Si pas spécifié, lance le jeu en mode joueur.

Sans argument, le jeu se lance en mode joueur en vue graphique.

Par exemple, pour le mode développeur en vue console sur Linux :
```bash
./run.sh console dev
```

### Avec les commandes Gradle

Assurez-vous d'avoir les permissions d'exécution sur `gradlew` (`chmod +x gradleComponent/gradlew`) à la racine du projet.

-   **Compiler le projet :**
    -   Sur Linux/macOS :
        ```bash
        ./gradleComponent/gradlew -p gradleComponent build
        ```
    -   Sur Windows :
        ```bat
        .\gradleComponent\gradlew.bat -p gradleComponent build
        ```

-   **Lancer le jeu :**
    -   Sur Linux/macOS :
        ```bash
        ./gradleComponent/gradlew -p gradleComponent run --quiet --console=plain
        ```
    -   Sur Windows :
        ```bat
        .\gradleComponent\gradlew.bat -p gradleComponent run --quiet --console=plain
        ```
    Par exemple, pour le mode développeur en vue graphique :
    ```bash
    ./gradleComponent/gradlew -p gradleComponent run --quiet --console=plain --args="console dev"
    ```

-   **Créer un JAR exécutable :**
    - Sur Linux/macOS :
        ```bash
        ./gradleComponent/gradlew -p gradleComponent jar
        ```
    -   Sur Windows :
        ```bat
        .\gradleComponent\gradlew.bat -p gradleComponent jar
        ```
    La JAR produit se situe dans gradleComponent/build/libs/

-   **Générer la Javadoc :**
    - Sur Linux/macOS :
        ```bash
        ./gradleComponent/gradlew -p gradleComponent javadoc
        ```
    -   Sur Windows :
        ```bat
        .\gradleComponent\gradlew.bat -p gradleComponent javadoc
        ```
    Les fichiers `html` se situe dans gradleComponent/build/docs/javadoc/.
    Il suffit d'ouvrir le fichier `index.html` dans un navigateur pour parcourir la Javadoc.

---

## Tests

Les tests unitaires (avec JUnit) seront ajoutés au fur et à mesure dans le dossier `src/test/java/`.
Ils permettront de valider les parties essentielles du modèle : instances correctement créées, gravité, collisions, tours de jeu, inventaire, etc.

Pour lancer les tests :  
-   Sur Linux/macOS :
```bash
./gradleComponent/gradlew -p gradleComponent test
```
-   Sur Windows :
```bat
.\gradleComponent\gradlew.bat -p gradleComponent test
```

---

## Outils

- GitLab (système de gestion de versions, CI/CD)

- Gradle (build & dépendances)

- Java 17

- Swing / AWT (interface graphique)

- JUnit 5 et Mockito (tests)

- Checkstyle

- Cobertura et Jacoco (couverture de tests)
---

## Auteurs et remerciements

Les auteurs sont consignés dans le fichier [AUTHORS](./AUTHORS.md).

Projet réalisé dans le cadre des UE Conduite de Projet et Programmation Orientée Objet (Université Paris Cité – 2025–2026).  
Serveur GitLab : https://moule.informatique.u-paris.fr/mesnildr/projet-worms


Projet académique — Licence libre MIT (voir fichier [LICENSE](./LICENSE)).
