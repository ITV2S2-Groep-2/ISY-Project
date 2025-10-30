package com.isy.gui;

import java.awt.*;

public class GridBagConstrainsUtil {
    public static void initConstraints(GridBagConstraints constraints, int width){
        constraints.gridx = -1;
        constraints.gridy = 0;
        constraints.gridwidth = width;

    }

    public static GridBagConstraints next(GridBagConstraints constraints){
        constraints.gridx += 1;

        return constraints;
    }

    public static GridBagConstraints row(GridBagConstraints constraints, int width){
        constraints.gridy += 1;
        constraints.gridwidth = width;
        constraints.gridx = -1;

        return constraints;
    }

    public static GridBagConstraints row(GridBagConstraints constraints){
        return row(constraints, constraints.gridwidth);
    }
}
