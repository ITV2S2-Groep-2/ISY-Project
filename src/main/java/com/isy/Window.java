package com.isy;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class Window {
    private Board board;

    public Window(){
        this.board = new Board();
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(3, 3);
        panel.setSize(100, 100);
        panel.setLayout(layout);

        for (int x = 0; x < board.getTiles().length; x++) {
            for (int y = 0; y < board.getTiles()[x].length; y++) {
                final JButton button = new JButton(board.getTile(x, y).toString());
                button.setBackground(new Color(255, 255, 255));
                button.setBorder(new StrokeBorder(new BasicStroke(2), new Color(0, 0, 0)));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setPreferredSize(new Dimension(50, 50));

                final int finalX = x;
                final int finalY = y;
                button.addActionListener((e -> {
                    board.setTile(finalX, finalY, Tile.X);
                    button.setText(board.getTile(finalX, finalY).toString());
                }));

                panel.add(button);
            }
        }

        controlPanel.add(panel);
        frame.add(controlPanel);

        //Display the window.
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
