package model;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Classe Config qui définit les paramètres globaux du jeu.
 * 
 * @see Hashtable#getClass()
 * 
 * @since 1.1
 * 
 * @version 1.1
 */
public class Config {

    /**
     * Création d'une nouvelle hashtable
     */
    public Hashtable<String, Object> parameters = new Hashtable<>();

    /**
     * Création d'une arrayListe pour les noms disponibles.
     */
    public ArrayList<String> possiblesNames = new ArrayList<String>();

    // ========================Builders========================//

    /**
     * Constructeur pour définir les paramètres globaux du jeu.
     * 
     * @see Hashtable#put(Object, Object)
     * 
     * @since 1.1
     */
    public Config() {

        // Main
        this.parameters.put("DELAY", 500);
        // Model
        this.parameters.put("TIMER", 45);
        this.parameters.put("WIND", false);

        // View
        this.parameters.put("DEFAULT_TURN_TIME", 30);
        this.parameters.put("TEAM_NAME_OPTION", false);

        // Worm
        this.parameters.put("HP", 100);
    }

    // ========================Accessors========================//

    /**
     * Getter pour récupérer la valeur de type String associée à une clé.
     * 
     * @param key - La clé
     * 
     * @return La valeur de type String associé à la clé
     * 
     * @see Hashtable#get(Object)
     * 
     * @since 1.1
     */
    public String getStringParameter(String key) {
        return (String) this.parameters.get(key);
    }

    /**
     * Getter pour récupérer la valeur de type int associée à une clé.
     * 
     * @param key - La clé
     * 
     * @return La valeur de type int associé à la clé
     * 
     * @see Hashtable#get(Object)
     * 
     * @since 1.1
     */
    public int getIntParameter(String key) {
        return (int) this.parameters.get(key);
    }

    /**
     * Getter pour récupérer la valeur de type boolean associée à une clé.
     * 
     * @param key - La clé
     * 
     * @return La valeur de type boolean associé à la clé
     * 
     * @see Hashtable#get(Object)
     * 
     * @since 1.1
     */
    public boolean getBoolParameter(String key) {
        return (boolean) this.parameters.get(key);
    }

    // ========================Methods========================//

    /**
     * Méthode pour remplacer la valeur associée à une clé à une nouvelle valeur.
     * 
     * @param key - La clé
     * @param value - La nouvelle valeur
     * 
     * @see Hashtable#replace(Object, Object)
     * 
     * @since 1.1
     */
    public void replace(String key, Object value) {
        this.parameters.put(key, value);
    }

    /**
     * Méthode pour définir tous les noms possibles pour un worm.
     * 
     * @see ArrayList#add(Object)
     * 
     * @since 1.1
     */
    private void initPossiblesNames() {
        // noms de base
        possiblesNames.add("BazookaBob");
        possiblesNames.add("SergeantSquirm");
        possiblesNames.add("MajorWorm");
        possiblesNames.add("CaptainCrawl");
        possiblesNames.add("PrivateBoom");
        possiblesNames.add("ColonelCluck");
        possiblesNames.add("CommandoCreepy");
        possiblesNames.add("GeneralSlime");

        // nouveuax noms
        possiblesNames.add("GrenadeGary");
        possiblesNames.add("SniperSly");
        possiblesNames.add("RocketRick");
        possiblesNames.add("DynamiteDan");
        possiblesNames.add("ConcreteCarl");
        possiblesNames.add("NinjaNed");
        possiblesNames.add("KamikazeKen");
        possiblesNames.add("BunkerBill");

        // noms des dev
        possiblesNames.add("HunterHugo");
        possiblesNames.add("ViperValentin");
        possiblesNames.add("RocketRomain");
        possiblesNames.add("SniperSeth");
        possiblesNames.add("SlayerSouleymane");
    }

    /**
     * Setter pour initialiser le nom du Worm, grâce à un choix aléatoire parmis les noms prédéfinis dans possibleNames.
     * 
     * @param teamName -La nom de l'équipe
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#isEmpty()
     * @see ArrayList#remove(Object)
     * @see ArrayList#size()
     * @see Math#random()
     * 
     * @return Le nom aléatoire du worms
     * 
     * @since 1.0
     */
    public String getWormRandomName(String teamName) {
        if (possiblesNames.isEmpty()) {
            initPossiblesNames();
        }

        String tmp = "";
        if (this.getBoolParameter("TEAM_NAME_OPTION")) {
            if (teamName.isEmpty()) {
                tmp = "NoNameTeam" + "_";
            } else {
                tmp = teamName + "_";
            }
        }

        String name;
        if (possiblesNames.isEmpty()) {
            name = (tmp + "NoNameWorm");
        } else {
            int index = (int) (Math.random() * possiblesNames.size());
            name = (tmp + possiblesNames.get(index));
            possiblesNames.remove(index);
        }

        return name;
    }
}
