package org.coonrapidsfree.obs;

import java.util.Arrays;
import java.util.List;

public enum SdCamEnum  implements CamEnum {
    FOUR("Generic SD 4", "Generic SD Camera with Slides 4", MainCamEnum.WIDE, null),
    FIVE("Generic SD 5", "Generic SD Camera with Slides 5", SdCamEnum.FOUR, null),
    SIX("Generic SD 6", "Generic SD Camera with Slides 6", SdCamEnum.FIVE, MainCamEnum.WIDE),
    WIDE("Generic SD", "Generic SD Camera with Slides", MainCamEnum.WIDE, MainCamEnum.WIDE);
    
    private String sceneName;
    private String slideSceneName;
    private CamEnum moveRight;
    private CamEnum moveLeft;

    private SdCamEnum(String sceneName, String slideSceneName, CamEnum left, CamEnum right) {
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
            for(; i < SdCamEnum.values().length; i++) {
                if(SdCamEnum.values()[i].equals(this)) {
                    break;
                }
            }
            i++;
            moveRight = SdCamEnum.values()[i];
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
        List<SdCamEnum> temp = Arrays.asList(SdCamEnum.values());
        return temp.contains(camera);        
    }
}
