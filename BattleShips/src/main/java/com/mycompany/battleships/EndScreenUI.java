/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class EndScreenUI extends JFrame {

    private JLabel resultLabel;
    private JButton playAgainButton;
    private JButton exitButton;

    public EndScreenUI(String resultText) {
        initializeFrame();
        initializeComponents(resultText);
        buildLayout();
        setVisible(true);

        playAgainButton.addActionListener(e -> {
            new StartScreenUI();
            dispose();
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });
    }

    private void initializeFrame() {
        setTitle("Battleship - Game Over");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void initializeComponents(String resultText) {
        resultLabel = new JLabel(resultText, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        resultLabel.setForeground(Color.WHITE);

        JLabel gameOverLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
        gameOverLabel.setForeground(new Color(255, 215, 100));

        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        playAgainButton.setBackground(new Color(46, 204, 113));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setPreferredSize(new Dimension(180, 50));

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(180, 50));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(8, 36, 64));

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(13, 52, 89));
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(90, 150, 210), 3, true),
                new EmptyBorder(50, 80, 50, 80)
        ));

        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(gameOverLabel);
        cardPanel.add(Box.createVerticalStrut(25));
        cardPanel.add(resultLabel);
        cardPanel.add(Box.createVerticalStrut(50));
        cardPanel.add(playAgainButton);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(exitButton);

        mainPanel.add(cardPanel);
        setContentPane(mainPanel);
    }

    private void buildLayout() {
    }
    public void calcutetime(){
        int totaltime = 0;
    }
    public JButton getPlayAgainButton() {
        return playAgainButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EndScreenUI("You Win!"));
    }
}
