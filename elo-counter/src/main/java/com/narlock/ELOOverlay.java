package com.narlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ELOOverlay extends JFrame {
    private int initialELO = 0;
    private int currentELO = 0;
    private int wins = 0;
    private int losses = 0;
    private int eloChange = 0;
    private boolean allowSetInitialELO = true;

    private JLabel eloLabel;
    private JLabel winsLabel;
    private JLabel lossesLabel;
    private JLabel eloChangeLabel;

    private Point mousePressLocation;

    public ELOOverlay() {
        // JFrame settings
        setTitle("ELO Overlay");
        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(400, 300);

        // Theme dropdown at the top
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setOpaque(false);
        JComboBox<String> themeDropdown = new JComboBox<>(new String[]{"Basic", "CS2"});
        themeDropdown.addActionListener(e -> applyTheme((String) themeDropdown.getSelectedItem()));
        themePanel.add(new JLabel("Theme:"));
        themePanel.add(themeDropdown);

        // Add draggable area
        JPanel draggableArea = new JPanel();
        draggableArea.setOpaque(false);
        draggableArea.setPreferredSize(new Dimension(400, 20));
        draggableArea.setCursor(new Cursor(Cursor.MOVE_CURSOR));

        draggableArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressLocation = e.getPoint();
            }
        });

        draggableArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentScreenLocation = e.getLocationOnScreen();
                setLocation(currentScreenLocation.x - mousePressLocation.x,
                        currentScreenLocation.y - mousePressLocation.y);
            }
        });

        // Panels for controls
        JPanel initialELOPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        initialELOPanel.setOpaque(false);
        JTextField initialELOField = new JTextField(5);
        JButton setInitialELOButton = new JButton("Set Initial ELO");
        JButton lockButton = new JButton("Lock");

        setInitialELOButton.addActionListener(e -> {
            if (allowSetInitialELO) {
                try {
                    initialELO = Integer.parseInt(initialELOField.getText());
                    currentELO = initialELO;
                    wins = 0;
                    losses = 0;
                    eloChange = 0;
                    updateLabels();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for ELO.");
                }
            }
        });

        lockButton.addActionListener(e -> {
            allowSetInitialELO = !allowSetInitialELO;
            lockButton.setText(allowSetInitialELO ? "Lock" : "Unlk");
        });

        initialELOPanel.add(initialELOField);
        initialELOPanel.add(setInitialELOButton);
        initialELOPanel.add(lockButton);

        JPanel newELOPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newELOPanel.setOpaque(false);
        JTextField newELOField = new JTextField(5);
        JButton addGameButton = new JButton("Add Game (New Elo)");

        addGameButton.addActionListener(e -> {
            try {
                int newELO = Integer.parseInt(newELOField.getText());
                if (newELO > currentELO) {
                    wins++;
                    eloChange += (newELO - currentELO);
                } else if (newELO < currentELO) {
                    losses++;
                    eloChange -= (currentELO - newELO);
                }
                currentELO = newELO;
                updateLabels();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for ELO.");
            }
        });

        newELOPanel.add(newELOField);
        newELOPanel.add(addGameButton);

        // Black rectangle for overlay information
        JPanel infoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                int width = 330;
                int height = 40;
                g2d.fillRoundRect(35, 5, width, height, 20, 20);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 120);
            }
        };

        infoPanel.setOpaque(false);
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Add draggable functionality to the black rectangle
        infoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressLocation = e.getPoint();
            }
        });

        infoPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentScreenLocation = e.getLocationOnScreen();
                setLocation(currentScreenLocation.x - mousePressLocation.x,
                        currentScreenLocation.y - mousePressLocation.y);
            }
        });

        eloLabel = new JLabel();
        winsLabel = new JLabel();
        lossesLabel = new JLabel();
        eloChangeLabel = new JLabel();

        eloLabel.setFont(new Font("Arial", Font.BOLD, 24));
        winsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        lossesLabel.setFont(new Font("Arial", Font.BOLD, 24));
        eloChangeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        infoPanel.add(eloLabel);
        infoPanel.add(winsLabel);
        infoPanel.add(lossesLabel);
        infoPanel.add(eloChangeLabel);

        add(themePanel);
        add(draggableArea);
        add(initialELOPanel);
        add(newELOPanel);
        add(infoPanel);

        applyTheme("Basic");
        updateLabels();
        setVisible(true);
    }

    private void updateLabels() {
        eloLabel.setText(String.valueOf(currentELO));
        winsLabel.setText("W: " + wins);
        lossesLabel.setText("L: " + losses);

        String eloText = (eloChange > 0) ? "+" + eloChange : String.valueOf(eloChange);
        eloChangeLabel.setText(eloText);

        // Update colors based on the value of ELO change
        if (eloText.equals("0")) {
            eloChangeLabel.setForeground(Color.WHITE);
        } else if (eloText.startsWith("+")) {
            eloChangeLabel.setForeground(Color.GREEN);
        } else {
            eloChangeLabel.setForeground(Color.RED);
        }

        // Update ELO color for the CS2 theme
        if ("CS2".equals(getCurrentTheme())) {
            updateEloColorCS2();
        }
    }

    private void applyTheme(String theme) {
        if ("Basic".equals(theme)) {
            eloLabel.setForeground(Color.WHITE);
            winsLabel.setForeground(Color.GREEN);
            lossesLabel.setForeground(Color.RED);
        } else if ("CS2".equals(theme)) {
            updateEloColorCS2();
        }
    }

    private String getCurrentTheme() {
        JComboBox<String> themeDropdown = (JComboBox<String>) ((JPanel) getContentPane().getComponent(0)).getComponent(1);
        return (String) themeDropdown.getSelectedItem();
    }

    private void updateEloColorCS2() {
        if (currentELO < 5000) {
            eloLabel.setForeground(Color.decode("#bfcfe9")); // Grey
        } else if (currentELO < 10000) {
            eloLabel.setForeground(Color.decode("#79cdfa")); // Light Blue
        } else if (currentELO < 15000) {
            eloLabel.setForeground(Color.decode("#6b91f8")); // Blue
        } else if (currentELO < 20000) {
            eloLabel.setForeground(Color.decode("#aa62ff")); // Purple
        } else if (currentELO < 25000) {
            eloLabel.setForeground(Color.decode("#c03ac4")); // Pink
        } else if (currentELO < 30000) {
            eloLabel.setForeground(Color.decode("#e75655")); // Red
        } else {
            eloLabel.setForeground(Color.decode("#edac39")); // Gold
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ELOOverlay::new);
    }
}
