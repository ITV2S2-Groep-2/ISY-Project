package com.isy.gui.scene;

import com.isy.util.lang.LangHandler;
import com.isy.game.*;
import com.isy.gui.Window;
import com.isy.gui.components.ComboBox;
import com.isy.gui.components.Header;
import com.isy.gui.components.TextField;
import com.isy.gui.components.UIButton;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import static com.isy.gui.GridBagConstrainsUtil.*;

public class GameMenuScene extends Scene{
    static JComboBox dropdown1, dropdown2;
    static JTextField textField1, textField2;
    static JTextField error;

    public GameMenuScene(Window window) {
        super("gameMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        error = TextField.createTextField();
        error.hide();

        //TODO: make combobox translated
        dropdown1 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .filter(val -> !val.equals(PlayerType.REMOTE))
                .map(val -> val.label)
                .toArray());
        dropdown2 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .map(val -> val.label)
                .toArray());

        textField1 = TextField.createTextField("ttt.game.player.text_field.placeholder", Math.round(Math.random() * 1000));
        textField2 = TextField.createTextField("ttt.game.player.text_field.placeholder", Math.round(Math.random() * 1000));

        initConstraints(gbc, 2);
        panel.add(error, next(gbc));

        row(gbc,2);
        panel.add(Header.createHeader("tic.tac.toe.header"), next(gbc));

        row(gbc,1);
        panel.add(dropdown1, next(gbc));
        panel.add(dropdown2, next(gbc));

        row(gbc,1);
        panel.add(textField1, next(gbc));
        panel.add(textField2, next(gbc));

        row(gbc,1);
        panel.add(UIButton.createButton("ttt.game.start_game.button", this::startGame), next(next(gbc)));
    }

    private void startGame(ActionEvent e){
        PlayerType player1Type = PlayerType.fromLabel((String) dropdown1.getSelectedItem());
        PlayerType player2Type = PlayerType.fromLabel((String) dropdown2.getSelectedItem());

        String player1Name = textField1.getText();
        String player2Name = textField2.getText();

        if (!player1Name.matches("^[A-Za-z0-9]{1,16}$")){
            joinError("error.incorrect_name");
            return;
        }

        GameCreator creator = GameCreator.getCurrentInstance();
        creator.setPlayers(player1Type, player2Type, player1Name, player2Name);

        if (player2Type.equals(PlayerType.REMOTE)) {
            creator.goToJoinGameServer();
        } else {
            creator.startLocalGame();
        }
    }

    @Override
    public void show() {
        super.show();
        error.hide();
    }

    public static void joinError(String errorKey){
        error.show();
        error.setText(LangHandler.get().translate(errorKey));
    }
}
