package org.coonrapidsfree.obs;

import org.coonrapidsfree.obs.shottype.ShotTypeEnum;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.coonrapidsfree.obs.two.ObsAutomationTwo;

public abstract class SlotPanel extends javax.swing.JPanel {

    /**
     * Creates new form FirstSlotPanel
     */
    public SlotPanel() {
        initComponents();
        postInitComponents();
    }

    SlideState slideState = SlideState.NO_SLIDE;

    /**
     * @param slideState the slideState to set
     */
    public void setSlideState(SlideState slideState) {
        this.slideState = slideState;
//        System.out.println("     setting slide state: " + slideState.toString());
        if (slideState.equals(SlideState.NO_SLIDE)) {
            try {
                int index = firstSlotOverlays.indexOf(firstSlotCurrentOverlay);

                firstSlotOverlays = firstSlotCamOverlays;
                if (index >= firstSlotCamOverlays.size()) {
                    index = firstSlotCamOverlays.size() - 1;
                }
                firstSlotCurrentOverlay = firstSlotCamOverlays.get(index);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            try {
                int index = firstSlotOverlays.indexOf(firstSlotCurrentOverlay);

                firstSlotOverlays = firstSlotSlideOverlays;
                if (index >= firstSlotSlideOverlays.size()) {
                    index = firstSlotSlideOverlays.size() - 1;
                }
                firstSlotCurrentOverlay = firstSlotSlideOverlays.get(index);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

//                Point p = sceneToMidpointMap.get(currentScene);
//
//                determinePotentialScene(p.x, p.y, firstSlotOverlayMap.get(firstSlotCurrentOverlay));
    }

    private Map<String, Rectangle> sceneToRectMap = new HashMap<String, Rectangle>();
    protected Map<String, Point> sceneToMidpointMap = new HashMap<String, Point>();

    private Rectangle programRect = new Rectangle(482, 32, 429, 239);
    protected Map<String, List<String>> slotOverlayMap = new HashMap<String, List<String>>();
    protected Rectangle slotThumbRect;
    private String potentialScene = "";
    private String initialScene = "";
    private static boolean showInitialScene = false;

    public static void setShowInitialScene(boolean showInitialScene) {
        SlotPanel.showInitialScene = showInitialScene;
    }

    private String firstSlotCurrentOverlay = "";
    List<String> firstSlotOverlays;
    List<String> firstSlotCamOverlays = new ArrayList<String>();
    List<String> firstSlotSlideOverlays = new ArrayList<String>();

    protected abstract void codeFromObsAutomationUtility();

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pickerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        pickerPanel.setLayout(new java.awt.GridLayout());
        add(pickerPanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pickerPanel;
    // End of variables declaration//GEN-END:variables
    private void setupFirstSlotControls() {
        codeFromObsAutomationUtility();
        for (String key : slotOverlayMap.keySet()) {
            if (key.contains("Slide")) {
                firstSlotSlideOverlays.add(key);
            } else {
                firstSlotCamOverlays.add(key);
            }
        }

        firstSlotSlideOverlays.sort(ObsAutomationTwo.overlaySorter);
        firstSlotCamOverlays.sort(ObsAutomationTwo.overlaySorter);
        firstSlotOverlays = firstSlotCamOverlays;
        firstSlotCurrentOverlay = firstSlotOverlays.get(0);

        setSlideState(SlideState.NO_SLIDE);
        final JLabel firstSlotLabel = new JLabel() {
            @Override
            public void paint(Graphics grphcs) {
                super.paint(grphcs);
                Graphics2D g2d = (Graphics2D) grphcs;
                final Object currentScene = ObsAutomationTwo.getCurrentScene();
                if (getSceneToRectMap().containsKey(currentScene)) {
                    Rectangle r = getSceneToRectMap().get(currentScene);
                    try {
                        g2d.setColor(Color.RED);
                        g2d.drawRect(r.x, r.y, r.width, r.height);

                    } catch (Exception e) {
                        System.out.println("Exeption in paint(current): " + e.getMessage());
                    }
                }
                if (showInitialScene && initialScene != null && !initialScene.isEmpty() && getSceneToRectMap().containsKey(initialScene)) {
                    try {
                        g2d.setStroke(new BasicStroke(4f));
                        g2d.setColor(Color.ORANGE);
                        Rectangle r = getSceneToRectMap().get(initialScene);
                        g2d.drawRect(r.x, r.y, r.width, r.height);
                    } catch (Exception e) {
                        System.out.println("Exception in paint(initial): " + e.getMessage());
                    }
                }
                try {
                    if (potentialScene != null && !potentialScene.isEmpty()) {
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.setColor(Color.YELLOW);
                        Rectangle r = getSceneToRectMap().get(potentialScene);
                        g2d.drawRect(r.x, r.y, r.width, r.height);
                    }
                } catch (Exception e) {
                    System.out.println("Exception in paint(potential): " + e.getMessage());
                }
            }
        };
        firstSlotLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                int x = me.getX();
                int y = me.getY();
                determinePotentialScene(x, y, slotOverlayMap.get(firstSlotCurrentOverlay));
            }

        });

        firstSlotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (potentialScene != null && !potentialScene.isEmpty()) {
                    changeToScene(potentialScene);
                }
            }
        });

        firstSlotLabel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                try {
                    int index = firstSlotOverlays.indexOf(firstSlotCurrentOverlay);
                    if (mwe.getWheelRotation() > 0) {
                        index++;
                        if (index >= firstSlotOverlays.size()) {
                            index = firstSlotOverlays.size() - 1;
                        }
                        firstSlotCurrentOverlay = firstSlotOverlays.get(index);
                    } else if (mwe.getWheelRotation() < 0) {
                        index--;
                        if (index < 0) {
                            index = 0;
                        }
                        firstSlotCurrentOverlay = firstSlotOverlays.get(index);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                int x = mwe.getX();
                int y = mwe.getY();
                determinePotentialScene(x, y, slotOverlayMap.get(firstSlotCurrentOverlay));
            }
        });
        JPanel firstSlotPanel = new JPanel(new BorderLayout());
        firstSlotPanel.add(firstSlotLabel, BorderLayout.CENTER);
        JPanel firstSlotZoomPanel = new JPanel(new GridLayout(0, 1));
        JButton firstSlotZoomInButton = new JButton("T");
        firstSlotZoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                try {
                    int index = firstSlotOverlays.indexOf(firstSlotCurrentOverlay);
                    index--;
                    if (index < 0) {
                        index = 0;
                    }
                    firstSlotCurrentOverlay = firstSlotOverlays.get(index);
//                    System.out.println("in " + firstSlotCurrentOverlay);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        firstSlotZoomPanel.add(firstSlotZoomInButton);

        JButton thirdSlotTransitionButton = new JButton("->");
        thirdSlotTransitionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                changeToScene(potentialScene);
            }
        });
        firstSlotZoomPanel.add(thirdSlotTransitionButton);

        JButton firstSlotZoomOutButton = new JButton("W");
        firstSlotZoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int index = firstSlotOverlays.indexOf(firstSlotCurrentOverlay);
                    index++;
                    if (index >= firstSlotOverlays.size()) {
                        index = firstSlotOverlays.size() - 1;
                    }
                    firstSlotCurrentOverlay = firstSlotOverlays.get(index);
