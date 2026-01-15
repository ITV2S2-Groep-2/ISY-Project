package com.isy.game.othello;

import com.isy.game.ITile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public enum OthelloTile implements ITile {
    EMPTY("othello_empty_tile.png"), POSSIBLE_MOVE("othello_opt.png"),
    PLAYER_1("othello_white.png"), PLAYER_2("othello_black.png");

    private Image img;
    private int imgWidth;
    private int imgHeight;

    private Icon icon;

    OthelloTile(String imagePath){
        this.imgWidth = 32;
        this.imgHeight = 32;

        try {
            this.img = ImageIO.read(getClass().getResource("/" + imagePath));
            this.icon = new ImageIcon(this.img);
        } catch (Exception e) {
//            System.out.println("othello tile image not found");
        }
    }

    @Override
    public void updateOnBoard(JButton jButton) {
        if ((this.imgHeight != jButton.getHeight() || this.imgWidth != jButton.getWidth()) && jButton.getHeight() > 0 && jButton.getWidth() > 0) {
            this.imgWidth = jButton.getWidth();
            this.imgHeight = jButton.getHeight();
            int smallestSide = this.imgHeight < this.imgWidth ? this.imgHeight : this.imgWidth;
            Image image = this.img.getScaledInstance(smallestSide, smallestSide, Image.SCALE_SMOOTH);
            this.icon = new ImageIcon(image);
        } else {
            jButton.setIcon(this.icon);
        }

        jButton.setIcon(this.icon);
    }

    public static OthelloTile[][] createBoard(int width, int height){
        return new OthelloTile[width][height];
    }
}
