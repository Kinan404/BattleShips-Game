/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships.HelperClasses;

import java.util.ArrayList;

public class Board {
    private final int SIZE = 10;
    private Cell[][] grid;
private ArrayList<Ship> ships;
    public Board() {
        grid = new Cell[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public int getSize() {
        return SIZE;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean canPlaceShip(Ship ship, int row, int col, boolean horizontal) {
        for (int i = 0; i < ship.getSize(); i++) {
            int newRow = row;
            int newCol = col;

            if (horizontal) {
                newCol = col + i;
            } else {
                newRow = row + i;
            }

            if (!isValidCoordinate(newRow, newCol)) {
                return false;
            }

            if (grid[newRow][newCol].hasShip()) {
                return false;
            }
        }

        return true;
    }

    public boolean placeShip(Ship ship, int row, int col, boolean horizontal) {
        if (!canPlaceShip(ship, row, col, horizontal)) {
            return false;
        }

        for (int i = 0; i < ship.getSize(); i++) {
            int newRow = row;
            int newCol = col;

            if (horizontal) {
                newCol = col + i;
            } else {
                newRow = row + i;
            }

            grid[newRow][newCol].setShip(ship);
        }

        return true;
    }

    public AttackResult receiveAttack(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            return AttackResult.INVALID;
        }

        Cell cell = grid[row][col];

        if (cell.isHit()) {
            return AttackResult.ALREADY_HIT;
        }

        cell.setHit(true);

        if (!cell.hasShip()) {
            return AttackResult.MISS;
        }

        Ship ship = cell.getShip();
        ship.hit();

        if (ship.isSunk()) {
            return AttackResult.SUNK;
        }

        return AttackResult.HIT;
    }

    public boolean allShipsSunk() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j].hasShip()) {
                    Ship ship = grid[i][j].getShip();
                    if (!ship.isSunk()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}