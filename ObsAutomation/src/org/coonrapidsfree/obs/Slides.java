/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.coonrapidsfree.obs;

/**
 *
 * @author KyleF
 */
public class Slides implements CamEnum {
    
    private static Slides instance = null;

    private Slides() {
        
    }
    
    public static Slides getInstance() {
        if(instance == null) {
            instance = new Slides();
        }
        return instance;
    }
    
    @Override
    public String getSceneName() {
        return "Slides With Overlay";
    }

    @Override
    public String getSlideSceneName() {
        return "Slides With Overlay";
    }

    @Override
    public CamEnum getMoveLeft() {
        return this;
    }

    @Override
    public CamEnum getMoveRight() {
        return this;
    }
    
}
