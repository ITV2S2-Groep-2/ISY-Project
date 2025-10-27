package com.isy.gui.scene;

import com.isy.server.await.Promise;
import com.isy.game.Game;
import com.isy.game.ticTacToe.GameState;
import com.isy.gui.PlayerEventManager;
import com.isy.gui.PlayerTurnEventListener;
import com.isy.game.ticTacToe.Tile;
import com.isy.gui.Window;
import com.isy.gui.components.BoardTile;
import com.isy.gui.components.Label;
import com.isy.gui.components.TextField;
import com.isy.gui.lang.LangHandler;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.isy.server.await.Await.await;

public class GameScene extends Scene {
    private final List<List<JButton>> boardButtons;

    private JPanel gridPanel;
    private JLabel playerNameLabel;
    private Game game;

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
        forfeitConstraints.gridy = 0;
        forfeitConstraints.anchor = GridBagConstraints.LINE_START;
        forfeitConstraints.insets = new Insets(5, 5, 5, 5);
        controlPanel.add(forfeitButton, forfeitConstraints);

        JTextField messageText = TextField.createTextField();
        GridBagConstraints messageTextConstraints = new GridBagConstraints();
        messageTextConstraints.gridx = 5;
        messageTextConstraints.gridy = 0;
        messageTextConstraints.anchor = GridBagConstraints.LINE_START;
        messageTextConstraints.insets = new Insets(5, 5, 5, 5);
        controlPanel.add(messageText, messageTextConstraints);

        JButton sendButton = UIButton.createButton("Send", actionEvent -> sendMessage(actionEvent, messageText.getText()));
        sendButton.setPreferredSize(new Dimension(128, 32));
        GridBagConstraints sendConstraints = new GridBagConstraints();
        sendConstraints.gridx = 5;
        sendConstraints.gridy = 1;
        sendConstraints.anchor = GridBagConstraints.LINE_START;
        sendConstraints.insets = new Insets(5, 5, 5, 5);
        controlPanel.add(sendButton, sendConstraints);

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
        controlPanel.add(gridPanel, new GridBagConstraints());
    }

    @Override
    public void initGame(Game game){
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

    public void reloadBoardValues(Game game) {
        Tile[][] tiles = game.getBoard().getTiles();
        for (int y = 0; y < game.getBoard().getHeight(); y++) {
            for (int x = 0; x < game.getBoard().getWidth(); x++) {
                this.boardButtons.get(x).get(y).setText(tiles[x][y].toString());
                this.boardButtons.get(x).get(y).repaint();
            }
        }
    }

    public void setPlayerName(String name) {
        if (playerNameLabel != null) {
            playerNameLabel.setText(LangHandler.get().translate("player.name.display", name));
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
