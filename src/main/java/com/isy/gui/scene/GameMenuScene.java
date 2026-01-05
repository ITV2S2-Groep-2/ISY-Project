package com.isy.gui.scene;

import com.isy.gui.components.swing.RoundedComboBox;
import com.isy.util.GameSettings;
import com.isy.util.lang.LangHandler;
import com.isy.game.*;
import com.isy.gui.Window;
import com.isy.gui.components.ComboBox;
import com.isy.gui.components.Header;
import com.isy.gui.components.TextField;
import com.isy.gui.components.UIButton;
import com.isy.gui.components.CheckBox;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
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

        GameType gameType = GameCreator.getCurrentInstance().getGameType();
        if (gameType == null) {
            return;
        }

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
                .map(val -> new RoundedComboBox.ComboBoxTranslatedElement("ui.combo_box.player_type." + val.label.toLowerCase()))
                .toArray());
        dropdown2 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .map(val -> new RoundedComboBox.ComboBoxTranslatedElement("ui.combo_box.player_type." + val.label.toLowerCase()))
                .toArray());

        textField1 = TextField.createTextField("game.general.player.text_field.placeholder", Math.round(Math.random() * 1000));
        textField2 = TextField.createTextField("game.general.player.text_field.placeholder", Math.round(Math.random() * 1000));

        JComponent gameSpecificComponent = null;
        switch (gameType) {
            case OTHELLO -> {
                JCheckBox checkBox = CheckBox.createCheckBox("game.reversi.use_reversi_rules", (itemEvent) -> {
                    GameSettings.get().setUseReversiRules(itemEvent.getStateChange() == ItemEvent.SELECTED);
                });
                gameSpecificComponent = checkBox;
                checkBox.setSelected(GameSettings.get().getUseReversiRules());
            }
        };

        initConstraints(gbc, 2);
        panel.add(error, next(gbc));

        row(gbc,2);
        panel.add(Header.createHeader("game." + gameType.label + ".header"), next(gbc));

        row(gbc,1);
        panel.add(dropdown1, next(gbc));
        panel.add(dropdown2, next(gbc));

        row(gbc,1);
        panel.add(textField1, next(gbc));
        panel.add(textField2, next(gbc));

        if (gameSpecificComponent != null) {
            row(gbc,1);
            panel.add(gameSpecificComponent, next(gbc));
        }

        row(gbc,1);
        panel.add(UIButton.createButton("settings.back.button", this::goMainMenu), next(gbc));
        panel.add(UIButton.createButton("game.general.start_game.button", this::startGame), next(gbc));
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

    private void goMainMenu(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("mainMenuScene");
    }
}
