package com.isy.gui.scene;

import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.input.UIButton;
import com.isy.gui.components.layout.ContentBox;
import com.isy.gui.components.layout.FlexBox;
import com.isy.gui.scene.manager.Scene;
import com.isy.util.lang.LangHandler;

import javax.swing.*;
import java.awt.*;

public class LangSwitchScene extends Scene {
    public LangSwitchScene(Window window) {
        super("langSwitchScene", window);
    }

    @Override
    public void init() {

        ContentBox contentBox = new ContentBox(getScenePanel(), BoxLayout.Y_AXIS);

        contentBox.add(Header.createHeader("settings.lang.menu.header"), 20);

        String[] langs = LangHandler.get().getAllAvailableLang();

        FlexBox flexBox = new FlexBox(BoxLayout.X_AXIS);

        for (int i = 0; i < langs.length; i++) {
            if (i % 2 == 0){
                flexBox = new FlexBox(BoxLayout.X_AXIS);
                contentBox.add(flexBox.getComponent(), 20);
            }

            final String lang = langs[i];

            JButton langButton = UIButton.createButton(lang, (event) -> {
                LangHandler.get().switchLang(lang);
            }, 120, 64, null);

            flexBox.add(langButton, i % 2 == 0 ? 16 : 0);
        }

        contentBox.add(UIButton.createButton("settings.back.button", (event) -> {
            this.getWindow().getManager().showScene("settingsScene");
        }), 0);
    }
}
