/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships;

import com.mycompany.battleships.HelperClasses.AttackResult;
import com.mycompany.battleships.HelperClasses.Board;
import com.mycompany.battleships.HelperClasses.Cell;
import com.mycompany.battleships.HelperClasses.Ship;
import com.mycompany.battleships.server.ClientConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 *
 * @author Kinan
 */
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Random;

public class BattleshipClientUI extends JFrame {

    private static final int BOARD_SIZE = 10;

    private Board myBoard;
    private Random random = new Random();

    private Board enemyBoard;

    // To save which enemy cells were already clicked
    private boolean[][] enemyClicked = new boolean[10][10];

    // To control the player turn and stop the game when it ends
    private boolean isMyTurn = true;
    private boolean isGameOver = false;

    private String playerRole;
    // For the player name
    private String playerName;

    private ClientConnection clientConnection;

    // GUI components
    private JPanel myBoardPanel;
    private JPanel enemyBoardPanel;
    private JButton[][] myBoardButtons;
    private JButton[][] enemyBoardButtons;

    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JLabel playerLabel;

    private JButton exitButton;

    // Temporary fields used for testing
    private JTextField attackRowField;
    private JTextField attackColField;
    private JButton attackMyBoardButton;

    public BattleshipClientUI(String playerRole, String playerName, ClientConnection clientConnection) {

        this.clientConnection = clientConnection;
        this.playerRole = playerRole;
        this.playerName = playerName;

        // Prepare the main window and its components
        initializeFrame();
        initializeComponents();
        buildLayout();
        setupWindowCloseBehavior();

        setStatusText("You are " + playerRole);
        isMyTurn = false;
        setTurnText("Waiting for turn...");

        // Add click action for each enemy board cell
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int r = row;
                int c = col;

                enemyBoardButtons[row][col].addActionListener(e -> handleEnemyClick(r, c));
            }
        }

        // Show player name and role on the screen
        playerLabel.setText(playerName + " (" + playerRole + ")");
        setVisible(true);

        setupExitButton();

    }

    public BattleshipClientUI() {
        this("PLAYER_1", "TestPlayer", null);
    }

    // temp for my  board
    private void handleAttackOnMyBoard() {
        try {
            int row = Integer.parseInt(attackRowField.getText().trim());
            int col = Integer.parseInt(attackColField.getText().trim());

            AttackResult result = myBoard.receiveAttack(row, col);
            updateMyBoardUI(row, col, result);

            if (myBoard.allShipsSunk()) {
                setStatusText("All your ships are sunk!");
                setTurnText("Game Over");
            }

        } catch (NumberFormatException e) {
            setStatusText("Enter valid numbers");
        }
    }

    private void updateMyBoardUI(int row, int col, AttackResult result) {

        JButton button = myBoardButtons[row][col];

        switch (result) {
            case HIT:
                styleAsHit(button);
                setStatusText("Your ship was hit!");
                break;

            case MISS:
                styleAsMiss(button);
                setStatusText("Enemy missed!");
                break;

            case SUNK:
                styleAsSunk(button);
                setStatusText("One of your ships was sunk!");
                break;

            case ALREADY_HIT:
                setStatusText("This cell was already attacked");
                break;

            case INVALID:
                setStatusText("Invalid coordinates");
                break;
        }
    }

    // Also for just trying hit miss
    private void handleEnemyClick(int row, int col) {
        if (isGameOver) {
            return;
        }

        if (!isMyTurn) {
            setStatusText("Wait for your turn");
            return;
        }

        if (enemyClicked[row][col]) {
            setStatusText("This cell was already used");
            return;
        }

        enemyClicked[row][col] = true;

        enemyClicked[row][col] = true;

        if (clientConnection != null) {
            clientConnection.sendAttack(row, col);
        }
        setStatusText("Attack sent: (" + row + ", " + col + ")");
        setTurnText("Opponent Turn");
        isMyTurn = false;

// later here:
// clientConnection.sendAttack(row, col);
        isMyTurn = false;
        setTurnText("Opponent Turn");

// later: send attack to server here
    }

    // Update enemy board after attack result
    public void updateEnemyBoardUI(int row, int col, AttackResult result) {

        JButton button = enemyBoardButtons[row][col];

        switch (result) {

            case HIT:
                styleAsHit(button);
                setStatusText("Hit!");
                break;

            case MISS:
                styleAsMiss(button);
                setStatusText("Miss!");
                break;

            case SUNK:
                styleAsSunk(button);
                setStatusText("Ship sunk!");
                break;

            case ALREADY_HIT:
                setStatusText("Already used");
                break;

            case INVALID:
                setStatusText("Invalid move");
                break;
        }
    }

    // Ask player if they want to play again
    private void showPlayAgainDialog() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    // Reset game values for a new game
    private void restartGame() {

        isGameOver = false;
        isMyTurn = true;

        // Clear clicked enemy cells
        enemyClicked = new boolean[10][10];

        resetAllBoardButtons();

        setStatusText("New game started");
        setTurnText("Your Turn");
    }

    // Handle attack received from network
    public void handleAttackOnMyBoardFromNetwork(int row, int col, AttackResult result) {
        updateMyBoardUI(row, col, result);
    }

    // Open end screen after game finishes
    public void showEndScreen(String resultText) {

        new EndScreenUI(resultText, clientConnection);

        dispose();
    }

    // Show ships on my board using data from server
    public void showShipsFromServer(String boardData) {

        int index = 0;

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                char value = boardData.charAt(index++);

                // If value is 1, show ship
                if (value == '1') {
                    styleAsShip(myBoardButtons[row][col]);
                } else {

                    // Otherwise show water
                    styleAsWater(myBoardButtons[row][col], false);
                }
            }
        }
    }

    // Setup main game window
    private void initializeFrame() {

        setTitle("Battleship - Client");
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
    }

    // Create all GUI components
    private void initializeComponents() {

        titleLabel = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(235, 244, 255));

        playerLabel = new JLabel("Player Name:");
        playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        playerLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("Status: Waiting to start...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(new Color(230, 240, 250));

        turnLabel = new JLabel("Turn: ---");
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        turnLabel.setForeground(new Color(255, 214, 102));

        exitButton = createControlButton("Exit", new Color(192, 57, 43));

        // Create board buttons
        myBoardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];
        enemyBoardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];

        // Create player and enemy boards
        myBoardPanel = createBoardPanel("My Board", myBoardButtons, false);
        enemyBoardPanel = createBoardPanel("Enemy Board", enemyBoardButtons, true);

        // Show player role
        turnLabel.setText("Role: " + playerRole);
    }

    // Build the main layout
    private void buildLayout() {

        JPanel backgroundPanel = new JPanel(new BorderLayout(20, 20));
        backgroundPanel.setBackground(new Color(8, 36, 64));
        backgroundPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        backgroundPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        backgroundPanel.add(createCenterPanel(), BorderLayout.CENTER);
        backgroundPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }

    // Create bottom panel with player name and exit button
    private JPanel createBottomPanel() {

        JPanel bottomPanel = new JPanel(new BorderLayout(20, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setOpaque(false);
        leftPanel.add(playerLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(exitButton);

        bottomPanel.add(leftPanel, BorderLayout.WEST);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    // Create the top panel
    private JPanel createHeaderPanel() {

        JPanel headerPanel = new JPanel(new BorderLayout(15, 10));
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);
        infoPanel.add(statusLabel);
        infoPanel.add(turnLabel);

        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(infoPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    // Create the center part with two boards
    private JPanel createCenterPanel() {

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        centerPanel.setOpaque(false);

        centerPanel.add(wrapPanel(myBoardPanel));
        centerPanel.add(wrapPanel(enemyBoardPanel));

        return centerPanel;
    }

    // Wrap panel inside another panel
    private JPanel wrapPanel(JPanel panel) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        wrapper.add(panel, BorderLayout.CENTER);

        return wrapper;
    }

    // Create game board panel
    private JPanel createBoardPanel(String title, JButton[][] boardButtons, boolean isEnemyBoard) {

        JPanel container = new JPanel(new BorderLayout(10, 10));

        container.setBackground(new Color(13, 52, 89));

        container.setBorder(new CompoundBorder(
                new LineBorder(new Color(90, 150, 210), 2, true),
                new EmptyBorder(14, 14, 14, 14)
        ));

        // Board title
        JLabel boardTitle = new JLabel(title, SwingConstants.CENTER);
        boardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        boardTitle.setForeground(Color.WHITE);

        // Grid for board cells
        JPanel gridPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 3, 3));

        gridPanel.setBackground(new Color(18, 72, 120));
        gridPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Create all board buttons
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {

                JButton cellButton = new JButton();

                styleCellButton(cellButton, isEnemyBoard);

                boardButtons[row][col] = cellButton;

                gridPanel.add(cellButton);
            }
        }

        container.add(boardTitle, BorderLayout.NORTH);
        container.add(gridPanel, BorderLayout.CENTER);

        return container;
    }

    // Style board buttons
    private void styleCellButton(JButton button, boolean enemyBoard) {

        button.setPreferredSize(new Dimension(48, 48));
        button.setFocusPainted(false);

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));

        button.setBackground(enemyBoard ? new Color(52, 152, 219) : new Color(41, 128, 185));

        button.setForeground(Color.WHITE);

        button.setBorder(new LineBorder(new Color(220, 240, 255), 1, true));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setText("");

        // Cell text can be updated later during gameplay
    }

    // Create buttons like exit button
    private JButton createControlButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFocusPainted(false);

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));

        button.setForeground(Color.WHITE);

        button.setBackground(color);

        button.setPreferredSize(new Dimension(125, 38));

        button.setBorder(new EmptyBorder(8, 14, 8, 14));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // -----------------------------
    // Button visual design methods
    // These methods style cells only.
    // Game logic can call them later.
    // -----------------------------
    public void styleAsWater(JButton button, boolean enemyBoard) {
        button.setText("");
        button.setBackground(enemyBoard ? new Color(52, 152, 219) : new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(220, 240, 255), 1, true));
        button.setEnabled(true);
    }

    public void styleAsShip(JButton button) {
        button.setText("S");
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(new Color(236, 240, 241));
        button.setBorder(new LineBorder(new Color(189, 195, 199), 1, true));
    }

    public void styleAsMiss(JButton button) {
        button.setText("O");
        button.setBackground(new Color(133, 193, 233));
        button.setForeground(new Color(21, 67, 96));
        button.setBorder(new LineBorder(new Color(214, 234, 248), 1, true));
    }

    public void styleAsHit(JButton button) {
        button.setText("X");
        button.setBackground(new Color(231, 76, 60));
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(255, 205, 210), 1, true));
    }

    public void styleAsSunk(JButton button) {
        button.setText("X");
        button.setBackground(new Color(123, 36, 28));
        button.setForeground(new Color(255, 230, 230));
        button.setBorder(new LineBorder(new Color(245, 183, 177), 1, true));
    }

    public void styleAsDisabled(JButton button) {
        button.setEnabled(false);
        button.setForeground(Color.WHITE);
    }

    public void resetAllBoardButtons() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                styleAsWater(myBoardButtons[row][col], false);
                styleAsWater(enemyBoardButtons[row][col], true);
            }
        }
    }

    // Place checkers functions
    private void showShipsOnMyBoard() {
        Cell[][] grid = myBoard.getGrid();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (grid[row][col].hasShip()) {
                    styleAsShip(myBoardButtons[row][col]);
                } else {
                    styleAsWater(myBoardButtons[row][col], false);
                }
            }
        }
    }

    // Exit function
    private void setupExitButton() {
        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit the game?",
                    "Exit Game",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                if (clientConnection != null) {
                    clientConnection.sendExit();
                }

                dispose();
            }
        });
    }

    // when the player press X to closr the frame
    private void setupWindowCloseBehavior() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        BattleshipClientUI.this,
                        "Are you sure you want to exit the game?",
                        "Exit Game",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    if (clientConnection != null) {
                        clientConnection.sendExit();
                        clientConnection.closeConnection();
                    }

                    dispose();
                }
            }
        });
    }

    // Small UI helper methods
    public void setStatusText(String text) {
        statusLabel.setText("Status: " + text);
    }

    public void setTurnText(String text) {
        turnLabel.setText("Turn: " + text);
    }

    public void setMyTurn(boolean value) {
        this.isMyTurn = value;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()
                -> new BattleshipClientUI("PLAYER_1", "TestPlayer", null)
        );
    }

}
