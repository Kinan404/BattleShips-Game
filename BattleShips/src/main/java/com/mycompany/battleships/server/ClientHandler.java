/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    private String playerRole;
    private GameSession gameSession;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void setPlayerRole(String playerRole) {
        this.playerRole = playerRole;
    }

    public String getPlayerRole() {
        return playerRole;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientInfo() {
        return socket.getInetAddress().toString() + ":" + socket.getPort();
    }

    @Override
    public void run() {

        try {
            String message;

            while ((message = in.readLine()) != null) {
                if (gameSession != null) {
                    gameSession.handleMessage(this, message);
                } else {
                    System.out.println("[ClientHandler] Message received before session was assigned: " + message);
                }
            }

        } catch (IOException e) {
            System.out.println("[ClientHandler] Client disconnected : " + getClientInfo());
        } finally {
            if (gameSession != null) {
                gameSession.handleDisconnect(this);
            }

            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("[ClientHandler] Error while closing connection for " + getClientInfo());
        }
    }
}