package com.mycompany.battleships.HelperClasses;

/**
 *
 * @author Kinan
 */
public class Cell {

    private Ship ship;
    private boolean hit;

    public Cell() {

        // Cell starts empty and not hit
        this.ship = null;
        this.hit = false;
    }

    // Check if cell contains a ship
    public boolean hasShip() {
        return ship != null;
    }

    public Ship getShip() {
        return ship;
    }

    // Put a ship in this cell
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    // Check if this cell was attacked
    public boolean isHit() {
        return hit;
    }

    // Update hit status
    public void setHit(boolean hit) {
        this.hit = hit;
    }
}