# Cahier des Charges 

## 1. Présentation du projet   1j
- **1.1. Contexte**
  - Projet universitaire réalisé dans le cadre des UE de POO et de Conduite de Projet
- **1.2. Objectifs du projet**
  - Développer un jeu Worms-like en Java 2D
  - Appliquer ue méthodologie Agile et utiliser GitLab pour la gestion du code, des tâches et des versions.

---

## 2. Description générale du jeu  3j
- **2.1. Concept global**
    - Deux équipes de vers s'affrontent sur un terrain 2D destructible. Dans ce jeu tour par tour, chaque ver peut se déplacer, viser et tirer avant la fin de son temps imparti.
    - L'eau est une zone mortelle : un ver qui y tombe est éliminé.
    - Rotation du ver actif (sélection automatique, ou manuelle via interface)
    - Tour se terminant automatiquement à la fin du temps

- **2.2. Principes de jeu (gameplay)**
    - Physique simple  avec gravitée verticale constante et vent horizontal optionnel.
    - Collisions avec le sol et l'eau.
    - Dégâts et élimination des vers à 0pv ou si tombés dans l'eau
    - Interruption du projectile dès contact terrain/eau (explosion et dégâts à la collision ou après délais selon l'arme)
    - Victoire lorsque tous les vers des autres équipes sont éliminés

- **2.3. Références et inspirations**
    - Worms (Team17) - version classique.

---

## 3. Spécifications fonctionnelles  9j
- **3.1. Fonctionnalités principales**
    - Système de tour par tour
    - Déplacement, sauts, tirs des/par les vers
    - Gestion des points de vie et élimination.

- **3.2. Mécaniques de jeu (déplacement, tir, dégâts, etc.)**
    - Gravité verticale constante.
    - Vent optionnel influençant la trajectoire des projectiles.
    - Collisions avec le terrain et l'eau.

- **3.3. Gestion des armes et des objets**
    - Arsenal limité par équipe (bazooka, sniper etc...)
    - Gestion des munitions et effets (explosions, temporisation, dégâts de zone, etc)

- **3.4. Interface utilisateur (menus, HUD, etc.)**
    - Ecran d'accueil (nouvelle partie, charger une partie, options, quitter)
    - Ecran de jeu (terrain (ciel, sol, eau), HUD, jauge de temps, inventaire (commun à toute l'équipe avec diminution de munitions à chaque utilisation), scrolling de la carte)

- **3.5. Système de sauvegarde / chargement**
    - Sauvegarde locale en fichier texte (état du terrain, vers, inventaires, tour courant)
    - Format texte lisible
    - Durée restante du tour si possibilité de sauvegarde en plein milieu

- **3.6. Options et paramètres**
    - Volume sonore
    - Contrôles (clavier/souris)
    - Difficulté (temps par tour, IA)

- **3.7. Modes de jeu (solo, multijoueur, etc.)**
    - Mode solo contre IA
    - Mode multijoueur local (hotseat)
    - Mode "Ranked" avec classement en ligne et des armes prédéfinies

- **3.8. Fonctionnalitées optionnelles**
    - Paramétrage avancé : ddurée de tour, PV initiaux, munitions, dégâts de chute, friendly fire.
    - Améliorations environnementales : vent avec indicateur, génération de cartes.
    - Armes et objets supplémentaires : ricochet, mines, tourelles, poutrelles, jet-pack, foreuse, kits de soins.
    - IA : bot basique capable de tirer et viser.
    - Progression entre manches : économie de points entre parties
    - Inventaire élaboré : caisses, crafting, inventaire par ver

--- 

## 4. Spécifications techniques  8j

- **4.1. Modes de jeu**
    - Vue textuelle (console)
        - Lancer une partie
        - Afficher le terrain et les vers et leurs états (PV, position)
        - Gérer les tours (déplacement, tir, fin de tour)
    - Vue graphique (Java Swing)

- **4.1. Langage et environnement de développement (Java, librairies, frameworks, etc.)**
    - Java SE 17+
    - Bibliothèque graphique : AWT ou Swing (pas de JavaFX)
    - Framework de test : JUnit

- **4.2. Architecture logicielle (MVC, entités, moteur physique, etc.)**
    - Modèle MVC : Séparation claire entre modèle (logique de jeu), vue (interface graphique) et contrôleur (gestion des entrées utilisateur)

- **4.3. Outils de build et CI/CD**
    - Gradle pour compilatione t dépendances
    - GitLab CI pour intégration continue et déploiement

- **4.4. Compatibilité**
    - Plateformes : Windows, macOS, Linux
    
---

## 5. Spécifications graphiques et sonores  5j
- **5.1. Style graphique**
    - Sprites simples

- **5.2. Ressources**
    - Ciel, sol, eau, vers, projectiles

- **5.3. Animations**
    - Déplacement et tir du ver, explosions.

- **5.4. Effets sonores et musique**
    - Bruitages pour tirs, explosions, déplacements.
    - Musique de fond optionnelle.

---

## 6. Spécifications ergonomiques  3j
- **6.1. Commandes et contrôles**
    - Clavier : flèches directionnelles (déplacement)
    - Souris : sélection d'armes, visée, puissance, tir

- **6.2. Lisibilité de l’interface**
    - Affichage clair des PV, jauge de temps et équipes actives

---

## 7. Architecture logicielle et organisation du code  3j
- **7.1. Structure du projet**
    - /src/model : clases du modèle (Terrain, Ver, Projectile, Arme, etc...)
    - /src/view : vues Swing et console
    - /src/controller : gestion des interactions
    
- **7.2. Gestion des dépendances**
    - Gradle (build.gradle)

---

## 8. Livrable et méthodologie  9j
- **8.1. Livrables attendus**
    - Contenu du dépît GitLab (sources, .gitignore, README, rapport, sauvegarde.txt (une partie avancée))
    - Contenu obligatoire du rapport PDF : résumé, problèmes, modèle de classes UML, bibliothèques externes
    - Documentation obligatoire :
        - Javadoc pour toutes les classes publiques
        - Fichiers : CHANGELOG.md, DESIGN.md, AUTHORS.md, LICENSE (MIT)

- **8.2. Méthodologie "Conduite de Projet"**
    - Méthodologie Agile / Scrum : backlog, sprints, revues
    - Gestion GitLab : issues, labels, milestones, boards
    - Workflow Git : branches feature/bugfix, merge requests, revue obligatoires
    - Tests automatisés (boite blanche): JUnit obligatoire, couverture de code, tests de robustess et de performance valorisés
    - Tests automatisés (boite noire) : tests fonctionnels, compilation automatisées (Gradle), pipeline GitLab, release à chaque sprint.
---

## 9. Annexes
- **9.1. Références techniques et bibliographiques**
  - [Worms Armageddon Sprites](https://www.spriters-resource.com/pc_computer/wormsgeddon/)

- **9.2. Glossaire**
    - PV : Points de Vie
    - IA : Intelligence Artificielle
    - HUD : Heads-Up Display (interface affichant les informations de jeu)
    - MVC : Modèle-Vue-Contrôleur


# ROADMAP
(pourra être sujet à des changements)
- **Jalon 0 - Semaines 1 : Pré-production**
    - Objectifs : 
        - Cahier des charges
        - Architecture globale
        - Environnement de développement opérationnel
    - Livrables :
        - DESIGN.md initial
        - Modèle UML v1
        - Pipeline GitLab CI fonctionnelle
        - Base du projet Gradle

- **Jalon 1 - Semaine 2-3 : Modèle & Mécaniques de base (version console minimale)**
    - Objectifs : 
        - Armes
        - Déplacements
        - Logique des tours
    - Livrables :
        - Interface console jouable minimale

- **Jalon 2 - Semaine 4-5 : Map et Sauvegarde. (version console complète)**
    - Objectifs :
        - Terrain 2D en ascii
        - Eau comme zone mortelle
        - Collision sur le terrain
        - Système de sauvegarde et chargement (fichier texte lisible)
        - Système d'inventaire avec armes / outils
    - Livrables :
        - Version console complète et fonctionnelle
        - Tests JUnit sur toute la version console
    
- **Jalon 3 - Semaine 6-7: Interface graphique minimale (Swing/awt)**
    - Objectifs :
        - Affichage du terrain (ciel/sol/eau)
        - Affichage des vers avec déplacements
        - Gestion des entrées clavier/souris pour déplacement et tir
        - Scrolling de la carte
    - Livrables :
        - Première version graphique jouable
        - Rendu swing de base + contrôles fonctionnels

- **Jalon 4 - Semaine 8-10 : Interface graphique complète**
    - Objectifs :
        - HUD complet : PV, jauge de temps, inventaire armes
        - Indicateur de vent
        - Animation simple (déplacement, explosions)
        - Menus : accueil, options, nouvelle partie / charger partie
    - Livrables :
        - Version graphique complète
        - Menus opérationnels
        - Build jouable "quasi final"

- **Jalon 5 - Semaine 11-12 : Finalisation & polish**
    - Objectifs :
        - IA basique (viser/tirer) si temps disponible
        - Correction des bugs
        - Optimisations (physique collisions, UI)
        - Documentation complète (Javadoc, rapport, README, CHANGELOG)
    - Livrables :
        - Version finale du jeu
        - Rapport PDF
        - Javadoc complète