package com.isy.gui.components.layout;

import com.isy.gui.Style;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ScenePanel extends JPanel{

    private BufferedImage backgroundImage;

    public ScenePanel() {
        // standaard kleuren
        setBackground(Style.sceneBackgroundColor);
        setForeground(Style.primaryTextColor);
    }

    public void setBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(getClass().getResource(path));
            repaint();
        } catch (IOException e) {
            System.err.println("Kon achtergrondafbeelding niet laden: " + path);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

}
