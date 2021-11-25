package org.coonrapidsfree.obs.two;

import java.awt.Rectangle;

public class SceneBox {
    private Rectangle r;
    private String scene;

    public SceneBox(Rectangle r, String scene) {
        this.r = r;
        this.scene = scene;
    }

    /**
     * @return the r
     */
    public Rectangle getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(Rectangle r) {
        this.r = r;
    }

    /**
     * @return the scene
     */
    public String getScene() {
        return scene;
    }

    /**
     * @param scene the scene to set
     */
    public void setScene(String scene) {
        this.scene = scene;
    }
    
}
