package org.coonrapidsfree.obs;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import net.twasi.obsremotejava.OBSRemoteController;
import net.twasi.obsremotejava.callbacks.Callback;
import net.twasi.obsremotejava.requests.SetCurrentScene.SetCurrentSceneResponse;
import net.twasi.obsremotejava.requests.SetSceneItemProperties.SetSceneItemPropertiesResponse;
import net.twasi.obsremotejava.requests.SetStudioModeEnabled.SetStudioModeEnabledResponse;
import net.twasi.obsremotejava.requests.SetTransitionDuration.SetTransitionDurationResponse;
import net.twasi.obsremotejava.requests.StartRecording.StartRecordingResponse;
import net.twasi.obsremotejava.requests.StartStreaming.StartStreamingResponse;
import net.twasi.obsremotejava.requests.StopRecording.StopRecordingResponse;
import net.twasi.obsremotejava.requests.StopStreaming.StopStreamingResponse;
import org.coonrapidsfree.util.ImageCreator;

public class ObsAutomation extends javax.swing.JFrame {

    private BufferedImage blankSlideImage;
    private boolean saveBlankSlideImage;

    /**
     * @return the currentSceneString
     */
    public String getCurrentSceneString() {
        return currentSceneString;
    }

    /**
     * @param currentSceneString the currentSceneString to set
     */
    public void setCurrentSceneString(String currentSceneString) {
        this.currentSceneString = currentSceneString;
        currentSceneLabel.setText(currentSceneString);
    }

    Map<String, List<String>> sceneMoveTransitionMap = new HashMap<String, List<String>>();
    CamEnum currentScene = Slides.getInstance();

    Robot r;

    OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false, "crefObsWebsockets", true);
