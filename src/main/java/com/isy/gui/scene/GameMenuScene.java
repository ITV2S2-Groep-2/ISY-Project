package com.isy.gui.scene;

import com.isy.gui.components.*;
import com.isy.gui.components.input.CheckBox;
import com.isy.gui.components.input.ComboBox;
import com.isy.gui.components.input.TextField;
import com.isy.gui.components.input.UIButton;
import com.isy.gui.components.layout.ContentBox;
import com.isy.gui.components.layout.FlexBox;
import com.isy.gui.components.swing.RoundedComboBox;
import com.isy.gui.scene.manager.Scene;
import com.isy.util.GameSettings;
import com.isy.util.lang.LangHandler;
import com.isy.game.*;
import com.isy.gui.Window;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Arrays;

public class GameMenuScene extends Scene {
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

        error = TextField.createTextField();
        error.hide();

        dropdown1 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .filter(val -> !val.equals(PlayerType.REMOTE))
                .map(val -> new RoundedComboBox.ComboBoxTranslatedElement("ui.combo_box.player_type.", val.label))
                .toArray());
        dropdown2 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .map(val -> new RoundedComboBox.ComboBoxTranslatedElement("ui.combo_box.player_type.", val.label))
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

        ContentBox contentBox = new ContentBox(getScenePanel(), BoxLayout.Y_AXIS);

        contentBox.add(error, 20);
        contentBox.add(Header.createHeader("game." + gameType.label + ".header"), 20);

        FlexBox flexBox = new FlexBox(BoxLayout.X_AXIS);
        flexBox.add(dropdown1, 20);
        flexBox.add(dropdown2, 20);
        contentBox.add(flexBox.getComponent(), 20);

        flexBox = new FlexBox(BoxLayout.X_AXIS);
        flexBox.add(textField1, 20);
        flexBox.add(textField2, 20);
        contentBox.add(flexBox.getComponent(), gameSpecificComponent != null ? 5 : 20);

        if (gameSpecificComponent != null){
            flexBox = new FlexBox(BoxLayout.X_AXIS);
            flexBox.add(gameSpecificComponent, 0);
            contentBox.add(flexBox.getComponent(), 5);
        }

        flexBox = new FlexBox(BoxLayout.X_AXIS);
        flexBox.add(UIButton.createButton("settings.back.button", this::goMainMenu), 20);
        flexBox.add(UIButton.createButton("game.general.start_game.button", this::startGame), 20);
        contentBox.add(flexBox.getComponent(), 0);
    }

    private void startGame(ActionEvent e){
        PlayerType player1Type = PlayerType.fromLabel(((RoundedComboBox.ComboBoxTranslatedElement)dropdown1.getSelectedItem()).getValue());
        PlayerType player2Type = PlayerType.fromLabel(((RoundedComboBox.ComboBoxTranslatedElement)dropdown2.getSelectedItem()).getValue());

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
