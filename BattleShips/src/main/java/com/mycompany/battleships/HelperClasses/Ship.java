package com.mycompany.battleships.HelperClasses;

/**
 *
 * @author Kinan
 */
public class Ship {

    private String name;
    private int size;
    private int health;

    public Ship(String name, int size) {

        this.name = name;
        this.size = size;

        // Ship health starts with its size
        this.health = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getHealth() {
        return health;
    }

    // Reduce ship health when it is hit
    public void hit() {

        if (health > 0) {
            health--;
        }
    }

    // Check if the ship is destroyed
    public boolean isSunk() {
        return health == 0;
    }
}