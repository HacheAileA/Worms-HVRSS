# CHANGELOG

### Récapitulatif des versions

| Version |    Date    |
|:-------:|:----------:| 
| 0.0     | 18-10-2025 |
| 1.0     | 11-11-2025 |
| 1.1     | 01-12-2025 |
| 2.0     | 28-12-2025 |
| 2.1     | 12-01-2025 |


========================
========================
| Version |    Date    |
|:-------:|:----------:| 
| 0.0     | 18-10-2025 |

---
# Status


### Organisation basique
>   - [AUTHORS](./AUTHORS.md)
>   - [CONTRIBUTING](./CONTRIBUTING.md)
>   - [CHANGELOG](./CHANGELOG.md)
>   - [README](./README.md)
>   - [DESIGN](./DESIGN.md)
>   - [LICENSE](./LICENSE)
>   - Board et labels GitLab

### Fichier de maintence (CI)
>   - [`.gitignore`](./.gitignore)
>   - [`runtest.sh`](./runtest.sh)
>   - [Intégration CI](./gitlab-ci.yml) avec Gradle
>   - Ajout d'un [checkstyle](./config/checkstyle/checkstyle.xml) pour que tout le monde suive la même convention


## Description

>   - Initialisation du dépôt :
>       - Création des fichiers de base (AUTHORS (dévelopeurs, enseignants), CONTRIBUTING (initialisation et méthodes de contibution au projet), CHANGELOG, README, LICENSE, .gitignore, .gitlab-ci.yml ...)
>   - Initialisation du dépôt Gradle et configuration de la CI
>   - Ajout des fichiers de documentation et scripts de base

## Modifications générales

>   - Réorganisation de la structure du dépôt (amélioration de la lisibilité des fichier Gradle surtout)
>   - Mise à jour du README et du DESIGN avec une structure initiale
>   - Création de toutes les classes du jeu :
>       - Worm > Team > Guns > Arsenal > GameModel, Main

========================
========================
| Version |    Date    |
|:-------:|:----------:| 
| 1.0     | 11-11-2025 |

---
# Status

## Description

>   - Mise en place de l'arborescence générale et mise à jour des tests unitaires pour chaque classe
>   - Génération/Affichage des javadocs avec Gradle
>   - Lancement du jeu avec le run.sh (présent à la racine du projet)


## Modifications générales

#### Création des méthodes d'orientation de l'utilisateur/des utilisateurs
Nous avons défini des actions déjà prédéfinies à l'utilisateur, qui sont numérotées de 1 à 5 (Déplacer; Sauter; Viser et tirer; Afficher les infos; Passer le tour)
Pour effectuer l'une d'entre elle, il lui suffit d'entrer l'entier correspondant.
Dans le cas où celui-ci/ceux-ci entre une erreur (un autre entier, un caractère, chaîne...) nous avons une méthode pour gérer ce genre de cas.
>   - turnChoice(), qui permet d'offrir le choix d'action
>   - checkIsInt(), vérifie si l'entrée est un entier
>   - incorrectChoice(), qui renvoie si le choix d'action a été correct ou non


#### Création de méthodes d'amélioration de la lisibilité de la console, lors du lancement
>   - clearConsole(), showStartGameMessage(), showEndGameMessage()
>   - sleepTime(), nexTurnInfo(), loading()


## Modifications structurelles
>   - Séparation des responsabilités --> création des répertoires (respectifs) "core" et "utils"
>       - Groupement des packages de toutes les classes permettant de de séparer les tâches (GameInitializer, InputValidator, TurnManager)
>   - **ANTICIPATION :** 
>       - Création du répertoire Display : centralisation des différents modes d'affichage (Console/2D)
>           - Classe Console : méthodes d'affichage  en mode Console
>           - Classe Display, qui sert d'interface à toutes les méthodes qui seront définies plus tard

========================
========================
| Version |    Date    |
|:-------:|:----------:| 
| 1.1     | 01-12-2025 |

---
# Status

## Description

