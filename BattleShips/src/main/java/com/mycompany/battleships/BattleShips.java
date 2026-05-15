package com.mycompany.battleships;

import javax.swing.SwingUtilities;

public class BattleShips {

    // Main method to start the game
    public static void main(String[] args) {

        // Open the start screen
        SwingUtilities.invokeLater(() -> new StartScreenUI());
    }
}