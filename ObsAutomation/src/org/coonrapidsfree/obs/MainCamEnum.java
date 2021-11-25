package org.coonrapidsfree.obs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MainCamEnum implements CamEnum {

    WIDE("Generic HD", "Generic HD Camera with Slides", Slides.getInstance(), Slides.getInstance()),
    FOUR("Generic HD Med 4", "Generic HD Camera with Slides 4", MainCamEnum.WIDE, null),
    FOUR_POINT_FIVE("Generic HD Med 4.5", "Generic HD Camera with Slides 4.5", MainCamEnum.FOUR, null),
    FIVE("Generic HD Med 5", "Generic HD Camera with Slides 5", MainCamEnum.FOUR_POINT_FIVE, null),
    FIVE_POINT_FIVE("Generic HD Med 5.5", "Generic HD Camera with Slides 5.5", MainCamEnum.FIVE, null),
    SIX("Generic HD Med 6", "Generic HD Camera with Slides 6", MainCamEnum.FIVE_POINT_FIVE, MainCamEnum.WIDE), 
    TIGHT("Generic HD Tight", "Generic HD Camera with Slides Tight", MainCamEnum.FOUR_POINT_FIVE, MainCamEnum.FIVE_POINT_FIVE);

    private String sceneName;
    private String slideSceneName;
    private CamEnum moveRight;
    private CamEnum moveLeft;

    private MainCamEnum(String sceneName, String slideSceneName, CamEnum left, CamEnum right) {
        this.sceneName = sceneName;
        this.slideSceneName = slideSceneName;
        this.moveLeft = left;
        this.moveRight = right;
    }

    /**
     * @return the sceneName
     */
    public String getSceneName() {
        return sceneName;
    }

    /**
     * @return the slideSceneName
     */
    public String getSlideSceneName() {
        return slideSceneName;
    }
    
    /**
     * @return the moveRight
     */
    public CamEnum getMoveRight() {
        if(moveRight == null) {
            int i = 0;
            for(; i < MainCamEnum.values().length; i++) {
                if(MainCamEnum.values()[i].equals(this)) {
                    break;
                }
            }
            i++;
            moveRight = MainCamEnum.values()[i];
        }
        return moveRight;
    }

    /**
     * @return the moveLeft
     */
    public CamEnum getMoveLeft() {
        return moveLeft;
    }

    public static boolean contains(CamEnum camera) {
        List<MainCamEnum> temp = Arrays.asList(MainCamEnum.values());
        return temp.contains(camera);        
    }
}
