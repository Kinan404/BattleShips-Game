/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import com.mycompany.battleships.server.ClientConnection;


public class StartScreenUI extends JFrame {

    private BackgroundPanel backgroundPanel;

    private JTextField nameField;
    private JButton startButton;
    private JLabel statusLabel;
    private ClientConnection clientConnection;

    public StartScreenUI() {

        initializeFrame();
        initializeComponents();
        buildLayout();

        // Create connection object for this screen
        clientConnection = new ClientConnection(this);

        setVisible(true);

    }

    // Setup start screen window
    private void initializeFrame() {

        setTitle("Battleship - Start");
        setSize(1100, 700);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Create start screen components
    private void initializeComponents() {

        nameField = new JTextField(15);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameField.setBorder(new CompoundBorder(
                new LineBorder(new Color(100, 160, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startButton.setBackground(new Color(46, 125, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setPreferredSize(new Dimension(160, 40));

        // Label for connection status
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(200, 220, 240));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Connect to server when start button is clicked
        startButton.addActionListener(e -> {
            setStatusText("Connecting to server...");
            startButton.setEnabled(false);
//            clientConnection.connectToServer("13.60.235.82", 5000);
            clientConnection.connectToServer("127.0.0.1", 5000);

            String playerName = nameField.getText().trim();

            // Send player name to connection class
            clientConnection.setPlayerName(playerName);
        });
    }

    // Update status text safely
    public void setStatusText(String text) {
        javax.swing.SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }


        // Build the start screen design
    private void buildLayout() {

        backgroundPanel = new BackgroundPanel("C:\\Users\\Kinan\\Documents\\NetBeansProjects\\BattleShips\\src\\main\\java\\com\\mycompany\\battleships\\Welcome_Page_Background.png");
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel overlayCard = new JPanel();
        overlayCard.setLayout(new BoxLayout(overlayCard, BoxLayout.Y_AXIS));
        overlayCard.setOpaque(true);
        overlayCard.setBackground(new Color(74, 70, 117));
        overlayCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(120, 180, 230), 2, true),
                new EmptyBorder(40, 55, 40, 55)
        ));
        overlayCard.setPreferredSize(new Dimension(420, 320));

        // Set card location
        backgroundPanel.setLayout(null);
        overlayCard.setBounds(350, 180, 400, 350);

        JLabel title = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Prepare for battle and enter your name", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(220, 235, 250));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField.setMaximumSize(new Dimension(280, 42));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.setPreferredSize(new Dimension(180, 46));
        startButton.setMaximumSize(new Dimension(180, 46));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        overlayCard.add(title);
        overlayCard.add(Box.createVerticalStrut(18));
        overlayCard.add(subtitle);
        overlayCard.add(Box.createVerticalStrut(28));
        overlayCard.add(nameField);
        overlayCard.add(Box.createVerticalStrut(30));
        overlayCard.add(startButton);
        overlayCard.add(Box.createVerticalStrut(15));
        overlayCard.add(statusLabel);

        backgroundPanel.add(overlayCard);
        setContentPane(backgroundPanel);
    }

    // Panel used to show background image
    private static class BackgroundPanel extends JPanel {

        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {

            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            // Draw image if it exists
            if (backgroundImage != null) {

                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            } else {

                // Use gradient if image cannot be loaded
                Graphics2D g2d = (Graphics2D) g;

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(6, 24, 44),
                        getWidth(), getHeight(), new Color(18, 84, 128)
                );

                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // Return start button
    public JButton getStartButton() {
        return startButton;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public static void main(String[] args) {
    }
}
