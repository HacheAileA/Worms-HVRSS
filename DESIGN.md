# Design - Description/Justification des structures
## Objectif du projet

L'objectif du projet est d’implémenter en Java, un jeu Worms-like. Il s'agit d'équipes de vers [de terre]
qui s’affrontent au tour par tour, sur un terrain, en utilisant un arsenal d'armes (ici arbitraire)
N'implémentant que la version console pour le moment, nous n'avons pas encore développé de vue Graphique,
par conséquent pas de terrain desctructible, de déplacements des vers, de physique générale dans le jeu, 
(collisions etc..)


---

## Structure de l'arborescence générale (*au Jalon 3*)
Utilisation de la commande `tree /f /a > structure.txt` en cmd.

```
.
|   .gitignore
|   .gitlab-ci.yml
|   .mailmap
|   AUTHORS.md
|   CHANGELOG.md
|   CONTRIBUTING.md
|   DESIGN.md
|   generate_tree.py
|   LICENSE
|   README.md
|   run.bat
|   run.sh
|   runtest.bat
|   runtest.sh
|   SPECIFICATIONS.md
|       
+---config
|   \---checkstyle
|           checkstyle.xml
|           
+---gradleComponent
|   |   build.gradle
|   |   gradlew
|   |   gradlew.bat
|   |   settings.gradle   
|   |
|   \---gradle
|       \---wrapper
|               gradle-wrapper.jar
|               gradle-wrapper.properties
|               
+---rapport
|       Rapport_G13-E7.pdf
|       Rapport_G13-E7.tex
|       
+---release
|       hvrss-1.1.jar
|       hvrss_1.0.jar
|       
+---saves
|       .gitkeep
|       
\---src
    +---main
    |   \---java
    |       +---app
    |       |       app.puml
    |       |       appUML.png
    |       |       Launcher.java
    |       |       Main.java
    |       |       
    |       +---controller
    |       |       controller.puml
    |       |       controllerUML.png
    |       |       InputValidator.java
    |       |       TurnManager.java
    |       |       
    |       +---model
    |       |   |   Config.java
    |       |   |   Game.java
    |       |   |   model.puml
    |       |   |   modelUML.png
    |       |   |   
    |       |   +---game
    |       |   |       game.puml
    |       |   |       GameInitializer.java
    |       |   |       GameSettings.java
    |       |   |       GameState.java
    |       |   |       gameUML.png
    |       |   |       Map.java
    |       |   |       
    |       |   +---items
    |       |   |   |   Arsenal.java
    |       |   |   |   ArsenalTools.java
    |       |   |   |   Inventory.java
    |       |   |   |   Item.java
    |       |   |   |   items.puml
    |       |   |   |   itemsUML.png
    |       |   |   |   
    |       |   |   +---guns
    |       |   |   |       Bazooka.java
    |       |   |   |       Guns.java
    |       |   |   |       ShotGun.java
    |       |   |   |       Sniper.java
    |       |   |   |       
    |       |   |   \---tools
    |       |   |           HealthPack.java
    |       |   |           Tools.java
    |       |   |           
    |       |   +---persistence
    |       |   |       LoadManager.java
    |       |   |       persistence.puml
    |       |   |       persistenceUML.png
    |       |   |       SaveManager.java
    |       |   |       
    |       |   \---players
    |       |           players.puml
    |       |           playersUML.png
    |       |           Team.java
    |       |           Worm.java
    |       |           
    |       \---view
    |           |   DisplayView.java
    |           |   view.puml
    |           |   viewUML.png
    |           |   
    |           +---console
    |           |       AnsiColor.java
    |           |       ConsoleHelper.java
    |           |       ConsoleView.java
    |           |       
    |           \---gui
    |                   GuiView.java
    |                   
    \---test
        \---java
            +---app
            |       LauncherTest.java
            |       MainTest.java
            |       
            +---controller
            |       InputValidatorTest.java
            |       TurnManagerTest.java
            |       
            +---model
            |   |   ConfigTest.java
            |   |   GameTest.java
            |   |   
            |   +---game
            |   |       GameInitializerTest.java
            |   |       GameSettingsTest.java
            |   |       GameStateTest.java
            |   |       MapTest.java
            |   |       
            |   +---items
            |   |   |   ArsenalTest.java
            |   |   |   ArsenalToolsTest.java
            |   |   |   InventoryTest.java
            |   |   |   
            |   |   +---guns
            |   |   |       BazookaTest.java
            |   |   |       GunsTest.java
            |   |   |       ShotGunTest.java
            |   |   |       SniperTest.java
            |   |   |       
            |   |   \---tools
            |   |           HealthPackTest.java
            |   |           ToolsTest.java
            |   |           
            |   +---persistence
            |   |       LoadManagerTest.java
            |   |       SaveManagerTest.java
            |   |       
            |   \---players
            |           TeamTest.java
            |           WormTest.java
            |           
            \---view
                |   DisplayViewTest.java
                |   
                +---console
                |       AnsiColorTest.java
                |       ConsoleHelperTest.java
                |       ConsoleViewTest.java
                |       
                \---gui
                        GuiViewTest.java
            


```
## Améliorations apportées
- Harmonisation des javadocs et des affichages
- Encapsulation : Décomposition des tâches de lancement (séparation des méthodes du Main, Console[Helper], Timer (en maintenance) en plusiers sous-classes)
- Réglages de l'affichage de la version console [terminal]
- Quelques bugs fixés concernant les conditions de victoire, l'ordre de jeu des équipes, les munitions infinies, une attaque ratée anis que d'autres bugs résolus.


