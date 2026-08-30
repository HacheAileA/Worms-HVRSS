package model.items.crates;

import model.GameModel;
import model.items.Item;
import model.items.guns.*;
import model.items.tools.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Enumération représentant les différents types de contenu de caisse. Chaque type est associé à une méthode pour créer le contenu spécifique.
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public enum CrateContentType {
    
    /** Crate du bazooka */
    BAZOOKA_CRATE {
        @Override
        public ArrayList<Item> createContents(GameModel model) {
            ArrayList<Item> items = new ArrayList<>();
            Bazooka bazooka = new Bazooka(model);
            bazooka.setAmmo(1);
            items.add(bazooka);
            return items;
        }
    },

    /** Crate du shotgun */
    SHOTGUN_CRATE {
        @Override
        public ArrayList<Item> createContents(GameModel model) {
            ArrayList<Item> items = new ArrayList<>();
            ShotGun shotGun = new ShotGun(model);
            shotGun.setAmmo(3);
            items.add(shotGun);
            return items;
        }
    },

    /** Crate du sniper */
    SNIPER_CRATE {
        @Override
        public ArrayList<Item> createContents(GameModel model) {
            ArrayList<Item> items = new ArrayList<>();
            Sniper sniper = new Sniper(model);
            sniper.setAmmo(2);
            items.add(sniper);
            return items;
        }
    },

    /** Crate des grenades */
    GRENADES_CRATE {
        @Override
        public ArrayList<Item> createContents(GameModel model) {
            ArrayList<Item> items = new ArrayList<>();
            Grenade grenade = new Grenade(model);
            grenade.setAmmo(2);
            items.add(grenade);
            return items;
        }
    },

    /** Crate de la frappe aérienne (air strike) */
    AIRSTRIKE_CRATE {
        @Override
        public ArrayList<Item> createContents(GameModel model) {
            ArrayList<Item> items = new ArrayList<>();
            AirStrike airStrike = new AirStrike(model);
            airStrike.setAmmo(1);
            items.add(airStrike);
            return items;
        }
    },

    /** Crate pour le pack de soin (Health Pack) */
    HEALTH_PACK_CRATE {
        @Override
        public ArrayList<Item> createContents(GameModel model) {
            ArrayList<Item> items = new ArrayList<>();
            HealthPack healthPack = new HealthPack(20, model);
            healthPack.setAmmo(1);
            items.add(healthPack);
            return items;
        }
    },

    /** Crate pour le tp aléatoire (RandomTP) */
    RANDOM_TP_CRATE {
        @Override
        public ArrayList<Item> createContents(GameModel model) {
            ArrayList<Item> items = new ArrayList<>();
            RandomTp randomTp = new RandomTp(model);
            randomTp.setAmmo(1);
            items.add(randomTp);
            return items;
        }
    };

    /**
     * Méthode abstraite de création du contenu de la caisse en fonction du type.
     *
     * @param model - Le modèle de jeu.
     * 
     * @return Une liste d'objets contenus dans la caisse.
     * 
     * @since 2.1
     */
    public abstract ArrayList<Item> createContents(GameModel model);

    /**
     * Méthode pour obtenir un type de contenu de caisse aléatoire.
     *
     * @param random - L'instance de Random à utiliser pour la sélection aléatoire.
     * 
     * @return Un type de contenu de caisse aléatoire.
     * 
     * @since 2.1
     */
    public static CrateContentType getRandomType(Random random) {
        CrateContentType[] types = values();
        return types[random.nextInt(types.length)];
    }
}