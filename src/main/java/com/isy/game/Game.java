package com.isy.game;

import com.isy.gui.scene.Scene;

public abstract class Game {
    private Scene renderScene;

    public void setRenderScene(Scene scene){
        this.renderScene = scene;
        scene.initGame(this);
    }

    public Scene getRenderScene(){
        return this.renderScene;
    }
}
