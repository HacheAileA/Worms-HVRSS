# Guide de contribution au projet « Worms »

Avant de commencer, veuillez lire attentivement les consignes ci-dessous, ainsi que le [README](./README.md).

## Table des matières

- [Définitions](#définitions)
- [Organisation des branches](#organisation-des-branches)
- [Comment contribuer au projet ?](#comment-contribuer-au-projet)
- [Mettre à jour le projet](#mettre-à-jour-le-projet)
- [Processus de relecture et validation](#processus-de-relecture-et-validation)
- [Questions](#questions)

## Définitions

- MR -> Merge Request

## Organisation des branches

Le projet utilise une organisation stricte des branches pour garantir la stabilité et la clarté des contributions.

### Branches principales

- **main** : sert uniquement lors des releases.
- **dev** : sert à sauvegarder les modifications stables et constitue le point de bascule vers `main` pour les releases.

Ces deux branches sont **protégées** et ne doivent pas être modifiées directement par les contributeurs.

### Branches secondaires protégées

- **tests** : pour les tests unitaires et d’intégration.
- **features** : pour le développement de nouvelles fonctionnalités.
- **bugfix** : pour la correction de bugs.
- **documentation** : pour la mise à jour ou la création de documentation.

### Création d’une branche de contribution

Pour contribuer, il est nécessaire de créer une branche dérivée d’une des branches secondaires selon le type de contribution. Le nom de la branche doit respecter la convention suivante :

`<lettre>_<nom_explicit>`

- `t_` pour **tests**
- `f_` pour **features**
- `b_` pour **bugfix**
- `d_` pour **documentation**

**Exemples :**

- Ajouter des tests unitaires pour la classe `Game` : `t_game`
- Compléter la documentation du `CONTRIBUTING.md` : `d_contributing`

Cette convention permet de facilement identifier le type de contribution et de maintenir une organisation claire dans le dépôt.

## Comment contribuer au projet ?

1. **Cloner** le dépôt. Dans le terminal, se placer dans le dossier souhaité et exécuter la commande suivante :

2. Créer un **ticket** pour proposer votre suggestion, ou traiter un ticket existant. N'oubliez pas d'y ajouter les bons labels et jalon.

3. Créer une **branche** dédiée à vos modifications :

   ```bash
   git checkout -b nom-de-la-branche
   ```

4. Faire des **modifications** et **ajouter** le fichier :

   ```bash
   git add nom-du-fichier
   ```

5. Faire un ou plusieurs **commit** après chaque modification avec un message clair :

   ```bash
   git commit -m "type(fichier): message"
   ```

   exemple :

   ```bash
   git commit -m "doc(readme): Ajout du README"
   ```

6. Répéter les étapes `4` et `5` aussi souvent que possible et **pousser** la branche créée sur le dépôt :

   ```bash
   git pull origin main
   git push -u origin nom-de-la-branche
   ```

7. Ouvrez une MR vers la branche appropriée (se reporter au [README](./README.md)).

## Mettre à jour le projet

Il se peut que le projet ait été modifé et que vous n'ayez plus la dernière version. Pour cela :

1. Dans le terminal, se placer dans le dossier où le projet a été cloné.
2. Exécuter la commande suivante :
   ```bash
   git pull
   ```

## Renommer une branche

Il se peut que votre branche n'ai pas un nom approprié. Pour la renommer :

1. Dans le terminal, exécuter la commande suivante :
   ```bash
   git branch -m <NomActuel> <NouveauNom>
   ```

## Processus de relecture et validation

Pour garantir la qualité et la cohérence des contributions, **chaque MR** doit suivre le processus suivant :

1. **Relecture initiale**

   - Dès qu’une MR est soumise, elle est assignée à un ou plusieurs relecteurs.

2. **Critères d’approbation**

   Une MR ne peut être fusionnée que si :

   - Tous les relecteurs ont approuvé la MR (et sont au moins au nombre de 2).
   - La MR passe les tests d'intégration (ne produit ni erreur ni warning).
   - Les modifications respectent les **contraintes techniques** et les **standards du projet**.
   - Les éventuels **commentaires** ou **demandes de modification** ont été pris en compte et corrigés.

3. **Fusion (merge)**

   - Une fois approuvée, la MR peut être fusionnée dans la branche appropriée.
   - La personne qui fusionne la MR doit vérifier que **tous les tests ou vérifications locales passent** avant de finaliser le merge.

4. **Délais de relecture**

   - Chaque MR doit être revue dans un délai **raisonnable** après sa soumission.
   - Les contributions urgentes ou critiques peuvent bénéficier d’un traitement accéléré.

5. **Communication**
   - Les relecteurs doivent **laisser des commentaires constructifs** et clairs.
   - En cas de désaccord, une **discussion** sur la MR permet de trouver un consensus avant la fusion.

```
MR soumise → Relecteurs assignés → Relecture & commentaires
                                 ↓
                              Validée ?
                            ↙          ↘
                        Non             Oui
                    Commentaires  Merge dans main
```

## Questions

Si vous avez une question, vérifiez d’abord que la réponse ne se trouve pas dans le [README](./README.md), la **documentation du projet**, ou dans un **ticket**.

Si vous n'avez pas de réponse à votre question, ouvrez un **ticket** sur le dépôt en décrivant clairement votre problème ou demande.

N’hésitez pas à participer aux discussions pour clarifier ou enrichir les réponses.
