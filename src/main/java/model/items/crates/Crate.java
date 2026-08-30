package model.items.crates;

import model.items.Item;
import java.util.ArrayList;

/**
 * Cette classe implémente une caisse contenant des objets.
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class Crate {
    private int x;
    private int y;
    private double velocityY;
    private ArrayList<Item> contents;
    private boolean collected;
    private boolean onGround;
    
    private static final double GRAVITY = 0.3;
    private static final double TERMINAL_VELOCITY = 1;

    /**
     * Constructeur de la classe Crate.
     * 
     * @param x - La position X de la caisse
     * @param contents - Le contenu de la caisse
     * 
     * @since 2.1
     */
    public Crate(int x, ArrayList<Item> contents) {
        this.x = x;
        this.y = 0;
        this.velocityY = 0;
        this.contents = contents;
        this.collected = false;
        this.onGround = false;
    }

    /**
     * Méthode pour mettre à jour la position de la caisse en fonction de la gravité.
     * 
     * @param dt - Le delta time depuis la dernière mise à jour
     * 
     * @since 2.1
     */
    public void update(double dt) {
        if (!onGround) {
            velocityY += GRAVITY * dt;
            if (velocityY > TERMINAL_VELOCITY) {
                velocityY = TERMINAL_VELOCITY;
            }
            y += (int) velocityY;
        }
    }

    /**
     * Méthode pour récupérer la position X de la caisse
     * 
     * @return La position X de la caisse
    */
    public int getX() {
        return x;
    }

    /**
     * Méthode pour récupérer la position Y de la caisse
     * 
     * @return La position Y de la caisse
    */
    public int getY() {
        return y;
    }

    /**
     * Méthode pour récupérer la position X de la caisse sur la carte
     * 
     * @return La position X de la caisse sur la carte
    */
    public int getMapX() {
        return x;
    }

    /**
     * Méthode pour récupérer la position Y de la caisse sur la carte
     * 
     * @return La position Y de la caisse sur la carte
    */
    public int getMapY() {
        return y;
    }

    /**
     * Méthode pour définir la position Y de la caisse
     * 
     * @param y - La nouvelle position Y de la caisse
    */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Méthode pour récupérer le contenu de la caisse
     * 
     * @return Le contenu de la caisse
    */
    public ArrayList<Item> getContents() {
        return contents;
    }

    /**
     * Méthode pour vérifier si la caisse a été collectée
     * 
     * @return true, si la caisse a été collectée, false sinon
    */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Méthode pour marquer la caisse comme collectée
     * 
     * @since 2.1
     */
    public void collect() {
        this.collected = true;
    }

    /**
     * Méthode pour vérifier si la caisse est au sol
     * 
     * @return true si la caisse est au sol, false sinon
    */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Méthode pour définir si la caisse est au sol
     * 
     * @param onGround - true si la caisse est au sol, false sinon
    */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        if (onGround) {
            this.velocityY = 0;
        }
    }
}