/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.coonrapidsfree.obs.shottype;

import java.awt.Rectangle;

/**
 *
 * @author KyleF
 */
public enum ShotTypeEnum {
    WIDE(new Wide()),
    TIGHT(new Tight());
    
    Wide shot;
    private ShotTypeEnum(Wide shot) {
        this.shot = shot;
    }

    public Rectangle convert(Rectangle convertedRect) {
        return shot.convert(convertedRect);
    }
}
