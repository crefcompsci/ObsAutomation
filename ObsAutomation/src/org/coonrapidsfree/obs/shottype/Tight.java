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
public class Tight extends Wide {
    public Rectangle convert(Rectangle r) {
        if(r.width < r.height) {
            return new Rectangle(r.x, r.y, r.width, r.width);
        }
        return r;
    }
}