---
## Choix structurels/technologiques
Plusieurs méthodes d'implémentation ont été choisies avec recul, afin de faciliter la compréhension ainsi que la visualisation du code.
- Utilisation d'ArrayList de Worm pour la class Team plutôt que de LinkedList. Cela nous permettrait d'avoir de la flexibilité sur la redimension des tableaux et les parcours linéaire et non linéaire de celles-ci (au besoin).
    - C'est le cas pour les classes Team, Arsenal

- Création d'un répertoire "core" dans lequel plusieurs classes (GameInitializer, InputValidator et TurnManager) permettent de faire tourner cette première version du jeu, avec Java au profit de Gradle, car impossible avec dernier.
Le Main, très peu lisible (englobant toutes les méthodes des 3 classes précédantes) a donc été découpé :
    - GameInitializer : Pour les différentes étapes de configuration du jeu
    - InputValidator : Centralisation des différentes validations d'entrées [dans le terminal]
    - TurnManager : Rassemblement des choix d'actions possibles, pour l'utilisateur

- Création d'un répertoire utils (src/main/java/game/utils/display/...) en charge d'encadrer les modes d'affichage (et les harmoniser), qui prend déjà en charge l'affichage console. On peut y retrouver :
    - La classe .../display/Console, pour gérer les foncitons d'affichage de la vue console
        - Affichage de début et fin de la partie, des rours par joueurs, ver actif, équipes, choix incorrect, chargement, tirs, touche(s), armes disponibles...
    - La classe .../utils/ConsoleHelper, pour la gestion de la console
        - Avec le booléan permettant d'activer 
        - Nettoyage de la console, le timing des affichages (animé) des informations dans le terminal

Les méthodes de préparation et de gestion d'étapes du jeu ont été séparées dans des classes diférentes (mentionnées plus tôt) au lieu de les encapsuler dans une seule et unique méthode main().

**EN MAINTENACE** Création d'une classe Timer pour gérer la contrainte temporelle, afin d'avoir un temps de jeu fini (avec une centralisation des attributs/méthodes, qui sont statiques)./
Le temps de jeu reste bloqué, c'est pourquoi une 
-   Limite de temps par joueur/tour
- **MAINTENACE** du multithreading

---
## Gradle - Création de la pipeline / Exécution des Test
Nous avons choisi d'utiliser Gradle, en raison de son efficacité (comparé à Maven) et son temps d'exécution plus rapide.
Automatisation du CI/CD et des test de compilation et de chestyle de manière classique, avec le fichier .gitlab-ci.yml (qui reprends le buils.gradle, cache.gradle, runtest.sh, gradlew, gradle.jar)
Et le run.sh, qui est éxécuté au début de la partie.


