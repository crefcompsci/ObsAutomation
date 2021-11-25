package org.coonrapidsfree.obs;

import java.util.Arrays;
import java.util.List;

public enum SdCam2Enum  implements CamEnum {
    TIGHT("Generic SD 2", "Generic SD 2 Camera with Slides", MainCamEnum.FIVE, MainCamEnum.FIVE);
    
    private String sceneName;
    private String slideSceneName;
    private CamEnum moveRight;
    private CamEnum moveLeft;

    private SdCam2Enum(String sceneName, String slideSceneName, CamEnum left, CamEnum right) {
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
            for(; i < SdCam2Enum.values().length; i++) {
                if(SdCam2Enum.values()[i].equals(this)) {
                    break;
                }
            }
            i++;
            moveRight = SdCam2Enum.values()[i];
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
        List<SdCam2Enum> temp = Arrays.asList(SdCam2Enum.values());
        return temp.contains(camera);        
    }
}
