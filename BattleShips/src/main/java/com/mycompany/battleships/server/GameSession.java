package com.mycompany.battleships.server;

import com.mycompany.battleships.HelperClasses.AttackResult;
import com.mycompany.battleships.HelperClasses.Board;
import com.mycompany.battleships.HelperClasses.Cell;
import com.mycompany.battleships.HelperClasses.Ship;
import java.util.Random;

public class GameSession {

    private final int sessionId;

    private final ClientHandler player1;
    private final ClientHandler player2;

    private final Board player1Board;
    private final Board player2Board;

    private String currentTurn;
    private boolean gameEnded;

    public GameSession(int sessionId, ClientHandler player1, ClientHandler player2) {

        this.sessionId = sessionId;
        this.player1 = player1;
        this.player2 = player2;

        this.player1Board = new Board();
        this.player2Board = new Board();

        // Player 1 starts first
        this.currentTurn = "PLAYER_1";
        this.gameEnded = false;

        // Create random ships for both players
        generateRandomShips(player1Board);
        generateRandomShips(player2Board);

        this.player1.setGameSession(this);
        this.player2.setGameSession(this);
    }

    // Start the game session
    public void startSession() {

        System.out.println("[Session " + sessionId + "] Match started.");
        System.out.println("[Session " + sessionId + "] Player 1: " + player1.getClientInfo());
        System.out.println("[Session " + sessionId + "] Player 2: " + player2.getClientInfo());

        player1.sendMessage("PLAYER_1");
        player2.sendMessage("PLAYER_2");

        player1.sendMessage("WAITING_DONE");
        player2.sendMessage("WAITING_DONE");

        player1.sendMessage("START");
        player2.sendMessage("START");

        // Send each player his own board
        player1.sendMessage(boardToMessage(player1Board));
        player2.sendMessage(boardToMessage(player2Board));

        player1.sendMessage("YOUR_TURN");
        player2.sendMessage("WAIT");

    }

    // Handle messages received from players
    public synchronized void handleMessage(ClientHandler sender, String message) {

        // Ignore messages if game already ended
        if (gameEnded) {
            System.out.println("[Session " + sessionId + "] Ignored message because game already ended: " + message);
            return;
        }

        String playerRole = sender.getPlayerRole();

        System.out.println("[Session " + sessionId + "] " + playerRole + " sent: " + message);

        // Handle player exit
        if (message.equals("EXIT")) {
            handleExit(sender);
            return;
        }

        // Handle attack message
        if (message.startsWith("ATTACK")) {
            handleAttack(sender, message);
            return;
        }

        System.out.println("[Session " + sessionId + "] Unknown message from " + playerRole + ": " + message);
    }