---
## Evolution(s) possible(s) (--> Prochain Jalon)
```
- Ajout de sauvegarde/chargement d'une partie
- Vue textuelle 'ASCII'
- Changement de la structure pour adopter une MVC
- Ajout d'une classe Inventaire et Outils
- Implémentation des méthodes pour se déplacer dans la vue ascii et réimplémentation des méthodes de tir
```


---

## DESIGN.md - Jalon 2

### Évolution de la structure et justification des choix
### 1. Introduction
Cette partie présentera l'évolution de l'architecture logicielle du projet entre le Jalon 1 et le Jalon 2.
Le premier jalon consistait à obtenir une version console minimale et fonctionnelle.
Ce second jalon a pour objectif une version console complète, incluant : 
- Une vue textuelle ASCII du terrain (en couleur),
- Un système de sauvegarde/chargement de patie,
- L'introduction d'un inventaire d'équipe,
- Une réimplémentation des déplacements (pour correspondre à la vue ascii),
- Une réorganisation complète du projet vers une structure MVC (en préparation de la future vue graphique).

### 2. Architecture générale : Adoption d'une structure MVC
L'un des objectifs principaux du Jalon 2 était de séparer les responsabilités afin d'éviter un mélange du modèle, de la logique de contrôle et de l'affichage console.

#### Découpage en 4 couches
Nous avons introduit une architecture inspirée du patterne MVC :
- ### _Modèle_
    Contiens la logique du jeu et ses données :
    - Game, GameInitializer, GameState etc.
    - Arsenal, Inventory, Guns, Bazooka etc.
    - Map, Team, Worm
    - SaveManager, LoadManager
    
    Ces classes ne contiennent aucun affichage et représentent l'état du jeu

- ### _View_
    Regroupe les éléments de la vue console ASCII :
    - AnsiiColor, ConsoleHelper
    - DisplayView, GuiView, ConsoleView

- ### _Controller_
    Gère les interactions utilisateurs et la progression du jeu
    - InputValidator
    - TurnManager

- ### _App_
    Permet de lancer le jeu
    - Launcher
    - Main

---

### 3. Vue ASCII : conception et choix techniques
Le Jalon 2 exige une vraie vue textuelle permetant de visualiser clairement le terrain et les actions des vers.

Ce dernier est stocké stocké dans un tableau de caractères avec 
- '#' pour la terre
- '~' pour l'eau
- ' ' pour l'air
- '{1-8}' pour les worms

Chaque team est représenté par une couleur différente et les worms sont différenciés par leur numéro.

##### Avantages de cette représentation :
Simple à manipuler en console.
Facile à sauvegarder dans un fichier texte.

### 4. Déplacements, physique et collisions (console)
Les déplacements des vers et les tirs ont été réimplémentés spécifiquement pour la vue ASCII.

Ont été ajoutés : 
- Déplacements horizontaux (contrôle des collisions avec le terrain)
- Vérification de chute (gravité simplifiée)
- Détection d'eau (mort immédiate du ver)
- Saut simplifié (gauche ou droite) pour tester les collisions

### 5. Système de sauvegarde / chargement
Ce sprint introduit une fonctionnalité complète de sauvegarde dans un fichier textuel save.txt situé à la racine du projet dans un répertoire saves

Format retenu (pour sa facilité à être lu et édité) : 
```
===== GAME SAVE =====

=== TEAMS ORDER ===

------------------------------
Team 1: Player | Symbol = O | TeamId = 1
  Worms:
    - Player_PrivateBoom | HP = 100 | Position = (1, 6) | Symbol = 1 | TeamId = 1
    - Player_BazookaBob | HP = 100 | Position = (0, 8) | Symbol = 2 | TeamId = 1
  Inventory:
    - Guns:
        - Bazooka | Ammo = 1
        - ShotGun | Ammo = 20
        - Sniper | Ammo = 15
    - Tools:
        - Health Pack | Uses left = 1

------------------------------
Team 2: Bot | Symbol = X | TeamId = 2
  Worms:
    - Bot_SergeantSquirm | HP = 100 | Position = (6, 9) | Symbol = 1 | TeamId = 2
    - Bot_MajorWorm | HP = 100 | Position = (4, 6) | Symbol = 2 | TeamId = 2
  Inventory:
    - Guns:
        - Bazooka | Ammo = 1
        - ShotGun | Ammo = 20
        - Sniper | Ammo = 15
    - Tools:
        - Health Pack | Uses left = 1


=== MAP ===
Dimensions: 10 x 13

. . . . . . . . . .
. . . . . . . . . .
. . . . . . . . . .
. . . . . . . . . . 
. . . . . . . . . .
. . . . . . . . . . 
. 1 # # 2 . . . . . 
. # # # # . . . . . 
2 # # # # # . . . . 
# # # # # # 1 . . . 
# # # # # # # . . # 
~ ~ ~ ~ ~ ~ ~ ~ ~ ~ 
~ ~ ~ ~ ~ ~ ~ ~ ~ ~
===== END SAVE =====
```


