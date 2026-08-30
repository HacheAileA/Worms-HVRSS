package model;

/**
 * Cette classe représente la carte du jeu.
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 1.1
 * 
 * @version 2.1
 */
public class Map {

    /**
     * Une matrice qui représente la carte de jeu.
     */
    public char[][] grid;

    /**
     * Type de carte généré (pour les décorations spécifiques)
     */
    private String mapType;

    /**
     * Constructeur de la classe Map.
     * 
     * @param height - La hauteur de la carte.
     * @param width - La largeur de la carte.
     * 
     * @since 1.1
     */
    public Map(int height, int width) {
        this.grid = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.grid[y][x] = ' ';
            }
        }
        this.mapType = "islands";
    }

    /**
     * Méthode qui retourne le type de la carte.
     * 
     * @return Le type de la carte.
     * 
     * @since 2.2
     */
    public String getMapType() {
        return mapType;
    }

    /**
     * Getter de la hauteur de la carte.
     * 
     * @return La hauteur de la carte.
     * 
     * @since 1.1
     */
    public int getHeight() {
        return this.grid.length;
    }

    /**
     * Getter de la largeur de la carte.
     * 
     * @return La largeur de la carte.
     * 
     * @since 1.1
     */
    public int getWidth() {
        return this.grid[0].length;
    }

    /**
     * Méthode de génération d'une carte aléatoire parmi les types disponibles.
     * Sélectionne aléatoirement entre Islands, Bridge et Cave.
     * 
     * @see #generateIslandsMap()
     * @see #generateBridgeMap()
     * @see #generateCaveMap()
     * 
     * @since 1.2
     */
    public void generateDefaultMap() {
        generateIslandsMap();
    }

    /**
     * Méthode qui définit le type de carte à générer.
     * 
     * @param type - Le type de la carte (islands, bridge, cave).
     * 
     * @since 2.2
     */
    public void setMapType(String type) {
        if (type.equals("islands") || type.equals("bridge") || type.equals("cave")) {
            this.mapType = type;
        } else {
            System.out.println("Type de carte inconnu, utilisation de 'islands' par défaut.");
            this.mapType = "islands";
        }
    }

    /**
     * Méthode de génération d'une carte de type "Islands" avec un terrain sinusoïdal et de l'eau en bas.
     * 
     * @see Math#sin(double)
     * @see Math#random()
     * 
     * @since 1.2
     */
    private void generateIslandsMap() {
        this.mapType = "islands";
        int height = grid.length;
        int width = this.grid[0].length;

        double amplitude = height * 0.1;

        double frequency1 = 0.3 + Math.random() * 0.3;
        double frequency2 = 0.05 + Math.random() * 0.1;

        int[] groundLevel = new int[width];

        for (int x = 0; x < width; x++) {
            double base = Math.sin(x * frequency1) * amplitude
                    + Math.cos(x * frequency2) * (amplitude / 2);

            double noise = (Math.random() - 0.5) * amplitude * 0.3;

            double offset = amplitude;
            groundLevel[x] = (int) (height - (base + offset + noise));
        }

        for (int x = 1; x < width; x++) {
            double t = 0.3 + Math.random() * 0.4;
            groundLevel[x] = (int) (groundLevel[x - 1] * t + groundLevel[x] * (1 - t));
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y >= groundLevel[x]) {
                    this.grid[y][x] = '#';
                } else {
                    this.grid[y][x] = ' ';
                }
            }
        }

        for (int y = height - 2; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.grid[y][x] = '~';
            }
        }

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < height; y++) {
                if (y == height - 1 || y == height - 2)
                    grid[y][x] = '~';
                else
                    grid[y][x] = ' ';
            }
        }

        for (int x = width - 3; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (y == height - 1 || y == height - 2)
                    grid[y][x] = '~';
                else
                    grid[y][x] = ' ';
            }
        }
    }

    /**
     * Méthode de génération d'une carte de type "Bridge" avec deux plateaux vallonnés reliés par un pont arqué.
     * 
     * @see Math#sin(double)
     * @see Math#random()
     * 
     * @since 1.2
     */
    private void generateBridgeMap() {
        this.mapType = "bridge";
        int height = grid.length;
        int width = grid[0].length;

        // Eau en bas (juste 2 dernières lignes)
        for (int y = height - 2; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '~';
            }
        }

        int leftPlateauEnd = (int) (width * 0.30);
        int rightPlateauStart = (int) (width * 0.70);

        double leftAmplitude = height * 0.08;
        double leftFrequency = 0.15 + Math.random() * 0.1;
        double leftBaseHeight = height * 0.7;

        double rightAmplitude = height * 0.08;
        double rightFrequency = 0.15 + Math.random() * 0.1;
        double rightBaseHeight = height * 0.7;

        int slopeWidth = 6;

        // Plateau gauche
        for (int x = 0; x < leftPlateauEnd; x++) {
            double wave = Math.sin(x * leftFrequency) * leftAmplitude;
            int surface = (int) (leftBaseHeight + wave);
            if (surface < height * 0.4)
                surface = (int) (height * 0.4);

            for (int y = surface; y < height - 2; y++)
                grid[y][x] = '#';

            for (int dx = 1; dx < slopeWidth; dx++) {
                if (x + dx < width) {
                    int slopeY = surface + dx;
                    for (int y = slopeY; y < height - 2; y++)
                        grid[y][x + dx] = '#';
                }
                if (x - dx >= 0) {
                    int slopeY = surface + dx;
                    for (int y = slopeY; y < height - 2; y++)
                        grid[y][x - dx] = '#';
                }
            }
        }

        // Plateau droit
        for (int x = rightPlateauStart; x < width; x++) {
            double wave = Math.sin((x - rightPlateauStart) * rightFrequency) * rightAmplitude;
            int surface = (int) (rightBaseHeight + wave);
            if (surface < height * 0.4)
                surface = (int) (height * 0.4);

            for (int y = surface; y < height - 2; y++)
                grid[y][x] = '#';

            for (int dx = 1; dx < slopeWidth; dx++) {
                if (x + dx < width) {
                    int slopeY = surface + dx;
                    for (int y = slopeY; y < height - 2; y++)
                        grid[y][x + dx] = '#';
                }
                if (x - dx >= 0) {
                    int slopeY = surface + dx;
                    for (int y = slopeY; y < height - 2; y++)
                        grid[y][x - dx] = '#';
                }
            }
        }

        smoothPlateauEdge(leftPlateauEnd - 8, leftPlateauEnd, height);
        smoothPlateauEdge(rightPlateauStart, rightPlateauStart + 8, height);

        // Pont – on recule un peu pour les transitions
        int bridgeStartX = leftPlateauEnd - 3;
        int bridgeEndX = rightPlateauStart + 3;
        int bridgeY = (int) (height * 0.65);
        int bridgeThickness = 6 + (int) (Math.random() * 2);
        double bridgeLength = bridgeEndX - bridgeStartX;
        double archHeight = height * 0.20;

        for (int x = bridgeStartX; x < bridgeEndX; x++) {
            double progress = (x - bridgeStartX) / bridgeLength;
            double archOffset = Math.sin(progress * Math.PI) * archHeight;
            int currentBridgeY = (int) (bridgeY + archHeight - archOffset);

            for (int thickness = 0; thickness < bridgeThickness; thickness++) {
                int y = currentBridgeY + thickness;
                if (y >= 0 && y < height - 2) {
                    // Zones de transition avec plateau → #
                    if (x < leftPlateauEnd + 1 || x >= rightPlateauStart - 1) {
                        grid[y][x] = '#';
                    } else {
                        grid[y][x] = 's';
                    }
                }
            }
        }

        // Sommet pont
        for (int x = bridgeStartX; x < bridgeEndX; x++) {
            for (int y = 0; y < height - 2; y++) {
                if (grid[y][x] == 's' && (y == 0 || grid[y - 1][x] != 's')) {
                    grid[y][x] = 'S';
                    break;
                }
            }
        }

        int clampedX1 = Math.min(Math.max(0, leftPlateauEnd - 10), width - 1);
        int clampedX2 = Math.min(Math.max(0, rightPlateauStart + 10), width - 1);

        smoothTerrainConnection(clampedX1, bridgeStartX, findSurfaceY(clampedX1),
                bridgeY + bridgeThickness);
        smoothTerrainConnection(bridgeEndX, clampedX2, findSurfaceY(clampedX2),
                bridgeY + bridgeThickness);

    }

    /**
     * Méthode pour trouver la coordonnée y de la surface du terrain à une position x donnée.
     * 
     * @param x -La coordonnée x.
     * 
     * @return La coordonnée y de la surface du terrain, sinon -1.
     * 
     * @since 1.2
     */
    private int findSurfaceY(int x) {
        for (int y = 0; y < grid.length - 2; y++) {
            if (grid[y][x] == '#' || grid[y][x] == 's' || grid[y][x] == 'S') {
                return y;
            }
        }
        return -1;
    }

    /**
     * Méthode de lissage des bords d'un plateau, pour un aspect plus naturel.
     * 
     * @param startX - La coordonnée x de début du lissage.
     * @param endX - La coordonnée x de fin du lissage.
     * @param height - La hauteur de la carte.
     * 
     * @since 1.2
     */
    private void smoothPlateauEdge(int startX, int endX, int height) {
        for (int x = startX; x < endX && x >= 0 && x < grid[0].length; x++) {
            int surfaceY = -1;
            for (int y = 0; y < height - 2; y++) {
                if (this.grid[y][x] == '#') {
                    surfaceY = y;
                    break;
                }
            }

            if (surfaceY == -1)
                continue;

            double edgeProgress = (double) (x - startX) / (endX - startX);
            int erosionDepth = (int) (Math.sin(edgeProgress * Math.PI) * 3);

            for (int y = surfaceY; y < surfaceY + erosionDepth && y < height - 2; y++) {
                if (Math.random() > 0.3) {
                    this.grid[y][x] = ' ';
                }
            }
        }
    }

    /**
     * Méthode de lissage de la connexion entre deux niveaux de terrain.
     * 
     * @param startX - La coordonnée x de début.
     * @param endX - La coordonnée x de fin.
     * @param startY - La coordonnée y de début.
     * @param endY - La coordonnée y de fin.
     * 
     * @since 1.2
     */
    private void smoothTerrainConnection(int startX, int endX, int startY, int endY) {
        int height = grid.length;
        for (int x = startX; x < endX && x >= 0 && x < grid[0].length; x++) {
            double progress = (double) (x - startX) / (endX - startX);
            int targetY = (int) (startY + progress * (endY - startY));

            for (int y = targetY; y < height - 2; y++) {
                if (this.grid[y][x] == ' ' || this.grid[y][x] == '~') {
                    this.grid[y][x] = '#';
                }
            }
        }
    }

    /**
     * Méthode de génération d'une carte de type "Cave" avec un terrain élevé et des cavités internes.
     * 
     * @since 1.2
     */
    private void generateCaveMap() {
        this.mapType = "cave";
        int height = grid.length;
        int width = this.grid[0].length;

        int baseGroundLevel = (int) (height * 0.3);
        int[] groundLevel = new int[width];

        for (int x = 0; x < width; x++) {
            double archProgress = (double) x / width;
            double archOffset = Math.sin(archProgress * Math.PI) * (height * 0.1);
            groundLevel[x] = (int) (baseGroundLevel - archOffset);
        }

        for (int pass = 0; pass < 3; pass++) {
            for (int x = 1; x < width - 1; x++) {
                groundLevel[x] = (groundLevel[x - 1] + groundLevel[x] + groundLevel[x + 1]) / 3;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = groundLevel[x]; y < height; y++) {
                this.grid[y][x] = 't';
            }
        }

        int numCavities = 2 + (int) (Math.random() * 2);

        for (int i = 0; i < numCavities; i++) {
            createCavity();
        }
    }

    /**
     * Méthode de création d'une cavité irrégulière dans le terrain, avec des bords très lisses et des petites cavités satellites autour.
     * 
     * @since 1.2
     */
    private void createCavity() {
        int height = grid.length;
        int width = grid[0].length;

        int centerX = width / 4 + (int) (Math.random() * (width / 2));
        int centerY = height / 2 + (int) (Math.random() * (height / 4));

        int cavityWidth = (int) (width * 0.12 + Math.random() * width * 0.15);
        int cavityHeight = (int) (height * 0.12 + Math.random() * height * 0.15);

        for (int x = centerX - cavityWidth - 8; x < centerX + cavityWidth + 8; x++) {
            for (int y = centerY - cavityHeight - 8; y < centerY + cavityHeight + 8; y++) {
                if (x >= 0 && x < width && y >= 0 && y < height - 2) {
                    double distX = (double) (x - centerX) / cavityWidth;
                    double distY = (double) (y - centerY) / cavityHeight;
                    double smoothDistance = Math.sqrt(distX * distX + distY * distY);
                    if (smoothDistance < 1.0) {
                        this.grid[y][x] = ' ';
                    }
                }
            }
        }

        int numSmallCavities = 2 + (int) (Math.random() * 3);

        for (int i = 0; i < numSmallCavities; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double distance = cavityWidth * (1.1 + Math.random() * 0.7);

            int smallX = (int) (centerX + Math.cos(angle) * distance);
            int smallY = (int) (centerY + Math.sin(angle) * distance);

            int smallWidth = (int) (cavityWidth * (0.25 + Math.random() * 0.35));
            int smallHeight = (int) (cavityHeight * (0.25 + Math.random() * 0.35));

            for (int x = smallX - smallWidth - 4; x < smallX + smallWidth + 4; x++) {
                for (int y = smallY - smallHeight - 4; y < smallY + smallHeight + 4; y++) {
                    if (x >= 0 && x < width && y >= 0 && y < height - 2) {
                        double distX = (double) (x - centerX) / cavityWidth;
                        double distY = (double) (y - centerY) / cavityHeight;
                        double smoothDistance = Math.sqrt(distX * distX + distY * distY);

                        if (smoothDistance < 1.0) {
                            this.grid[y][x] = ' ';
                        }
                    }
                }
            }
        }
    }

    /**
     * Méthode qui retourne le caractère à la position (x, y) de la carte.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return Le caractère à la position (x, y).
     * 
     * @since 1.1
     */
    public char getCell(int x, int y) {
        return this.grid[y][x];
    }

    /**
     * Définit le caractère à la position (x, y) de la carte.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * @param value - Le caractère à définir.
     * 
     * @since 1.1
     */
    public void setCell(int x, int y, char value) {
        this.grid[y][x] = value;
    }

    /**
     * Méthode qui retourne l'équipe associée au caractère à la position (x, y) de la carte.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return L'équipe associée au caractère à la position (x, y), 0 si aucun, 1 pour l'équipe 1, 2 pour l'équipe 2.
     * 
     * @since 1.1
     */
    public int getTeam(int x, int y) {
        char c = grid[y][x];
        if (c >= '1' && c <= '4')
            return (c - '1') + 1;
        return 0;
    }

    /**
     * Méthode qui vérifie si la cellule à la position (x, y) est vide.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si la cellule est vide, false sinon.
     * 
     * @since 1.1
     */
    public boolean isEmpty(double x, double y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            return false;
        char c = this.grid[(int)y][(int)x];
        return c == ' ' || c == 'R' || c == 'E' || c == 'L' || c == 'I' || c == 'P'
                || c == 'r' || c == 'c' || c == 'W';
    }

    /**
     * Méthode qui vérifie si la cellule à la position (x, y) est du terrain.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si la cellule est du terrain, false sinon.
     * 
     * @since 1.1
     */
    public boolean isGround(double x, double y) {
        if (x < 0 || x >= this.grid[0].length || y < 0 || y >= this.grid.length)
            return false;
        char c = this.grid[(int)y][(int)x];
        return (c == '#' || c == 'G' || c == 't' || c == 'T' || c == 's' || c == 'S' || c == 'b' || c == 'B' || c == 'H' || c == 'h')
                && !isWorm(x, y) && c != 'R' && c != 'E' && c != 'L' && c != 'I' && c != 'P'
                && c != 'W' && c != 'c' && c != 'r' && c != 'w' && c != '~';
    }

    /**
     * Méthode qui vérifie si la cellule à la position (x, y) est de l'eau.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si la cellule est de l'eau, false sinon.
     * 
     * @since 1.1
     */
    public boolean isWater(double x, double y) {
        return this.grid[(int)y][(int)x] == '~';
    }

    /**
     * Méthode qui vérifie si la cellule à la position (x, y) contient un ver.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si la cellule contient un ver, false sinon.
     * 
     * @since 1.1
     */
    public boolean isWorm(double x, double y) {
        return this.grid[(int)y][(int)x] >= '1' && this.grid[(int)y][(int)x] <= '8';
    }

    /**
     * Méthode qui vérifie si un ver peut être placé à la position (x, y).
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si un ver peut être placé à la position (x, y), false sinon.
     * 
     * @since 1.1
     */
    public boolean canWormBePlaced(int x, int y) {
        if (x < 0 || x >= this.grid[0].length || y < 0 || y >= this.grid.length)
            return false;

        if (!isEmpty(x, y))
            return false;

        if (y + 1 >= this.grid.length)
            return false;

        return this.isGroundForGenerating(x, y + 1);
    }

    /**
     * Méthode de génération de la carte en fonction du type actuellement défini dans mapType.
     * Si mapType est null ou invalide, génère une carte aléatoire.
     * 
     * @since 1.2
     */
    public void generateMapByType() {
        if (mapType == null) {
            generateDefaultMap();
            return;
        }

        switch (mapType) {
            case "islands" -> generateIslandsMap();
            case "bridge" -> generateBridgeMap();
            case "cave" -> generateCaveMap();
            default -> generateDefaultMap();
        }
    }

    /**
     * Méthode de génèration de la map de base et ajoute des décorations selon le type de carte.
     * 
     * @since 1.2
     */
    public void mapWithDecorations() {
        // Utilise le type déjà défini plutôt que de générer aléatoirement
        generateMapByType();

        int height = getHeight() - 1;
        int width = getWidth() - 1;

        if (mapType.equals("islands")) {
            addIslandsDecorations(height, width);
        } else if (mapType.equals("cave")) {
            addCaveDecorations(height, width);
        } else if (mapType.equals("bridge")) {
            addBridgeDecorations(height, width);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (this.grid[y][x] == '~') {
                    if (y == 0 || this.grid[y - 1][x] == ' ') {
                        this.grid[y - 1][x] = 'W';
                    }
                }
            }
        }
    }

    /**
     * Méthode qui permet d'ajouter les décorations pour la carte Islands.
     * Utilise 'G' pour le dessus du terrain, et ajoute des plantes, rochers, etc.
     * 
     * @param height - La hauteur de la carte.
     * @param width - La largeur de la carte.
     * 
     * @since 1.2
     */
    private void addIslandsDecorations(int height, int width) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean emptyAbove = isEmptyForGenerating(x, y - 1);
                if (isGroundForGenerating(x, y)) {
                    if (isEmpty(x, y - 1) || isWorm(x, y - 1)) {
                        setCell(x, y, 'G');
                    }
                }

                if (isGroundForGenerating(x, y + 1) && emptyAbove && Math.random() < 0.3) {
                    setCell(x, y, 'P');
                }
                if (isGroundForGenerating(x, y + 1) && emptyAbove && Math.random() < 0.05) {
                    setCell(x, y, 'R');
                }
                if (isGroundForGenerating(x, y + 1) && emptyAbove && Math.random() < 0.005) {
                    setCell(x, y, 'L');
                }
                if (isGroundForGenerating(x, y + 1) && emptyAbove && Math.random() < 0.005) {
                    setCell(x, y, 'I');
                }
                if (isGroundForGenerating(x, y + 1) && emptyAbove && Math.random() < 0.005) {
                    setCell(x, y, 'E');
                }
            }
        }
    }

    /**
     * Méthode qui permet d'ajouter les décorations spécifiques pour la carte Cave.
     * Utilise 'T' pour le dessus du terrain, et ajoute des boîtes, chaînes, cordes
     * 
     * @param height - La hauteur de la carte.
     * @param width - La largeur de la carte.
     * 
     * @since 1.2
     */
    private void addCaveDecorations(int height, int width) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (this.grid[y][x] == 't' && (y == 0 || this.grid[y - 1][x] == ' ' || isWorm(x, y - 1))) {

                    this.grid[y][x] = 'T';
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean emptyAbove = isEmptyForGenerating(x, y - 1);
                boolean groundBelow = isGroundForGenerating(x, y + 1);

                if (groundBelow && emptyAbove && Math.random() < 0.05) {
                    setCell(x, y, Math.random() < 0.5 ? 'b' : 'B');
                }

                if (hasGroundAbove(x, y) && Math.random() < 0.25) {
                    placeChain(x, y);
                }

                if (groundBelow && emptyAbove && Math.random() < 0.02) {
                    setCell(x, y, 'w');
                }

                if (groundBelow && emptyAbove && Math.random() < 0.02) {
                    setCell(x, y, Math.random() < 0.5 ? 'h' : 'H');
                }
            }
        }
    }

    /**
     * Méthode qui permet d'ajouter les décorations pour la carte Bridge (pas de décorations spéciales on reprend island).
     * 
     * @param height - La hauteur de la carte.
     * @param width - La largeur de la carte.
     * 
     * @since 1.2
     */
    private void addBridgeDecorations(int height, int width) {
        addIslandsDecorations(height, width);
    }

    /**
     * Méthode qui vérifie s'il y a du terrain au-dessus de la position (x, y).
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true s'il y a du terrain au-dessus, false sinon.
     * 
     * @since 1.2
     */
    private boolean hasGroundAbove(int x, int y) {
        if (isGroundForGenerating(x, y)) {
            return true;
        }
        return false;
    }

    /**
     * Méthode de placement d'une chaîne de caractère à partir de (x, y) vers le bas, et potentiellement une corde en dessous.
     * 
     * @param x - La coordonnée x.
     * @param startY - La coordonnée y de départ.
     * 
     * @since 1.2
     */
    private void placeChain(int x, int startY) {
        int y = startY;
        while (y > 0 && !isGroundForGenerating(x, y - 1)) {
            y--;
        }

        if (y <= 0 || !isEmptyForGenerating(x, y)) {
            return;
        }

        int chainLength = 1 + (int) (Math.random() * 3);

        for (int i = 0; i < chainLength; i++) {
            int currentY = y + i;
            if (currentY >= grid.length - 1 || !isEmptyForGenerating(x, currentY)) {
                break;
            }
            this.grid[currentY][x] = 'c';
        }

        int ropeY = y + chainLength;
        if (ropeY < grid.length && isEmptyForGenerating(x, ropeY) && Math.random() < 0.5) {
            this.grid[ropeY][x] = 'r';
        }
    }

    /**
     * Méthode qui vérifie si la cellule à la position (x, y) est du terrain pour la génération (décoration).
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si la cellule est du terrain, false sinon.
     * 
     * @since 1.2
     */
    private boolean isGroundForGenerating(int x, int y) {
        if (x < 0 || x >= this.grid[0].length || y < 0 || y >= this.grid.length)
            return false;
        char c = this.grid[y][x];
        return c == '#' || c == 't' || c == 'T';
    }

    /**
     * Méthode qui vérifie si la cellule à la position (x, y) est vide pour la génération (décoration).
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si la cellule est vide, false sinon.
     * 
     * @since 1.2
     */
    private boolean isEmptyForGenerating(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            return false;
        char c = this.grid[y][x];
        return c == ' ';
    }

    /**
     * Méthode qui vérifie si la cellule à la position (x, y) est une décoration.
     * 
     * @param x - La coordonnée x.
     * @param y - La coordonnée y.
     * 
     * @return true, si la cellule est une décoration, false sinon.
     * 
     * @since 1.2
     */
    private boolean isDecoration(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            return false;
        char c = this.grid[y][x];
        return c == 'R' || c == 'E' || c == 'L' || c == 'I' || c == 'P'
                || c == 'b' || c == 'B' || c == 'w' || c == 'h' || c == 'H';
    }

    /**
     * Crée une explosion à la position (x, y) avec un rayon donné.
     * Détruit le terrain ('#') dans ce rayon.
     * 
     * @param x - La coordonnée x du centre.
     * @param y - La coordonnée y du centre.
     * @param r - Le rayon de l'explosion.
     * 
     * @since 1.3
     */
    public void createExplosion(double x, double y, int r) {
        if (r <= 0)
            return;

        for (int i = (int)x - r; i <= x + r; i++) {
            for (int j = (int)y - r; j <= y + r; j++) {
                if (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) {
                    if (Math.pow(i - x, 2) + Math.pow(j - y, 2) <= Math.pow(r, 2)) {
                        if (this.isGround(i, j) || this.isDecoration(i, j)) {
                            this.grid[j][i] = ' ';
                        }

                        int waterLine = getHeight() - 3;
                        if (waterLine >= 0 && this.grid[waterLine][i] == ' ') {
                            this.grid[waterLine][i] = 'W';
                        }
                    }
                }
            }
        }
    }
}