//    OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false);

    Callback<SetCurrentSceneResponse> callback = new Callback<SetCurrentSceneResponse>() {
        @Override
        public void run(SetCurrentSceneResponse rt) {
            if (rt.getError() != null) {
                System.out.println(rt.getError());
            }

        }
    };
    private Callback<SetTransitionDurationResponse> responseCallback = new Callback<SetTransitionDurationResponse>() {
        @Override
        public void run(SetTransitionDurationResponse rt) {
            if (rt.getError() != null) {
                System.out.println(rt.getError());
            }
        }
    };

    /**
     * Creates new form ObsAutomation
     */
    public ObsAutomation() {

        try {
            r = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (controller.isFailed()) { // Awaits response from OBS
            // Here you can handle a failed connection request
            System.out.println("CONNECTION TO OBS FAILED");
        }

        List<String> tempList = new ArrayList<String>();
        for (MainCamEnum mce : MainCamEnum.values()) {
            tempList.add(mce.getSlideSceneName());
        }
        for (SdCamEnum mce : SdCamEnum.values()) {
            tempList.add(mce.getSlideSceneName());
        }
        for (SdCam2Enum ce : SdCam2Enum.values()) {
            tempList.add(ce.getSlideSceneName());
        }

        sceneMoveTransitionMap.put("Slides With Overlay", tempList);

        tempList = new ArrayList<String>();
        for (MainCamEnum mce : MainCamEnum.values()) {
            tempList.add(mce.getSlideSceneName());
        }
        tempList.add("Slides With Overlay");

        for (int i = 0; i < tempList.size() - 1; i++) {
            String s = tempList.get(i);
            sceneMoveTransitionMap.put(s, tempList);
        }

        tempList = new ArrayList<String>();
        for (SdCamEnum mce : SdCamEnum.values()) {
            tempList.add(mce.getSlideSceneName());
        }
        tempList.add("Slides With Overlay");

        for (int i = 0; i < tempList.size() - 1; i++) {
            String s = tempList.get(i);
            sceneMoveTransitionMap.put(s, tempList);
        }
        
        tempList = new ArrayList<String>();
        for (SdCam2Enum mce : SdCam2Enum.values()) {
            tempList.add(mce.getSlideSceneName());
        }
        tempList.add("Slides With Overlay");

        for (int i = 0; i < tempList.size() - 1; i++) {
            String s = tempList.get(i);
            sceneMoveTransitionMap.put(s, tempList);
        }

        tempList = new ArrayList<String>();
        for (MainCamEnum mce : MainCamEnum.values()) {
            tempList.add(mce.getSceneName());
        }

        for (String s : tempList) {
            sceneMoveTransitionMap.put(s, tempList);
        }

        tempList = new ArrayList<String>();
        for (SdCamEnum mce : SdCamEnum.values()) {
            tempList.add(mce.getSceneName());
        }

        for (String s : tempList) {
            sceneMoveTransitionMap.put(s, tempList);
        }
        
        tempList = new ArrayList<String>();
        for (SdCam2Enum mce : SdCam2Enum.values()) {
            tempList.add(mce.getSceneName());
        }

        for (String s : tempList) {
            sceneMoveTransitionMap.put(s, tempList);
        }

        initComponents();
        setLocation(960, 0);

        Notifier.getInstance().addObserver(new Observer() {
            @Override
            public void notify(CamEnum shot) {
                if (!cancelAutoPilot) {
                    if (Slides.getInstance().equals(shot)) {
                        transition(shot);
                    } else if (!currentScene.equals(shot) && !SlideState.FULL_SLIDES.equals(slideState) && lastTransitionTime + 1500 < System.currentTimeMillis()) {
                        transition(shot);
                    }
                }
            }

            @Override
            public void setTransition(boolean on) {
                inTransition = on;
            }
        });

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int sdX = 486;
                int sdY = 288;
                int sdW = 168;
                int sdH = 88;
                Stroke stroke = new BasicStroke(3f);
                int red = 255;
                while (true) {
                    red += 20;
                    if (red > 255) {
                        red = red - 155;
                    }
                    Color slideComboColor = new Color(red, 0, 0, 200);

                    BufferedImage tempImage = r.createScreenCapture(new Rectangle(new Point(53, 292), new Dimension(123, 66)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(1, 1, 80, 60);
                    }
                    double scale = .8;
                    genHdButton4.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, scale, 0)));
                    genHdButton4.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(70, 292), new Dimension(123, 66)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(1, 1, 80, 60);
                    }
                    genHdButton45.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, scale, 0)));
                    genHdButton45.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(90, 292), new Dimension(123, 66)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(20, 1, 80, 60);
                    }
                    genHdButton5.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, scale, 0)));
                    genHdButton5.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(115, 292), new Dimension(123, 66)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(25, 1, 80, 60);
                    }
                    genHdButton55.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, scale, 0)));
                    genHdButton55.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(133, 292), new Dimension(123, 66)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(43, 1, 80, 60);
                    }
                    genHdButton6.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, scale, 0)));
                    genHdButton6.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(50, 280), new Dimension(210, 113)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(21, 1, 166, 110);
                    }
                    genHdButton.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, .6, 0)));
                    genHdButton.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(115, 297), new Dimension(78, 41)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(new BasicStroke(1f));
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(7, 1, 62, 40);
                    }
                    genHdButtonTight.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, 1.6, 0)));
                    genHdButtonTight.setText("");

                    scale = .7;
                    genSdButton4.setIcon(new ImageIcon(ImageUtilities.getImage(r.createScreenCapture(new Rectangle(new Point(sdX, sdY), new Dimension(sdW, sdH))), scale, 0)));
                    genSdButton4.setText("");

                    genSdButton5.setIcon(new ImageIcon(ImageUtilities.getImage(r.createScreenCapture(new Rectangle(new Point(sdX + 20, sdY), new Dimension(sdW, sdH))), scale, 0)));
                    genSdButton5.setText("");

                    genSdButton6.setIcon(new ImageIcon(ImageUtilities.getImage(r.createScreenCapture(new Rectangle(new Point(sdX + 40, sdY), new Dimension(sdW, sdH))), scale, 0)));
                    genSdButton6.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(480, 280), new Dimension(210, 113)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(21, 1, 166, 110);
                    }
                    genSdButton.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, .6, 0)));
                    genSdButton.setText("");

                    tempImage = r.createScreenCapture(new Rectangle(new Point(710, 280), new Dimension(200, 113)));
                    if (SlideState.SLIDE_COMBO.equals(slideState)) {
                        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                        g2d.setStroke(stroke);
                        g2d.setColor(slideComboColor);
                        g2d.drawRect(11, 1, 166, 110);
                    }
                    sdCam2Button.setIcon(new ImageIcon(ImageUtilities.getImage(tempImage, .6, 0)));
                    sdCam2Button.setText("");

                    fullSlidesButton.setIcon(new ImageIcon(ImageUtilities.getImage(r.createScreenCapture(new Rectangle(new Point(265, 280), new Dimension(210, 113))), .75, 0)));
                    fullSlidesButton.setText("");
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        t.start();

        Thread slideThread = new Thread(new Runnable() {
            @Override
            public void run() {
                prevSlideImage = null;
                boolean prevNotADifference = true;
                while (true) {
                    slideImage = r.createScreenCapture(new Rectangle(new Point(270, 280), new Dimension(200, 110)));
                    slideLabelImage = r.createScreenCapture(new Rectangle(new Point(270, 280), new Dimension(200, 110)));

                    Graphics g = slideLabelImage.getGraphics();
                    g.setColor(Color.RED);
                    g.drawRect(slidesX, slidesY, slidesW, slidesH);

                    if (prevSlideImage != null) {
                        boolean slideDifference = false;
                        for (int i = slidesX; i < slidesW + slidesX && !slideDifference; i++) {
                            for (int j = slidesY; j < slidesH + slidesY && !slideDifference; j++) {
                                if (isDifferent(slideImage.getRGB(i, j), prevSlideImage.getRGB(i, j))) {
                                    slideDifference = true;
                                }
                            }
                        }

                        if (saveBlankSlideImage) {
                            blankSlideImage = prevSlideImage;
                            saveBlankSlideImage = false;

                            jButton16.setIcon(new ImageIcon(blankSlideImage.getSubimage(slidesX, slidesY, slidesW, slidesH)));
                            jButton16.setText("");
                        }

                        if (slideDifference) {
                            if (prevNotADifference) {
                                boolean blankSlide = false;
                                if (blankSlideImage != null) {
                                    blankSlide = true;
                                    for (int i = slidesX; i < slidesW + slidesX && blankSlide; i++) {
                                        for (int j = slidesY; j < slidesH + slidesY && blankSlide; j++) {
                                            if (isDifferent(slideImage.getRGB(i, j), blankSlideImage.getRGB(i, j))) {
                                                blankSlide = false;
                                            }
                                        }
                                    }
                                }

                                if (blankSlide) {
                                    if (!cancelAutoPilot) {
                                        setSlideState(SlideState.NO_SLIDE);
                                        transition(lastCamRecommendation);
                                    }
                                } else {

                                    System.out.println("New Slide.");
                                    newSlideTime = System.currentTimeMillis();
                                    try {
//                                        System.out.println("autopilot on? " + !cancelAutoPilot);
                                        if (!cancelAutoPilot) {
                                            setSlideState(SlideState.FULL_SLIDES);
                                            fullSlidesButton.doClick();
                                            final long slideTime = newSlideTime;
                                            Thread t = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Thread.sleep(Long.valueOf(serviceSectionState.getFullScreenSlideTime()));
                                                    } catch (InterruptedException ex) {
                                                        Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                                                    }
                                                    if (!cancelAutoPilot && SlideState.FULL_SLIDES.equals(slideState) && newSlideTime == slideTime) {
                                                        setSlideState(SlideState.SLIDE_COMBO);
                                                        transition(lastSdCamRecommendation);
//                                                        transition(lastMainCamRecommendation);
                                                        Thread fsThread = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    System.out.println("    waiting to switch to full camera in (ms): " + serviceSectionState.getComboSlideScreenTime());
                                                                    Thread.sleep(Long.valueOf(serviceSectionState.getComboSlideScreenTime()));
                                                                } catch (InterruptedException ex) {
                                                                    Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                                                                }
                                                                if (!cancelAutoPilot && SlideState.SLIDE_COMBO.equals(slideState) && newSlideTime == slideTime) {
                                                                    setSlideState(SlideState.NO_SLIDE);
                                                                    transition(lastCamRecommendation);
                                                                }
                                                            }
                                                        });
                                                        fsThread.start();
                                                    }
                                                }
                                            });
                                            t.start();
                                        } else if (!cancelAutoSlideSwitch) {
                                            if (slideState.equals(SlideState.NO_SLIDE)) {
                                                fullSlidesButton.doClick();
                                            }
                                        } else {
                                            Notifier.getInstance().notify(Slides.getInstance());
                                            slidesPanel.setOpaque(true);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    prevNotADifference = false;
                                }
                            }
                        } else {
                            prevNotADifference = true;
                        }
                    }

                    slidesLabel.setIcon(new ImageIcon(slideLabelImage));
                    prevSlideImage = slideImage;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        slideThread.start();

        overlaySwitchButton.setIcon(new ImageIcon(SlideState.SLIDE_COMBO.getOverlayImage()));
        overlaySwitchButton.setText("");
        overlaySwitchButton1.setIcon(new ImageIcon(SlideState.NO_SLIDE.getOverlayImage()));
        overlaySwitchButton1.setText("");

        setSize(new Dimension(950, 800));
    }

    boolean inTransition = false;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        helperDialog = new javax.swing.JDialog();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        worshipSetCountDownDialog = new javax.swing.JDialog();
        jPanel20 = new javax.swing.JPanel();
        countDownCancelButton = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        countDownLabel = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel16 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        prepHelperButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        fadeDelayField = new javax.swing.JTextField();
        moveDelaySpinner = new javax.swing.JTextField();
        worshipCountdownTimer = new javax.swing.JTextField();
        startCountdownButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        startStreamButton = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        autoPilotButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        sensitivityField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel12 = new javax.swing.JPanel();
        jPanel37 = new javax.swing.JPanel();
        sdCameraLabel = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jPanel30 = new javax.swing.JPanel();
        camera1Label = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        slidesLabel = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jButton16 = new javax.swing.JButton();
        jPanel28 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        currentSceneLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        currentOverlayLabel = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        sdCam2Button = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        genHdButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        genHdButtonTight = new javax.swing.JButton();
        jPanel42 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        genHdButton4 = new javax.swing.JButton();
        genHdButton45 = new javax.swing.JButton();
        genHdButton5 = new javax.swing.JButton();
        genHdButton55 = new javax.swing.JButton();
        genHdButton6 = new javax.swing.JButton();
        jPanel35 = new javax.swing.JPanel();
        jPanel40 = new javax.swing.JPanel();
        genSdButton = new javax.swing.JButton();
        jPanel41 = new javax.swing.JPanel();
        jPanel39 = new javax.swing.JPanel();
        genSdButton4 = new javax.swing.JButton();
        genSdButton5 = new javax.swing.JButton();
        genSdButton6 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        slidesPanel = new javax.swing.JPanel();
        fullSlidesButton = new javax.swing.JButton();
        jPanel31 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        overlaySwitchButton = new javax.swing.JButton();
        overlaySwitchButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        welcomeButton = new javax.swing.JButton();
        welcomeButton2 = new javax.swing.JButton();
        welcomeButton1 = new javax.swing.JButton();
        goodByeButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        oneButton = new javax.swing.JButton();
        twoButton = new javax.swing.JButton();

        helperDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                helperDialogWindowClosing(evt);
            }
        });
        helperDialog.getContentPane().setLayout(new java.awt.GridLayout(0, 1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Go to youtube.com in Chrome. Click Create (Looks like a video camera with a + on it).  Select Go Live.");
        helperDialog.getContentPane().add(jLabel10);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Click Edit.  Update the title and description to the sermon title.  Minimize Chrome.");
        helperDialog.getContentPane().add(jLabel11);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        helperDialog.getContentPane().add(jLabel12);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("In OBS, Select the View menu, Select Multiview (Windowed), and drag the resulting popup window to the upper left hand corner of the screen.");
        helperDialog.getContentPane().add(jLabel13);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("Close this Dialog");
        helperDialog.getContentPane().add(jLabel14);

        worshipSetCountDownDialog.setUndecorated(true);

        countDownCancelButton.setText("Cancel");
        countDownCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countDownCancelButtonActionPerformed(evt);
            }
        });
        jPanel20.add(countDownCancelButton);

        worshipSetCountDownDialog.getContentPane().add(jPanel20, java.awt.BorderLayout.SOUTH);

        countDownLabel.setText("jLabel15");
        jPanel21.add(countDownLabel);

        worshipSetCountDownDialog.getContentPane().add(jPanel21, java.awt.BorderLayout.NORTH);

        jPanel22.setLayout(new java.awt.BorderLayout());

        jLabel15.setText("<html><body>Camera 1 Overlay will be automatically pressed.<br/>after 2 seconds -CREF lower third will automatically be pressed.<br/>after 10 seconds -CREF lower thrid will automatically turn off.");
        jPanel22.add(jLabel15, java.awt.BorderLayout.CENTER);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(153, 0, 51));
        jPanel22.add(jLabel21, java.awt.BorderLayout.PAGE_START);

        worshipSetCountDownDialog.getContentPane().add(jPanel22, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jPanel16.setLayout(new java.awt.BorderLayout());

        jPanel19.setLayout(new java.awt.BorderLayout());

        jPanel17.setLayout(new java.awt.GridLayout(0, 1));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Stream Prep Helper:");
        jPanel17.add(jLabel3);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jPanel17.add(jLabel18);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Fade Delay (milliseconds):");
        jPanel17.add(jLabel1);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Move Delay (milliseconds):");
        jPanel17.add(jLabel2);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Countdown Timer (seconds):");
        jPanel17.add(jLabel4);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Start Countdown Worship Set:");
        jPanel17.add(jLabel5);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jPanel17.add(jLabel8);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Stream and Record:");
        jPanel17.add(jLabel6);

        jPanel19.add(jPanel17, java.awt.BorderLayout.WEST);

        jPanel18.setLayout(new java.awt.GridLayout(0, 1));

        prepHelperButton.setText("Helper");
        prepHelperButton.setToolTipText("<html><body>Switch to studio mode and back (fixes elgato audio problems)<br/>Open YouTube Studio<br/>");
        prepHelperButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prepHelperButtonActionPerformed(evt);
            }
        });
        jPanel18.add(prepHelperButton);
        jPanel18.add(jPanel4);

        fadeDelayField.setColumns(6);
        fadeDelayField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fadeDelayField.setText("1500");
        jPanel18.add(fadeDelayField);

        moveDelaySpinner.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        moveDelaySpinner.setText("5000");
        jPanel18.add(moveDelaySpinner);

        worshipCountdownTimer.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        worshipCountdownTimer.setText("10");
        jPanel18.add(worshipCountdownTimer);

        startCountdownButton.setText("Start");
        startCountdownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startCountdownButtonActionPerformed(evt);
            }
        });
        jPanel18.add(startCountdownButton);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jPanel18.add(jLabel9);

        startStreamButton.setText("Start");
        startStreamButton.setEnabled(false);
        startStreamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStreamButtonActionPerformed(evt);
            }
        });
        jPanel18.add(startStreamButton);

        jPanel19.add(jPanel18, java.awt.BorderLayout.EAST);

        jPanel16.add(jPanel19, java.awt.BorderLayout.NORTH);

        jPanel23.setLayout(new java.awt.BorderLayout());
        jPanel23.add(jPanel25, java.awt.BorderLayout.PAGE_START);

        jPanel26.setLayout(new java.awt.BorderLayout());

        autoPilotButton.setText("Auto-Pilot");
        autoPilotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoPilotButtonActionPerformed(evt);
            }
        });
        jPanel8.add(autoPilotButton);

        jPanel26.add(jPanel8, java.awt.BorderLayout.CENTER);

        jLabel20.setText("Sensitivity:");
        jLabel20.setToolTipText("The lower the number, the more sensitive.");
        jPanel7.add(jLabel20);

        sensitivityField.setColumns(3);
        sensitivityField.setText("30");
        jPanel7.add(sensitivityField);

        jPanel26.add(jPanel7, java.awt.BorderLayout.EAST);

        jPanel23.add(jPanel26, java.awt.BorderLayout.PAGE_END);

        jPanel12.setLayout(new java.awt.GridLayout(1, 0));

        jPanel37.setBorder(javax.swing.BorderFactory.createTitledBorder("SD"));
        jPanel37.setLayout(new java.awt.BorderLayout());
        jPanel37.add(sdCameraLabel, java.awt.BorderLayout.CENTER);

        jPanel10.setLayout(new java.awt.GridLayout(1, 0));

        jButton6.setText("o");
        jButton6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton6);

        jButton7.setText("o");
        jButton7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton7);

        jButton8.setText("o");
        jButton8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton8);

        jButton9.setText("o");
        jButton9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton9);

        jButton10.setText("o");
        jButton10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton10);

        jPanel37.add(jPanel10, java.awt.BorderLayout.SOUTH);

        jPanel12.add(jPanel37);

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder("HD"));
        jPanel30.setLayout(new java.awt.BorderLayout());
        jPanel30.add(camera1Label, java.awt.BorderLayout.CENTER);

        jPanel11.setLayout(new java.awt.GridLayout(1, 0));

        jButton11.setText("o");
        jButton11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton11);

        jButton12.setText("o");
        jButton12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton12);

        jButton13.setText("o");
        jButton13.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton13);

        jButton14.setText("o");
        jButton14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton14);

        jButton15.setText("o");
        jButton15.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton15);

        jPanel30.add(jPanel11, java.awt.BorderLayout.SOUTH);

        jPanel12.add(jPanel30);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Slides"));
        jPanel13.setLayout(new java.awt.BorderLayout());
        jPanel13.add(slidesLabel, java.awt.BorderLayout.CENTER);

        jPanel14.setLayout(new java.awt.GridLayout(1, 0));

        jButton16.setText("blank");
        jButton16.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton16);

        jPanel13.add(jPanel14, java.awt.BorderLayout.SOUTH);

        jPanel12.add(jPanel13);

        jScrollPane1.setViewportView(jPanel12);

        jPanel23.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel16.add(jPanel23, java.awt.BorderLayout.CENTER);

        jPanel28.setLayout(new java.awt.BorderLayout());
        jPanel16.add(jPanel28, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(jPanel16);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel16.setText("Service Section:");
        jLabel16.setToolTipText("Sets transition duration defaults.");
        jPanel24.add(jLabel16);

        jButton1.setText("Worship Set");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel24.add(jButton1);

        jButton2.setText("Announcements");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel24.add(jButton2);

        jButton3.setText("Sermon");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel24.add(jButton3);

        jPanel1.add(jPanel24, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Current Scene:");
        jPanel34.add(jLabel17);
        jPanel34.add(currentSceneLabel);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Current Overlay:");
        jPanel34.add(jLabel7);
        jPanel34.add(currentOverlayLabel);

        jPanel3.add(jPanel34, java.awt.BorderLayout.SOUTH);

        jPanel36.setLayout(new java.awt.GridLayout(0, 1));

        jPanel29.setLayout(new java.awt.BorderLayout());

        sdCam2Button.setText("SD Cam2");
        sdCam2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sdCam2ButtonActionPerformed(evt);
            }
        });
        jPanel38.add(sdCam2Button);
        jPanel38.add(filler2);

        genHdButton.setText("ZO");
        genHdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genHdButtonActionPerformed(evt);
            }
        });
        jPanel38.add(genHdButton);
        jPanel38.add(filler1);

        genHdButtonTight.setText("Tight");
        genHdButtonTight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genHdButtonTightActionPerformed(evt);
            }
        });
        jPanel38.add(genHdButtonTight);

        jPanel29.add(jPanel38, java.awt.BorderLayout.NORTH);

        jPanel27.setLayout(new java.awt.GridLayout(1, 0));

        genHdButton4.setText("4");
        genHdButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genHdButton4ActionPerformed(evt);
            }
        });
        jPanel27.add(genHdButton4);

        genHdButton45.setText("4.5");
        genHdButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genHdButton45ActionPerformed(evt);
            }
        });
        jPanel27.add(genHdButton45);

        genHdButton5.setText("5");
        genHdButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genHdButton5ActionPerformed(evt);
            }
        });
        jPanel27.add(genHdButton5);

        genHdButton55.setText("5.5");
        genHdButton55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genHdButton55ActionPerformed(evt);
            }
        });
        jPanel27.add(genHdButton55);

        genHdButton6.setText("6");
        genHdButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genHdButton6ActionPerformed(evt);
            }
        });
        jPanel27.add(genHdButton6);

        jPanel42.add(jPanel27);

        jPanel29.add(jPanel42, java.awt.BorderLayout.PAGE_END);

        jPanel36.add(jPanel29);

        jPanel35.setLayout(new java.awt.BorderLayout());

        genSdButton.setText("ZO");
        genSdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSdButtonActionPerformed(evt);
            }
        });
        jPanel40.add(genSdButton);

        jPanel35.add(jPanel40, java.awt.BorderLayout.PAGE_START);

        jPanel39.setLayout(new java.awt.GridLayout(1, 0));

        genSdButton4.setText("4");
        genSdButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSdButton4ActionPerformed(evt);
            }
        });
        jPanel39.add(genSdButton4);

        genSdButton5.setText("5");
        genSdButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSdButton5ActionPerformed(evt);
            }
        });
        jPanel39.add(genSdButton5);

        genSdButton6.setText("6");
        genSdButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSdButton6ActionPerformed(evt);
            }
        });
        jPanel39.add(genSdButton6);

        jPanel41.add(jPanel39);

        jPanel35.add(jPanel41, java.awt.BorderLayout.CENTER);

        jPanel36.add(jPanel35);

        jPanel3.add(jPanel36, java.awt.BorderLayout.CENTER);

        jButton4.setText("Auto");
        jButton4.setToolTipText("Auto Switch To New Slides");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton4);

        slidesPanel.setBackground(new java.awt.Color(51, 153, 0));
        slidesPanel.setOpaque(false);

        fullSlidesButton.setForeground(new java.awt.Color(51, 102, 0));
        fullSlidesButton.setText("Full Screen Slides");
        fullSlidesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullSlidesButtonActionPerformed(evt);
            }
        });
        slidesPanel.add(fullSlidesButton);

        jPanel9.add(slidesPanel);

        jPanel33.setBorder(javax.swing.BorderFactory.createTitledBorder("Switch Overlay:"));
        jPanel33.setLayout(new java.awt.GridLayout(0, 1));

        overlaySwitchButton.setText("Slide Combo");
        overlaySwitchButton.setToolTipText("Slide Combo");
        overlaySwitchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overlaySwitchButtonActionPerformed(evt);
            }
        });
        jPanel33.add(overlaySwitchButton);

        overlaySwitchButton1.setText("No Slides");
        overlaySwitchButton1.setToolTipText("No Slides");
        overlaySwitchButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overlaySwitchButton1ActionPerformed(evt);
            }
        });
        jPanel33.add(overlaySwitchButton1);

        jPanel31.add(jPanel33);

        jPanel9.add(jPanel31);

        jPanel3.add(jPanel9, java.awt.BorderLayout.PAGE_START);

        jPanel1.add(jPanel3, java.awt.BorderLayout.SOUTH);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Scenes with no audio"));

        welcomeButton.setText("Hi");
        welcomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                welcomeButtonActionPerformed(evt);
            }
        });
        jPanel2.add(welcomeButton);

        welcomeButton2.setText("Slides");
        welcomeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                welcomeButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(welcomeButton2);

        welcomeButton1.setForeground(new java.awt.Color(0, 102, 204));
        welcomeButton1.setText("BRB");
        welcomeButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                welcomeButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(welcomeButton1);

        goodByeButton.setForeground(new java.awt.Color(204, 0, 51));
        goodByeButton.setText("Bye");
        goodByeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goodByeButtonActionPerformed(evt);
            }
        });
        jPanel2.add(goodByeButton);

        jPanel5.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Lower Thirds Toggles (Hotkey Enabled)"));

        oneButton.setText("Pastor");
        oneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneButtonActionPerformed(evt);
            }
        });
        jPanel15.add(oneButton);

        twoButton.setText("CREF");
        twoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoButtonActionPerformed(evt);
            }
        });
        jPanel15.add(twoButton);

        jPanel6.add(jPanel15, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    long lastTransitionTime = 0l;
    private SlideState slideState = SlideState.NO_SLIDE;

    private void transition(CamEnum scene) {
        currentScene = scene;
        if (!Slides.getInstance().equals(scene)) {
            lastCamRecommendation = scene;
        }
        System.out.println("Transition to " + scene + " : " + getSlideState());
        transition(getSlideState().getTransition().go(scene));
    }

    private String currentSceneString = "";

    public void transition(String scene) {
        transition(scene, null, -1);
    }

    public void transition(String scene, String paramTransition, int paramDuration) {
//        System.out.println("    In transition? " + inTransition);
        if (inTransition) {
            return;
        }
        if (getCurrentSceneString().equals(scene)) {
            return;//do nothing, we're already there
        }
        Notifier.getInstance().setTransition(true);
//        System.out.println("          ~Entering transition.");
        System.out.println("Transition to Scene: " + scene);

        Thread captureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!cancelAutoPilot) {
                    BufferedImage bi = r.createScreenCapture(new Rectangle(0, 0, 960, 520));
                    Graphics2D g2d = (Graphics2D) bi.getGraphics();
                    g2d.setColor(Color.RED);
                    g2d.drawString(currentSceneString, 150, 20);
                    File outputFile = new File("D:\\Dustin\\OBS Images\\" + System.currentTimeMillis() + ".png");
                    try {
                        ImageIO.write(bi, "PNG", outputFile);
                    } catch (IOException ex) {
                        Logger.getLogger(ImageCreator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        captureThread.start();

        int duration = serviceSectionState.getFadeDelay();
        try {
            duration = Integer.valueOf(fadeDelayField.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String transition = "Fade";
        if (getCurrentSceneString() != null) {
            if (getCurrentSceneString().equals(scene)) {
                duration = 100;
            } else {
                List<String> list = sceneMoveTransitionMap.get(getCurrentSceneString());
                if (list != null) {
                    if (list.contains(scene)) {
                        transition = "Slow Move";
                        duration = serviceSectionState.getMoveDelay();
                        try {
                            duration = Integer.valueOf(moveDelaySpinner.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        lastTransitionTime = System.currentTimeMillis() + duration;
        if (paramDuration > 0) {
            duration = paramDuration;
        }
        if (paramTransition != null) {
            transition = paramTransition;
        }
        controller.setTransitionDuration(duration, responseCallback);
        controller.changeSceneWithTransition(scene, transition, callback);
        final int finalDuration = duration;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    System.out.println("          ~Waiting for transition to end for (ms): " + finalDuration);
                    Thread.sleep(finalDuration);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Notifier.getInstance().setTransition(false);
//                System.out.println("          ~Out of transition.");
//                System.out.println("ReEnable Buttons?");
            }
        });
        t.start();

        setCurrentSceneString(scene);
    }

    private void welcomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_welcomeButtonActionPerformed
        transition("Intro (No Audio Broadcast)");
    }//GEN-LAST:event_welcomeButtonActionPerformed

    private void goodByeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goodByeButtonActionPerformed
        cancelAutoPilot();
        cancelAutoSlideSwitch = true;
        transition("Ending (No Audio Broadcast)", "Cut", 100);
    }//GEN-LAST:event_goodByeButtonActionPerformed

    private void fullSlidesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullSlidesButtonActionPerformed
        transition(Slides.getInstance());
//        if (SlideState.NO_SLIDE.equals(slideState)) {
//            overlaySwitchButton.doClick();
//        }
        slidesPanel.setOpaque(false);
    }//GEN-LAST:event_fullSlidesButtonActionPerformed

    private void oneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneButtonActionPerformed
//        r.keyPress(KeyEvent.VK_ALT);
//        r.keyPress(KeyEvent.VK_TAB);
//        r.keyRelease(KeyEvent.VK_TAB);
//        r.keyRelease(KeyEvent.VK_ALT);

        r.mouseMove(10, 100);
        r.mousePress(MouseEvent.BUTTON1_MASK);
        r.mouseRelease(MouseEvent.BUTTON1_MASK);

        r.delay(500);

        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_NUMPAD1);
        r.keyRelease(KeyEvent.VK_NUMPAD1);
        r.keyRelease(KeyEvent.VK_CONTROL);

        r.delay(500);

        r.keyPress(KeyEvent.VK_ALT);
        r.keyPress(KeyEvent.VK_TAB);
        r.keyRelease(KeyEvent.VK_TAB);
        r.keyRelease(KeyEvent.VK_ALT);

        Point p = ((JButton) evt.getSource()).getLocationOnScreen();
        r.mouseMove(p.x, p.y);
    }//GEN-LAST:event_oneButtonActionPerformed

    private void twoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoButtonActionPerformed
//        r.keyPress(KeyEvent.VK_ALT);
//        r.keyPress(KeyEvent.VK_TAB);
//        r.keyRelease(KeyEvent.VK_TAB);
//        r.keyRelease(KeyEvent.VK_ALT);

        r.mouseMove(10, 100);
        r.mousePress(MouseEvent.BUTTON1_MASK);
        r.mouseRelease(MouseEvent.BUTTON1_MASK);

        r.delay(500);

        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_NUMPAD2);
        r.keyRelease(KeyEvent.VK_NUMPAD2);
        r.keyRelease(KeyEvent.VK_CONTROL);

        r.delay(500);

        r.keyPress(KeyEvent.VK_ALT);
        r.keyPress(KeyEvent.VK_TAB);
        r.keyRelease(KeyEvent.VK_TAB);
        r.keyRelease(KeyEvent.VK_ALT);

        Point p = ((JButton) evt.getSource()).getLocationOnScreen();
        r.mouseMove(p.x, p.y);
    }//GEN-LAST:event_twoButtonActionPerformed

    private void prepHelperButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prepHelperButtonActionPerformed
        System.out.println("Switch to Studio Mode and Back");
        Callback<SetStudioModeEnabledResponse> cb = new Callback<SetStudioModeEnabledResponse>() {
            @Override
            public void run(SetStudioModeEnabledResponse rt) {
                if (rt.getError() != null) {
                    System.out.println(rt.getError());
                }
            }
        };
        controller.setStudioModeEnabled(false, cb);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller.setStudioModeEnabled(true, cb);

        helperDialog.setVisible(true);
        helperDialog.pack();
        helperDialog.setAlwaysOnTop(true);
        helperDialog.setLocation(100, 100);

        welcomeButton.doClick();
        startStreamButton.setEnabled(true);
    }//GEN-LAST:event_prepHelperButtonActionPerformed

    boolean cancelWorshipSetTimer = true;
    int worshipSetTimerLength = 0;

    private void startCountdownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCountdownButtonActionPerformed
        startCountdownButton.setEnabled(false);
        worshipSetCountDownDialog.setVisible(true);
        worshipSetCountDownDialog.pack();

        if (startStreamButton.getText().equals("Start")) {
            jLabel21.setText("Don't forget to start recording!!!");
            worshipSetCountDownDialog.pack();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 6; i++) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                startStreamButton.setForeground(new Color(0, 150, 0));
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                startStreamButton.setForeground(Color.BLACK);
                            }
                        });

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            t.start();
        }

        try {
            worshipSetTimerLength = Integer.valueOf(worshipCountdownTimer.getText());

            cancelWorshipSetTimer = false;

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (; worshipSetTimerLength > 0; worshipSetTimerLength--) {
                        countDownLabel.setText(worshipSetTimerLength + "");
                        try {
                            Thread.sleep(990);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (cancelWorshipSetTimer) {
                            break;
                        }
                    }

                    worshipSetCountDownDialog.setVisible(false);
                    startCountdownButton.setEnabled(true);

                    if (!cancelWorshipSetTimer) {
                        transition("Slides With Overlay", "Cut", 500);
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        setSlideState(SlideState.NO_SLIDE);
                        transition(MainCamEnum.WIDE);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        twoButton.doClick();
                        twoButton.setEnabled(false);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        twoButton.setEnabled(true);
                        twoButton.doClick();
                    }
                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_startCountdownButtonActionPerformed

    private void startStreamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStreamButtonActionPerformed
        startStreamButton.setEnabled(false);
        if (startStreamButton.getText().equals("Start")) {
            controller.startRecording(new Callback<StartRecordingResponse>() {
                @Override
                public void run(StartRecordingResponse rt) {
                    if (rt.getError() != null) {
                        System.out.println(rt.getError());
                    }
                }
            });

            controller.startStreaming(new Callback<StartStreamingResponse>() {
                @Override
                public void run(StartStreamingResponse rt) {
                    if (rt.getError() != null) {
                        System.out.println(rt.getError());
                    }
                }
            });

            startStreamButton.setText("Stop");
            startStreamButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/coonrapidsfree/images/record.png")));
        } else {
            controller.stopRecording(new Callback<StopRecordingResponse>() {
                @Override
                public void run(StopRecordingResponse rt) {
                    if (rt.getError() != null) {
                        System.out.println(rt.getError());
                    }
                }
            });

            controller.stopStreaming(new Callback<StopStreamingResponse>() {
                @Override
                public void run(StopStreamingResponse rt) {
                    if (rt.getError() != null) {
                        System.out.println(rt.getError());
                    }
                }
            });
            startStreamButton.setText("Start");
            startStreamButton.setIcon(null);
        }
        startStreamButton.setEnabled(true);
    }//GEN-LAST:event_startStreamButtonActionPerformed

    private void countDownCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countDownCancelButtonActionPerformed
        cancelWorshipSetTimer = true;
    }//GEN-LAST:event_countDownCancelButtonActionPerformed

    boolean cancelAutoPilot = true;

    BufferedImage sixD;
    BufferedImage sixDLabel;
    BufferedImage sixDPrev = null;

    BufferedImage camera1;
    BufferedImage camera1Lbl;
    BufferedImage camera1Prev = null;

    int cam1X = 50;
    int cam1Y = 280;
    int cam1W = 211;
    int cam1H = 110;

    int cam1X4 = 20;
    int cam1Y4 = 70;
    int cam1W4 = 10;
    int cam1H4 = 10;

    int cam1X4_5 = 50;
    int cam1Y4_5 = cam1Y4;
    int cam1W4_5 = 20;
    int cam1H4_5 = 10;

    int cam1X5 = 90;
    int cam1Y5 = cam1Y4;
    int cam1W5 = 30;
    int cam1H5 = 10;

    int cam1X5_5 = 140;
    int cam1Y5_5 = cam1Y4;
    int cam1W5_5 = 20;
    int cam1H5_5 = 10;

    int cam1X6 = 180;
    int cam1Y6 = cam1Y4;
    int cam1W6 = 10;
    int cam1H6 = 10;

    int sdXL = 0;
    int sdYL = 40;
    int sdWL = 34;
    int sdHL = 20;

    int sdX4 = sdXL + sdWL + 10;
    int sdY4 = sdYL;
    int sdW4 = sdWL;
    int sdH4 = sdHL;

    int sdX5 = sdX4 + sdW4 + 10;
    int sdY5 = sdYL;
    int sdW5 = sdWL;
    int sdH5 = sdHL;

    int sdX6 = sdX5 + sdW5 + 10;
    int sdY6 = sdYL;
    int sdW6 = sdWL;
    int sdH6 = sdHL;

    int sdXR = sdX6 + sdW6 + 5;
    int sdYR = sdYL;
    int sdWR = sdWL - 5;
    int sdHR = sdHL;

    BufferedImage sdLImage = null;
    boolean saveSdLImage = false;

    BufferedImage sd4Image = null;
    boolean saveSd4Image = false;

    BufferedImage sd5Image = null;
    boolean saveSd5Image = false;

    BufferedImage sd6Image = null;
    boolean saveSd6Image = false;

    BufferedImage sdRImage = null;
    boolean saveSdRImage = false;

    BufferedImage hd4Image = null;
    boolean saveHd4Image = false;

    BufferedImage hd4_5Image = null;
    boolean saveHd4_5Image = false;

    BufferedImage hd5Image = null;
    boolean saveHd5Image = false;

    BufferedImage hd5_5Image = null;
    boolean saveHd5_5Image = false;

    BufferedImage hd6Image = null;
    boolean saveHd6Image = false;
    private void autoPilotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoPilotButtonActionPerformed
        if (cancelAutoPilot) {
            autoPilotButton.setForeground(Color.GREEN);
            cancelAutoPilot = false;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!cancelAutoPilot) {
                        int sensitivity = 60;
                        try {
                            sensitivity = Integer.valueOf(sensitivityField.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (sensitivity == 0) {
                            sensitivity = 60;
                        }
                        sixD = r.createScreenCapture(new Rectangle(new Point(483, cam1Y), new Dimension(cam1W, cam1H)));
                        sixDLabel = r.createScreenCapture(new Rectangle(new Point(483, cam1Y), new Dimension(cam1W, cam1H)));

                        Graphics g = sixDLabel.getGraphics();
                        g.setColor(Color.RED);
                        g.drawRect(sdXL, sdYL, sdWL, sdHL);
                        g.setColor(Color.YELLOW);
                        g.drawRect(sdX4, sdY4, sdW4, sdH4);
                        g.setColor(Color.GREEN);
                        g.drawRect(sdX5, sdY5, sdW5, sdH5);
                        g.setColor(Color.GRAY);
                        g.drawRect(sdX6, sdY6, sdW6, sdH6);
                        g.setColor(Color.RED);
                        g.drawRect(sdXR, sdYR, sdWR, sdHR);

                        if (sixDPrev != null) {
                            int sdL = 0;
                            if (saveSdLImage) {
                                saveSdLImage = false;
                                sdLImage = sixDPrev.getSubimage(sdXL, sdYL, sdWL, sdHL);
                                jButton6.setIcon(new ImageIcon(sdLImage));
                                jButton6.setText("");
                            }
                            if (sdLImage != null) {
                                BufferedImage temp = sixD.getSubimage(sdXL, sdYL, sdWL, sdHL);
                                sdL = countDifferences(temp, sdLImage, sensitivity);
                            } else {
                                for (int i = sdXL; i < sdWL + sdXL; i++) {
                                    for (int j = sdYL; j < cam1H4 + sdYL; j++) {
                                        if (isDifferent(sixD.getRGB(i, j), sixDPrev.getRGB(i, j), sensitivity)) {
                                            sdL++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.RED);
                            g.drawString(sdL + "", sdXL, sdYL);

                            int sd4 = 0;
                            if (saveSd4Image) {
                                saveSd4Image = false;
                                sd4Image = sixDPrev.getSubimage(sdX4, sdY4, sdW4, sdH4);
                                jButton7.setIcon(new ImageIcon(sd4Image));
                                jButton7.setText("");
                            }
                            if (sd4Image != null) {
                                BufferedImage temp = sixD.getSubimage(sdX4, sdY4, sdW4, sdH4);
                                sd4 = countDifferences(temp, sd4Image, sensitivity);
                            } else {
                                for (int i = sdX4; i < sdW4 + sdX4; i++) {
                                    for (int j = sdY4; j < cam1H4 + sdY4; j++) {
                                        if (isDifferent(sixD.getRGB(i, j), sixDPrev.getRGB(i, j), sensitivity)) {
                                            sd4++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.YELLOW);
                            g.drawString(sd4 + "", sdX4, sdY4);

                            int sd5 = 0;
                            if (saveSd5Image) {
                                saveSd5Image = false;
                                sd5Image = sixDPrev.getSubimage(sdX5, sdY5, sdW5, sdH5);
                                jButton8.setIcon(new ImageIcon(sd5Image));
                                jButton8.setText("");
                            }
                            if (sd5Image != null) {
                                BufferedImage temp = sixD.getSubimage(sdX5, sdY5, sdW5, sdH5);
                                sd5 = countDifferences(temp, sd5Image, sensitivity);
                            } else {
                                for (int i = sdX5; i < sdW5 + sdX5; i++) {
                                    for (int j = sdY5; j < cam1H4 + sdY5; j++) {
                                        if (isDifferent(sixD.getRGB(i, j), sixDPrev.getRGB(i, j), sensitivity)) {
                                            sd5++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.GREEN);
                            g.drawString(sd5 + "", sdX5, sdY5);

                            int sd6 = 0;
                            if (saveSd6Image) {
                                saveSd6Image = false;
                                sd6Image = sixDPrev.getSubimage(sdX6, sdY6, sdW6, sdH6);
                                jButton9.setIcon(new ImageIcon(sd6Image));
                                jButton9.setText("");
                            }
                            if (sd6Image != null) {
                                BufferedImage temp = sixD.getSubimage(sdX6, sdY6, sdW6, sdH6);
                                sd6 = countDifferences(temp, sd6Image, sensitivity);
                            } else {
                                for (int i = sdX6; i < sdW6 + sdX6; i++) {
                                    for (int j = sdY6; j < cam1H4 + sdY6; j++) {
                                        if (isDifferent(sixD.getRGB(i, j), sixDPrev.getRGB(i, j), sensitivity)) {
                                            sd6++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.GRAY);
                            g.drawString(sd6 + "", sdX6, sdY6);

                            int sdR = 0;
                            if (saveSdRImage) {
                                saveSdRImage = false;
                                sdRImage = sixDPrev.getSubimage(sdXR, sdYR, sdWR, sdHR);
                                jButton10.setIcon(new ImageIcon(sdRImage));
                                jButton10.setText("");
                            }
                            if (sdRImage != null) {
                                BufferedImage temp = sixD.getSubimage(sdXR, sdYR, sdWR, sdHR);
                                sdR = countDifferences(temp, sdRImage, sensitivity);
                            } else {
                                for (int i = sdXR; i < sdWR + sdXR; i++) {
                                    for (int j = sdYR; j < cam1H4 + sdYR; j++) {
                                        if (isDifferent(sixD.getRGB(i, j), sixDPrev.getRGB(i, j), sensitivity)) {
                                            sdR++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.RED);
                            g.drawString(sdR + "", sdXR, sdYR);

                            analyzeSdCameraDifferencesAndMakeRecommendation(sdL, sd4, sd5, sd6, sdR);
                        }

                        sdCameraLabel.setIcon(new ImageIcon(sixDLabel));
                        sixDPrev = sixD;

                        camera1 = r.createScreenCapture(new Rectangle(new Point(cam1X, cam1Y), new Dimension(cam1W, cam1H)));
                        camera1Lbl = r.createScreenCapture(new Rectangle(new Point(cam1X, cam1Y), new Dimension(cam1W, cam1H)));

                        g = camera1Lbl.getGraphics();
                        g.setColor(Color.RED);
                        g.drawRect(cam1X4, cam1Y4, cam1W4, cam1H4);
                        g.setColor(Color.YELLOW);
                        g.drawRect(cam1X4_5, cam1Y4_5, cam1W4_5, cam1H4_5);
                        g.setColor(Color.GREEN);
                        g.drawRect(cam1X5, cam1Y5, cam1W5, cam1H5);
                        g.setColor(Color.GRAY);
                        g.drawRect(cam1X5_5, cam1Y5_5, cam1W5_5, cam1H5_5);
                        g.setColor(Color.RED);
                        g.drawRect(cam1X6, cam1Y6, cam1W6, cam1H6);

                        if (camera1Prev != null) {
                            int cam14 = 0;
                            if (saveHd4Image) {
                                saveHd4Image = false;
                                hd4Image = camera1Prev.getSubimage(cam1X4, cam1Y4, cam1W4, cam1H4);
                                jButton11.setIcon(new ImageIcon(hd4Image));
                                jButton11.setText("");
                            }
                            if (hd4Image != null) {
                                BufferedImage temp = camera1.getSubimage(cam1X4, cam1Y4, cam1W4, cam1H4);
                                cam14 = countDifferences(temp, hd4Image, sensitivity);
                            } else {
                                for (int i = cam1X4; i < cam1W4 + cam1X4; i++) {
                                    for (int j = cam1Y4; j < cam1H4 + cam1Y4; j++) {
                                        if (isDifferent(camera1.getRGB(i, j), camera1Prev.getRGB(i, j), sensitivity)) {
                                            cam14++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.RED);
                            g.drawString(cam14 + "", cam1X4, cam1Y4);

                            int cam14_5 = 0;
                            if (saveHd4_5Image) {
                                saveHd4_5Image = false;
                                hd4_5Image = camera1Prev.getSubimage(cam1X4_5, cam1Y4_5, cam1W4_5, cam1H4_5);
                                jButton12.setIcon(new ImageIcon(hd4_5Image));
                                jButton12.setText("");
                            }
                            if (hd4_5Image != null) {
                                BufferedImage temp = camera1.getSubimage(cam1X4_5, cam1Y4_5, cam1W4_5, cam1H4_5);
                                cam14_5 = countDifferences(temp, hd4_5Image, sensitivity);
                            } else {
                                for (int i = cam1X4_5; i < cam1W4_5 + cam1X4_5; i++) {
                                    for (int j = cam1Y4_5; j < cam1H4_5 + cam1Y4_5; j++) {
                                        if (isDifferent(camera1.getRGB(i, j), camera1Prev.getRGB(i, j), sensitivity)) {
                                            cam14_5++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.YELLOW);
                            g.drawString(cam14_5 + "", cam1X4_5, cam1Y4_5);

                            int cam15 = 0;
                            if (saveHd5Image) {
                                saveHd5Image = false;
                                hd5Image = camera1Prev.getSubimage(cam1X5, cam1Y5, cam1W5, cam1H5);
                                jButton13.setIcon(new ImageIcon(hd5Image));
                                jButton13.setText("");
                            }
                            if (hd5Image != null) {
                                BufferedImage temp = camera1.getSubimage(cam1X5, cam1Y5, cam1W5, cam1H5);
                                cam15 = countDifferences(temp, hd5Image, sensitivity);
                            } else {
                                for (int i = cam1X5; i < cam1W5 + cam1X5; i++) {
                                    for (int j = cam1Y5; j < cam1H5 + cam1Y5; j++) {
                                        if (isDifferent(camera1.getRGB(i, j), camera1Prev.getRGB(i, j), sensitivity)) {
                                            cam15++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.GREEN);
                            g.drawString(cam15 + "", cam1X5, cam1Y5);

                            int cam15_5 = 0;
                            if (saveHd5_5Image) {
                                saveHd5_5Image = false;
                                hd5_5Image = camera1Prev.getSubimage(cam1X5_5, cam1Y5_5, cam1W5_5, cam1H5_5);
                                jButton14.setIcon(new ImageIcon(hd5_5Image));
                                jButton14.setText("");
                            }
                            if (hd5_5Image != null) {
                                BufferedImage temp = camera1.getSubimage(cam1X5_5, cam1Y5_5, cam1W5_5, cam1H5_5);
                                cam15_5 = countDifferences(temp, hd5_5Image, sensitivity);
                            } else {
                                for (int i = cam1X5_5; i < cam1W5_5 + cam1X5_5; i++) {
                                    for (int j = cam1Y5_5; j < cam1H5_5 + cam1Y5_5; j++) {
                                        if (isDifferent(camera1.getRGB(i, j), camera1Prev.getRGB(i, j), sensitivity)) {
                                            cam15_5++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.GRAY);
                            g.drawString(cam15_5 + "", cam1X5_5, cam1Y5_5);

                            int cam16 = 0;
                            if (saveHd6Image) {
                                saveHd6Image = false;
                                hd6Image = camera1Prev.getSubimage(cam1X6, cam1Y6, cam1W6, cam1H6);
                                jButton15.setIcon(new ImageIcon(hd6Image));
                                jButton15.setText("");
                            }
                            if (hd6Image != null) {
                                BufferedImage temp = camera1.getSubimage(cam1X6, cam1Y6, cam1W6, cam1H6);
                                cam16 = countDifferences(temp, hd6Image, sensitivity);
                            } else {
                                for (int i = cam1X6; i < cam1W6 + cam1X6; i++) {
                                    for (int j = cam1Y6; j < cam1H6 + cam1Y6; j++) {
                                        if (isDifferent(camera1.getRGB(i, j), camera1Prev.getRGB(i, j), sensitivity)) {
                                            cam16++;
                                        }
                                    }
                                }
                            }
                            g.setColor(Color.RED);
                            g.drawString(cam16 + "", cam1X6, cam1Y6);

                            analyzeMainCameraDifferencesAndMakeRecommendation(cam14, cam14_5, cam15, cam15_5, cam16);
                        }

                        camera1Label.setIcon(new ImageIcon(camera1Lbl));
                        camera1Prev = camera1;

//                    makeDecision();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            });
            t.start();
        } else {
            cancelAutoPilot();
        }
    }//GEN-LAST:event_autoPilotButtonActionPerformed
    private int countDifferences(BufferedImage temp, BufferedImage prev, int sensitivity) {
        int diffCount = 0;
        try {
            for (int i = 0; i < temp.getWidth(); i++) {
                for (int j = 0; j < temp.getHeight(); j++) {
                    if (isDifferent(temp.getRGB(i, j), prev.getRGB(i, j), sensitivity)) {
                        diffCount++;
                    }
                }
            }
        } catch (Exception e) {
            //do nothing.
        }
        return diffCount;
    }

//    private void makeDecision() {
////        System.out.println(newSlideTime + 2000);
////        System.out.println((newSlideTime + 2000) > System.currentTimeMillis());
////        System.out.println(lastTransitionTime + 3000);
////        System.out.println((lastTransitionTime + 3000) > System.currentTimeMillis());
////        System.out.println(System.currentTimeMillis());
////        if (newSlideTime + serviceSectionState.getFullScreenSlideTime() > System.currentTimeMillis()) {
////            transition("Slides With Overlay");
////        } else {
//
////            try {
////                boolean sdSame = true;
////                SdCamEnum sdDecision = sdCamRecommendation.get(0);
////                for (int i = 1; i < sdCamRecommendation.size(); i++) {
////                    SdCamEnum mce = sdCamRecommendation.get(i);
////                    if (mce == null || !mce.equals(sdDecision)) {
////                        sdSame = false;
////                        break;
////                    }
////                }
////
////                if (sdSame) {
////                    if (lastTransitionTime + serviceSectionState.getTransitionTime() < System.currentTimeMillis()) {
////                        if (newSlideTime + serviceSectionState.getComboSlideScreenTime() > System.currentTimeMillis()) {
////                            transition(sdDecision.getSlideSceneName());
////                        } else {
////                            transition(sdDecision.getSceneName());
////                        }
////                    }
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//        try {
//            boolean hdSame = true;
//            MainCamEnum decision = mainCamRecommendation.get(0);
//            for (int i = 1; i < mainCamRecommendation.size(); i++) {
//                MainCamEnum mce = mainCamRecommendation.get(i);
//                if (!mce.equals(decision)) {
//                    hdSame = false;
//                    break;
//                }
//            }
//
//            if (hdSame) {
//                if (lastTransitionTime + 3000 < System.currentTimeMillis()) {
//                    if (newSlideTime + 30000 > System.currentTimeMillis()) {
//                        transition(decision.getSlideSceneName());
//                    } else {
//                        transition(decision.getSceneName());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        }
//    }
    List<ValuePair> sdCamList = new ArrayList<ValuePair>();
    List<SdCamEnum> sdCamRecommendation = new ArrayList<SdCamEnum>();

    private void analyzeSdCameraDifferencesAndMakeRecommendation(int sdL, int sd4, int sd5, int sd6, int sdR) {
        sdCamList.clear();
        sdCamList.add(new ValuePair("L", sdL));
        sdCamList.add(new ValuePair("4", sd4));
        sdCamList.add(new ValuePair("5", sd5));
        sdCamList.add(new ValuePair("6", sd6));
        sdCamList.add(new ValuePair("R", sdR));

        sdCamList.sort(new Comparator<ValuePair>() {
            @Override
            public int compare(ValuePair t, ValuePair t1) {
                return t.value - t1.value;
            }
        });

        int i = 0;
        for (; i < sdCamList.size(); i++) {
            int diff = sdCamList.get(i).value;
            if (diff > 0) {
                break;
            }
        }

        List<ValuePair> tempCamList = new ArrayList<ValuePair>();
        for (; i < sdCamList.size(); i++) {
            tempCamList.add(sdCamList.get(i));
        }

        String sections = "";
        for (ValuePair vp : tempCamList) {
            sections += vp.name;
        }

        if (sections.startsWith("R") || sections.startsWith("L")) {
            sdCamRecommendation.add(0, null);
            if (SdCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(lastMainCamRecommendation);
            }
        } else if (sections.length() > 3) {
            sdCamRecommendation.add(0, SdCamEnum.WIDE);
            lastSdCamRecommendation = SdCamEnum.WIDE;
            if (SdCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(SdCamEnum.WIDE);
            }
        } else if (sections.startsWith("4")) {
            sdCamRecommendation.add(0, SdCamEnum.FOUR);
            lastSdCamRecommendation = SdCamEnum.FOUR;

            if (SdCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(SdCamEnum.FOUR);
            }
        } else if (sections.startsWith("5")) {
            sdCamRecommendation.add(0, SdCamEnum.FIVE);
            lastSdCamRecommendation = SdCamEnum.FIVE;

            if (SdCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(SdCamEnum.FIVE);
            }
        } else if (sections.startsWith("6")) {
            sdCamRecommendation.add(0, SdCamEnum.SIX);
            lastSdCamRecommendation = SdCamEnum.SIX;

            if (SdCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(SdCamEnum.SIX);
            }
        } else {
            sdCamRecommendation.add(0, null);
//            if (SdCamEnum.contains(currentScene)) {
//                Notifier.getInstance().notify(lastMainCamRecommendation);
//            }
        }

        while (sdCamRecommendation.size() > 3) {
            sdCamRecommendation.remove(sdCamRecommendation.size() - 1);
        }
    }

    List<ValuePair> mainCamList = new ArrayList<ValuePair>();
    List<MainCamEnum> mainCamRecommendation = new ArrayList<MainCamEnum>();

    private void analyzeMainCameraDifferencesAndMakeRecommendation(int mainCamera1Difference, int mainCamera2Difference, int mainCamera3Difference, int mainCamera4Difference, int mainCamera5Difference) {
        mainCamList.clear();
        mainCamList.add(new ValuePair("1", mainCamera1Difference));
        mainCamList.add(new ValuePair("2", mainCamera2Difference));
        mainCamList.add(new ValuePair("3", mainCamera3Difference));
        mainCamList.add(new ValuePair("4", mainCamera4Difference));
        mainCamList.add(new ValuePair("5", mainCamera5Difference));

        mainCamList.sort(new Comparator<ValuePair>() {
            @Override
            public int compare(ValuePair t, ValuePair t1) {
                return t.value - t1.value;
            }
        });

//        System.out.println(mainCamList.get(0) + "      " + mainCamList.get(mainCamList.size() - 1));
        int i = 0;
        for (; i < mainCamList.size(); i++) {
            int diff = mainCamList.get(i).value;
            if (diff > 0) {
                break;
            }
        }

        List<ValuePair> tempCamList = new ArrayList<ValuePair>();
        for (; i < mainCamList.size(); i++) {
//            System.out.print(mainCamList.get(i).name + " ");
            if (mainCamList.get(i).value != 0) {
                tempCamList.add(mainCamList.get(i));
            }
        }

        tempCamList.sort(new Comparator<ValuePair>() {
            @Override
            public int compare(ValuePair t, ValuePair t1) {
                return t.name.compareTo(t1.name);
            }
        });

        if (tempCamList.size() > 3) {
            mainCamRecommendation.add(MainCamEnum.WIDE);
            lastMainCamRecommendation = MainCamEnum.WIDE;
            if (MainCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(MainCamEnum.WIDE);
            }
        } else if (tempCamList.size() == 0) {
            mainCamRecommendation.add(null);
        } else if (tempCamList.get(0).name.equals("1")) {
            mainCamRecommendation.add(MainCamEnum.FOUR);
            lastMainCamRecommendation = MainCamEnum.FOUR;
            if (MainCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(MainCamEnum.FOUR);
            }
        } else if (tempCamList.get(0).name.equals("2") || tempCamList.get(0).name.equals("12")) {
            mainCamRecommendation.add(MainCamEnum.FOUR_POINT_FIVE);
            lastMainCamRecommendation = MainCamEnum.FOUR_POINT_FIVE;
            if (MainCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(MainCamEnum.FOUR_POINT_FIVE);
            }
        } else if (tempCamList.get(0).name.equals("3") || tempCamList.get(0).name.equals("23") || tempCamList.get(0).name.equals("34")) {
            mainCamRecommendation.add(MainCamEnum.FIVE);
            lastMainCamRecommendation = MainCamEnum.FIVE;
            if (MainCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(MainCamEnum.FIVE);
            }
        } else if (tempCamList.get(0).name.equals("4") || tempCamList.get(0).name.equals("45")) {
            mainCamRecommendation.add(MainCamEnum.FIVE_POINT_FIVE);
            lastMainCamRecommendation = MainCamEnum.FIVE_POINT_FIVE;
            if (MainCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(MainCamEnum.FIVE_POINT_FIVE);
            }
        } else if (tempCamList.get(0).name.equals("5")) {
            mainCamRecommendation.add(MainCamEnum.SIX);
            lastMainCamRecommendation = MainCamEnum.SIX;
            if (MainCamEnum.contains(currentScene)) {
                Notifier.getInstance().notify(MainCamEnum.SIX);
            }
        }

        while (mainCamRecommendation.size() > 3) {
            mainCamRecommendation.remove(mainCamRecommendation.size() - 1);
        }

//        System.out.println();
    }

    int requiredColorDifference = 20;

    private boolean isDifferent(int rgb, int rgb0) {
        return isDifferent(rgb, rgb0, requiredColorDifference);
    }

    private boolean isDifferent(int rgb, int rgb0, int requiredColorDifference) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb >> 0) & 0xFF;

        int r0 = (rgb0 >> 16) & 0xFF;
        int g0 = (rgb0 >> 8) & 0xFF;
        int b0 = (rgb0 >> 0) & 0xFF;

        boolean redDiff = Math.abs(r0 - r) > requiredColorDifference;
        boolean greenDiff = Math.abs(g0 - g) > requiredColorDifference;
        boolean blueDiff = Math.abs(b0 - b) > requiredColorDifference;

        if (redDiff) {
            if (greenDiff || blueDiff) {
                return true;
            }
        }

        return greenDiff && blueDiff;
    }

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        if (!cancelAutoPilot) {
            cancelAutoPilot();
        }
        if (!cancelAutoSlideSwitch) {
            cancelAutoSlideSwitch();
        }
    }//GEN-LAST:event_formWindowLostFocus

    private void welcomeButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_welcomeButton1ActionPerformed
        transition("For CREF Only (No Audio Broadcast)");
    }//GEN-LAST:event_welcomeButton1ActionPerformed

    ServiceSectionEnum serviceSectionState = ServiceSectionEnum.WORSHIP_SET;
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        serviceSectionState = ServiceSectionEnum.WORSHIP_SET;
        fadeDelayField.setText(serviceSectionState.getFadeDelay() + "");
        moveDelaySpinner.setText(serviceSectionState.getMoveDelay() + "");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        serviceSectionState = ServiceSectionEnum.ANNOUNCEMENTS;
        fadeDelayField.setText(serviceSectionState.getFadeDelay() + "");
        moveDelaySpinner.setText(serviceSectionState.getMoveDelay() + "");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        serviceSectionState = ServiceSectionEnum.SERMON;
        fadeDelayField.setText(serviceSectionState.getFadeDelay() + "");
        moveDelaySpinner.setText(serviceSectionState.getMoveDelay() + "");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 10; i++) {
                    oneButton.setForeground(new Color(51, 102, 0));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    oneButton.setForeground(new Color(153, 255, 0));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                oneButton.setForeground(new Color(0, 0, 0));
            }
        });
        t.start();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void helperDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_helperDialogWindowClosing
        requestFocus();
    }//GEN-LAST:event_helperDialogWindowClosing

    private void welcomeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_welcomeButton2ActionPerformed
        transition("Slides (No Audio Broadcast)");
    }//GEN-LAST:event_welcomeButton2ActionPerformed
    int slidesX = 70;
    int slidesY = 10;
    int slidesW = 40;
    int slidesH = 80;

    BufferedImage slideImage;
    BufferedImage slideLabelImage;
    BufferedImage prevSlideImage;
    boolean cancelSlideWatch = true;

    long newSlideTime = System.currentTimeMillis();
    private CamEnum lastMainCamRecommendation = MainCamEnum.WIDE;
    private CamEnum lastSdCamRecommendation = SdCamEnum.WIDE;
    private CamEnum lastCamRecommendation = MainCamEnum.WIDE;

    private void genHdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genHdButtonActionPerformed
        transition(MainCamEnum.WIDE);
    }//GEN-LAST:event_genHdButtonActionPerformed

    private void genHdButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genHdButton6ActionPerformed
        transition(MainCamEnum.SIX);
    }//GEN-LAST:event_genHdButton6ActionPerformed

    private void genHdButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genHdButton55ActionPerformed
        transition(MainCamEnum.FIVE_POINT_FIVE);
    }//GEN-LAST:event_genHdButton55ActionPerformed

    private void genHdButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genHdButton5ActionPerformed
        transition(MainCamEnum.FIVE);
    }//GEN-LAST:event_genHdButton5ActionPerformed

    private void genHdButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genHdButton45ActionPerformed
        transition(MainCamEnum.FOUR_POINT_FIVE);
    }//GEN-LAST:event_genHdButton45ActionPerformed

    private void genHdButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genHdButton4ActionPerformed
        transition(MainCamEnum.FOUR);
    }//GEN-LAST:event_genHdButton4ActionPerformed

    private void overlaySwitchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overlaySwitchButtonActionPerformed
        if (!SlideState.SLIDE_COMBO.equals(slideState)) {
            setSlideState(SlideState.SLIDE_COMBO);
        }
    }//GEN-LAST:event_overlaySwitchButtonActionPerformed

    private void genSdButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSdButton5ActionPerformed
        transition(SdCamEnum.FIVE);
    }//GEN-LAST:event_genSdButton5ActionPerformed

    private void genSdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSdButtonActionPerformed
        transition(SdCamEnum.WIDE);
    }//GEN-LAST:event_genSdButtonActionPerformed

    private void genSdButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSdButton4ActionPerformed
        transition(SdCamEnum.FOUR);
    }//GEN-LAST:event_genSdButton4ActionPerformed

    private void genSdButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSdButton6ActionPerformed
        transition(SdCamEnum.SIX);
    }//GEN-LAST:event_genSdButton6ActionPerformed

    boolean cancelAutoSlideSwitch = true;

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        cancelAutoSlideSwitch = !cancelAutoSlideSwitch;
        if (cancelAutoSlideSwitch) {
            jButton4.setForeground(Color.RED);
        } else {
            jButton4.setForeground(Color.GREEN);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void cancelAutoSlideSwitch() {
        if (!cancelAutoSlideSwitch) {
            cancelAutoSlideSwitch = true;
            jButton4.setForeground(Color.RED);
        }
    }

    private void overlaySwitchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overlaySwitchButton1ActionPerformed
        if (!SlideState.NO_SLIDE.equals(slideState)) {
            setSlideState(SlideState.NO_SLIDE);
        }
    }//GEN-LAST:event_overlaySwitchButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (sdLImage != null) {
            sdLImage = null;
            jButton6.setText("o");
            jButton6.setIcon(null);
        } else {
            saveSdLImage = true;
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (sd4Image != null) {
            sd4Image = null;
            jButton7.setText("o");
            jButton7.setIcon(null);
        } else {
            saveSd4Image = true;
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (sd5Image != null) {
            sd5Image = null;
            jButton8.setText("o");
            jButton8.setIcon(null);
        } else {
            saveSd5Image = true;
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (sd6Image != null) {
            sd6Image = null;
            jButton9.setText("o");
            jButton9.setIcon(null);
        } else {
            saveSd6Image = true;
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        if (sdRImage != null) {
            sdRImage = null;
            jButton10.setText("o");
            jButton10.setIcon(null);
        } else {
            saveSdRImage = true;
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        if (hd4Image != null) {
            hd4Image = null;
            jButton11.setText("o");
            jButton11.setIcon(null);
        } else {
            saveHd4Image = true;
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        if (hd4_5Image != null) {
            hd4_5Image = null;
            jButton12.setText("o");
            jButton12.setIcon(null);
        } else {
            saveHd4_5Image = true;
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        if (hd5Image != null) {
            hd5Image = null;
            jButton13.setText("o");
            jButton13.setIcon(null);
        } else {
            saveHd5Image = true;
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        if (hd5_5Image != null) {
            hd5_5Image = null;
            jButton14.setText("o");
            jButton14.setIcon(null);
        } else {
            saveHd5_5Image = true;
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        if (hd6Image != null) {
            hd6Image = null;
            jButton15.setText("o");
            jButton15.setIcon(null);
        } else {
            saveHd6Image = true;
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        if (blankSlideImage != null) {
            blankSlideImage = null;
            jButton16.setText("blank");
            jButton16.setIcon(null);
        } else {
            saveBlankSlideImage = true;
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void genHdButtonTightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genHdButtonTightActionPerformed
        transition(MainCamEnum.TIGHT);
    }//GEN-LAST:event_genHdButtonTightActionPerformed

    private void sdCam2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sdCam2ButtonActionPerformed
        transition(SdCam2Enum.TIGHT);
    }//GEN-LAST:event_sdCam2ButtonActionPerformed

    private void cancelAutoPilot() {
        cancelAutoPilot = true;
        autoPilotButton.setForeground(Color.RED);
        autoPilotButton.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ObsAutomation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ObsAutomation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ObsAutomation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ObsAutomation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ObsAutomation().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton autoPilotButton;
    private javax.swing.JLabel camera1Label;
    private javax.swing.JButton countDownCancelButton;
    private javax.swing.JLabel countDownLabel;
    private javax.swing.JLabel currentOverlayLabel;
    private javax.swing.JLabel currentSceneLabel;
    private javax.swing.JTextField fadeDelayField;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton fullSlidesButton;
    private javax.swing.JButton genHdButton;
    private javax.swing.JButton genHdButton4;
    private javax.swing.JButton genHdButton45;
    private javax.swing.JButton genHdButton5;
    private javax.swing.JButton genHdButton55;
    private javax.swing.JButton genHdButton6;
    private javax.swing.JButton genHdButtonTight;
    private javax.swing.JButton genSdButton;
    private javax.swing.JButton genSdButton4;
    private javax.swing.JButton genSdButton5;
    private javax.swing.JButton genSdButton6;
    private javax.swing.JButton goodByeButton;
    private javax.swing.JDialog helperDialog;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField moveDelaySpinner;
    private javax.swing.JButton oneButton;
    private javax.swing.JButton overlaySwitchButton;
    private javax.swing.JButton overlaySwitchButton1;
    private javax.swing.JButton prepHelperButton;
    private javax.swing.JButton sdCam2Button;
    private javax.swing.JLabel sdCameraLabel;
    private javax.swing.JTextField sensitivityField;
    private javax.swing.JLabel slidesLabel;
    private javax.swing.JPanel slidesPanel;
    private javax.swing.JButton startCountdownButton;
    private javax.swing.JButton startStreamButton;
    private javax.swing.JButton twoButton;
    private javax.swing.JButton welcomeButton;
    private javax.swing.JButton welcomeButton1;
    private javax.swing.JButton welcomeButton2;
    private javax.swing.JTextField worshipCountdownTimer;
    private javax.swing.JDialog worshipSetCountDownDialog;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the slideState
     */
    public SlideState getSlideState() {
        return slideState;
    }

    /**
     * @param slideState the slideState to set
     */
    public void setSlideState(SlideState slideState) {
//        System.out.println("     setting slide state: " + slideState.toString());
        this.slideState = slideState;
        currentOverlayLabel.setIcon(new ImageIcon(slideState.getOverlayImage()));
        currentOverlayLabel.setToolTipText(slideState.toString());
    }
}
