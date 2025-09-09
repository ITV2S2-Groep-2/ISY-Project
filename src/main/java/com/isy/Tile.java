package com.isy;

public enum Tile {
    EMPTY, X, O;

    @Override
    public String toString() {
        if (this == EMPTY)
            return " ";

        return this.name();
    }
}
