package com.isy;

import javax.swing.*;
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

        JTable table = new JTable(3, 3);
        for (int x = 0; x < board.getTiles().length; x++) {
            for (int y = 0; y < board.getTiles()[x].length; y++) {
                table.setValueAt(board.getTiles()[x][y].toString(), x, y);
            }
        }

        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(100, 100));

        frame.add(pane);

        //Display the window.
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
