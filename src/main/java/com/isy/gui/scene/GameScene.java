package com.isy.gui.scene;

import com.isy.game.ITile;
import com.isy.server.await.Promise;
import com.isy.game.Game;
import com.isy.game.ticTacToe.GameState;
import com.isy.util.PlayerEventManager;
import com.isy.util.PlayerTurnEventListener;
import com.isy.game.ticTacToe.TicTacToeTile;
import com.isy.gui.Window;
import com.isy.gui.components.BoardTile;
import com.isy.gui.components.Label;
import com.isy.util.lang.LangHandler;
import com.isy.gui.components.UIButton;
import com.isy.gui.components.TextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.isy.server.ServerUtils.await;

public class GameScene extends Scene {
    private final List<List<JButton>> boardButtons;

    private JPanel gridPanel;
    private JLabel playerNameLabel;
    private Game<?> game;

    public GameScene(Window window) {
        super("game", window);
        this.boardButtons = new ArrayList<>();
    }

    @Override
    public void init() {
        JPanel controlPanel = this.getScenePanel();
        controlPanel.setLayout(new GridBagLayout());

        JButton forfeitButton = UIButton.createButton("ttt.game.forfeit.button", this::goForfeit);
        forfeitButton.setPreferredSize(new Dimension(128, 32));
        GridBagConstraints forfeitConstraints = new GridBagConstraints();
        forfeitConstraints.gridx = 0;
        forfeitConstraints.gridy = 1;
        forfeitConstraints.anchor = GridBagConstraints.LINE_START;
        forfeitConstraints.insets = new Insets(5, 5, 5, 5);
        controlPanel.add(forfeitButton, forfeitConstraints);

        JTextField messageText = TextField.createTextField();
        GridBagConstraints messageTextConstraints = new GridBagConstraints();
        messageTextConstraints.gridx = 2;
        messageTextConstraints.gridy = 15;
        messageTextConstraints.anchor = GridBagConstraints.LINE_START;
        messageTextConstraints.insets = new Insets(5, 5, 5, 5);
        controlPanel.add(messageText, messageTextConstraints);

        JButton sendButton = UIButton.createButton("game.scene.button", actionEvent -> sendMessage(actionEvent, messageText.getText()));
        sendButton.setPreferredSize(new Dimension(128, 32));
        GridBagConstraints sendConstraints = new GridBagConstraints();
        sendConstraints.gridx = 2;
        sendConstraints.gridy = 16;
        sendConstraints.anchor = GridBagConstraints.LINE_START;
        sendConstraints.insets = new Insets(5, 5, 5, 5);
        controlPanel.add(sendButton, sendConstraints);

        playerNameLabel = Label.createLabel("");
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

        GridBagConstraints gridPanelConstrains = new GridBagConstraints();
        gridPanelConstrains.gridy = 2;
        gridPanel = new JPanel();
        controlPanel.add(gridPanel, gridPanelConstrains);
    }

    @Override
    public void initGame(Game<?> game){
        this.game = game;

        GridLayout layout = new GridLayout(this.game.getBoard().getHeight(), this.game.getBoard().getWidth());
        gridPanel.setSize(this.game.getBoard().getHeight() * 100, this.game.getBoard().getWidth() * 100);
        gridPanel.setLayout(layout);

        gridPanel.removeAll();

        for (int x = 0; x < game.getBoard().getHeight(); x++) {
            this.boardButtons.add(new ArrayList<>());
            for (int y = 0; y < game.getBoard().getWidth(); y++) {
                final JButton button = BoardTile.createButton(game.getBoard().getTile(x, y).toString());

                button.addActionListener(new PlayerTurnEventListener(game, x, y));
                this.boardButtons.get(x).add(button);
                gridPanel.add(button);
            }
        }
    }

    public void reloadBoardValues(Game<?> game) {
        ITile[][] tiles = game.getBoard().getTiles();

        for (int y = 0; y < game.getBoard().getHeight(); y++) {
            for (int x = 0; x < game.getBoard().getWidth(); x++) {
                tiles[x][y].updateOnBoard(this.boardButtons.get(x).get(y));
                this.boardButtons.get(x).get(y).repaint();
            }
        }
    }

    public void setPlayerNames(String player1Name, String player2Name, boolean iStart) {
        if (playerNameLabel != null) {
            playerNameLabel.setText(LangHandler.get().translate("player.name.display", iStart ? "X" : "O", player1Name, !iStart ? "X" : "O", player2Name));
        }
    }

    private void goForfeit(ActionEvent actionEvent) {
        this.game.setState(GameState.LOST);
        PlayerEventManager.get().stop();

        await(new Promise().setCommand("forfeit"));
    }

    private void sendMessage(ActionEvent actionEvent, String message) {
        await(new Promise().setCommand("message " + message));
    }
}
