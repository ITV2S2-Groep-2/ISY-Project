package com.isy.gui.scene;

import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.UIButton;
import com.isy.util.lang.LangHandler;

import javax.swing.*;
import java.awt.*;

public class LangSwitchScene extends Scene{
    public LangSwitchScene(Window window) {
        super("langSwitchScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        gbc.gridx = 0;

        panel.add(Header.createHeader("settings.lang.menu.header"), gbc);
        gbc.gridy += 1;
        gbc.gridx = 0;

        gbc.gridwidth = 1;

        for (final String lang : LangHandler.get().getAllAvailableLang()) {
            JButton langButton = UIButton.createButton(lang, (event) -> {
                LangHandler.get().switchLang(lang);
            });

            langButton.setPreferredSize(new Dimension(128,64));
            panel.add(langButton, gbc);

            if (gbc.gridx == 1)
                gbc.gridy += 1;
            gbc.gridx = (gbc.gridx + 1) % 2;
        }

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy += 1;
        JButton backButton = UIButton.createButton("settings.back.button", (event) -> {
            this.getWindow().getManager().showScene("settingsScene");
        });
        backButton.setPreferredSize(new Dimension(128,64));
        panel.add(backButton, gbc);

    }
}
