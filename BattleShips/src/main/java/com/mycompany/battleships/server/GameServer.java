/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("==========================================");
            System.out.println(" Battleship Server Started");
            System.out.println(" Port: " + PORT);
            System.out.println(" Waiting for clients...");
            System.out.println("==========================================");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("[Server] New client connected: "
                        + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                ClientHandler newClient = new ClientHandler(clientSocket);

                synchronized (GameServer.class) {
                    if (waitingPlayer == null) {
                        waitingPlayer = newClient;
                        waitingPlayer.setPlayerRole("PLAYER_1");

                        waitingPlayer.sendMessage("PLAYER_1");
                        waitingPlayer.sendMessage("WAIT");

                        waitingPlayer.start();

                        System.out.println("[Server] Waiting for another player to create a match.");

                    } else {
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
