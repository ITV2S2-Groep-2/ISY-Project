package com.isy.gui.scene;

import com.isy.game.ITile;
import com.isy.gui.scene.manager.Scene;
import com.isy.server.await.Promise;
import com.isy.game.Game;
import com.isy.game.ticTacToe.GameState;
import com.isy.util.PlayerEventManager;
import com.isy.util.PlayerTurnEventListener;
import com.isy.gui.Window;
import com.isy.gui.components.BoardTile;
import com.isy.gui.components.Label;
import com.isy.util.ResizeBoardListener;
import com.isy.util.lang.LangHandler;
import com.isy.gui.components.input.UIButton;
import com.isy.gui.components.input.TextField;

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
        JPanel scenePanel = this.getScenePanel();
        scenePanel.setLayout(new FlowLayout());

        JButton forfeitButton = UIButton.createButton("game.general.forfeit.button", this::goForfeit);
        forfeitButton.setPreferredSize(new Dimension(128, 32));
        scenePanel.add(forfeitButton);

        playerNameLabel = Label.createLabel("");
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        scenePanel.add(playerNameLabel);
        scenePanel.add(Box.createHorizontalStrut(10000));
        gridPanel = new JPanel();
        scenePanel.add(gridPanel);
        scenePanel.add(Box.createHorizontalStrut(10000));

        JTextField messageText = TextField.createTextField();
        scenePanel.add(messageText);

        JButton sendButton = UIButton.createButton("game.scene.button", actionEvent -> sendMessage(actionEvent, messageText.getText()));
        sendButton.setPreferredSize(new Dimension(128, 32));
        scenePanel.add(sendButton);

        this.gridPanel.addComponentListener(new ResizeBoardListener(this));
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
                final JButton button = BoardTile.createButton();
                game.getBoard().getTile(x, y).updateOnBoard(button);

                button.addActionListener(new PlayerTurnEventListener(game, x, y));
                this.boardButtons.get(x).add(button);
                gridPanel.add(button);
            }
        }
    }

    public void reloadBoardValues() {
        for (int y = 0; y < game.getBoard().getHeight(); y++) {
            for (int x = 0; x < game.getBoard().getWidth(); x++) {
                game.getBoard().getTile(x, y).updateOnBoard(this.boardButtons.get(x).get(y));
                this.boardButtons.get(x).get(y).repaint();
            }
        }
    }

    //TODO: FIX THIS SINCE THIS DOESNT DO ANYRHING RIGHT NOW OR IS COMPLETLY BROKEN
    public void setPlayerNames(String player1Name, String player2Name, boolean iStart) {
        if (playerNameLabel != null) {
            playerNameLabel.setText(LangHandler.get().translate("player.name.display", iStart ? "X" : "O", player1Name, !iStart ? "X" : "O", player2Name));
            playerNameLabel.repaint();
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
