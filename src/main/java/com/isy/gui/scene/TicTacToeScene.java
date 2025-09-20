package com.isy.gui.scene;

import com.isy.PlayerTurnEventListener;
import com.isy.Tile;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class TicTacToeScene extends Scene {
    JButton[][] boardButtons;

    public TicTacToeScene(Window window) {
        super("ticTacToe", window);
        this.boardButtons = new JButton[][]{new JButton[]{null,null,null}, new JButton[]{null,null,null}, new JButton[]{null,null,null}};
    }

    @Override
    public void init() {
        JPanel controlPanel = this.getScenePanel();
        controlPanel.setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(3, 3);
        panel.setSize(100, 100);
        panel.setLayout(layout);

        for (int y = 0; y < this.getWindow().getBoard().getHeight(); y++) {
            for (int x = 0; x < this.getWindow().getBoard().getWidth(); x++) {
                final JButton button = new JButton(this.getWindow().getBoard().getTile(x, y).toString());
                button.setBackground(Style.primaryBackgroundColor);
                button.setBorder(new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setPreferredSize(new Dimension(80, 80));

                button.addActionListener(new PlayerTurnEventListener(this.getWindow().getGame(), x, y));
                this.boardButtons[x][y] = button;
                panel.add(button);
            }
        }
        controlPanel.add(panel, new GridBagConstraints());
    }

    public void reloadBoardValues() {
        Tile[][] tiles = this.getWindow().getBoard().getTiles();
        for (int y = 0; y < this.getWindow().getBoard().getHeight(); y++) {
            for (int x = 0; x < this.getWindow().getBoard().getWidth(); x++) {
                this.boardButtons[x][y].setText(tiles[x][y].toString());
                this.boardButtons[x][y].repaint();
            }
        }
    }
}
