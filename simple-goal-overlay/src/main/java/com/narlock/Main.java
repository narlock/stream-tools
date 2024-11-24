package com.narlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Main {

    public static int sharedTextX; // Shared X-coordinate for title and goals

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Goals");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true); // Remove window decorations
            frame.setBackground(new Color(0, 0, 0, 0)); // Set the background to be completely transparent

            // Shared title state
            final String[] titleText = {"Goals"}; // Title stored in an array to allow modification

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;

                    // Enable anti-aliasing for smoother text rendering
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    // Custom painting for the title text
                    g2d.setFont(new Font("Impact", Font.BOLD | Font.ITALIC, 36));
                    String text = titleText[0];

                    // Calculate the width of the red line
                    int lineWidth = (int) (getWidth() * 0.7); // Line is 70% of the panel width
                    int lineX = (getWidth() - lineWidth) / 2; // Center the line horizontally

                    // Calculate the width of the text
                    FontMetrics metrics = g2d.getFontMetrics();
                    int textWidth = metrics.stringWidth(text);

                    // Center the text with respect to the red line's center
                    int textX = lineX + (lineWidth - textWidth) / 2; // Center the text horizontally within the line
                    int textY = 148; // Fixed Y-coordinate for the title

                    // Semi-transparent drop shadow for the title text
                    g2d.setColor(new Color(0, 0, 0, 128)); // Black with 50% opacity
                    g2d.drawString(text, textX + 4, textY + 4); // Shadow position slightly offset

                    // Black border (draw text multiple times slightly offset for border effect)
                    g2d.drawString(text, textX - 1, textY); // Left
                    g2d.drawString(text, textX + 1, textY); // Right
                    g2d.drawString(text, textX, textY - 1); // Top
                    g2d.drawString(text, textX, textY + 1); // Bottom

                    // Main text (white)
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(text, textX, textY);

                    // Draw drop shadow for the line
                    g2d.setColor(new Color(0, 0, 0, 128)); // Semi-transparent black (50% opacity)
                    int lineY = 165; // Fixed Y-coordinate for the red line
                    g2d.fillRect(lineX + 4, lineY + 4, lineWidth, 12); // Offset shadow slightly (4px right, 4px down)

                    // Draw gradient line below the title text
                    GradientPaint gradient = new GradientPaint(
                            lineX, lineY, new Color(0xff1244), // Start color (left side)
                            lineX + lineWidth, lineY, new Color(0xde001b) // End color (right side)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(lineX, lineY, lineWidth, 12); // Draw the gradient line

                    // Store textX in a shared variable for use in goal alignment
                    Main.sharedTextX = textX;
                }

            };
            panel.setOpaque(false);
            panel.setLayout(null);

            ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
            int[] currentY = {190}; // Keep track of the Y position for new checkboxes

            JButton exitButton = new JButton("Exit");
            exitButton.setBounds(20, 20, 80, 30);
            exitButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            });
            panel.add(exitButton);

            JButton resetButton = new JButton("Reset");
            resetButton.setBounds(110, 20, 80, 30);
            resetButton.addActionListener(e -> {
                for (JCheckBox checkBox : checkBoxes) {
                    panel.remove(checkBox);
                }
                checkBoxes.clear();
                currentY[0] = 190;
                panel.revalidate();
                panel.repaint();
            });
            panel.add(resetButton);

            JTextField titleInput = new JTextField();
            JButton changeTitleButton = new JButton("Set Title");
            titleInput.setBounds(200, 20, 150, 30);
            changeTitleButton.setBounds(360, 20, 100, 30);

            changeTitleButton.addActionListener(e -> {
                String newTitle = titleInput.getText().trim();
                if (!newTitle.isEmpty()) {
                    titleText[0] = newTitle;
                    panel.repaint();
                }
            });

            panel.add(titleInput);
            panel.add(changeTitleButton);

            JTextField goalInput = new JTextField();
            JButton addButton = new JButton("Add Goal");

            goalInput.setBounds(20, 60, 250, 30);
            addButton.setBounds(280, 60, 100, 30);
            panel.add(goalInput);
            panel.add(addButton);

            addButton.addActionListener(e -> {
                String goalText = goalInput.getText().trim();
                if (!goalText.isEmpty()) {
                    JCheckBox newGoal = createCustomCheckBox(goalText);
                    int goalX = sharedTextX; // Align X with the title's textX
                    newGoal.setBounds(goalX, currentY[0], 360, 30); // Adjust width and height as needed
                    checkBoxes.add(newGoal);
                    panel.add(newGoal);
                    panel.revalidate();
                    panel.repaint();
                    currentY[0] += 35; // Spacing between goals
                    goalInput.setText("");
                }
            });

            MouseAdapter mouseAdapter = new MouseAdapter() {
                private Point mouseDownCompCoords = null;

                @Override
                public void mousePressed(MouseEvent e) {
                    mouseDownCompCoords = e.getPoint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (mouseDownCompCoords != null) {
                        Point currCoords = e.getLocationOnScreen();
                        frame.setLocation(currCoords.x - mouseDownCompCoords.x,
                                currCoords.y - mouseDownCompCoords.y);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mouseDownCompCoords = null;
                }
            };

            panel.addMouseListener(mouseAdapter);
            panel.addMouseMotionListener(mouseAdapter);

            frame.setLayout(new BorderLayout());
            frame.add(panel);

            frame.setSize(500, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JCheckBox createCustomCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));

                int boxSize = 20;
                int boxX = 5;
                int boxY = (getHeight() - boxSize) / 2;
                int textX = boxX + boxSize + 10;
                int textY = (getHeight() + g2d.getFontMetrics().getAscent()) / 2 - 2;

                g2d.setColor(Color.WHITE);
                g2d.fillRect(boxX, boxY, boxSize, boxSize);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(boxX, boxY, boxSize, boxSize);

                if (isSelected()) {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawLine(boxX + 4, boxY + boxSize / 2, boxX + boxSize / 2, boxY + boxSize - 4);
                    g2d.drawLine(boxX + boxSize / 2, boxY + boxSize - 4, boxX + boxSize - 4, boxY + 4);
                }

                g2d.setColor(new Color(0, 0, 0, 128));
                g2d.drawString(text, textX + 2, textY + 2);

                g2d.setColor(Color.WHITE);
                g2d.drawString(text, textX, textY);
            }
        };

        checkBox.setOpaque(false);
        return checkBox;
    }
}
