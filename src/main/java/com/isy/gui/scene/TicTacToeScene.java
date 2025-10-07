package com.isy.gui.scene;

import com.isy.game.Game;
import com.isy.game.ticTacToe.TicTacToeGame;
import com.isy.gui.PlayerTurnEventListener;
import com.isy.game.ticTacToe.Tile;
import com.isy.gui.Window;
import com.isy.gui.components.BoardTile;
import com.isy.gui.components.Label;
import com.isy.gui.lang.LangHandler;

import javax.swing.*;
import java.awt.*;

public class TicTacToeScene extends Scene {
    private final JButton[][] boardButtons;
    private JPanel gridPanel;
    private JLabel playerNameLabel;

    public TicTacToeScene(Window window) {
        super("ticTacToe", window);
        this.boardButtons = new JButton[][]{new JButton[]{null,null,null}, new JButton[]{null,null,null}, new JButton[]{null,null,null}};
    }

    @Override
    public void init() {
        JPanel controlPanel = this.getScenePanel();
        controlPanel.setLayout(new GridBagLayout());

        playerNameLabel = Label.createLabel();
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        labelConstraints.insets = new Insets(0, 0, 10, 0);
        labelConstraints.anchor = GridBagConstraints.CENTER;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;

        controlPanel.add(playerNameLabel, labelConstraints);

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
                final JButton button = BoardTile.createButton(ticTacToeGame.getBoard().getTile(x, y).toString());

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

    public void setPlayerName(String name) {
        if (playerNameLabel != null) {
            playerNameLabel.setText(LangHandler.get().translate("player.name.display", name));
        }
    }
}
