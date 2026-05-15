
package com.mycompany.battleships.server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
public class GameServer {

    private static final int PORT = 5000;

    private static ClientHandler waitingPlayer = null;
    private static final AtomicInteger sessionCounter = new AtomicInteger(1);

    // Remove player if he was waiting and disconnected
    public static synchronized void removeWaitingPlayer(ClientHandler client) {
        if (waitingPlayer == client) {
            waitingPlayer = null;
            System.out.println("[Server] Waiting player disconnected and was removed from queue.");
        }
    }

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("==========================================");
            System.out.println(" Battleship Server Started");
            System.out.println(" Port: " + PORT);
            System.out.println(" Waiting for clients...");
            System.out.println("==========================================");

            // Keep server running and accepting clients
            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println("[Server] New client connected: "
                        + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                ClientHandler newClient = new ClientHandler(clientSocket);

                synchronized (GameServer.class) {

                    // First player waits for another player
                    if (waitingPlayer == null) {

                        waitingPlayer = newClient;
                        waitingPlayer.setPlayerRole("PLAYER_1");

                        waitingPlayer.sendMessage("PLAYER_1");
                        waitingPlayer.sendMessage("WAIT");

                        waitingPlayer.start();

                        System.out.println("[Server] Waiting for another player to create a match.");

                    } else {

                        // Second player joins and game session starts
                        ClientHandler player1 = waitingPlayer;
                        ClientHandler player2 = newClient;

                        waitingPlayer = null;

                        player2.setPlayerRole("PLAYER_2");

                        int sessionId = sessionCounter.getAndIncrement();

                        GameSession session = new GameSession(sessionId, player1, player2);

                        player2.start();

                        session.startSession();

                        System.out.println("[Server] Session " + sessionId + " is now running.");
                    }
                }
            }

        } catch (BindException e) {

            System.out.println("[Server] Port " + PORT + " is already in use.");
            System.out.println("[Server] Please stop the previous server first, or use another port.");

        } catch (IOException e) {

            System.out.println("[Server] Server error occurred.");
            e.printStackTrace();

        }
    }
}