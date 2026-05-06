/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 5000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            String tempMassage;
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server: " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}