### 7. Réimplémentation des armes
Gestion des armes/munitions via un inventaire permettant une meilleure séparation logique. Ajout des outils (pack de heal seulement actuellement).
Arborescence actuelle des armes comme suit : 
```
├── Arsenal.java
├── ArsenalTools.java
├── guns
│   ├── Bazooka.java
│   ├── Guns.java
│   ├── ShotGun.java
│   └── Sniper.java
├── Inventory.java
├── Item.java
├── items.puml
├── itemsUML.png
└── tools
    ├── HealthPack.java
    └── Tools.java
```

### 8. Tests Unitaires
Ce Sprint a été l'occasion de rédiger le plus de tests possibles. On a réussi à atteindre une couverture de code de 80% !
Seules les classes Game, SaveManager et LoadManager n'ont pas été totalement testées.
Nous avons utilisé JUnit ainsi que mockito pour les réaliser afin de pouvoir bien tout tester correctement sans avoir à instancier trop de classes (car beaucoup dépendent d'autres ou alors pour tester les classes abstraites).

### 9. Limitations actuelles et pistes d'amélioration (pour le(s) prochain(s) jalon(s))

- Destructibilité du terrain non implémentée.
- "Vraie" physique (notamment physique sur les projectiles, possibilité de viser etc.)
- Implémentation d'une interface graphique.
- Pas de vent.
- Ajout d'autres objets (armes ou outils)


---

## DESIGN.md - Jalon 3

### Évolution de la structure et justification des choix

### 1. Introduction
Le Jalon 3 marque un changement majeur dans le projet. C'est l'implémentation de la version graphique.
L'objectif principal de ce jalon était d'introduire une version graphique jouable du jeu, tout en conservant la structure MVC mise en place lors que Jalon précédent.

Ce jalon a donc servi de passage de la version ASCII à la version graphique en posant toutes les bases nécessaires : 
- Affichage graphique
- Interactions clavier/souris pour les déplacements et les tirs
- Rendu du terrain et des worms / projectiles
- Destructibilité du terrain

### 2. Architecture générale : Extension de la MVC vers le graphique
La structure MVC introduite au Jalon 2 a été conservée en supportant l'interface graphique.

**Modèle (model/)**
Le modèle reste responsable de :
- La carte
- Les équipes et les worms
- Les armes, les projectiles et inventaires
- La logique de jeu
Il est volontairement indépendant de toute notion graphique, ce qui permet de réutiliser la logique aussi bien pour la vue console que graphique.

**Vue (view/)**
Une nouvelle vue graphique a été introduite, basée sur AWT et Swing et qui n'est donc plus TUI.
Elle est responsable de :
- L'affichage du terrain
- Le rendu des worms
- L'affichage des projectiles et de la prédiction de leur trajectoire
- L'interface utilisateur (HUD, menus)

**Contrôleur (controller/)**
Le contrôleur a été enrichi opur gérer : 
- Les entrées clavier
- Les clics souris
- Le bot

### 3. Vue graphique : conception et choix techniques
Le choix s'est porté sur AWT et Swing ce qui a été très pratique de par leur disponibilité native en Java et leur simplicité d'intégration.

### 4. Gestion des entrées : clavier et souris
Un InputController a été introduit pour centralier la gestion des entrées utilisateur.
Il permet : 
- Les déplacements via le clavier
- Le déclenchement des tirs (par clic gauche)
- La navigation dans les menus.

### 5. Bot
Un début de bot a été implémenté. Il ne fait que tirer mais possède un niveau de difficulté réglable.

### 6. Documentation et cohérence
Ce jalon a également été l'occasion de : 
- Mettre à jour le README
- Compléter le DESIGN et CHANGELOG
- Mettre à jour le rapport
L'objectif était d'asurer une cohérence entre le code et la documentation

---

## DESIGN.md - Jalon 4 

### Évolution de la structure et justification des choix

### 1. Introduction
Le Jalon 4 correspond à une phase de finition du projet, visant à transformer la version graphique initiale en un jeu complet, sans bug majeur et un minimum esthétique.

Alors que le Jalon 3 se concentrait principalement sur l'introduction de la vue graphique et l'adaptation de l'architecture MVC, ce Jalon 4 a pour objectifs principaux :

- L'enrichissement du gameplay (déplacements fluides, ajout d'armes et de fonctionnalités externes)
- L'amélioration de l'expérience utilisateur (amélioration des maps, menus etc.) plus généralement amélioration de l'esthétique globale du jeu.
- La consolidtion de l'architecture MVC existante
- Et la finalisation de nombreuses fonctionnalités avancées

