package controller.bot;

import model.GameModel;
import model.items.crates.Crate;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;
import view.gui.GuiView;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe décisionnaire du bot.
 * Lui permet de svoir quoi faire et en quelle circonstance.
 * 
 * @author MESNILDREY Valentin
 * @since 2.0
 * @version 2.1
 */
public class BotDecisionEngine {
    private final int difficulty;
    private final GuiView view;
    private static final double CRATE_COLLECTION_THRESHOLD = 100.0;
    private static final double HEALTH_THRESHOLD = 50.0;
    private static final double CRITICAL_HEALTH = 30.0;

    private static final double SHOTGUN_MAX_RANGE = 8.0;
    private static final double BAZOOKA_EFFECTIVE_RANGE = 80.0;
    private static final double GRENADE_EFFECTIVE_RANGE = 120.0;
    private static final double SNIPER_EFFECTIVE_RANGE = 200.0;

    /**
     * Constructeur pour décider quelle action le bot doit effectuer
     * @param difficulty le niveau du bot (plus c'est élevé et plus le bot sera fort)
     * @param view la vue (pour que le bot "voit" le terrain)
     * 
     * @since 2.0
     */
    public BotDecisionEngine(int difficulty, GuiView view) {
        this.difficulty = difficulty;
        this.view = view;
    }

    /**
     * Méthode pour prendre les décisions principales du bot.
     * <p>
     * Cette méthode représente le "cerveau" du bot. Elle évalue l'état actuel du
     * jeu, les conditions du bot, son environnement et les ressources disponibles
     * pour choisir l'action {@link BotAction} la plus appropriée à effectuer pour le tour.
     * <p>
     * Le processus est strictement ordonné par priorité, suivant une stratégie :
     * <ol>
     * <li><b>Survie d'urgence</b>: se soigner ou échappter si le bot est en situation critique.</li>
     * <li><b>Gestion des dangers immédiats</b>: se téléporte si le bot a un risque de 
     * tomber dans l'eau.</li>
     * <li><b>Sélection de la cible</b>: Choisi un worm ennemie.</li>
     * <li><b>Outils à haut impact</b>: utilise les attaques aériennes quand c'est possible.</li>
     * <li><b>Résolution du combat</b>: Sélectionne l'arme la plus appropriée et tente de tirer.</li>
     * <li><b>Repositionnement</b>: Se déplace pour obtenir une ligne de tir plus dégagée ou se rapprocher
     * de la cible.</li>
     * <li><b>Gestion des ressources</b>: Se soigne ou récupère des crates si aucune action offensive n'est possible.</li>
     * </ol>
     * 
     * Détail de la stratégie
     * <ul>
     * <li>
     * Si la vie du bot est en dessous du seuil critique, il tente de se soigner.
     * </li>
     * <li>
     * Si le bot est détecté comme étant en danger immédiat (eau, risque de chute), il peut utiliser un
     * téléport en fonction du niveau de difficulté.
     * </li>
     * <li>
     * Le bot sélectionne une cible en utilisant {@link #chooseTarget(GameModel, Worm)}. Si aucune
     * cible n'est disponible, il switch à un comportement passif en utilisant 
     * {@link #handleNoCombatSituation(GameModel, Worm, String)}.
     * </li>
     * <li>
     * Si un air strike est disponible et que la map le permet, le bot peut l'utiliser
     * </li>
     * <li>
     * Le bot sélectionne l'arme la plus appropriée pour la distance à la cible avec
     * {@link #chooseBestGunForDistance(Worm, Worm, Team, GameModel, double)}.
     * </li>
     * <li>
     * Pour les armes balistiques, le bot calcule un angle de tir en utilisant
     * {@link #computeAngle(Worm, Worm, Guns, GameModel, boolean)}
     * et vérifie les obstacles sur la trajectoire. Il bascule dynamiquement entre
     * des trajectoires hautes et basses pour maximiser la probabilité de toucher.
     * </li>
     * <li>
     * Si pas de tir direct n'est possible, le bot tente de repositionner avec
     * {@link #findClearShotPosition(GameModel, Worm, Worm, Guns, String)}.
     * </li>
     * <li>
     * Si repositionnement échoue, le bot peut utiliser des armes destructrices pour casser
     * les obstacles du terrain.
     * </li>
     * <li>
     * Si aucune action offensive n'est possible, le bot planifie un mouvement vers la
     * cible en utilisant
     * {@link #planMovementToTarget(GameModel, Worm, Worm, String, double)}.
     * </li>
     * <li>
     * Finallment, si le combat et le mouvement ne sont pas viables, le bot priorise le soint
     * et la collecte de crates
     * </li>
     * </ul>
     *
     * <p>
     * La méthode retourne la première {@link BotAction} valide trouvée dans cet ordre de priorité.
     * Si aucune action n'est possible, elle retourne null indiquant que le bot passe son tour.
     * </p>
     *
     * @param model Le modèle courrant utilisé pour la partie
     * @param self  Le worm contrôlé par le bot
     * @return la {@link BotAction} sélectionnée pour être exécutée (ou null si pas d'action possible)
     */
    public BotAction decide(GameModel model, Worm self) {
        if (!self.getTeam().isBot()) {
            return null;
        }

        String mapType = model.getMap().getMapType();

        if (self.getHp() < CRITICAL_HEALTH) {
            Tools healthPack = findHealthPack(self.getTeam());
            if (healthPack != null) {
                return new UseToolAction(healthPack, self);
            }
        }

        if (isInImmediateDanger(model, self)) {
            Tools teleport = findRandomTeleport(self.getTeam());
            if (teleport != null && Math.random() < (difficulty / 10.0)) {
                return new UseToolAction(teleport, self);
            }
        }

        Worm target = chooseTarget(model, self);
        if (target == null) {
            return handleNoCombatSituation(model, self, mapType);
        }

        double distanceToTarget = getDistance(self.getX(), target.getX(), self.getY(), target.getY());

        Tools airStrike = findAirStrike(self.getTeam());
        if (airStrike != null && canUseAirStrike(model, self, target, mapType)) {
            return new UseToolAction(airStrike, self, (int) target.getX());
        }

        Guns gun = chooseBestGunForDistance(self, target, self.getTeam(), model, distanceToTarget);

        if (gun != null) {
            boolean useHighTrajectory = true;
            boolean isCloseRange = distanceToTarget <= SHOTGUN_MAX_RANGE * 1.5;

            if (isCloseRange && gun.getGravity() > 0.01) {
                double lowAngle = computeAngle(self, target, gun, model, false);
                if (lowAngle != 0 && !isTrajectoryBlocked(model, self, target, gun, lowAngle)) {
                    useHighTrajectory = false;
                }
            }

            double angle = computeAngle(self, target, gun, model, useHighTrajectory);

            if (angle != 0) {
                if (!isTrajectoryBlocked(model, self, target, gun, angle)) {
                    return new ShootAction(gun, angle, view);
                }

                if (useHighTrajectory && gun.getGravity() > 0.01) {
                    double lowAngle = computeAngle(self, target, gun, model, false);
                    if (lowAngle != 0 && !isTrajectoryBlocked(model, self, target, gun, lowAngle)) {
                        return new ShootAction(gun, lowAngle, view);
                    }
                }

                BotAction clearShotAction = findClearShotPosition(model, self, target, gun, mapType);
                if (clearShotAction != null) {
                    return clearShotAction;
                }

                Guns destructiveGun = findDestructiveGun(self.getTeam(), self, target, model);
                if (destructiveGun != null && isWeaponInRange(destructiveGun, distanceToTarget)) {
                    angle = computeAngle(self, target, destructiveGun, model, true);
                    if (angle != 0 && !isTrajectoryBlocked(model, self, target, destructiveGun, angle)) {
                        return new ShootAction(destructiveGun, angle, view);
                    }
                }
            }
        }

        BotAction moveAction = planMovementToTarget(model, self, target, mapType, distanceToTarget);
        if (moveAction != null) {
            return moveAction;
        }

        if (self.getHp() < HEALTH_THRESHOLD) {
            Tools healthPack = findHealthPack(self.getTeam());
            if (healthPack != null) {
                return new UseToolAction(healthPack, self);
            }
        }

        Crate nearestCrate = findNearestCrate(model, self);
        if (nearestCrate != null && shouldCollectCrate(model, self, nearestCrate, mapType)) {
            return new CollectCrateAction(nearestCrate);
        }

        return null;
    }

