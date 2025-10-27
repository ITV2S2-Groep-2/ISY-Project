package com.isy.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class PanelBackgroundUtil {

    public static void setBackgroundImage(JPanel panel, String imagePath) {
        try {
            System.out.println("Image URL = " + PanelBackgroundUtil.class.getResource(imagePath));
            BufferedImage img = ImageIO.read(PanelBackgroundUtil.class.getResource(imagePath));
            // Maak een anonieme subclass met custom paintComponent
            JPanel backgroundWrapper = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    //g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(Color.RED);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            // Kopieer layout en componenten
            backgroundWrapper.setLayout(panel.getLayout());
            for (Component c : panel.getComponents()) {
                backgroundWrapper.add(c);
            }

            // Vervang de originele panel in zijn parent (optioneel)
            Container parent = panel.getParent();
            if (parent != null) {
                int index = -1;
                for (int i = 0; i < parent.getComponentCount(); i++) {
                    if (parent.getComponent(i) == panel) {
                        index = i;
                        break;
                    }
                }
                if (index >= 0) {
                    parent.remove(index);
                    parent.add(backgroundWrapper, index);
                    parent.revalidate();
                    parent.repaint();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