Ce jalon marque également la finalité du projet en mettant un fort accent sur l'amélioration de la qualité du code, les performances ainsi que la cohérence globale du projet.

### 2. Système de caméra (GUI) : conception et intégration

L'introduction d'une caméra dynamique a été un point central de ce jalon.
**Objectifs**
- Centrer automatiquement la vue sur le worm actif
- Améliorer la lisibilité et le confort de jeu
**Choix techniques**
- La caméra agit comme un offset appliqué au rendu de la carte.
- Le modèle reste en coordonnées absolues, seule la vue applique une transformation
Ce choix permet de ne pas impacter la logique du je tout en offrant une vue dynamique.

### 4. Amélioration des déplacements et animations

Les déplacements des worms ont été lissés afin d'éviter des déplacements case par case des versions précédentes.

Ont été ajoutés : 
- Particules lors de sauts et tirs
- Transitions plus fluides lors des chutes en cas de tirs ou de passage de tours
- Ajout des sons (sauts et tirs)
Ces animations sont gérées côté vue garantissant la cohérence de la logique de jeu dans une poursuite de l'utilisation de la MVC 

### 5. Vent : extension du moteur de jeu

**Vent**
Le vent est désormais une option activable/désactivable, influençant la trajectoire des projectiles.
- Implémenté dans la logique de calcul des tirs.
- Paramétrable via les paramètres avancés.
- Sauvegardé dans l'état de la partie

### 6. Génération de cartes variées
La génération de cate ne repose plus uniquement sur un seul type. Sa génération aléatoire est restée mais désormais on aura la possibilité de choisir parmis plusieurs types de carte.

Ces différentes map ont été implémentées dans l'optique d'augmenter la rejouabilité et introduire des situations tactiques différentes. 

### 7. Bot

Un bot jouable avait été implémenté lors du jalon précédent et a été complété dans celui là afin de prendre en compte les nouvelles fonctionnalité ainsi que la possibilité pour ce dernier de se déplacer.
Le bot est géré côté contrôleur, mais s'appuie exclusivement sur le modèle pour prendre ses décisions, garantissant l'absence de dépendance à la vue.

### 8. Qualité du code, tests et résolution de bugs
Ce jalon a également été une phase intensive de nettoyage du code et de corrections de bugs.

**Ont été réalisés :**
- Suppression des méthodes intuiles
- Réduction du nesting excessig
- Réorganisation stricte des classes (en suivant l'ordre respectif : champs, constructeurs, getters, méthodes publiques/protected, méthodes privées)
- Relecture et correction de la Javadoc
- Revue de tous les tickets "A revoir"

**Tests**
- Finalisation des tests unitaires
- Ajout des tyests de performance
- Session de jeu intensive pour debug manuel 

### 9. Revue comlpète de l'UML et de la documentation
Tous les diagrammes UML ont été revus, corrigés et mis en cohérence avec l'implémentation réelle.
La documentation (DESIGN, SPECIFICATIONS, etc.) a été mise à jour pour refléter fidèlement l'état du projet