package org.coonrapidsfree.obs;

public enum ServiceSectionEnum {
    WORSHIP_SET(1000, 10000, 10000, 120000, 5000),
    ANNOUNCEMENTS(500, 2500, 2000, 5000, 5000),
    SERMON(1000, 3000, 5000, 30000, 12000);

    private int fadeDelay;
    private int moveDelay;
    private int fullScreenSlideTime;
    private int comboSlideScreenTime;
    private int transitionTime;

    private ServiceSectionEnum(int fadeDelay, int moveDelay, int fullScreenSlideTime, int comboSlideScreenTime, int transitionTime) {
        this.fadeDelay = fadeDelay;
        this.moveDelay = moveDelay;
        this.fullScreenSlideTime = fullScreenSlideTime;
        this.comboSlideScreenTime = comboSlideScreenTime;
        this.transitionTime = transitionTime;
    }

    /**
     * @return the fadeDelay
     */
    public int getFadeDelay() {
        return fadeDelay;
    }

    /**
     * @return the moveDelay
     */
    public int getMoveDelay() {
        return moveDelay;
    }

    /**
     * @return the fullScreenSlideTime
     */
    public int getFullScreenSlideTime() {
        return fullScreenSlideTime;
    }

    /**
     * @return the comboSlideScreenTime
     */
    public int getComboSlideScreenTime() {
        return comboSlideScreenTime;
    }

    /**
     * @return the transitionTime
     */
    public int getTransitionTime() {
        return transitionTime;
    }

}
