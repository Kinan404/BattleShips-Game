/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    public void hit() {
        if (health > 0) {
            health--;
        }
    }

    public boolean isSunk() {
        return health == 0;
    }
}
