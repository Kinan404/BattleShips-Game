
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

    // Send message to this client
    public void sendMessage(String message) {
        out.println(message);
    }

    // Return client IP and port
    public String getClientInfo() {
        return socket.getInetAddress().toString() + ":" + socket.getPort();
    }

    @Override
    public void run() {

        try {
            String message;

            // Keep reading client messages
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

            // Inform session when client disconnects
            if (gameSession != null) {
                gameSession.handleDisconnect(this);
            }

            GameServer.removeWaitingPlayer(this);

            closeConnection();
        }
    }

    // Close client socket
    private void closeConnection() {

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("[ClientHandler] Error while closing connection for " + getClientInfo());
        }
    }
}
