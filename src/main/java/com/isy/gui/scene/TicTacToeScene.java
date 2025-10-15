package com.isy.gui.scene;

import com.isy.await.Await;
import com.isy.game.Game;
import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.ticTacToe.GameState;
import com.isy.game.ticTacToe.TicTacToeGame;
import com.isy.gui.PlayerEventManager;
import com.isy.gui.PlayerTurnEventListener;
import com.isy.game.ticTacToe.Tile;
import com.isy.gui.Window;
import com.isy.gui.components.BoardTile;
import com.isy.gui.components.Label;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TicTacToeScene extends Scene {
    private final JButton[][] boardButtons;
    private JPanel gridPanel;
    private JLabel playerNameLabel;
    private TicTacToeGame game;

    public TicTacToeScene(Window window) {
        super("ticTacToe", window);
        this.boardButtons = new JButton[][]{new JButton[]{null,null,null}, new JButton[]{null,null,null}, new JButton[]{null,null,null}};
    }

    @Override
    public void init() {
        JPanel controlPanel = this.getScenePanel();
        controlPanel.setLayout(new GridBagLayout());

        JButton forfeitButton = UIButton.createButton("Forfeit", this::goForfeit);
        forfeitButton.setPreferredSize(new Dimension(128, 32));
        GridBagConstraints forfeitConstraints = new GridBagConstraints();
        forfeitConstraints.gridx = 0;
        forfeitConstraints.gridy = 0;
        forfeitConstraints.anchor = GridBagConstraints.LINE_START;
        forfeitConstraints.insets = new Insets(5, 5, 5, 5);
        controlPanel.add(forfeitButton, forfeitConstraints);

        playerNameLabel = Label.createLabel("");
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 1;
        labelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        labelConstraints.insets = new Insets(0, 0, 10, 0);
        labelConstraints.anchor = GridBagConstraints.CENTER;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;

        controlPanel.add(playerNameLabel, labelConstraints);

        gridPanel = new JPanel();
        GridLayout layout = new GridLayout(3, 3);
        gridPanel.setSize(300, 300);
        gridPanel.setLayout(layout);

        controlPanel.add(gridPanel, new GridBagConstraints());
    }

    @Override
    public void initGame(Game game){
        TicTacToeGame ticTacToeGame = (TicTacToeGame) game;
        this.game = (TicTacToeGame) game;

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
            playerNameLabel.setText("Jij bent: " + name);
        }
    }

    private void goForfeit(ActionEvent actionEvent) {
        this.game.setState(GameState.LOST);
        PlayerEventManager.get().stop();

        if(this.game.getClient() != null){
            this.game.getClient().sendCommand("forfeit");
        }
    }
}