//                    System.out.println("out" + firstSlotCurrentOverlay);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        firstSlotZoomPanel.add(firstSlotZoomOutButton);
        firstSlotPanel.add(firstSlotZoomPanel, BorderLayout.WEST);
        pickerPanel.add(firstSlotPanel);
        Thread firstSlotThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ObsAutomationTwo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Image firstSlotThumbCapture = ImageUtilities.getImage(ObsAutomationTwo.robot.createScreenCapture(slotThumbRect), 2, 0);
                    firstSlotLabel.setIcon(new ImageIcon(firstSlotThumbCapture));
                }
            }
        });
        firstSlotThread.start();
    }

    private void determinePotentialScene(int x, int y, List<String> sceneList) {
        double minDist = 99999;
        String minScene = "";
        for (String scene : sceneList) {
            Point midPoint = sceneToMidpointMap.get(scene);
            double tempDistance = midPoint.distance(x, y);
//                    System.out.println(tempDistance);
            if (tempDistance < minDist) {
                minDist = tempDistance;
                minScene = scene;
            }
        }
//                System.out.println(minScene);
        potentialScene = minScene;
    }

    protected abstract void changeToScene(String scene);

    private void postInitComponents() {
        setupFirstSlotControls();
    }

    private ShotTypeEnum shotType = ShotTypeEnum.TIGHT;

    public void recommendScene(Rectangle r) {
        if (r == null) {
            return;
        }

        Rectangle convertedRect = new Rectangle(r.x, r.y, (r.width - 1), (r.height - 1));
        convertedRect.add(r.x, r.y - 1);
        convertedRect = new Rectangle(convertedRect.x * 2, convertedRect.y * 2, convertedRect.width * 2, convertedRect.height * 2);

        convertedRect = getShotType().convert(convertedRect);

//        System.out.println("  r: " + convertedRect);
        List<String> tempOverlays = firstSlotSlideOverlays;

        if (slideState.equals(SlideState.NO_SLIDE)) {
            tempOverlays = firstSlotCamOverlays;
        }

        List<String> potentialScenes = new ArrayList<String>();
        for (String tempOverlay : tempOverlays) {
            List<String> sceneList = slotOverlayMap.get(tempOverlay);
            for (String scene : sceneList) {
                Rectangle sceneRect = getSceneToRectMap().get(scene);

//                System.out.println("  s: " + scene + "      " + sceneRect);
                if (sceneRect.contains(convertedRect)) {

//                    System.out.println("     recommending " + scene);
                    potentialScenes.add(scene);
                }
            }
        }

        if (!potentialScenes.isEmpty()) {
            potentialScene = potentialScenes.get(potentialScenes.size() / 2);
        }
    }

    /**
     * @return the sceneToRectMap
     */
    public Map<String, Rectangle> getSceneToRectMap() {
        return sceneToRectMap;
    }

    /**
     * @return the shotType
     */
    public ShotTypeEnum getShotType() {
        return shotType;
    }

    /**
     * @param shotType the shotType to set
     */
    public void setShotType(ShotTypeEnum shotType) {
        this.shotType = shotType;
    }

    /**
     * @param initialScene the initialScene to set
     */
    public void setInitialScene(String initialScene) {
        this.initialScene = initialScene;
    }
}