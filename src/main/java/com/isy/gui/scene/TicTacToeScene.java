package com.isy.gui.scene;

import com.isy.Tile;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class TicTacToeScene extends Scene {
    public TicTacToeScene(Window window) {
        super("ticTacToe", window);
    }

    @Override
    public void init() {
        JPanel controlPanel = this.getScenePanel();
        controlPanel.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(3, 3);
        panel.setSize(100, 100);
        panel.setLayout(layout);

        for (int x = 0; x < this.getWindow().getBoard().getTiles().length; x++) {
            for (int y = 0; y < this.getWindow().getBoard().getTiles()[x].length; y++) {
                final JButton button = new JButton(this.getWindow().getBoard().getTile(x, y).toString());
                button.setBackground(Style.primaryBackgroundColor);
                button.setBorder(new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setPreferredSize(new Dimension(50, 50));

                final int finalX = x;
                final int finalY = y;
                button.addActionListener((e -> {
                    this.getWindow().getBoard().setTile(finalX, finalY, Tile.X);
                    button.setText(this.getWindow().getBoard().getTile(finalX, finalY).toString());
                }));

                panel.add(button);
            }
        }

        controlPanel.add(panel);
    }
}