    // Handle attack from player
    private void handleAttack(ClientHandler sender, String message) {

        String playerRole = sender.getPlayerRole();

        // Check if it is the correct player turn
        if (!playerRole.equals(currentTurn)) {

            System.out.println("[Session " + sessionId + "] Invalid turn. " + playerRole + " tried to play, but current turn is " + currentTurn);

            sender.sendMessage("WAIT");

            return;
        }

        String[] parts = message.split(" ");

        // Check attack message format
        if (parts.length != 3) {

            System.out.println("[Session " + sessionId + "] Invalid ATTACK message format: " + message);

            return;
        }

        int row;
        int col;

        try {

            row = Integer.parseInt(parts[1]);
            col = Integer.parseInt(parts[2]);

        } catch (NumberFormatException e) {

            System.out.println("[Session " + sessionId + "] Invalid attack coordinates: " + message);

            return;
        }

        ClientHandler attacker;
        ClientHandler defender;
        Board defenderBoard;

        // Decide attacker and defender
        if (playerRole.equals("PLAYER_1")) {

            attacker = player1;
            defender = player2;
            defenderBoard = player2Board;

        } else {

            attacker = player2;
            defender = player1;
            defenderBoard = player1Board;
        }

        // Check attack result
        AttackResult result = defenderBoard.receiveAttack(row, col);

        System.out.println(
                "[Session " + sessionId + "] " + playerRole
                + " attacked cell (" + row + ", " + col + ") -> " + result
        );

        // Send result to both players
        attacker.sendMessage("RESULT " + result + " " + row + " " + col);
        defender.sendMessage("OPPONENT_ATTACK " + result + " " + row + " " + col);

        // Check if game ended
        if (defenderBoard.allShipsSunk()) {

            gameEnded = true;

            attacker.sendMessage("WIN");
            defender.sendMessage("LOSE");

            System.out.println("[Session " + sessionId + "] Game ended.");
            System.out.println("[Session " + sessionId + "] Winner: " + attacker.getPlayerRole());
            System.out.println("[Session " + sessionId + "] Loser: " + defender.getPlayerRole());

            closeSession();

            return;
        }

        // Change turn if attack missed
        if (result == AttackResult.MISS) {

            switchTurn();

            attacker.sendMessage("WAIT");
            defender.sendMessage("YOUR_TURN");

            System.out.println("[Session " + sessionId + "] Turn changed. Current turn: " + currentTurn);

        } else {

            // Same player continues if hit
            attacker.sendMessage("YOUR_TURN");
            defender.sendMessage("WAIT");

        }
    }

    // Handle normal player exit
    private void handleExit(ClientHandler sender) {

        gameEnded = true;

        ClientHandler opponent = getOpponent(sender);

        System.out.println("[Session " + sessionId + "] " + sender.getPlayerRole() + " exited the game.");

        if (opponent != null) {
            opponent.sendMessage("OPPONENT_EXITED");
            System.out.println("[Session " + sessionId + "] Opponent informed about exit.");
        }

        closeSession();
    }

    // Handle unexpected disconnect
    public synchronized void handleDisconnect(ClientHandler disconnectedPlayer) {

        if (gameEnded) {
            return;
        }

        gameEnded = true;

        ClientHandler opponent = getOpponent(disconnectedPlayer);

        System.out.println("[Session " + sessionId + "] " + disconnectedPlayer.getPlayerRole() + " disconnected unexpectedly.");

        if (opponent != null) {
            opponent.sendMessage("OPPONENT_EXITED");
            System.out.println("[Session " + sessionId + "] Opponent informed about disconnection.");
        }

        closeSession();
    }

    // Get the other player
    private ClientHandler getOpponent(ClientHandler player) {

        if (player == player1) {
            return player2;
        } else if (player == player2) {
            return player1;
        }

        return null;
    }

    // Change the current turn
    private void switchTurn() {

        if (currentTurn.equals("PLAYER_1")) {
            currentTurn = "PLAYER_2";
        } else {
            currentTurn = "PLAYER_1";
        }
    }

    // Close the session
    private void closeSession() {
        System.out.println("[Session " + sessionId + "] Session closed.");
    }

    // Generate random ships for a board
    private void generateRandomShips(Board board) {

        Random random = new Random();

        Ship[] ships = {
            new Ship("Carrier", 5),
            new Ship("Battleship", 4),
            new Ship("Cruiser", 3),
            new Ship("Submarine", 3),
            new Ship("Destroyer", 2)
        };

        for (Ship ship : ships) {

            boolean placed = false;

            // Try until the ship is placed correctly
            while (!placed) {

                int row = random.nextInt(10);
                int col = random.nextInt(10);
                boolean horizontal = random.nextBoolean();

                placed = board.placeShip(ship, row, col, horizontal);
            }
        }
    }

    // Convert board to message for client
    private String boardToMessage(Board board) {

        StringBuilder sb = new StringBuilder("BOARD ");

        Cell[][] grid = board.getGrid();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                if (grid[row][col].hasShip()) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
            }
        }

        return sb.toString();
    }
}