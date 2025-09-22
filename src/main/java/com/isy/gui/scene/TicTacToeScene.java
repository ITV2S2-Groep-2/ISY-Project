package com.isy.gui.scene;

import com.isy.game.Game;
import com.isy.game.ticTacToe.TicTacToeGame;
import com.isy.gui.PlayerTurnEventListener;
import com.isy.game.ticTacToe.Tile;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class TicTacToeScene extends Scene {
    private final JButton[][] boardButtons;
    private JPanel gridPanel;

    public TicTacToeScene(Window window) {
        super("ticTacToe", window);
        this.boardButtons = new JButton[][]{new JButton[]{null,null,null}, new JButton[]{null,null,null}, new JButton[]{null,null,null}};
    }

    @Override
    public void init() {
        JPanel controlPanel = this.getScenePanel();
        controlPanel.setLayout(new GridBagLayout());

        gridPanel = new JPanel();
        GridLayout layout = new GridLayout(3, 3);
        gridPanel.setSize(100, 100);
        gridPanel.setLayout(layout);

        controlPanel.add(gridPanel, new GridBagConstraints());
    }

    @Override
    public void initGame(Game game){
        TicTacToeGame ticTacToeGame = (TicTacToeGame) game;

        gridPanel.removeAll();

        for (int y = 0; y < ticTacToeGame.getBoard().getHeight(); y++) {
            for (int x = 0; x < ticTacToeGame.getBoard().getWidth(); x++) {
                final JButton button = new JButton(ticTacToeGame.getBoard().getTile(x, y).toString());
                button.setBackground(Style.primaryBackgroundColor);
                button.setBorder(new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setPreferredSize(new Dimension(80, 80));

                button.addActionListener(new PlayerTurnEventListener(ticTacToeGame, x, y));
                this.boardButtons[x][y] = button;
                gridPanel.add(button);
            }
        }
    }

    public void reloadBoardValues(TicTacToeGame ticTacToeGame) {
        Tile[][] tiles = ticTacToeGame.getBoard().getTiles();
        for (int y = 0; y < ticTacToeGame.getBoard().getHeight(); y++) {
            for (int x = 0; x < ticTacToeGame.getBoard().getWidth(); x++) {
                this.boardButtons[x][y].setText(tiles[x][y].toString());
                this.boardButtons[x][y].repaint();
            }
        }
    }
}
