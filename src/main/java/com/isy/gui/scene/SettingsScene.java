package com.isy.gui.scene;

import com.isy.gui.components.layout.ContentBox;
import com.isy.gui.components.layout.FlexBox;
import com.isy.gui.scene.manager.Scene;
import com.isy.util.GameSettings;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.input.TextField;
import com.isy.gui.components.input.UIButton;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsScene extends Scene {
    static JTextField portTextField, hostNameTextField;
    private JSlider volumeSlider;
    private JLabel volumeLabel;

    public SettingsScene(Window window) {
        super("settingsScene", window);
    }

    @Override
    public void init() {
        ContentBox content = new ContentBox(getScenePanel(), BoxLayout.Y_AXIS);

        content.add(Header.createHeader("settings.menu.header"), 20);

        hostNameTextField = TextField.createTextField(GameSettings.get().getHostName());
        content.add(hostNameTextField, 20);

        portTextField = TextField.createTextField(String.valueOf(GameSettings.get().getPortNumber()));
        content.add(portTextField, 20);

        content.add(createVolumePanel(), 20);

        content.add(UIButton.createButton("settings.lang.button", this::langSettings), 20);

        FlexBox flexBox = new FlexBox(BoxLayout.X_AXIS);
        flexBox.add(UIButton.createButton("settings.back.button", this::goMainMenu, 120, 64, null), 16);
        flexBox.add(UIButton.createButton("settings.save.button", this::saveSettings, 120, 64, null), 0);
        content.add(flexBox.getComponent(), 20);
    }

    private JPanel createVolumePanel(){
        JPanel volumePanel = new JPanel(new BorderLayout(8, 0));
        volumeLabel = new JLabel("Volume:"); // je kunt dit vervangen door een lokale tekst key
        volumePanel.add(volumeLabel, BorderLayout.WEST);

        // slider 0..100
        GameSettings gs = GameSettings.get();
        int initialVolume = gs.convertFromDB(gs.getBackgroundVolume()); // nieuw in GameSettings (zorg dat het bestaat)
        volumeSlider = new JSlider(0, 100, initialVolume);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setFocusable(false);

        // update label als slider verandert en save naar GameSettings direct
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = volumeSlider.getValue();
                // bijvoorbeeld: toon % in label
                volumeLabel.setText("Volume: " + value + "%");
                GameSettings.get().setBackgroundVolume(value);
            }
        });

        volumePanel.add(volumeSlider, BorderLayout.CENTER);

        return volumePanel;
    }

    private void langSettings(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("langSwitchScene");
    }

    private void goMainMenu(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("mainMenuScene");
    }

    private void saveSettings(ActionEvent actionEvent) {
        GameSettings gs = GameSettings.get();
        int portNumber = Integer.parseInt(portTextField.getText());
        gs.setGameSettings(hostNameTextField.getText(), portNumber);
//        System.out.println(gs.getHostName());
//        System.out.println(gs.getPortNumber());

        if (volumeSlider != null) {
            gs.setBackgroundVolume(volumeSlider.getValue());
            gs.setEffectsVolume(volumeSlider.getValue());
        }

        this.getWindow().getManager().showScene("mainMenuScene");
    }
}
