package com.isy.gui.scene;

import com.isy.game.player.Player;
import com.isy.gui.components.PlayerLabel;
import com.isy.gui.components.layout.FlexBox;
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
import com.isy.gui.components.input.UIButton;
import com.isy.gui.components.input.TextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.isy.server.ServerUtils.await;

public class GameScene extends Scene {
    private final List<List<JButton>> boardButtons;

    private JPanel gridPanel;
    private FlexBox playerFlexBox;
    private PlayerLabel player1Label;
    private PlayerLabel player2Label;
    private Game<?> game;

    public GameScene(Window window) {
        super("game", window);
        this.boardButtons = new ArrayList<>();
    }

    @Override
    public void init() {
        JPanel scenePanel = this.getScenePanel();
        scenePanel.setLayout(new FlowLayout());

        playerFlexBox = new FlexBox(BoxLayout.X_AXIS);
        scenePanel.add(playerFlexBox.getComponent());
        scenePanel.add(Box.createHorizontalStrut(10000));

        JButton forfeitButton = UIButton.createButton("game.general.forfeit.button", this::goForfeit);
        forfeitButton.setPreferredSize(new Dimension(128, 32));
        scenePanel.add(forfeitButton);
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

    public void initGame(Game<?> game){
        this.game = game;

        GridLayout layout = new GridLayout(this.game.getBoard().getHeight(), this.game.getBoard().getWidth());
        layout.setHgap(2);
        layout.setVgap(2);
        gridPanel.setSize(this.game.getBoard().getHeight() * 100, this.game.getBoard().getWidth() * 100);
        gridPanel.setLayout(layout);
        gridPanel.removeAll();
        gridPanel.setOpaque(false);

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

        player1Label.setActiveTurnPlayer(game.getActiveTurnPlayer());
        player2Label.setActiveTurnPlayer(game.getActiveTurnPlayer());
    }

    public void setHighLights(boolean[][] updatedTiles, int[] newTile){
        for (int y = 0; y < game.getBoard().getHeight(); y++) {
            for (int x = 0; x < game.getBoard().getWidth(); x++) {
                BoardTile.setHighLight(this.boardButtons.get(x).get(y), updatedTiles[x][y], newTile[0] == x && newTile[1] == y);
                this.boardButtons.get(x).get(y).repaint();
            }
        }
    }

    public void setPlayerNames(Player<?> player1, Player<?> player2) {
        this.playerFlexBox.clear();

        player1Label = new PlayerLabel(player1);
        player2Label = new PlayerLabel(player2);

        this.playerFlexBox.add(player1Label.getLabel(), 10);
        this.playerFlexBox.add(Label.createLabel("game.player_label.versus"), 10);
        this.playerFlexBox.add(player2Label.getLabel(), 10);
        this.playerFlexBox.getComponent().repaint();
    }

    private void goForfeit(ActionEvent actionEvent) {
        this.game.setState(GameState.LOST);
        PlayerEventManager.get().stop();

        await(new Promise().setCommand("forfeit"));
    }

    private void sendMessage(ActionEvent actionEvent, String message) {
        await(new Promise().setCommand("message " + message));
    }

    public void setValue(int x, int y, String value) {
        if(!Objects.equals(value, "")) this.boardButtons.get(x).get(y).setIcon(null);
        this.boardButtons.get(x).get(y).setText(value);
    }
}
