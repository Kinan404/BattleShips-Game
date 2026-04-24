/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships.server;

import com.mycompany.battleships.HelperClasses.AttackResult;
import com.mycompany.battleships.HelperClasses.Board;
import com.mycompany.battleships.HelperClasses.Cell;
import com.mycompany.battleships.HelperClasses.Ship;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class GameServer {

    private static final int PORT = 5000;

    private static ClientHandler player1;
    private static ClientHandler player2;
    private static String currentTurn = "PLAYER_1";

    private static Board player1Board;
    private static Board player2Board;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for players...");

            while (true) {
                System.out.println("Waiting for new match...");

                Socket player1Socket = serverSocket.accept();
                System.out.println("Player 1 connected: " + player1Socket.getInetAddress());

                Socket player2Socket = serverSocket.accept();
                System.out.println("Player 2 connected: " + player2Socket.getInetAddress());

                startNewMatch(player1Socket, player2Socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void startNewMatch(Socket player1Socket, Socket player2Socket) {
        currentTurn = "PLAYER_1";

        player1 = new ClientHandler(player1Socket, "PLAYER_1");
        player2 = new ClientHandler(player2Socket, "PLAYER_2");

        player1Board = new Board();
        player2Board = new Board();

        generateRandomShips(player1Board);
        generateRandomShips(player2Board);

        player1.setServer(GameServer::handleMessageFromClient);
        player2.setServer(GameServer::handleMessageFromClient);

        player1.start();
        player2.start();

        player1.sendMessage("PLAYER_1");
        player2.sendMessage("PLAYER_2");

        player1.sendMessage("WAITING_DONE");
        player2.sendMessage("WAITING_DONE");

        player1.sendMessage("START");
        player2.sendMessage("START");

        player1.sendMessage(boardToMessage(player1Board));
        player2.sendMessage(boardToMessage(player2Board));

        player1.sendMessage("YOUR_TURN");
        player2.sendMessage("WAIT");

        System.out.println("New match started.");
    }

    private static void handleMessageFromClient(String playerRole, String message) {
        System.out.println(playerRole + " sent: " + message);

        if (message.startsWith("ATTACK")) {

            if (!playerRole.equals(currentTurn)) {
                return;
            }

            String[] parts = message.split(" ");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);

            ClientHandler attacker;
            ClientHandler defender;
            Board defenderBoard;

            if (playerRole.equals("PLAYER_1")) {
                attacker = player1;
                defender = player2;
                defenderBoard = player2Board;
            } else {
                attacker = player2;
                defender = player1;
                defenderBoard = player1Board;
            }

            AttackResult result = defenderBoard.receiveAttack(row, col);

            attacker.sendMessage("RESULT " + result + " " + row + " " + col);
            defender.sendMessage("OPPONENT_ATTACK " + result + " " + row + " " + col);

            if (defenderBoard.allShipsSunk()) {
                attacker.sendMessage("WIN");
                defender.sendMessage("LOSE");
                return;
            }

            if (result == AttackResult.MISS) {
                if (playerRole.equals("PLAYER_1")) {
                    currentTurn = "PLAYER_2";
                } else {
                    currentTurn = "PLAYER_1";
                }

                attacker.sendMessage("WAIT");
                defender.sendMessage("YOUR_TURN");
            } else {
                attacker.sendMessage("YOUR_TURN");
                defender.sendMessage("WAIT");
            }
        }
    }

    private static void generateRandomShips(Board board) {
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

            while (!placed) {
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                boolean horizontal = random.nextBoolean();

                placed = board.placeShip(ship, row, col, horizontal);
            }
        }
    }

    private static String boardToMessage(Board board) {
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
