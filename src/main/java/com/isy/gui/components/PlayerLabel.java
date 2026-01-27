package com.isy.gui.components;

import com.isy.game.player.Player;
import com.isy.gui.Style;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PlayerLabel {
    private static final Border border = new CompoundBorder(
            new LineBorder(Style.extraHighlightBackgroundColor, 5),
            new EmptyBorder(0, 5, 0, 5)
    );

    private final Player<?> player;
    private final JLabel label;

    public PlayerLabel(Player<?> player){
        this.player = player;
        this.label = Label.createLabel(this.player.getName());
        this.player.getSymbol().createDisplayIcon(this.label);
    }

    public JLabel getLabel(){
        return this.label;
    }

    public void setActiveTurnPlayer(Player<?> player){
        if (player.getSymbol() == this.player.getSymbol()){
            this.label.setBorder(border);
        }else{
            this.label.setBorder(null);
        }

        this.label.repaint();
    }
}
