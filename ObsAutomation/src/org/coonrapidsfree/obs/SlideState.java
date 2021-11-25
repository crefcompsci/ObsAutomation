package org.coonrapidsfree.obs;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public enum SlideState {
    FULL_SLIDES(new Transition() {
        @Override
        public String go(CamEnum camEnum) {
            return Slides.getInstance().getSceneName();
        }
    },SlideState.class.getResource("/org/coonrapidsfree/images/fullSlides.PNG")),
    SLIDE_COMBO(new Transition() {
        @Override
        public String go(CamEnum camEnum) {
            return camEnum.getSlideSceneName();
        }
    },SlideState.class.getResource("/org/coonrapidsfree/images/slideOverlay.PNG")),
    NO_SLIDE(new Transition() {
        @Override
        public String go(CamEnum camEnum) {
            return camEnum.getSceneName();
        }
    },SlideState.class.getResource("/org/coonrapidsfree/images/cameraOverlay.PNG"));

    private Transition transition;
    private Image overlayImage;
    private SlideState(Transition transition, URL url) {
        this.transition = transition;
        try {
            Image i = ImageIO.read(url);
            
        this.overlayImage = ImageUtilities.getImage(i, .1, 0);
        } catch (IOException ex) {
            Logger.getLogger(SlideState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the transition
     */
    public Transition getTransition() {
        return transition;
    }

    /**
     * @return the overlayImage
     */
    public Image getOverlayImage() {
        return overlayImage;
    }
}
