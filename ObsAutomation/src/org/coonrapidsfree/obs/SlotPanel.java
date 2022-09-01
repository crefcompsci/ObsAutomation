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

public abstract class SlotPanel extends javax.swing.JPanel {

    private final String baseSceneName;
    private static SlotPanel currentActiveSlotPanel = null;

    /**
     * Creates new form FirstSlotPanel
     */
    public SlotPanel(String baseSceneName, Rectangle slotThumbRect) {
        this.baseSceneName = baseSceneName;
        this.slotThumbRect = slotThumbRect;
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

    private Rectangle programRect = new Rectangle(ObsAutomationUtility.PROGRAM_X, ObsAutomationUtility.PROGRAM_Y, ObsAutomationUtility.PROGRAM_W, ObsAutomationUtility.PROGRAM_H);
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

    private void codeFromObsAutomationUtility() {
        slotOverlayMap.put("FullA5", new ArrayList<String>());
        slotOverlayMap.get("FullA5").add("FullA 5 7,7");
        sceneToRectMap.put("FullA 5 7,7", new Rectangle(211, 121, 215, 114));
        sceneToMidpointMap.put("FullA 5 7,7", new Point(318, 178));
        slotOverlayMap.put("FullA4", new ArrayList<String>());
        slotOverlayMap.get("FullA4").add("FullA 4 5,5");
        sceneToRectMap.put("FullA 4 5,5", new Rectangle(186, 109, 240, 126));
        sceneToMidpointMap.put("FullA 4 5,5", new Point(306, 172));
        slotOverlayMap.put("FullA3", new ArrayList<String>());
        slotOverlayMap.get("FullA3").add("FullA 3 5,5");
        sceneToRectMap.put("FullA 3 5,5", new Rectangle(142, 86, 284, 149));
        sceneToMidpointMap.put("FullA 3 5,5", new Point(284, 160));
        slotOverlayMap.put("FullA2", new ArrayList<String>());
        slotOverlayMap.get("FullA2").add("FullA 2 3,3");
        sceneToRectMap.put("FullA 2 3,3", new Rectangle(85, 51, 341, 184));
        sceneToMidpointMap.put("FullA 2 3,3", new Point(255, 143));
        slotOverlayMap.put("FullA1", new ArrayList<String>());
        slotOverlayMap.get("FullA1").add("FullA 1 1,1");
        sceneToRectMap.put("FullA 1 1,1", new Rectangle(5, 5, 421, 230));
        sceneToMidpointMap.put("FullA 1 1,1", new Point(215, 120));
        slotOverlayMap.put("SlidesA1", new ArrayList<String>());
        slotOverlayMap.get("SlidesA1").add("SlidesA 1 1,3");
        sceneToRectMap.put("SlidesA 1 1,3", new Rectangle(108, 5, 318, 230));
        sceneToMidpointMap.put("SlidesA 1 1,3", new Point(267, 120));
        slotOverlayMap.put("SlidesA2", new ArrayList<String>());
        slotOverlayMap.get("SlidesA2").add("SlidesA 2 2,3");
        sceneToRectMap.put("SlidesA 2 2,3", new Rectangle(154, 40, 272, 195));
        sceneToMidpointMap.put("SlidesA 2 2,3", new Point(290, 137));
        slotOverlayMap.put("SlidesA3", new ArrayList<String>());
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 3,5");
        sceneToRectMap.put("SlidesA 3 3,5", new Rectangle(186, 63, 240, 172));
        sceneToMidpointMap.put("SlidesA 3 3,5", new Point(306, 149));
        slotOverlayMap.put("SlidesA4", new ArrayList<String>());
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 3,7");
        sceneToRectMap.put("SlidesA 4 3,7", new Rectangle(234, 98, 192, 137));
        sceneToMidpointMap.put("SlidesA 4 3,7", new Point(330, 166));
        slotOverlayMap.put("SlidesA5", new ArrayList<String>());
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 4,7");
        sceneToRectMap.put("SlidesA 5 4,7", new Rectangle(268, 121, 158, 114));
        sceneToMidpointMap.put("SlidesA 5 4,7", new Point(347, 178));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 3,7");
        sceneToRectMap.put("SlidesA 5 3,7", new Rectangle(176, 121, 160, 114));
        sceneToMidpointMap.put("SlidesA 5 3,7", new Point(256, 178));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 2,7");
        sceneToRectMap.put("SlidesA 5 2,7", new Rectangle(96, 121, 149, 114));
        sceneToMidpointMap.put("SlidesA 5 2,7", new Point(170, 178));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 1,7");
        sceneToRectMap.put("SlidesA 5 1,7", new Rectangle(5, 121, 160, 114));
        sceneToMidpointMap.put("SlidesA 5 1,7", new Point(85, 178));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 4,6");
        sceneToRectMap.put("SlidesA 5 4,6", new Rectangle(268, 98, 158, 115));
        sceneToMidpointMap.put("SlidesA 5 4,6", new Point(347, 155));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 3,6");
        sceneToRectMap.put("SlidesA 5 3,6", new Rectangle(176, 98, 160, 115));
        sceneToMidpointMap.put("SlidesA 5 3,6", new Point(256, 155));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 2,6");
        sceneToRectMap.put("SlidesA 5 2,6", new Rectangle(96, 98, 149, 115));
        sceneToMidpointMap.put("SlidesA 5 2,6", new Point(170, 155));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 1,6");
        sceneToRectMap.put("SlidesA 5 1,6", new Rectangle(5, 98, 160, 115));
        sceneToMidpointMap.put("SlidesA 5 1,6", new Point(85, 155));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 4,5");
        sceneToRectMap.put("SlidesA 5 4,5", new Rectangle(268, 86, 158, 104));
        sceneToMidpointMap.put("SlidesA 5 4,5", new Point(347, 138));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 3,5");
        sceneToRectMap.put("SlidesA 5 3,5", new Rectangle(176, 86, 160, 104));
        sceneToMidpointMap.put("SlidesA 5 3,5", new Point(256, 138));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 2,5");
        sceneToRectMap.put("SlidesA 5 2,5", new Rectangle(96, 86, 149, 104));
        sceneToMidpointMap.put("SlidesA 5 2,5", new Point(170, 138));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 1,5");
        sceneToRectMap.put("SlidesA 5 1,5", new Rectangle(5, 86, 160, 104));
        sceneToMidpointMap.put("SlidesA 5 1,5", new Point(85, 138));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 4,4");
        sceneToRectMap.put("SlidesA 5 4,4", new Rectangle(268, 63, 158, 115));
        sceneToMidpointMap.put("SlidesA 5 4,4", new Point(347, 120));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 3,4");
        sceneToRectMap.put("SlidesA 5 3,4", new Rectangle(176, 63, 160, 115));
        sceneToMidpointMap.put("SlidesA 5 3,4", new Point(256, 120));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 2,4");
        sceneToRectMap.put("SlidesA 5 2,4", new Rectangle(96, 63, 149, 115));
        sceneToMidpointMap.put("SlidesA 5 2,4", new Point(170, 120));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 1,4");
        sceneToRectMap.put("SlidesA 5 1,4", new Rectangle(5, 63, 160, 115));
        sceneToMidpointMap.put("SlidesA 5 1,4", new Point(85, 120));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 4,3");
        sceneToRectMap.put("SlidesA 5 4,3", new Rectangle(268, 40, 158, 115));
        sceneToMidpointMap.put("SlidesA 5 4,3", new Point(347, 97));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 3,3");
        sceneToRectMap.put("SlidesA 5 3,3", new Rectangle(176, 40, 160, 115));
        sceneToMidpointMap.put("SlidesA 5 3,3", new Point(256, 97));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 2,3");
        sceneToRectMap.put("SlidesA 5 2,3", new Rectangle(96, 40, 149, 115));
        sceneToMidpointMap.put("SlidesA 5 2,3", new Point(170, 97));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 1,3");
        sceneToRectMap.put("SlidesA 5 1,3", new Rectangle(5, 40, 160, 115));
        sceneToMidpointMap.put("SlidesA 5 1,3", new Point(85, 97));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 4,2");
        sceneToRectMap.put("SlidesA 5 4,2", new Rectangle(268, 28, 158, 104));
        sceneToMidpointMap.put("SlidesA 5 4,2", new Point(347, 80));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 3,2");
        sceneToRectMap.put("SlidesA 5 3,2", new Rectangle(176, 28, 160, 104));
        sceneToMidpointMap.put("SlidesA 5 3,2", new Point(256, 80));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 2,2");
        sceneToRectMap.put("SlidesA 5 2,2", new Rectangle(96, 28, 149, 104));
        sceneToMidpointMap.put("SlidesA 5 2,2", new Point(170, 80));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 1,2");
        sceneToRectMap.put("SlidesA 5 1,2", new Rectangle(5, 28, 160, 104));
        sceneToMidpointMap.put("SlidesA 5 1,2", new Point(85, 80));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 4,1");
        sceneToRectMap.put("SlidesA 5 4,1", new Rectangle(268, 5, 158, 116));
        sceneToMidpointMap.put("SlidesA 5 4,1", new Point(347, 63));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 3,1");
        sceneToRectMap.put("SlidesA 5 3,1", new Rectangle(176, 5, 160, 116));
        sceneToMidpointMap.put("SlidesA 5 3,1", new Point(256, 63));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 2,1");
        sceneToRectMap.put("SlidesA 5 2,1", new Rectangle(96, 5, 149, 116));
        sceneToMidpointMap.put("SlidesA 5 2,1", new Point(170, 63));
        slotOverlayMap.get("SlidesA5").add("SlidesA 5 1,1");
        sceneToRectMap.put("SlidesA 5 1,1", new Rectangle(5, 5, 160, 116));
        sceneToMidpointMap.put("SlidesA 5 1,1", new Point(85, 63));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 2,7");
        sceneToRectMap.put("SlidesA 4 2,7", new Rectangle(119, 98, 195, 137));
        sceneToMidpointMap.put("SlidesA 4 2,7", new Point(216, 166));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 1,7");
        sceneToRectMap.put("SlidesA 4 1,7", new Rectangle(5, 98, 194, 137));
        sceneToMidpointMap.put("SlidesA 4 1,7", new Point(102, 166));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 3,6");
        sceneToRectMap.put("SlidesA 4 3,6", new Rectangle(234, 74, 192, 151));
        sceneToMidpointMap.put("SlidesA 4 3,6", new Point(330, 149));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 2,6");
        sceneToRectMap.put("SlidesA 4 2,6", new Rectangle(119, 74, 195, 152));
        sceneToMidpointMap.put("SlidesA 4 2,6", new Point(216, 150));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 1,6");
        sceneToRectMap.put("SlidesA 4 1,6", new Rectangle(5, 74, 194, 152));
        sceneToMidpointMap.put("SlidesA 4 1,6", new Point(102, 150));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 3,5");
        sceneToRectMap.put("SlidesA 4 3,5", new Rectangle(234, 63, 192, 139));
        sceneToMidpointMap.put("SlidesA 4 3,5", new Point(330, 132));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 2,5");
        sceneToRectMap.put("SlidesA 4 2,5", new Rectangle(119, 63, 195, 139));
        sceneToMidpointMap.put("SlidesA 4 2,5", new Point(216, 132));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 1,5");
        sceneToRectMap.put("SlidesA 4 1,5", new Rectangle(5, 63, 194, 139));
        sceneToMidpointMap.put("SlidesA 4 1,5", new Point(102, 132));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 3,4");
        sceneToRectMap.put("SlidesA 4 3,4", new Rectangle(234, 51, 192, 139));
        sceneToMidpointMap.put("SlidesA 4 3,4", new Point(330, 120));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 2,4");
        sceneToRectMap.put("SlidesA 4 2,4", new Rectangle(119, 51, 195, 139));
        sceneToMidpointMap.put("SlidesA 4 2,4", new Point(216, 120));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 1,4");
        sceneToRectMap.put("SlidesA 4 1,4", new Rectangle(5, 51, 194, 139));
        sceneToMidpointMap.put("SlidesA 4 1,4", new Point(102, 120));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 3,3");
        sceneToRectMap.put("SlidesA 4 3,3", new Rectangle(234, 28, 192, 150));
        sceneToMidpointMap.put("SlidesA 4 3,3", new Point(330, 103));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 2,3");
        sceneToRectMap.put("SlidesA 4 2,3", new Rectangle(119, 28, 195, 150));
        sceneToMidpointMap.put("SlidesA 4 2,3", new Point(216, 103));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 1,3");
        sceneToRectMap.put("SlidesA 4 1,3", new Rectangle(5, 28, 194, 150));
        sceneToMidpointMap.put("SlidesA 4 1,3", new Point(102, 103));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 3,2");
        sceneToRectMap.put("SlidesA 4 3,2", new Rectangle(234, 17, 192, 138));
        sceneToMidpointMap.put("SlidesA 4 3,2", new Point(330, 86));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 2,2");
        sceneToRectMap.put("SlidesA 4 2,2", new Rectangle(119, 17, 195, 138));
        sceneToMidpointMap.put("SlidesA 4 2,2", new Point(216, 86));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 1,2");
        sceneToRectMap.put("SlidesA 4 1,2", new Rectangle(5, 17, 194, 138));
        sceneToMidpointMap.put("SlidesA 4 1,2", new Point(102, 86));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 3,1");
        sceneToRectMap.put("SlidesA 4 3,1", new Rectangle(234, 5, 192, 139));
        sceneToMidpointMap.put("SlidesA 4 3,1", new Point(330, 74));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 2,1");
        sceneToRectMap.put("SlidesA 4 2,1", new Rectangle(119, 5, 195, 139));
        sceneToMidpointMap.put("SlidesA 4 2,1", new Point(216, 74));
        slotOverlayMap.get("SlidesA4").add("SlidesA 4 1,1");
        sceneToRectMap.put("SlidesA 4 1,1", new Rectangle(5, 5, 194, 139));
        sceneToMidpointMap.put("SlidesA 4 1,1", new Point(102, 74));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 2,5");
        sceneToRectMap.put("SlidesA 3 2,5", new Rectangle(96, 63, 240, 172));
        sceneToMidpointMap.put("SlidesA 3 2,5", new Point(216, 149));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 1,5");
        sceneToRectMap.put("SlidesA 3 1,5", new Rectangle(5, 63, 240, 172));
        sceneToMidpointMap.put("SlidesA 3 1,5", new Point(125, 149));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 3,4");
        sceneToRectMap.put("SlidesA 3 3,4", new Rectangle(186, 51, 240, 162));
        sceneToMidpointMap.put("SlidesA 3 3,4", new Point(306, 132));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 2,4");
        sceneToRectMap.put("SlidesA 3 2,4", new Rectangle(96, 51, 240, 162));
        sceneToMidpointMap.put("SlidesA 3 2,4", new Point(216, 132));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 1,4");
        sceneToRectMap.put("SlidesA 3 1,4", new Rectangle(5, 51, 240, 162));
        sceneToMidpointMap.put("SlidesA 3 1,4", new Point(125, 132));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 3,3");
        sceneToRectMap.put("SlidesA 3 3,3", new Rectangle(188, 40, 238, 162));
        sceneToMidpointMap.put("SlidesA 3 3,3", new Point(307, 121));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 2,3");
        sceneToRectMap.put("SlidesA 3 2,3", new Rectangle(96, 40, 240, 162));
        sceneToMidpointMap.put("SlidesA 3 2,3", new Point(216, 121));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 1,3");
        sceneToRectMap.put("SlidesA 3 1,3", new Rectangle(5, 40, 240, 162));
        sceneToMidpointMap.put("SlidesA 3 1,3", new Point(125, 121));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 3,2");
        sceneToRectMap.put("SlidesA 3 3,2", new Rectangle(188, 17, 238, 173));
        sceneToMidpointMap.put("SlidesA 3 3,2", new Point(307, 103));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 2,2");
        sceneToRectMap.put("SlidesA 3 2,2", new Rectangle(96, 17, 240, 173));
        sceneToMidpointMap.put("SlidesA 3 2,2", new Point(216, 103));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 1,2");
        sceneToRectMap.put("SlidesA 3 1,2", new Rectangle(5, 17, 240, 173));
        sceneToMidpointMap.put("SlidesA 3 1,2", new Point(125, 103));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 3,1");
        sceneToRectMap.put("SlidesA 3 3,1", new Rectangle(188, 5, 238, 173));
        sceneToMidpointMap.put("SlidesA 3 3,1", new Point(307, 91));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 2,1");
        sceneToRectMap.put("SlidesA 3 2,1", new Rectangle(96, 5, 240, 173));
        sceneToMidpointMap.put("SlidesA 3 2,1", new Point(216, 91));
        slotOverlayMap.get("SlidesA3").add("SlidesA 3 1,1");
        sceneToRectMap.put("SlidesA 3 1,1", new Rectangle(5, 5, 240, 173));
        sceneToMidpointMap.put("SlidesA 3 1,1", new Point(125, 91));
        slotOverlayMap.get("SlidesA2").add("SlidesA 2 1,3");
        sceneToRectMap.put("SlidesA 2 1,3", new Rectangle(5, 40, 274, 195));
        sceneToMidpointMap.put("SlidesA 2 1,3", new Point(142, 137));
        slotOverlayMap.get("SlidesA2").add("SlidesA 2 2,2");
        sceneToRectMap.put("SlidesA 2 2,2", new Rectangle(154, 17, 272, 196));
        sceneToMidpointMap.put("SlidesA 2 2,2", new Point(290, 115));
        slotOverlayMap.get("SlidesA2").add("SlidesA 2 1,2");
        sceneToRectMap.put("SlidesA 2 1,2", new Rectangle(5, 17, 274, 196));
        sceneToMidpointMap.put("SlidesA 2 1,2", new Point(142, 115));
        slotOverlayMap.get("SlidesA2").add("SlidesA 2 2,1");
        sceneToRectMap.put("SlidesA 2 2,1", new Rectangle(154, 5, 272, 197));
        sceneToMidpointMap.put("SlidesA 2 2,1", new Point(290, 103));
        slotOverlayMap.get("SlidesA2").add("SlidesA 2 1,1");
        sceneToRectMap.put("SlidesA 2 1,1", new Rectangle(5, 5, 274, 197));
        sceneToMidpointMap.put("SlidesA 2 1,1", new Point(142, 103));
        slotOverlayMap.get("SlidesA1").add("SlidesA 1 1,2");
        sceneToRectMap.put("SlidesA 1 1,2", new Rectangle(5, 5, 320, 230));
        sceneToMidpointMap.put("SlidesA 1 1,2", new Point(165, 120));
        slotOverlayMap.get("SlidesA1").add("SlidesA 1 1,1");
        sceneToRectMap.put("SlidesA 1 1,1", new Rectangle(5, 5, 320, 230));
        sceneToMidpointMap.put("SlidesA 1 1,1", new Point(165, 120));
        slotOverlayMap.get("FullA2").add("FullA 2 2,3");
        sceneToRectMap.put("FullA 2 2,3", new Rectangle(39, 51, 343, 184));
        sceneToMidpointMap.put("FullA 2 2,3", new Point(210, 143));
        slotOverlayMap.get("FullA2").add("FullA 2 1,3");
        sceneToRectMap.put("FullA 2 1,3", new Rectangle(5, 51, 331, 184));
        sceneToMidpointMap.put("FullA 2 1,3", new Point(170, 143));
        slotOverlayMap.get("FullA2").add("FullA 2 3,2");
        sceneToRectMap.put("FullA 2 3,2", new Rectangle(85, 28, 341, 185));
        sceneToMidpointMap.put("FullA 2 3,2", new Point(255, 120));
        slotOverlayMap.get("FullA2").add("FullA 2 2,2");
        sceneToRectMap.put("FullA 2 2,2", new Rectangle(39, 28, 343, 185));
        sceneToMidpointMap.put("FullA 2 2,2", new Point(210, 120));
        slotOverlayMap.get("FullA2").add("FullA 2 1,2");
        sceneToRectMap.put("FullA 2 1,2", new Rectangle(5, 28, 331, 185));
        sceneToMidpointMap.put("FullA 2 1,2", new Point(170, 120));
        slotOverlayMap.get("FullA2").add("FullA 2 3,1");
        sceneToRectMap.put("FullA 2 3,1", new Rectangle(85, 5, 341, 185));
        sceneToMidpointMap.put("FullA 2 3,1", new Point(255, 97));
        slotOverlayMap.get("FullA2").add("FullA 2 2,1");
        sceneToRectMap.put("FullA 2 2,1", new Rectangle(39, 5, 343, 185));
        sceneToMidpointMap.put("FullA 2 2,1", new Point(210, 97));
        slotOverlayMap.get("FullA2").add("FullA 2 1,1");
        sceneToRectMap.put("FullA 2 1,1", new Rectangle(5, 5, 331, 185));
        sceneToMidpointMap.put("FullA 2 1,1", new Point(170, 97));
        slotOverlayMap.get("FullA3").add("FullA 3 4,5");
        sceneToRectMap.put("FullA 3 4,5", new Rectangle(108, 86, 285, 149));
        sceneToMidpointMap.put("FullA 3 4,5", new Point(250, 160));
        slotOverlayMap.get("FullA3").add("FullA 3 3,5");
        sceneToRectMap.put("FullA 3 3,5", new Rectangle(74, 86, 285, 149));
        sceneToMidpointMap.put("FullA 3 3,5", new Point(216, 160));
        slotOverlayMap.get("FullA3").add("FullA 3 2,5");
        sceneToRectMap.put("FullA 3 2,5", new Rectangle(39, 86, 286, 149));
        sceneToMidpointMap.put("FullA 3 2,5", new Point(182, 160));
        slotOverlayMap.get("FullA3").add("FullA 3 1,5");
        sceneToRectMap.put("FullA 3 1,5", new Rectangle(5, 86, 274, 149));
        sceneToMidpointMap.put("FullA 3 1,5", new Point(142, 160));
        slotOverlayMap.get("FullA3").add("FullA 3 5,4");
        sceneToRectMap.put("FullA 3 5,4", new Rectangle(142, 63, 284, 150));
        sceneToMidpointMap.put("FullA 3 5,4", new Point(284, 138));
        slotOverlayMap.get("FullA3").add("FullA 3 4,4");
        sceneToRectMap.put("FullA 3 4,4", new Rectangle(108, 63, 285, 150));
        sceneToMidpointMap.put("FullA 3 4,4", new Point(250, 138));
        slotOverlayMap.get("FullA3").add("FullA 3 3,4");
        sceneToRectMap.put("FullA 3 3,4", new Rectangle(74, 63, 285, 150));
        sceneToMidpointMap.put("FullA 3 3,4", new Point(216, 138));
        slotOverlayMap.get("FullA3").add("FullA 3 2,4");
        sceneToRectMap.put("FullA 3 2,4", new Rectangle(39, 63, 286, 150));
        sceneToMidpointMap.put("FullA 3 2,4", new Point(182, 138));
        slotOverlayMap.get("FullA3").add("FullA 3 1,4");
        sceneToRectMap.put("FullA 3 1,4", new Rectangle(5, 63, 274, 150));
        sceneToMidpointMap.put("FullA 3 1,4", new Point(142, 138));
        slotOverlayMap.get("FullA3").add("FullA 3 5,3");
        sceneToRectMap.put("FullA 3 5,3", new Rectangle(142, 40, 284, 162));
        sceneToMidpointMap.put("FullA 3 5,3", new Point(284, 121));
        slotOverlayMap.get("FullA3").add("FullA 3 4,3");
        sceneToRectMap.put("FullA 3 4,3", new Rectangle(108, 40, 285, 162));
        sceneToMidpointMap.put("FullA 3 4,3", new Point(250, 121));
        slotOverlayMap.get("FullA3").add("FullA 3 3,3");
        sceneToRectMap.put("FullA 3 3,3", new Rectangle(74, 40, 285, 162));
        sceneToMidpointMap.put("FullA 3 3,3", new Point(216, 121));
        slotOverlayMap.get("FullA3").add("FullA 3 2,3");
        sceneToRectMap.put("FullA 3 2,3", new Rectangle(39, 40, 286, 162));
        sceneToMidpointMap.put("FullA 3 2,3", new Point(182, 121));
        slotOverlayMap.get("FullA3").add("FullA 3 1,3");
        sceneToRectMap.put("FullA 3 1,3", new Rectangle(5, 40, 274, 162));
        sceneToMidpointMap.put("FullA 3 1,3", new Point(142, 121));
        slotOverlayMap.get("FullA3").add("FullA 3 5,2");
        sceneToRectMap.put("FullA 3 5,2", new Rectangle(142, 17, 284, 161));
        sceneToMidpointMap.put("FullA 3 5,2", new Point(284, 97));
        slotOverlayMap.get("FullA3").add("FullA 3 4,2");
        sceneToRectMap.put("FullA 3 4,2", new Rectangle(108, 17, 285, 161));
        sceneToMidpointMap.put("FullA 3 4,2", new Point(250, 97));
        slotOverlayMap.get("FullA3").add("FullA 3 3,2");
        sceneToRectMap.put("FullA 3 3,2", new Rectangle(74, 17, 285, 161));
        sceneToMidpointMap.put("FullA 3 3,2", new Point(216, 97));
        slotOverlayMap.get("FullA3").add("FullA 3 2,2");
        sceneToRectMap.put("FullA 3 2,2", new Rectangle(39, 17, 286, 161));
        sceneToMidpointMap.put("FullA 3 2,2", new Point(182, 97));
        slotOverlayMap.get("FullA3").add("FullA 3 1,2");
        sceneToRectMap.put("FullA 3 1,2", new Rectangle(5, 17, 274, 161));
        sceneToMidpointMap.put("FullA 3 1,2", new Point(142, 97));
        slotOverlayMap.get("FullA3").add("FullA 3 5,1");
        sceneToRectMap.put("FullA 3 5,1", new Rectangle(142, 5, 284, 150));
        sceneToMidpointMap.put("FullA 3 5,1", new Point(284, 80));
        slotOverlayMap.get("FullA3").add("FullA 3 4,1");
        sceneToRectMap.put("FullA 3 4,1", new Rectangle(108, 5, 285, 150));
        sceneToMidpointMap.put("FullA 3 4,1", new Point(250, 80));
        slotOverlayMap.get("FullA3").add("FullA 3 3,1");
        sceneToRectMap.put("FullA 3 3,1", new Rectangle(74, 5, 285, 150));
        sceneToMidpointMap.put("FullA 3 3,1", new Point(216, 80));
        slotOverlayMap.get("FullA3").add("FullA 3 2,1");
        sceneToRectMap.put("FullA 3 2,1", new Rectangle(39, 5, 286, 150));
        sceneToMidpointMap.put("FullA 3 2,1", new Point(182, 80));
        slotOverlayMap.get("FullA3").add("FullA 3 1,1");
        sceneToRectMap.put("FullA 3 1,1", new Rectangle(5, 5, 274, 150));
        sceneToMidpointMap.put("FullA 3 1,1", new Point(142, 80));
        slotOverlayMap.get("FullA4").add("FullA 4 4,5");
        sceneToRectMap.put("FullA 4 4,5", new Rectangle(142, 109, 240, 126));
        sceneToMidpointMap.put("FullA 4 4,5", new Point(262, 172));
        slotOverlayMap.get("FullA4").add("FullA 4 3,5");
        sceneToRectMap.put("FullA 4 3,5", new Rectangle(96, 109, 240, 126));
        sceneToMidpointMap.put("FullA 4 3,5", new Point(216, 172));
        slotOverlayMap.get("FullA4").add("FullA 4 2,5");
        sceneToRectMap.put("FullA 4 2,5", new Rectangle(51, 109, 240, 126));
        sceneToMidpointMap.put("FullA 4 2,5", new Point(171, 172));
        slotOverlayMap.get("FullA4").add("FullA 4 1,5");
        sceneToRectMap.put("FullA 4 1,5", new Rectangle(5, 109, 240, 126));
        sceneToMidpointMap.put("FullA 4 1,5", new Point(125, 172));
        slotOverlayMap.get("FullA4").add("FullA 4 5,4");
        sceneToRectMap.put("FullA 4 5,4", new Rectangle(186, 74, 240, 139));
        sceneToMidpointMap.put("FullA 4 5,4", new Point(306, 143));
        slotOverlayMap.get("FullA4").add("FullA 4 4,4");
        sceneToRectMap.put("FullA 4 4,4", new Rectangle(142, 74, 240, 139));
        sceneToMidpointMap.put("FullA 4 4,4", new Point(262, 143));
        slotOverlayMap.get("FullA4").add("FullA 4 3,4");
        sceneToRectMap.put("FullA 4 3,4", new Rectangle(96, 74, 240, 139));
        sceneToMidpointMap.put("FullA 4 3,4", new Point(216, 143));
        slotOverlayMap.get("FullA4").add("FullA 4 2,4");
        sceneToRectMap.put("FullA 4 2,4", new Rectangle(51, 74, 240, 139));
        sceneToMidpointMap.put("FullA 4 2,4", new Point(171, 143));
        slotOverlayMap.get("FullA4").add("FullA 4 1,4");
        sceneToRectMap.put("FullA 4 1,4", new Rectangle(5, 74, 240, 139));
        sceneToMidpointMap.put("FullA 4 1,4", new Point(125, 143));
        slotOverlayMap.get("FullA4").add("FullA 4 5,3");
        sceneToRectMap.put("FullA 4 5,3", new Rectangle(188, 51, 238, 139));
        sceneToMidpointMap.put("FullA 4 5,3", new Point(307, 120));
        slotOverlayMap.get("FullA4").add("FullA 4 4,3");
        sceneToRectMap.put("FullA 4 4,3", new Rectangle(142, 51, 240, 139));
        sceneToMidpointMap.put("FullA 4 4,3", new Point(262, 120));
        slotOverlayMap.get("FullA4").add("FullA 4 3,3");
        sceneToRectMap.put("FullA 4 3,3", new Rectangle(96, 51, 240, 139));
        sceneToMidpointMap.put("FullA 4 3,3", new Point(216, 120));
        slotOverlayMap.get("FullA4").add("FullA 4 2,3");
        sceneToRectMap.put("FullA 4 2,3", new Rectangle(51, 51, 240, 139));
        sceneToMidpointMap.put("FullA 4 2,3", new Point(171, 120));
        slotOverlayMap.get("FullA4").add("FullA 4 1,3");
        sceneToRectMap.put("FullA 4 1,3", new Rectangle(5, 51, 240, 139));
        sceneToMidpointMap.put("FullA 4 1,3", new Point(125, 120));
        slotOverlayMap.get("FullA4").add("FullA 4 5,2");
        sceneToRectMap.put("FullA 4 5,2", new Rectangle(188, 28, 238, 127));
        sceneToMidpointMap.put("FullA 4 5,2", new Point(307, 91));
        slotOverlayMap.get("FullA4").add("FullA 4 4,2");
        sceneToRectMap.put("FullA 4 4,2", new Rectangle(142, 28, 240, 127));
        sceneToMidpointMap.put("FullA 4 4,2", new Point(262, 91));
        slotOverlayMap.get("FullA4").add("FullA 4 3,2");
        sceneToRectMap.put("FullA 4 3,2", new Rectangle(96, 28, 240, 127));
        sceneToMidpointMap.put("FullA 4 3,2", new Point(216, 91));
        slotOverlayMap.get("FullA4").add("FullA 4 2,2");
        sceneToRectMap.put("FullA 4 2,2", new Rectangle(51, 28, 240, 127));
        sceneToMidpointMap.put("FullA 4 2,2", new Point(171, 91));
        slotOverlayMap.get("FullA4").add("FullA 4 1,2");
        sceneToRectMap.put("FullA 4 1,2", new Rectangle(5, 28, 240, 127));
        sceneToMidpointMap.put("FullA 4 1,2", new Point(125, 91));
        slotOverlayMap.get("FullA4").add("FullA 4 5,1");
        sceneToRectMap.put("FullA 4 5,1", new Rectangle(188, 5, 238, 127));
        sceneToMidpointMap.put("FullA 4 5,1", new Point(307, 68));
        slotOverlayMap.get("FullA4").add("FullA 4 4,1");
        sceneToRectMap.put("FullA 4 4,1", new Rectangle(142, 5, 240, 127));
        sceneToMidpointMap.put("FullA 4 4,1", new Point(262, 68));
        slotOverlayMap.get("FullA4").add("FullA 4 3,1");
        sceneToRectMap.put("FullA 4 3,1", new Rectangle(96, 5, 240, 127));
        sceneToMidpointMap.put("FullA 4 3,1", new Point(216, 68));
        slotOverlayMap.get("FullA4").add("FullA 4 2,1");
        sceneToRectMap.put("FullA 4 2,1", new Rectangle(51, 5, 240, 127));
        sceneToMidpointMap.put("FullA 4 2,1", new Point(171, 68));
        slotOverlayMap.get("FullA4").add("FullA 4 1,1");
        sceneToRectMap.put("FullA 4 1,1", new Rectangle(5, 5, 240, 127));
        sceneToMidpointMap.put("FullA 4 1,1", new Point(125, 68));
        slotOverlayMap.get("FullA5").add("FullA 5 6,7");
        sceneToRectMap.put("FullA 5 6,7", new Rectangle(176, 121, 217, 114));
        sceneToMidpointMap.put("FullA 5 6,7", new Point(284, 178));
        slotOverlayMap.get("FullA5").add("FullA 5 5,7");
        sceneToRectMap.put("FullA 5 5,7", new Rectangle(142, 121, 217, 114));
        sceneToMidpointMap.put("FullA 5 5,7", new Point(250, 178));
        slotOverlayMap.get("FullA5").add("FullA 5 4,7");
        sceneToRectMap.put("FullA 5 4,7", new Rectangle(108, 121, 217, 114));
        sceneToMidpointMap.put("FullA 5 4,7", new Point(216, 178));
        slotOverlayMap.get("FullA5").add("FullA 5 3,7");
        sceneToRectMap.put("FullA 5 3,7", new Rectangle(74, 121, 205, 114));
        sceneToMidpointMap.put("FullA 5 3,7", new Point(176, 178));
        slotOverlayMap.get("FullA5").add("FullA 5 2,7");
        sceneToRectMap.put("FullA 5 2,7", new Rectangle(39, 121, 206, 114));
        sceneToMidpointMap.put("FullA 5 2,7", new Point(142, 178));
        slotOverlayMap.get("FullA5").add("FullA 5 1,7");
        sceneToRectMap.put("FullA 5 1,7", new Rectangle(5, 121, 206, 114));
        sceneToMidpointMap.put("FullA 5 1,7", new Point(108, 178));
        slotOverlayMap.get("FullA5").add("FullA 5 7,6");
        sceneToRectMap.put("FullA 5 7,6", new Rectangle(211, 98, 215, 115));
        sceneToMidpointMap.put("FullA 5 7,6", new Point(318, 155));
        slotOverlayMap.get("FullA5").add("FullA 5 6,6");
        sceneToRectMap.put("FullA 5 6,6", new Rectangle(176, 98, 217, 115));
        sceneToMidpointMap.put("FullA 5 6,6", new Point(284, 155));
        slotOverlayMap.get("FullA5").add("FullA 5 5,6");
        sceneToRectMap.put("FullA 5 5,6", new Rectangle(142, 98, 217, 115));
        sceneToMidpointMap.put("FullA 5 5,6", new Point(250, 155));
        slotOverlayMap.get("FullA5").add("FullA 5 4,6");
        sceneToRectMap.put("FullA 5 4,6", new Rectangle(108, 98, 217, 115));
        sceneToMidpointMap.put("FullA 5 4,6", new Point(216, 155));
        slotOverlayMap.get("FullA5").add("FullA 5 3,6");
        sceneToRectMap.put("FullA 5 3,6", new Rectangle(74, 98, 205, 115));
        sceneToMidpointMap.put("FullA 5 3,6", new Point(176, 155));
        slotOverlayMap.get("FullA5").add("FullA 5 2,6");
        sceneToRectMap.put("FullA 5 2,6", new Rectangle(39, 98, 206, 115));
        sceneToMidpointMap.put("FullA 5 2,6", new Point(142, 155));
        slotOverlayMap.get("FullA5").add("FullA 5 1,6");
        sceneToRectMap.put("FullA 5 1,6", new Rectangle(5, 98, 206, 115));
        sceneToMidpointMap.put("FullA 5 1,6", new Point(108, 155));
        slotOverlayMap.get("FullA5").add("FullA 5 7,5");
        sceneToRectMap.put("FullA 5 7,5", new Rectangle(211, 86, 215, 116));
        sceneToMidpointMap.put("FullA 5 7,5", new Point(318, 144));
        slotOverlayMap.get("FullA5").add("FullA 5 6,5");
        sceneToRectMap.put("FullA 5 6,5", new Rectangle(176, 86, 217, 116));
        sceneToMidpointMap.put("FullA 5 6,5", new Point(284, 144));
        slotOverlayMap.get("FullA5").add("FullA 5 5,5");
        sceneToRectMap.put("FullA 5 5,5", new Rectangle(142, 86, 217, 116));
        sceneToMidpointMap.put("FullA 5 5,5", new Point(250, 144));
        slotOverlayMap.get("FullA5").add("FullA 5 4,5");
        sceneToRectMap.put("FullA 5 4,5", new Rectangle(108, 86, 217, 116));
        sceneToMidpointMap.put("FullA 5 4,5", new Point(216, 144));
        slotOverlayMap.get("FullA5").add("FullA 5 3,5");
        sceneToRectMap.put("FullA 5 3,5", new Rectangle(74, 86, 205, 116));
        sceneToMidpointMap.put("FullA 5 3,5", new Point(176, 144));
        slotOverlayMap.get("FullA5").add("FullA 5 2,5");
        sceneToRectMap.put("FullA 5 2,5", new Rectangle(39, 86, 206, 116));
        sceneToMidpointMap.put("FullA 5 2,5", new Point(142, 144));
        slotOverlayMap.get("FullA5").add("FullA 5 1,5");
        sceneToRectMap.put("FullA 5 1,5", new Rectangle(5, 86, 206, 116));
        sceneToMidpointMap.put("FullA 5 1,5", new Point(108, 144));
        slotOverlayMap.get("FullA5").add("FullA 5 7,4");
        sceneToRectMap.put("FullA 5 7,4", new Rectangle(211, 63, 215, 115));
        sceneToMidpointMap.put("FullA 5 7,4", new Point(318, 120));
        slotOverlayMap.get("FullA5").add("FullA 5 6,4");
        sceneToRectMap.put("FullA 5 6,4", new Rectangle(176, 63, 217, 115));
        sceneToMidpointMap.put("FullA 5 6,4", new Point(284, 120));
        slotOverlayMap.get("FullA5").add("FullA 5 5,4");
        sceneToRectMap.put("FullA 5 5,4", new Rectangle(142, 63, 217, 115));
        sceneToMidpointMap.put("FullA 5 5,4", new Point(250, 120));
        slotOverlayMap.get("FullA5").add("FullA 5 4,4");
        sceneToRectMap.put("FullA 5 4,4", new Rectangle(108, 63, 217, 115));
        sceneToMidpointMap.put("FullA 5 4,4", new Point(216, 120));
        slotOverlayMap.get("FullA5").add("FullA 5 3,4");
        sceneToRectMap.put("FullA 5 3,4", new Rectangle(74, 63, 205, 115));
        sceneToMidpointMap.put("FullA 5 3,4", new Point(176, 120));
        slotOverlayMap.get("FullA5").add("FullA 5 2,4");
        sceneToRectMap.put("FullA 5 2,4", new Rectangle(39, 63, 206, 115));
        sceneToMidpointMap.put("FullA 5 2,4", new Point(142, 120));
        slotOverlayMap.get("FullA5").add("FullA 5 1,4");
        sceneToRectMap.put("FullA 5 1,4", new Rectangle(5, 63, 206, 115));
        sceneToMidpointMap.put("FullA 5 1,4", new Point(108, 120));
        slotOverlayMap.get("FullA5").add("FullA 5 7,3");
        sceneToRectMap.put("FullA 5 7,3", new Rectangle(211, 40, 215, 115));
        sceneToMidpointMap.put("FullA 5 7,3", new Point(318, 97));
        slotOverlayMap.get("FullA5").add("FullA 5 6,3");
        sceneToRectMap.put("FullA 5 6,3", new Rectangle(176, 40, 217, 115));
        sceneToMidpointMap.put("FullA 5 6,3", new Point(284, 97));
        slotOverlayMap.get("FullA5").add("FullA 5 5,3");
        sceneToRectMap.put("FullA 5 5,3", new Rectangle(142, 40, 263, 185));
        sceneToMidpointMap.put("FullA 5 5,3", new Point(273, 132));
        slotOverlayMap.get("FullA5").add("FullA 5 4,3");
        sceneToRectMap.put("FullA 5 4,3", new Rectangle(108, 40, 217, 115));
        sceneToMidpointMap.put("FullA 5 4,3", new Point(216, 97));
        slotOverlayMap.get("FullA5").add("FullA 5 3,3");
        sceneToRectMap.put("FullA 5 3,3", new Rectangle(74, 40, 205, 115));
        sceneToMidpointMap.put("FullA 5 3,3", new Point(176, 97));
        slotOverlayMap.get("FullA5").add("FullA 5 2,3");
        sceneToRectMap.put("FullA 5 2,3", new Rectangle(39, 40, 366, 185));
        sceneToMidpointMap.put("FullA 5 2,3", new Point(222, 132));
        slotOverlayMap.get("FullA5").add("FullA 5 1,3");
        sceneToRectMap.put("FullA 5 1,3", new Rectangle(5, 40, 206, 115));
        sceneToMidpointMap.put("FullA 5 1,3", new Point(108, 97));
        slotOverlayMap.get("FullA5").add("FullA 5 7,2");
        sceneToRectMap.put("FullA 5 7,2", new Rectangle(211, 17, 215, 115));
        sceneToMidpointMap.put("FullA 5 7,2", new Point(318, 74));
        slotOverlayMap.get("FullA5").add("FullA 5 6,2");
        sceneToRectMap.put("FullA 5 6,2", new Rectangle(176, 17, 217, 115));
        sceneToMidpointMap.put("FullA 5 6,2", new Point(284, 74));
        slotOverlayMap.get("FullA5").add("FullA 5 5,2");
        sceneToRectMap.put("FullA 5 5,2", new Rectangle(142, 17, 263, 208));
        sceneToMidpointMap.put("FullA 5 5,2", new Point(273, 121));
        slotOverlayMap.get("FullA5").add("FullA 5 4,2");
        sceneToRectMap.put("FullA 5 4,2", new Rectangle(108, 17, 217, 115));
        sceneToMidpointMap.put("FullA 5 4,2", new Point(216, 74));
        slotOverlayMap.get("FullA5").add("FullA 5 3,2");
        sceneToRectMap.put("FullA 5 3,2", new Rectangle(74, 17, 205, 115));
        sceneToMidpointMap.put("FullA 5 3,2", new Point(176, 74));
        slotOverlayMap.get("FullA5").add("FullA 5 2,2");
        sceneToRectMap.put("FullA 5 2,2", new Rectangle(39, 17, 366, 208));
        sceneToMidpointMap.put("FullA 5 2,2", new Point(222, 121));
        slotOverlayMap.get("FullA5").add("FullA 5 1,2");
        sceneToRectMap.put("FullA 5 1,2", new Rectangle(5, 17, 206, 115));
        sceneToMidpointMap.put("FullA 5 1,2", new Point(108, 74));
        slotOverlayMap.get("FullA5").add("FullA 5 7,1");
        sceneToRectMap.put("FullA 5 7,1", new Rectangle(211, 5, 215, 116));
        sceneToMidpointMap.put("FullA 5 7,1", new Point(318, 63));
        slotOverlayMap.get("FullA5").add("FullA 5 6,1");
        sceneToRectMap.put("FullA 5 6,1", new Rectangle(176, 5, 217, 116));
        sceneToMidpointMap.put("FullA 5 6,1", new Point(284, 63));
        slotOverlayMap.get("FullA5").add("FullA 5 5,1");
        sceneToRectMap.put("FullA 5 5,1", new Rectangle(142, 5, 263, 220));
        sceneToMidpointMap.put("FullA 5 5,1", new Point(273, 115));
        slotOverlayMap.get("FullA5").add("FullA 5 4,1");
        sceneToRectMap.put("FullA 5 4,1", new Rectangle(108, 5, 217, 116));
        sceneToMidpointMap.put("FullA 5 4,1", new Point(216, 63));
        slotOverlayMap.get("FullA5").add("FullA 5 3,1");
        sceneToRectMap.put("FullA 5 3,1", new Rectangle(74, 5, 205, 116));
        sceneToMidpointMap.put("FullA 5 3,1", new Point(176, 63));
        slotOverlayMap.get("FullA5").add("FullA 5 2,1");
        sceneToRectMap.put("FullA 5 2,1", new Rectangle(39, 5, 366, 220));
        sceneToMidpointMap.put("FullA 5 2,1", new Point(222, 115));
        slotOverlayMap.get("FullA5").add("FullA 5 1,1");
        sceneToRectMap.put("FullA 5 1,1", new Rectangle(5, 5, 206, 116));
        sceneToMidpointMap.put("FullA 5 1,1", new Point(108, 63));
    }

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

        firstSlotSlideOverlays.sort(ObsAutomation.overlaySorter);
        firstSlotCamOverlays.sort(ObsAutomation.overlaySorter);
        firstSlotOverlays = firstSlotCamOverlays;
        firstSlotCurrentOverlay = firstSlotOverlays.get(0);

        setSlideState(SlideState.NO_SLIDE);
        final JLabel firstSlotLabel = new JLabel() {
            @Override
            public void paint(Graphics grphcs) {
                super.paint(grphcs);
                Graphics2D g2d = (Graphics2D) grphcs;
                final String currentScene = ObsAutomation.getCurrentScene();
                if (currentActiveSlotPanel == SlotPanel.this && getSceneToRectMap().containsKey(currentScene)) {
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
                    currentActiveSlotPanel = SlotPanel.this;
                    changeToScene(baseSceneName, potentialScene);
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
                changeToScene(baseSceneName, potentialScene);
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
                        Logger.getLogger(ObsAutomation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Image firstSlotThumbCapture = ImageUtilities.getImage(ObsAutomation.robot.createScreenCapture(slotThumbRect), 2, 0);
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

    protected abstract void changeToScene(String baseSceneName, String scene);

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