>   - Implémentation complète de la vue ASCII permettant d'afficher
>       - Le terrain (sol, ciel, eau)
>       - Les vers des équipes
>       - Les informations concernant le tours
>   - Amélioration global du mode console (résolution de bugs)
>   - Création d'un système de carte entièrement généré aléatoirement représentée sous forme de grille ASCII colorées
>       - Gestion du terrain
>       - Gestion de l'eau
>       - Gestion des collisions
>   - Réimplémentation des déplacements :
>       - Mouvements horizontaux 
>       - Sauts
>       - Gravité (chute automatique si le sol n'est pas directement sous le worm)
>   - Mise en place d'un inventaire commun d'équipe regroupant armes et outils
>   - Ajout d'un système de sauvegarde / chargement dans un fichier texte (save.txt)
>       - Cate ASCII
>       - États des vers (dans l'ordre de jeu)
>       - Inventaire

## Modifications générales

#### Réorganisation du code vers une structure MVC
- Séparation correcte entre modèle (carte,vers,équipes,inventaire,jeu)
- La logique du jeu avec controller (gestion des tours)
- L'affichage (vue ASCII)

#### Mise à jour de la documentation
- Mise à jour du DESIGN, CHANGELOG, SPECIFICATION
- Ajout des diagrammes UML au dépôt

#### Amélioration des tests unitaires
Ajout des tests pour toutes les parties du code (à l'exception du SaveManager, LoadManager et Game). Couverture du code à hauteur de 80%

## Modifications structurelles
>   - Création de nouvelles classes essentielles : 
>       - Map : Représentation du terrain
>       - Inventory : Gestion de l'inventaire comprenant des armes et des outils
>       - SaveManager/LoadManager : Gestion de la sauvegarde et du chargement de partie
>
>   - Réfactor important :
>       - Séparation d'anciennement ('core', 'utils' et 'display') en une MVC pure ('model', 'view', 'control', 'app')


>   - **ANTICIPATION :** 
>       - Début de réflexion sur la partie graphique (classes à implémenter et potentielle répartition des tâches)

========================
========================
| Version |    Date    |
|:-------:|:----------:| 
| 2.0     | 28-12-2025 |

---
# Status

## Description

>   - Mise en place de la version graphique fonctionnelle (GuiView + package `view/gui`)
>       - Création et utilisation de différents menus et d'une barre de menu
>       - Affichage d'une carte (terre, eau, air, herbe)
>       - Affichage des worms (HUD)
>       - Affichage de l'inventaire de l'équipe (HUD)

## Modifications générales

#### Réorganisation du code vers une structure MVC (v2)
- Reprise de l'ancienne MVC et ajustement

#### Mise à jour de la documentation
- Correction de tous les fichiers UML
- Correction du CHANGELOG
- Complétion du rapport (.text et .pdf)

#### Finition des tests unitaires restants
- Correction des tests unitaires ayant été laissé de côté

## Modifications structurelles
>   - Corrections des classes pour coller à une vraie structure MVC
>   - Créations du dossier `resources` pour stocker les images des tuiles, worms et outils + armes

>   - **ANTICIPATION :** 
>       - Images pré-sélectionnées en local
>       - Sons pré-sélectionnés en local

========================
========================
| Version |    Date    |
|:-------:|:----------:| 
| 2.1     | 12-01-2025 |

---

# Status

## Description

>   - Finalisation et intégration complète de nombreuses fonctionnalités majeures :
>       - Système de caméra dynamique en version GUI
>       - Possibilité de passer le tour en version graphique
>       - Ajout du vent (optionnel, disponible dans les réglages) influençant les projectiles
>       - Implémentation de dégâts de zone
>       - Ajout de nouvelles armes et nouveaux outils
>       - Ajout de paramétrages avancés (durée du tour, friendly fire, bot, choix de la map etc.)
>       - Génération de cartes variées (différents types de terrains)
>       - Ajout de sons
>       - Complétion de l'implémentation du bot
>       - Ajout d'animation (particules lors du saut ou tir)
>       - Lissage des déplacements des worms (plus du case par case)
>   - Phase de stabilisation générale du projet :
>       - Relecture complète du code
>       - Corrections de bugs
>       - Amélioration des performances (ainsi que les tests qui vont avec)
>       - Mise à jour complète de la documentation et des UML

## Modifications générales

#### Amélioration majeures du gameplay
- Ajout du vent (activable ou non dans les paramètres) impactant les trajectoires
- Implémentationd es dégâts de zone pour certaines armes
- Ajout de nouvelles armes (grenade) et rééquilibrage de celles déja présentes
- Ajout de nouveaux outils (tp aléatoire, frappe aérienne)
- Ajout de paramètres avancés
    - Durée des tours
    - Friendly fire
    - Bot ou non
    - Choix de la map

#### Version graphique (GUI)
- Implémentation complète du système de caméra : 
    - Changement de position vers le worms actif au changement du tour
    - Zoom de la carte et mise en plein écran du jeu
- Ajout de la possibilité de passer le tour via l'interface graphique
- Ajout d'animations (particules)
- Amélioration globale des graphismes
- Ajout de sons (ambiance, sound effect)

#### Génération et gestion des cartes
- Ajout de plusieurs types de génération de cartes (toujours aléatoire):
    - Terrain plus plats comportant un pont
    - Terrain avec grottes
    - Terrain avec des îles
- Amélioration de la gestion des collisions et de la cohérence du terrain.
- Complétion du bot pour s'adapter aux différentes nouvelles feature (notamment le vent, les crates et l'équilibrage des armes) ainsi que la possibilité de se déplacer

#### Améliorationd es déplacements
- Lissage des déplacements des worms
- Amélioration de la fluidité des sauts et chutes
- Meilleure gestion de la gravité et des transitions (worms qui tir en l'air, fin des tours ou destruction du terrain)

#### Maintenance et qualité du code
- Revue complète de tous les tickets avec le label "A revoir"
- Repassage exhaustif sur la version console pour corriger des bugs étant survenu après l'implémentation GUI
- Nettoyage du code : 
    - Suppression des méthodes inutiles
    - Suppression du nesting excessif
    - Réorganisation stricte des classes (dans l'ordre respectif suivant : champs, constructeurs, getters, méthodes publiques/protected, méthodes privées)
- Relecture et correction de la Javadoc
- Completion des tests : 
    - Ajout et finalisation des tests unitaires
    - Ajout de tests de performance
    - Phase de jeu intensif pour debug (tests manuels)
- Révision complète des diagrammes UML
- Mise à jour de la documentation associée (DESIGN, SPECIFICATION, ETC.)

## Modifications structurelles
>   - Ajustements des packages liés à la vue pour supporter caméra, animations et osns
>   - Ajustements du modèle pour intégrer :
>       - Vent
>       - Dégâts de zone
>       - Paramètres avancés
>       - Nouvelles armes et outils
>   - Renforcement de la cohérence de la structure MVC