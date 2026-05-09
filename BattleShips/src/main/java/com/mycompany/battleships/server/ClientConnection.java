/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships.server;

import com.mycompany.battleships.BattleshipClientUI;
import com.mycompany.battleships.HelperClasses.AttackResult;
import com.mycompany.battleships.StartScreenUI;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;

public class ClientConnection {

    private Socket socket;
    private BufferedReader in;
    private StartScreenUI startScreenUI;

    private String playerRole;

    private BattleshipClientUI gameUI;

    private PrintWriter out;

    private String playerName;

    // solving the problem of not show the ships for each player
    private String pendingBoardData;

    public ClientConnection(StartScreenUI startScreenUI) {
        this.startScreenUI = startScreenUI;
    }

    public void connectToServer(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                startScreenUI.setStatusText("Connected. Waiting for another player...");

                String message;
                while ((message = in.readLine()) != null) {
                    handleServerMessage(message);
                }

            } catch (Exception e) {
                startScreenUI.setStatusText("Connection failed");
                e.printStackTrace();
            }
        }).start();
    }

    private void handleServerMessage(String message) {

        if (message.startsWith("RESULT")) {
            String[] parts = message.split(" ");
            String result = parts[1];
            int row = Integer.parseInt(parts[2]);
            int col = Integer.parseInt(parts[3]);

            if (gameUI != null) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (result.equals("MISS")) {
                        gameUI.updateEnemyBoardUI(row, col, AttackResult.MISS);
                        gameUI.setStatusText("Miss!");
                    } else if (result.equals("HIT")) {
                        gameUI.updateEnemyBoardUI(row, col, AttackResult.HIT);
                        gameUI.setStatusText("Hit!");
                    } else if (result.equals("SUNK")) {
                        gameUI.updateEnemyBoardUI(row, col, AttackResult.SUNK);
                        gameUI.setStatusText("Ship sunk!");
                    }
                });
            }
            return;
        }

        if (message.startsWith("OPPONENT_ATTACK")) {
            String[] parts = message.split(" ");
            String resultText = parts[1];
            int row = Integer.parseInt(parts[2]);
            int col = Integer.parseInt(parts[3]);

            if (gameUI != null) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    AttackResult result = AttackResult.valueOf(resultText);
                    gameUI.handleAttackOnMyBoardFromNetwork(row, col, result);
                });
            }
            return;
        }
        if (message.startsWith("BOARD ")) {
            String boardData = message.substring(6);

            if (gameUI != null) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    gameUI.showShipsFromServer(boardData);
                });
            } else {
                pendingBoardData = boardData;
            }
            return;
        }
        switch (message) {
            case "PLAYER_1":
                playerRole = "PLAYER_1";
                startScreenUI.setStatusText("You are Player 1. Waiting...");
                break;

            case "PLAYER_2":
                playerRole = "PLAYER_2";
                startScreenUI.setStatusText("You are Player 2. Waiting...");
                break;

            case "WAITING_DONE":
                startScreenUI.setStatusText("Both players connected");
                break;

            case "START":
                startScreenUI.setStatusText("Game starting...");

                javax.swing.SwingUtilities.invokeLater(() -> {
                    gameUI = new BattleshipClientUI(playerRole, playerName, this);
                    startScreenUI.dispose();

                    if (pendingBoardData != null) {
                        gameUI.showShipsFromServer(pendingBoardData);
                        pendingBoardData = null;
                    }

                    if ("PLAYER_1".equals(playerRole)) {
                        gameUI.setMyTurn(true);
                        gameUI.setTurnText("Your Turn");
                    } else {
                        gameUI.setMyTurn(false);
                        gameUI.setTurnText("Opponent Turn");
                    }
                });
                break;

            case "YOUR_TURN":
                if (gameUI != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        gameUI.setTurnText("Your Turn");
                        gameUI.setStatusText("Your turn to play");
                        gameUI.setMyTurn(true);
                    });
                }
                break;

            case "WAIT":
                if (gameUI != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        gameUI.setTurnText("Opponent Turn");
                        gameUI.setStatusText("Wait for opponent");
                        gameUI.setMyTurn(false);
                    });
                }
                break;
            case "WIN":
                if (gameUI != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        gameUI.setMyTurn(false);
                        gameUI.setStatusText("You win!");
                        gameUI.setTurnText("Game Over");
                        gameUI.showEndScreen("You Win!");
                    });
                }
                break;

            case "LOSE":
                if (gameUI != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        gameUI.setMyTurn(false);
                        gameUI.setStatusText("You lose!");
                        gameUI.setTurnText("Game Over");
                        gameUI.showEndScreen("You Lose!");
                    });
                }
                break;
            case "OPPONENT_EXITED":
                if (gameUI != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        gameUI.setMyTurn(false);
                        gameUI.setStatusText("Opponent left the game.");
                        gameUI.setTurnText("Game Over");
                        gameUI.showEndScreen("Opponent left the game.");
                    });
                }
                break;

            default:
                startScreenUI.setStatusText("Server: " + message);
                break;
        }
    }

    public void sendAttack(int row, int col) {
        if (out != null) {
            out.println("ATTACK " + row + " " + col);
        }
    }

    public void sendExit() {
        if (out != null) {
            out.println("EXIT");
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPlayerRole() {
        return playerRole;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }
}
// the end of the function 

