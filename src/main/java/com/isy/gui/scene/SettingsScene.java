package com.isy.gui.scene;

import com.isy.gui.components.SoundUtils;
import com.isy.util.GameSettings;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.TextField;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsScene extends Scene{
    static JTextField portTextField, hostNameTextField;
    private JSlider volumeSlider;
    private JLabel volumeLabel;

    public SettingsScene(Window window) {
        super("settingsScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(Header.createHeader("settings.menu.header"), gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        hostNameTextField = TextField.createTextField(GameSettings.get().getHostName());
        panel.add(hostNameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        portTextField = TextField.createTextField(String.valueOf(GameSettings.get().getPortNumber()));
        panel.add(portTextField, gbc);

        // ---- Volume slider ----
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

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
        panel.add(volumePanel, gbc);


        // temp back button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JButton backButton = UIButton.createButton("settings.back.button", this::goMainMenu);
        backButton.setPreferredSize(new Dimension(128,64));
        panel.add(backButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JButton saveButton = UIButton.createButton("settings.save.button", this::saveSettings);
        saveButton.setPreferredSize(new Dimension(128,64));
        panel.add(saveButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton langButton = UIButton.createButton("settings.lang.button", this::langSettings);
        langButton.setPreferredSize(new Dimension(128,64));
        panel.add(langButton, gbc);
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
        System.out.println(gs.getHostName());
        System.out.println(gs.getPortNumber());

        if (volumeSlider != null) {
            gs.setBackgroundVolume(volumeSlider.getValue());
            gs.setEffectsVolume(volumeSlider.getValue());
        }

        this.getWindow().getManager().showScene("mainMenuScene");
    }
}