    private BotAction handleNoCombatSituation(GameModel model, Worm self, String mapType) {
        if (self.getHp() < HEALTH_THRESHOLD) {
            Tools healthPack = findHealthPack(self.getTeam());
            if (healthPack != null) {
                return new UseToolAction(healthPack, self);
            }
        }

        Crate nearestCrate = findNearestCrate(model, self);
        if (nearestCrate != null && shouldCollectCrate(model, self, nearestCrate, mapType)) {
            return new CollectCrateAction(nearestCrate);
        }

        return null;
    }

    private Worm chooseTarget(GameModel model, Worm self) {
        double minDistance = Double.MAX_VALUE;
        Worm target = null;

        for (Team team : model.getTeams()) {
            for (Worm worm : team.getWorms()) {
                if (worm.getTeam().getTeamId() != self.getTeam().getTeamId() && !worm.isDead()) {
                    double dx = self.getX() - worm.getX();
                    double dy = self.getY() - worm.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < minDistance) {
                        minDistance = distance;
                        target = worm;
                    }
                }
            }
        }
        return target;
    }

    private boolean isWeaponInRange(Guns gun, double distance) {
        String name = gun.getName().toLowerCase();

        if (name.contains("shotgun")) {
            return distance <= SHOTGUN_MAX_RANGE;
        } else if (name.contains("bazooka")) {
            return distance <= BAZOOKA_EFFECTIVE_RANGE;
        } else if (name.contains("grenade")) {
            return distance <= GRENADE_EFFECTIVE_RANGE;
        } else if (name.contains("sniper")) {
            return distance <= SNIPER_EFFECTIVE_RANGE;
        }

        return true;
    }

    /**
     * Sélectionne l'arme la plus appropriée à utiliser contre la cible en fonction
     * de la distance et des armes disponibles (et des potentiels obstacles)
     * <p>
     * L'arme sélectionnée suit la priorité suivante :  
     * <ul>
     * <li> Pour une distance très courte, priorité aux shotguns</li>
     * <li>Armes non destructives pour préserver le terrain</li>
     * <li>Armes destructives uniquement si nécessaire</li>
     * </ul>
     *
     * <h3>Sélection stratgiéu : </h3>
     * <ol>
     * <li>
     * <b>Shotgun priorisé à des distances faibles:</b><br>
     * Si la cible est dans la portée maximale du shotgun ({@code SHOTGUN_MAX_RANGE}),
     * le bot va priorisé cette dernière (en vérifiant au préalable qu'un angle est bien possible)
     * </li>
     * <li>
     * <b>Filtrage par portée:</b><br>
     * Toutes les armes disponibles avec munitions restantes sont filtrées en utilisant
     * {@link #isWeaponInRange(Guns, double)} pour ne garder que celles capables de toucher
     * la cible.
     * </li>
     * <li>
     * <b>Armes non destructives en premier:</b><br>
     * Parmi les armes à portée, le bot privilégie les armes non destructives pour préserver
     * leur munitions.
     * </li>
     * <li>
     * <b>Armes destructives comme dernier recours:</b><br>
     * Si aucune arme non destructrice appropriée ne peut être utilisée, les armes destructives
     * sont considérées comme un dernier recours, à condition qu'un angle de tir valide puisse être calculé.
     * </li>
     * </ol>
     *
     * <p>
     * Pour chaque arme, un angle est calculé en utilisant 
     * {@link #computeAngle(Worm, Worm, Guns, GameModel, boolean)}.
     * Une arme n'est considérée utilisable que si un angle valide peut être calculé (i.e.
     * le tir est physiquement réalisable).
     * </p>
     *
     * <p>
     * Cette méthode retourne la première arme qui satisfait toutes les contraintes 
     * selon cette priorité.
     * </p>
     *
     * @param self     Le worm controllé par le bot
     * @param target   La cible ennemie
     * @param team     La team du bot pour accéder à son inventaire
     * @param model    Le modèle de jeu actuel, requis pour le calcul de trajectoire
     * @param distance La distance horizontale entre le bot et la cible
     * @return L'arme {@link Guns} sélectionnée
     */
    private Guns chooseBestGunForDistance(Worm self, Worm target, Team team, GameModel model, double distance) {
        List<Guns> availableGuns = team.getInventory().getAvailableGuns();

        if (distance <= SHOTGUN_MAX_RANGE) {
            for (Guns gun : availableGuns) {
                if (gun.getAmmo() > 0 && gun.getName().toLowerCase().contains("shotgun")) {
                    double angle = computeAngle(self, target, gun, model, true);
                    if (angle != 0) {
                        return gun;
                    }
                }
            }
        }

        List<Guns> inRangeGuns = new ArrayList<>();
        for (Guns gun : availableGuns) {
            if (gun.getAmmo() > 0 && isWeaponInRange(gun, distance)) {
                inRangeGuns.add(gun);
            }
        }

        for (Guns gun : inRangeGuns) {
            if (!gun.isCanDestruct()) {
                double angle = computeAngle(self, target, gun, model, true);
                if (angle != 0) {
                    return gun;
                }
            }
        }

        for (Guns gun : inRangeGuns) {
            if (gun.isCanDestruct()) {
                double angle = computeAngle(self, target, gun, model, true);
                if (angle != 0) {
                    return gun;
                }
            }
        }

        return null;
    }

    /**
     * Calcul l'angle requis pour toucher une cible avec une arme fournie.
     * Prends en compte : la vitesse du projectile, la gravité, sa position relative
     * et la trajectoire voulue (trajectoire haute ou tendue).
     * <p>
     * Cette méthode implémente un calcul de trajectoire balistique basé sur les
     * équations classiques de la physique du mouvement projectile vu en spé physique en terminale
     * Elle prend en charge les trajectoires hautes et basses, et s'adapte dynamiquement aux armes
     * affectées ou non par la gravité.
     *
     * <h3>Modèle physique</h3>
     * <p>
     * Pour les armes affectées par la gravité, l'équation balistique suivante est utilisée :
     * </p>
     * 
     * <pre>
     * tan(θ) = (v² ± √(v⁴ − g (g x² + 2 y v²))) / (g x)
     * </pre>
     * 
     * où:
     * <ul>
     * <li><b>v</b> est la vitesse initiale du projectile</li>
     * <li><b>g</b> est le facteur de gravité de l'arme</li>
     * <li><b>x</b> est la distance horizontale vers la cible</li>
     * <li><b>y</b> est la distance verticale vers la cible</li>
     * <li><b>θ</b> est l'angle de tir</li>
     * </ul>
     *
     * <h3>Sélection de trajectoire</h3>
     * <ul>
     * <li>
     * Si {@code preferHighTrajectory} est {@code true}, la méthode sélectionne la
     * solution d'arc plus élevé
     * (angle plus grand), ce qui est utile pour tirer au-dessus d'obstacles.
     * </li>
     * <li>
     * Si {@code preferHighTrajectory} est {@code false}, la trajectoire plus basse et plate
     * est sélectionnée,
     * </li>
     * </ul>
     *
     * <h3>Cas spéciaux</h3>
     * <ul>
     * <li>
     * Si le facteur de gravité de l'arme est négligeable (par exemple, bazooka ou shotgun), la
     * méthode délègue
     * à {@link #computeStraightAngle(Worm, Worm, GameModel)}.
     * </li>
     * <li>
     * Si le discriminant est négatif, aucune solution balistique réelle n'existe et la
     * méthode retourne {@code 0}.
     * </li>
     * <li>
     * Si la distance horizontale est nulle, le calcul est interrompu pour éviter
     * la division par zéro.
     * </li>
     * </ul>
     *
     * <h3>Difficulté : </h3>
     * <p>
     * Pour simulet un visée imparfaite et rendre l'IA plus humaine, un facteur erreur est appliqué
     * en fonction de la difficulté.
     * </p>
     *
     * <p>
     * L'angle final est retourné en radians.
     * Une valeur de retour de {@code 0} indique qu'aucun angle valide n'a pu être calculé.
     * </p>
     *
     * @param self                 Le worm controllé par le bot
     * @param target               La cible
     * @param gun                  L'arme utilisée pour calculer la trajectoire
     * @param model                Le modèle de jeu actuel
     * @param preferHighTrajectory {@code true} pour favoriser une trajectoire haute,
     *                             {@code false} pour des tirs bas et directs (arme sans gravité)
     * @return l'angle calculé en radian ou 0 si aucune solution valide n'existe
     */
    private double computeAngle(Worm self, Worm target, Guns gun, GameModel model, boolean preferHighTrajectory) {
        if (target == null || gun == null) {
            return 0;
        }

        double dx = target.getX() - self.getX();
        double dy = self.getY() - target.getY();
        double v = gun.getProjectileSpeed();
        double g = gun.getGravity();

        if (g < 0.01) {
            return computeStraightAngle(self, target, model);
        }

        g = g * 15;

        double v2 = v * v;
        double v4 = v2 * v2;
        double discriminant = v4 - g * (g * dx * dx + 2 * dy * v2);

        if (discriminant < 0) {
            return 0;
        }

        double sqrtDisc = Math.sqrt(discriminant);

        double numerator;
        if (preferHighTrajectory) {
            numerator = v2 + sqrtDisc;
        } else {
            numerator = v2 - sqrtDisc;
        }

        double denominator = g * Math.abs(dx);

        if (denominator == 0) {
            return 0;
        }

        double tanTheta = numerator / denominator;
        double angleRad = Math.atan(tanTheta);

        double finalAngle;
        if (dx < 0) {
            finalAngle = angleRad - Math.PI / 2;
        } else {
            finalAngle = Math.PI / 2 - angleRad;
        }

        double maxError = Math.toRadians(3);
        double error = maxError * (1.0 - difficulty / 10.0);
        error *= (Math.random() * 2 - 1);
        finalAngle += error;

        return finalAngle;
    }

    private double computeStraightAngle(Worm self, Worm target, GameModel model) {
        double dx = target.getX() - self.getX();
        double dy = self.getY() - target.getY();

        double angleRad = Math.atan2(dy, Math.abs(dx));

        double finalAngle;
        if (dx < 0) {
            finalAngle = angleRad - Math.PI / 2;
        } else {
            finalAngle = Math.PI / 2 - angleRad;
        }

        double maxError = Math.toRadians(2);
        double error = maxError * (1.0 - difficulty / 10.0);
        error *= (Math.random() * 2 - 1);
        finalAngle += error;

        return finalAngle;
    }

    /**
     * Tente de trouver une position proche depuis laquelle le bot peut obtenir un tir
     * sans obstruction sur la cible donnée.
     * <p>
     * Cette méthode est utilisée lorsque le bot ne peut pas tirer directement sur la cible
     * depuis sa position à cause d'obstacles / trajectoire bloqué.
     * Il explore alors les positions proches et évalue si s'y déplacer permettrait un tir valide.
     *
     * <h3>Stratégie de recherche</h3>
     * <p>
     * La méthode effecture une recherche directionnelle à partir de la position actuelle du bot : 
     * </p>
     * <ul>
     * <li>
     * Premièrement, il cherche dans la direction de la cible
     * </li>
     * <li>
     * Si c'est pas possible, il cherche dans la direction opposée
     * </li>
     * </ul>
     * <p>
     * Pour chaque direction, la méthode vérifie les positions en incrémentant la distance à
     * une portée maximale de 15
     * </p>
     *
     * <h3>Validation de la position</h3>
     * <p>
     * Pour chaque position horizontale :
     * </p>
     * <ul>
     * <li>
     * Le niveau du sol est déterminé en utilisant
     * {@link #findGroundLevel(GameModel, int, int)}.
     * </li>
     * <li>
     * La position est validée en utilisant {@link #isPositionSafe(GameModel, int, int)}
     * afin de s'assurer que
     * le bot ne se déplace pas dans l'eau.
     * </li>
     * </ul>
     *
     * <h3>Validation du tir</h3>
     * <p>
     * Une fois une position sûre trouvée, la méthode évalue si un tir valide peut
     * être effectué depuis
     * cette position :
     * </p>
     * <ul>
     * <li>
     * Si l'arme est affectée par la gravité, elle tente d'abord une trajectoire basse.
     * </li>
     * <li>
     * Si la trajectoire basse n'est pas viable, elle tente une trajectoire haute.
     * </li>
     * <li>
     * Pour chaque tentative,
     * {@link #computeAngleFromPosition(int, int, Worm, Guns, GameModel, boolean)}
     * est utilisé pour calculer l'angle de tir, et
     * {@link #wouldBeBlockedFromPosition(GameModel, int, int, Worm, Guns, double)}
     * est utilisé pour vérifier que le chemin du projectile n'est pas obstrué.
     * </li>
     * </ul>
     *
     * <h3>Repositionnement</h3>
     * <p>
     * Si un tir clair est trouvé à partir d'une position candidate, la méthode retourne une
     * {@link MoveAction} qui :
     * </p>
     * <ul>
     * <li>
     * Se déplace dans la direction appropriée
     * </li>
     * <li>
     * Calcule une durée de déplacement proportionnelle à la distance, limitée pour éviter
     * un déplacement excessif.
     * </li>
     * <li>
     * Déclenche un saut si un obstacle est détecté sur le chemin.
     * </li>
     * </ul>
     *
     * @param model   Le model de jeu actuel
     * @param self    Le worm controllé par le bot
     * @param target  La cible
     * @param gun     l'arme que le bot souhaite utiliser
     * @param mapType le type de la carte actuelle (utilisé indirectement pour les contraintes de déplacement et de sécurité)
     * @return une {@link BotAction} indiquant le bot de se déplacer vers une position avec un tir clair,
     */
    private BotAction findClearShotPosition(GameModel model, Worm self, Worm target, Guns gun, String mapType) {
        int selfX = (int) self.getX();
        int selfY = (int) self.getY();

        int targetX = (int) target.getX();
        int direction = targetX > selfX ? 1 : -1;

        for (int distance = 1; distance <= 15; distance++) {
            int testX = selfX + (direction * distance);

            if (testX < 0 || testX >= model.getMap().getWidth()) {
                break;
            }

            int testY = findGroundLevel(model, testX, selfY);
            if (testY < 0) {
                continue;
            }

            if (isPositionSafe(model, testX, testY)) {
                boolean foundClearShot = false;

                if (gun.getGravity() > 0.01) {
                    double lowAngle = computeAngleFromPosition(testX, testY, target, gun, model, false);
                    if (lowAngle != 0 && !wouldBeBlockedFromPosition(model, testX, testY, target, gun, lowAngle)) {
                        foundClearShot = true;
                    }
                }

                if (!foundClearShot) {
                    double highAngle = computeAngleFromPosition(testX, testY, target, gun, model, true);
                    if (highAngle != 0 && !wouldBeBlockedFromPosition(model, testX, testY, target, gun, highAngle)) {
                        foundClearShot = true;
                    }
                }

                if (foundClearShot) {
                    boolean needJump = hasObstacleInPath(model, selfX, selfY, testX);
                    double moveDuration = Math.min(distance * 0.05, 0.4);
                    return new MoveAction(direction, moveDuration, needJump);
                }
            }
        }

        direction = -direction;
        for (int distance = 1; distance <= 15; distance++) {
            int testX = selfX + (direction * distance);

            if (testX < 0 || testX >= model.getMap().getWidth()) {
                break;
            }

            int testY = findGroundLevel(model, testX, selfY);
            if (testY < 0) {
                continue;
            }

            if (isPositionSafe(model, testX, testY)) {
                boolean foundClearShot = false;

                if (gun.getGravity() > 0.01) {
                    double lowAngle = computeAngleFromPosition(testX, testY, target, gun, model, false);
                    if (lowAngle != 0 && !wouldBeBlockedFromPosition(model, testX, testY, target, gun, lowAngle)) {
                        foundClearShot = true;
                    }
                }

                if (!foundClearShot) {
                    double highAngle = computeAngleFromPosition(testX, testY, target, gun, model, true);
                    if (highAngle != 0 && !wouldBeBlockedFromPosition(model, testX, testY, target, gun, highAngle)) {
                        foundClearShot = true;
                    }
                }

                if (foundClearShot) {
                    boolean needJump = hasObstacleInPath(model, selfX, selfY, testX);
                    double moveDuration = Math.min(distance * 0.05, 0.4);
                    return new MoveAction(direction, moveDuration, needJump);
                }
            }
        }

        return null;
    }

    private int findGroundLevel(GameModel model, int x, int startY) {
        for (int y = startY; y < model.getMap().getHeight() - 2; y++) {
            if (model.getMap().isGround(x, y + 1)) {
                return y;
            }
        }

        for (int y = startY; y >= 0; y--) {
            if (model.getMap().isEmpty(x, y) && model.getMap().isGround(x, y + 1)) {
                return y;
            }
        }

        return -1;
    }

    private double computeAngleFromPosition(int fromX, int fromY, Worm target, Guns gun, GameModel model,
            boolean preferHigh) {
        double dx = target.getX() - fromX;
        double dy = fromY - target.getY();
        double v = gun.getProjectileSpeed();
        double g = gun.getGravity();

        if (g < 0.01) {
            double angleRad = Math.atan2(dy, Math.abs(dx));
            return (dx < 0) ? angleRad - Math.PI / 2 : Math.PI / 2 - angleRad;
        }

        g = g * 15;
        double v2 = v * v;
        double v4 = v2 * v2;
        double discriminant = v4 - g * (g * dx * dx + 2 * dy * v2);

        if (discriminant < 0) {
            return 0;
        }

        double sqrtDisc = Math.sqrt(discriminant);
        double numerator = preferHigh ? (v2 + sqrtDisc) : (v2 - sqrtDisc);
        double denominator = g * Math.abs(dx);

        if (denominator == 0) {
            return 0;
        }

        double tanTheta = numerator / denominator;
        double angleRad = Math.atan(tanTheta);

        return (dx < 0) ? angleRad - Math.PI / 2 : Math.PI / 2 - angleRad;
    }

    private boolean wouldBeBlockedFromPosition(GameModel model, int fromX, int fromY, Worm target, Guns gun,
            double angle) {
        double g = gun.getGravity();

        if (g < 0.01) {
            return checkLineBlocked(model, fromX, fromY, (int) target.getX(), (int) target.getY());
        }

        return simulateTrajectoryFromPosition(model, fromX, fromY, target, gun, angle);
    }

    /**
     * Simule la trajectoire d'un projectile depuis une position donnée et détermine
     * si le tire serait bloqué par le terrain avant d'atteindre la cible.
     * <p>
     * Cette méthode effectue une simulation pas à pas de la physique du projectile
     *
     * <h3>Modèle physique</h3>
     * <p>
     * La simulation utilise:
     * </p>
     * <ul>
     * <li>La vitesse initiale dérivée de la vitesse du projectile de l'arme et de l'angle de tir</li>
     * <li>Gravité multiplié par un scalaire pour correspondre à la physique du jeu</li>
     * <li>Force du vent obtenue à partir du modèle de jeu</li>
     * <li>Un pas de temps fixe</li>
     * </ul>
     *
     * <h3>Processus de simulation</h3>
     * <p>
     * Pour chaque étape de simulation:
     * </p>
     * <ul>
     * <li>La vitesse du projectile est mise à jour avec la gravité et le vent</li>
     * <li>La position du projectile est mise à jour en conséquence</li>
     * <li>La distance à la cible est évaluée pour détecter un impact</li>
     * <li>Les conditions de dépassement et de sortie des limites sont vérifiées</li>
     * <li>Les collisions avec le terrain sont détectées et comptées</li>
     * </ul>
     *
     * <h3>Collision et blocage</h3>
     * <p>
     * Une trajectoire est considérée comme bloquée si:
     * </p>
     * <ul>
     * <li>Le projectile entre en collision avec le terrain trop tôt dans son vol</li>
     * <li> Le nombre de collisions avec le terrain dépasse un seuil défini</li>
     * </ul>
     * <p>
     * Le blocage est toléré pour les collisions légères avec le terrain afin de permettre des tirs obliques ou glanants, mais
     * les collisions répétées au début du vol sont traitées comme une trajectoire bloquée.
     * </p>
     *
     * <h3>Conditions d'arrêt</h3>
     * <p>
     * La simulation s'arrête précocement si:
     * </p>
     * <ul>
     * <li>Le projectile atteint la cible</li>
     * <li>Le projectile dépasse la cible horizontalement</li>
     * <li>Le projectile quitte les limites de la carte</li>
     * <li>Le projectile va trop haut ou trop bas</li>
     * </ul>
     *
     * @param model  Le modèle de jeu actuel
     * @param fromX  La position horizontale de départ du projectile
     * @param fromY  La position verticale de départ du projectile
     * @param target la cible vers laquelle le projectile est tiré
     * @param gun    l'arme utilisée, fournissant les paramètres de vitesse et de gravité
     * @param angle  l'angle de tir en radians
     * @return {@code true} si la trajectoire est bloquée par le terrain avant d'atteindre la cible,
     *         {@code false} si la trajectoire est clean
     */
    private boolean simulateTrajectoryFromPosition(GameModel model, int fromX, int fromY, Worm target, Guns gun,
            double angle) {
        double x = fromX;
        double y = fromY;
        double v = gun.getProjectileSpeed();
        double g = gun.getGravity() * 15;

        double vx = v * Math.sin(angle);
        double vy = v * Math.cos(angle);

        double windForce = model.getWind().getForce();
        double dt = 0.1;
        double targetX = target.getX();
        double targetY = target.getY();

        int terrainHits = 0;
        int maxTerrainHits = 2;

        for (int step = 0; step < 300; step++) {
            vx += windForce * dt;
            vy -= g * dt;

            x += vx * dt;
            y -= vy * dt;

            double distToTarget = Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));
            if (distToTarget < 5) {
                return false;
            }

            if ((vx > 0 && x > targetX + 10) || (vx < 0 && x < targetX - 10)) {
                return false;
            }

            int checkX = (int) Math.round(x);
            int checkY = (int) Math.round(y);

            if (checkX < 0 || checkX >= model.getMap().getWidth() ||
                    checkY < 0 || checkY >= model.getMap().getHeight()) {
                return false;
            }

            if (model.getMap().isGround(checkX, checkY)) {
                terrainHits++;

                double distTraveled = Math.sqrt(Math.pow(x - fromX, 2) + Math.pow(y - fromY, 2));
                double distToTargetFromStart = Math.sqrt(Math.pow(targetX - fromX, 2) + Math.pow(targetY - fromY, 2));

                if (distTraveled < distToTargetFromStart * 0.75) {
                    if (terrainHits > maxTerrainHits) {
                        return true;
                    }
                }
            }

            if (y > model.getMap().getHeight() + 10 || y < -50) {
                return false;
            }
        }

        return false;
    }

    private boolean hasObstacleInPath(GameModel model, int fromX, int fromY, int toX) {
        int direction = toX > fromX ? 1 : -1;

        for (int x = fromX; x != toX; x += direction) {
            if (x >= 0 && x < model.getMap().getWidth()) {
                if (model.getMap().isGround(x, fromY) || model.getMap().isGround(x, fromY + 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Détermine si une position donnée sur la carte est sûre pour que le bot l'occupe.
     * <p>
     * Une position est considérée comme sûre si elle n'expose pas le bot à des dangers immédiats tels que :
     * <ul>
     * <li>Tomber dans l'eau sans terrain soutenant au-dessus</li>
     * <li>Se tenir sur un vide ou un sol instable</li>
     * </ul>
     *
     * <h3>Check des limites</h3>
     * <p>
     * La méthode vérifie que la position n'est pas trop proche des bords horizontaux
     * ou verticaux.
     * </p>
     *
     * <h3>Détection des dangers liés à l'eau</h3>
     * <p>
     * La méthode analyse la zone autour de la position dans plusieurs directions pour détecter les tuiles d'eau :
     * </p>
     * <ul>
     * <li>A droite de la position</li>
     * <li>A gauche de la position</li>
     * <li>Dans une zone rectangulaire autour de la position</li>
     * </ul>
     * <p>
     * Pour chaque tuile d'eau détectée, la méthode vérifie s'il y a un terrain solide
     * au-dessus. Si c'est pas le cas la position n'est pas considérée sûre.
     * </p>
     *
     * <h3>Check de stabilité du sol</h3>
     * <p>
     * Enfin, la méthode vérifie qu'il y a un terrain solide directement sous la
     * position.
     * </p>
     *
     * @param model Le model de jeu
     * @param x     la coordonnée x de la position à évaluer
     * @param y     la coordonnée y de la position à évaluer
     * @return {@code true} si la position est sûre,
     *         {@code false} sinon
     */
    private boolean isPositionSafe(GameModel model, int x, int y) {
        if (x < 2 || x >= model.getMap().getWidth() - 2 || y < 0 || y >= model.getMap().getHeight() - 2) {
            return false;
        }

        if (x + 1 < model.getMap().getWidth()) {
            for (int dy = -2; dy <= 3; dy++) {
                int checkY = y + dy;
                if (checkY >= 0 && checkY < model.getMap().getHeight()) {
                    if (model.getMap().isWater(x + 1, checkY)) {
                        boolean hasGroundAbove = false;
                        for (int above = checkY - 1; above >= Math.max(0, checkY - 4); above--) {
                            if (model.getMap().isGround(x + 1, above)) {
                                hasGroundAbove = true;
                                break;
                            }
                        }
                        if (!hasGroundAbove) {
                            return false;
                        }
                    }
                }
            }
        }

        if (x - 1 >= 0) {
            for (int dy = -2; dy <= 3; dy++) {
                int checkY = y + dy;
                if (checkY >= 0 && checkY < model.getMap().getHeight()) {
                    if (model.getMap().isWater(x - 1, checkY)) {
                        boolean hasGroundAbove = false;
                        for (int above = checkY - 1; above >= Math.max(0, checkY - 4); above--) {
                            if (model.getMap().isGround(x - 1, above)) {
                                hasGroundAbove = true;
                                break;
                            }
                        }
                        if (!hasGroundAbove) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                int checkX = x + dx;
                int checkY = y + dy;
                if (checkX >= 0 && checkX < model.getMap().getWidth() &&
                        checkY >= 0 && checkY < model.getMap().getHeight()) {
                    if (model.getMap().isWater(checkX, checkY)) {
                        boolean hasGroundAbove = false;
                        for (int above = checkY - 1; above >= Math.max(0, checkY - 4); above--) {
                            if (model.getMap().isGround(checkX, above)) {
                                hasGroundAbove = true;
                                break;
                            }
                        }
                        if (!hasGroundAbove) {
                            return false;
                        }
                    }
                }
            }
        }

        if (!model.getMap().isGround(x, y + 1) && !model.getMap().isGround(x, y + 2)) {
            return false;
        }

        return true;
    }

    /**
     * Prévoie un mouvement qui rapproche le bot de sa cible tout en évitant les terrains
     * dangereux (eau)
     * <p>
     * Cette méthode est responsable des décisions de navigation à bas niveau. Elle ne
     * réalise pas de pathfinding complexe,
     * mais évalue les options de mouvement immédiates dans la direction de la cible et
     * sélectionne un mouvement sûr et
     * faisable.
     *
     * <h3>Pré-check de sécurité</h3>
     * <p>
     * Si le bot est dans une position safe actuellement, la méthode vérifie d'abord si
     * se déplacer d'un pas vers la
     * cible ne le ferait pas entrer dans l'eau ou une zone dangereuse. Si une
     * configuration d'eau est détectée sans terrain au-dessus, le mouvement est annulé.
     * </p>
     *
     * <h3>Mouvement principal</h3>
     * <p>
     * Le bot tente ensuite de se déplacer dans la direction de la cible :
     * </p>
     * <ul>
     * <li>
     * La direction est déterminé en comparant les positions horizontales du bot et de la
     * cible.
     * </li>
     * <li>
     * {@link #canMoveInDirection(GameModel, int, int, int)} est utilisé pour vérifier que le
     * mouvement est possible
     * </li>
     * <li>
     * Si un obstacle est détecté, on saute
     * </li>
     * </ul>
     *
     * <h3>Stratégie de dernier recours</h3>
     * <p>
     * Si le mouvement vers la cible n'est pas possible, la méthode essaie d'avancer la position opposée.
     * </p>
     *
     * @param model    Le model de jeu
     * @param self     le worm controllé par le bot
     * @param target   la cible
     * @param mapType  Le type de carte qui influence le type de mouvement (plus ou moins rapide)
     * @param distance la distance à la cible
     * @return {@link BotAction} représentant le mouvement prévu
     */
    private BotAction planMovementToTarget(GameModel model, Worm self, Worm target, String mapType, double distance) {
        int selfX = (int) self.getX();
        int selfY = (int) self.getY();

        if (isPositionSafe(model, selfX, selfY)) {
            int direction = target.getX() > self.getX() ? 1 : -1;

            int nextX = selfX + direction;
            if (nextX >= 0 && nextX < model.getMap().getWidth()) {
                for (int dy = -2; dy <= 3; dy++) {
                    int checkY = selfY + dy;
                    if (checkY >= 0 && checkY < model.getMap().getHeight()) {
                        if (model.getMap().isWater(nextX, checkY)) {
                            boolean hasGroundAbove = false;
                            for (int above = checkY - 1; above >= Math.max(0, checkY - 4); above--) {
                                if (model.getMap().isGround(nextX, above)) {
                                    hasGroundAbove = true;
                                    break;
                                }
                            }
                            if (!hasGroundAbove) {
                                return null;
                            }
                        }
                    }
                }
            }
        }

        int direction = target.getX() > self.getX() ? 1 : -1;

        if (canMoveInDirection(model, selfX, selfY, direction)) {
            boolean needJump = model.getMap().isGround(selfX + direction, selfY) ||
                    model.getMap().isGround(selfX + direction, selfY + 1);

            double moveDuration = 0.25;
            if (mapType.equals("cave")) {
                moveDuration = 0.2;
            } else if (mapType.equals("bridge")) {
                moveDuration = 0.22;
            }

            if (needJump) {
                moveDuration = 0.1;
            }

            return new MoveAction(direction, moveDuration, needJump);
        }

        direction = -direction;
        if (canMoveInDirection(model, selfX, selfY, direction)) {
            boolean needJump = model.getMap().isGround(selfX + direction, selfY) ||
                    model.getMap().isGround(selfX + direction, selfY + 1);
            return new MoveAction(direction, 0.15, needJump);
        }

        return null;
    }

    private boolean canMoveInDirection(GameModel model, int x, int y, int direction) {
        for (int ahead = 1; ahead <= 3; ahead++) {
            int checkX = x + (direction * ahead);

            if (checkX < 0 || checkX >= model.getMap().getWidth()) {
                return false;
            }

            for (int dy = -2; dy <= 3; dy++) {
                int checkY = y + dy;
                if (checkY >= 0 && checkY < model.getMap().getHeight()) {
                    if (model.getMap().isWater(checkX, checkY)) {
                        boolean hasGroundAbove = false;
                        for (int above = checkY - 1; above >= Math.max(0, checkY - 4); above--) {
                            if (model.getMap().isGround(checkX, above)) {
                                hasGroundAbove = true;
                                break;
                            }
                        }
                        if (!hasGroundAbove) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean isTrajectoryBlocked(GameModel model, Worm self, Worm target, Guns gun, double angle) {
        double g = gun.getGravity();

        if (g < 0.01) {
            return checkLineBlocked(model, (int) self.getX(), (int) self.getY(),
                    (int) target.getX(), (int) target.getY());
        }

        return simulateTrajectoryFromPosition(model, (int) self.getX(), (int) self.getY(), target, gun, angle);
    }

    private boolean checkLineBlocked(GameModel model, int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int steps = 0;
        int maxSteps = dx + dy;

        while (steps < maxSteps) {
            if (steps > 2) {
                if (x1 >= 0 && x1 < model.getMap().getWidth() &&
                        y1 >= 0 && y1 < model.getMap().getHeight()) {
                    if (model.getMap().isGround(x1, y1)) {
                        if (Math.abs(x1 - x2) <= 2 && Math.abs(y1 - y2) <= 2) {
                            return false;
                        }
                        return true;
                    }
                }
            }

            if (Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1) {
                return false;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }

            steps++;
        }

        return false;
    }

    private Guns findDestructiveGun(Team team, Worm self, Worm target, GameModel model) {
        for (Guns gun : team.getInventory().getAvailableGuns()) {
            if (gun.getAmmo() > 0 && gun.isCanDestruct()) {
                double angle = computeAngle(self, target, gun, model, true);
                if (angle != 0) {
                    return gun;
                }
            }
        }
        return null;
    }

    private Crate findNearestCrate(GameModel model, Worm self) {
        ArrayList<Crate> crates = model.getCrateManager().getActiveCrates();
        if (crates == null || crates.isEmpty()) {
            return null;
        }

        Crate nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Crate crate : crates) {
            if (!crate.isCollected() && crate.isOnGround()) {
                double distance = getDistance(self.getX(), self.getY(), crate.getX(), crate.getY());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = crate;
                }
            }
        }

        return nearest;
    }

    private boolean shouldCollectCrate(GameModel model, Worm self, Crate crate, String mapType) {
        double distance = getDistance(self.getX(), self.getY(), crate.getX(), crate.getY());

        if (distance > CRATE_COLLECTION_THRESHOLD) {
            return false;
        }

        int selfX = (int) self.getX();
        int crateX = crate.getX();
        int direction = crateX > selfX ? 1 : -1;

        if (!canMoveInDirection(model, selfX, (int) self.getY(), direction)) {
            return false;
        }

        double riskTolerance = difficulty / 10.0;
        return Math.random() < riskTolerance || distance < 30;
    }

    private Tools findHealthPack(Team team) {
        for (Tools tool : team.getInventory().getAvailableTools()) {
            if (tool.getName().equalsIgnoreCase("Health Pack") && tool.hasAmmo()) {
                return tool;
            }
        }
        return null;
    }

    private Tools findAirStrike(Team team) {
        for (Tools tool : team.getInventory().getAvailableTools()) {
            if (tool.getName().equalsIgnoreCase("Air Strike") && tool.hasAmmo()) {
                return tool;
            }
        }
        return null;
    }

    private Tools findRandomTeleport(Team team) {
        for (Tools tool : team.getInventory().getAvailableTools()) {
            if (tool.getName().equalsIgnoreCase("RandomTP") && tool.hasAmmo()) {
                return tool;
            }
        }
        return null;
    }

    private boolean canUseAirStrike(GameModel model, Worm self, Worm target, String mapType) {
        boolean goodPosition = self.getY() <= target.getY() + 20;

        int targetX = (int) target.getX();
        int targetY = (int) target.getY();

        boolean hasTerrainAbove = false;
        for (int y = 0; y < targetY && y < model.getMap().getHeight(); y++) {
            if (model.getMap().isGround(targetX, y)) {
                hasTerrainAbove = true;
                break;
            }
        }

        if (mapType.equals("cave") && hasTerrainAbove) {
            return false;
        }

        return goodPosition && !hasTerrainAbove;
    }

    private boolean isInImmediateDanger(GameModel model, Worm self) {
        int selfX = (int) self.getX();
        int selfY = (int) self.getY();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 3; dy++) {
                int checkX = selfX + dx;
                int checkY = selfY + dy;
                if (checkX >= 0 && checkX < model.getMap().getWidth() &&
                        checkY >= 0 && checkY < model.getMap().getHeight()) {
                    if (model.getMap().isWater(checkX, checkY)) {
                        return true;
                    }
                }
            }
        }

        if (selfY + 1 < model.getMap().getHeight() && selfY + 2 < model.getMap().getHeight()) {
            if (!model.getMap().isGround(selfX, selfY + 1) &&
                    !model.getMap().isGround(selfX, selfY + 2)) {
                return true;
            }
        }

        return false;
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}