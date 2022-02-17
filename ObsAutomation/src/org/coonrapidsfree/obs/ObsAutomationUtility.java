package org.coonrapidsfree.obs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.twasi.obsremotejava.OBSRemoteController;
import net.twasi.obsremotejava.callbacks.Callback;
import net.twasi.obsremotejava.objects.Scene;
import net.twasi.obsremotejava.objects.Source;
import net.twasi.obsremotejava.requests.GetCurrentScene.GetCurrentSceneResponse;
import net.twasi.obsremotejava.requests.GetSceneList.GetSceneListResponse;
import net.twasi.obsremotejava.requests.SetCurrentScene.SetCurrentSceneResponse;
import net.twasi.obsremotejava.requests.SetMute.SetMuteResponse;
import net.twasi.obsremotejava.requests.SetSceneItemProperties.SetSceneItemPropertiesResponse;
import net.twasi.obsremotejava.requests.SetTransitionDuration.SetTransitionDurationResponse;
import org.coonrapidsfree.obs.ImageUtilities;

public class ObsAutomationUtility extends javax.swing.JFrame {

    private static Robot robot = null;

    static {
        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static Map<String, Scene> sceneMap = new HashMap<String, Scene>();
    private static String currentSceneName = null;
    private static List<String> sceneList = new ArrayList<String>();

    public static final int THUMBS_FIRST_LINE_Y = 276;
    public static final int THUMBS_SECOND_LINE_Y = 398;
    public static final int THUMBS_WIDTH = 212;
    public static final int THUMBS_HEIGHT = 117;

    public static final int THUMBS_FIRST_COL_X = 48;
    public static final int THUMBS_SECOND_COL_X = 265;
    public static final int THUMBS_THIRD_COL_X = 482;
    public static final int THUMBS_FORTH_COL_X = 699;

    private static Map<String, Rectangle> sceneToCornersMap;

    private static List<String> sceneNames = new ArrayList<String>();

    public static List<String> getSceneNames() {
        if (sceneNames.isEmpty()) {
            getSceneToCornersMap();
        }
        return sceneNames;
    }

    public static Map<String, Rectangle> getSceneToCornersMap() {
        if (allScenes == null) {
            initScenes();
        }
        if (sceneToCornersMap == null) {
            sceneToCornersMap = new HashMap<String, Rectangle>();

            for (int i = 0; i < allScenes.size() && i < 8; i++) {
                sceneNames.add(allScenes.get(i));
                if (i == 0) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_FIRST_COL_X, THUMBS_FIRST_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                } else if (i == 1) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_SECOND_COL_X, THUMBS_FIRST_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                } else if (i == 2) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_THIRD_COL_X, THUMBS_FIRST_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                } else if (i == 3) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_FORTH_COL_X, THUMBS_FIRST_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                } else if (i == 4) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_FIRST_COL_X, THUMBS_SECOND_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                } else if (i == 5) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_SECOND_COL_X, THUMBS_SECOND_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                } else if (i == 6) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_THIRD_COL_X, THUMBS_SECOND_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                } else if (i == 7) {
                    sceneToCornersMap.put(allScenes.get(i), new Rectangle(THUMBS_FORTH_COL_X, THUMBS_SECOND_LINE_Y, THUMBS_WIDTH, THUMBS_HEIGHT));
                }
            }
        }
        return sceneToCornersMap;

    }

    private OBSRemoteController controller;

    /**
     * Creates new form ObsAutomationOne
     */
    public ObsAutomationUtility(OBSRemoteController controller) {
        this.controller = controller;
        initScenes();
        initComponents();

        if (controller.isFailed()) { // Awaits response from OBS
            // Here you can handle a failed connection request
            System.out.println("CONNECTION TO OBS FAILED");
        }
        Callback<GetCurrentSceneResponse> getSceneCallback = new Callback<GetCurrentSceneResponse>() {
            @Override
            public void run(GetCurrentSceneResponse rt) {
                System.out.println(rt.getName());
            }
        };

        controller.getCurrentScene(getSceneCallback);

        getSceneToCornersMap();

        for (String sceneName : sceneNames) {
            final String tempSceneName = sceneName;
            JButton temp = new JButton(tempSceneName);
            temp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ObsAutomationUtility.this.controller.changeSceneWithTransition(tempSceneName, "Cut", callback);
                    currentSceneName = tempSceneName;

                    //save scene info
                    clickCount = 0;
                    BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize()));
                    JLabel l = new JLabel(new ImageIcon(ImageUtilities.getImage(screenCapture, increase, 0)));
                    l.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent me) {
                            messagePanel.setVisible(false);
                            messagePanel.removeAll();

//                            clickCount++;
//                            if (clickCount == 1) {
//                                thumbX = me.getX() / (int) increase;
//                                thumbY = me.getY() / (int) increase;
//                                messagePanel.add(new JLabel("press the lower right corner of the thumbnail of " + tempSceneName));}
//                             else if (clickCount == 2) {
//                                thumbW = me.getX() / (int) increase;
//                                thumbW -= thumbX;
//                                thumbH = me.getY() / (int) increase;
//                                thumbH -= thumbY;
//                                messagePanel.add(new JLabel("press the upper left corner of Program"));}
//                             else if (clickCount == 3) {
//                                programX = me.getX() / (int) increase;
//                                programY = me.getY() / (int) increase;
//                                messagePanel.add(new JLabel("press the lower right corner of Program"));
//                            } else if (clickCount == 4) {
//                                programW = me.getX() / (int) increase;
//                                programW -= programX;
//                                programH = me.getY() / (int) increase;
//                                programH -= programY;
                            Rectangle r = sceneToCornersMap.get(tempSceneName);
                            thumbX = r.x;
                            thumbY = r.y;
                            thumbW = r.width;
                            thumbH = r.height;

                            System.out.println("Thumb: (" + thumbX + ", " + thumbY + ", " + thumbW + ", " + thumbH + ")");
                            System.out.println("Program: (" + PROGRAM_X + ", " + PROGRAM_Y + ", " + PROGRAM_W + ", " + PROGRAM_H + ")");
                            JButton temp = new JButton("Parse");
                            temp.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent ae) {
                                    parse();
                                }

                            });
                            messagePanel.add(temp);
                            imagePanel.removeAll();
                            imagePanel.setVisible(false);

                            thumbCapture = robot.createScreenCapture(new Rectangle(thumbX, thumbY, thumbW, thumbH));
                            imagePanel.add(new JLabel(new ImageIcon(thumbCapture)), BorderLayout.NORTH);
                            programCapture = robot.createScreenCapture(new Rectangle(PROGRAM_X, PROGRAM_Y, PROGRAM_W, PROGRAM_H));
                            Graphics2D g2d = (Graphics2D) programCapture.getGraphics();
                            g2d.setColor(Color.BLACK);
                            g2d.fillRect(672 - PROGRAM_X, 238 - PROGRAM_Y, 49, 15);
                            imagePanel.add(new JLabel(new ImageIcon(programCapture)), BorderLayout.CENTER);

//                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    messagePanel.setVisible(true);
                                    imagePanel.setVisible(true);
                                    repaint();
                                }
                            });
                        }

                    });
                    imagePanel.removeAll();
                    imagePanel.setVisible(false);
                    imagePanel.add(l, BorderLayout.CENTER);
                    messagePanel.setVisible(false);
                    messagePanel.removeAll();
                    messagePanel.add(new JLabel("press the upper left corner of the thumbnail of " + tempSceneName));
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            messagePanel.setVisible(true);
                            imagePanel.setVisible(true);
                            repaint();
                        }
                    });
                }
            });
            jPanel1.add(temp);
        }
//        for (String pref2 : prefix2) {
//            for (String midpoint : mid) {
//                String scenePrefix = prefix + pref2 + midpoint;
//                for (int i = 1; i <= rows; i++) {
//                    for (int j = 1; j <= columns; j++) {
//                        final String sceneName = scenePrefix + i + "," + j + ".5";
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(ObsAutomationUtility.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                        System.out.println(sceneName);
//                        controller.setCurrentScene(sceneName, new Callback<SetCurrentSceneResponse>() {
//                            @Override
//                            public void run(SetCurrentSceneResponse rt) {
//                                System.out.println(rt.getStatus());
//                                if (rt.getStatus().equals("ok")) {
//                                    sceneList.add(sceneName);
//                                    JButton temp = new JButton(sceneName);
//                                    temp.addActionListener(new ActionListener() {
//                                        @Override
//                                        public void actionPerformed(ActionEvent ae) {
//                                            controller.changeSceneWithTransition(sceneName, "Cut", callback);
//                                        }
//                                    });
//                                    jPanel1.add(temp);
//                                    SwingUtilities.invokeLater(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            jPanel1.setVisible(true);
//                                        }
//                                    });
//                                } else if ((rt.getStatus().equals("error"))) {
//                                    System.out.println("scene doesn't exist.");
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        }

//        controller.getScenes(new Callback<GetSceneListResponse>() {
//            @Override
//            public void run(GetSceneListResponse rt) {
//                List<Scene> scenes = rt.getScenes();
//                for (Scene s : scenes) {
//                    sceneMap.put(s.getName(), s);
//                    System.out.println(s.getName());
//                    for (Source source : s.getSources()) {
//                        System.out.println("    " + source.getName() + "    \t" + source.getType() + "    \t" + source.getX() + "," + source.getY() + "    \t");
//                    }
//
//                    final String sceneName = s.getName();
//                    JButton temp = new JButton(sceneName);
//                    temp.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent ae) {
//                            String transition = "Cut";
//                            if (currentSceneName != null) {
//                                boolean similiar = true;
//                                Scene current = sceneMap.get(currentSceneName);
//                                Scene next = sceneMap.get(sceneName);
//
//                                if (currentSceneName.equals("Slides With Overlay")) {
//                                    similiar = false;
//                                    for (Source source : next.getSources()) {
//                                        if (source.getName().equals("Overlay Slide Combo Scene")) {
//                                            similiar = true;
//                                            break;
//                                        }
//                                    }
//                                } else if (sceneName.equals("Slides With Overlay")) {
//                                    similiar = false;
//                                    for (Source source : current.getSources()) {
//                                        if (source.getName().equals("Overlay Slide Combo Scene")) {
//                                            similiar = true;
//                                            break;
//                                        }
//                                    }
//                                } else {
//                                    List<String> sourceNames = new ArrayList<String>();
//                                    for (Source source : current.getSources()) {
//                                        sourceNames.add(source.getName());
//                                    }
//                                    for (Source source : next.getSources()) {
//                                        if (!sourceNames.contains(source.getName())) {
//                                            similiar = false;
//                                            break;
//                                        }
//                                    }
//                                }
//
//                                if (similiar) {
//                                    transition = "Slow Move";
//                                }
//
//                            }
//
//                            controller.changeSceneWithTransition(sceneName, transition, callback);
//                            currentSceneName = sceneName;
//
//                            //save scene info
//                            clickCount = 0;
//                            BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize()));
//                            JLabel l = new JLabel(new ImageIcon(ImageUtilities.getImage(screenCapture, increase, 0)));
//                            l.addMouseListener(new MouseAdapter() {
//                                @Override
//                                public void mousePressed(MouseEvent me) {
//                                    clickCount++;
//                                    messagePanel.setVisible(false);
//                                    messagePanel.removeAll();
//                                    if (clickCount == 1) {
//                                        thumbX = me.getX() / (int) increase;
//                                        thumbY = me.getY() / (int) increase;
//                                        messagePanel.add(new JLabel("press the lower right corner of the thumbnail of " + sceneName));
//                                    } else if (clickCount == 2) {
//                                        thumbW = me.getX() / (int) increase;
//                                        thumbW -= thumbX;
//                                        thumbH = me.getY() / (int) increase;
//                                        thumbH -= thumbY;
//                                        messagePanel.add(new JLabel("press the upper left corner of Program"));
//                                    } else if (clickCount == 3) {
//                                        programX = me.getX() / (int) increase;
//                                        programY = me.getY() / (int) increase;
//                                        messagePanel.add(new JLabel("press the lower right corner of Program"));
//                                    } else if (clickCount == 4) {
//                                        programW = me.getX() / (int) increase;
//                                        programW -= programX;
//                                        programH = me.getY() / (int) increase;
//                                        programH -= programY;
//
//                                        System.out.println("Thumb: (" + thumbX + ", " + thumbY + ", " + thumbW + ", " + thumbH + ")");
//                                        System.out.println("Program: (" + programX + ", " + programY + ", " + programW + ", " + programH + ")");
//                                        JButton temp = new JButton("Parse");
//                                        temp.addActionListener(new ActionListener() {
//                                            @Override
//                                            public void actionPerformed(ActionEvent ae) {
//                                                parse();
//                                            }
//
//                                        });
//                                        messagePanel.add(temp);
//                                        imagePanel.removeAll();
//                                        imagePanel.setVisible(false);
//
//                                        thumbCapture = robot.createScreenCapture(new Rectangle(thumbX, thumbY, thumbW, thumbH));
//                                        imagePanel.add(new JLabel(new ImageIcon(thumbCapture)), BorderLayout.NORTH);
//                                        programCapture = robot.createScreenCapture(new Rectangle(programX, programY, programW, programH));
//                                        imagePanel.add(new JLabel(new ImageIcon(programCapture)), BorderLayout.CENTER);
//
//                                    }
//
//                                    SwingUtilities.invokeLater(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            messagePanel.setVisible(true);
//                                            imagePanel.setVisible(true);
//                                            repaint();
//                                        }
//                                    });
//                                }
//
//                            });
//                            imagePanel.removeAll();
//                            imagePanel.setVisible(false);
//                            imagePanel.add(l, BorderLayout.CENTER);
//                            messagePanel.setVisible(false);
//                            messagePanel.removeAll();
//                            messagePanel.add(new JLabel("press the upper left corner of the thumbnail of " + sceneName));
//                            SwingUtilities.invokeLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    messagePanel.setVisible(true);
//                                    imagePanel.setVisible(true);
//                                    repaint();
//                                }
//                            });
//                        }
//                    });
//                    jPanel1.add(temp);
//                }
//            }
//        });
        setLocation(960, 0);
        setSize(new Dimension(950, 800));
    }
    private static final double increase = 10.0; //needs to effectively be an integer
    static int clickCount = 0;
    static int thumbX = 0;
    static int thumbY = 0;
    static int thumbW = 0;
    static int thumbH = 0;

    public static final int PROGRAM_X = 482;
    public static final int PROGRAM_Y = 32;
    public static final int PROGRAM_W = 429;
    public static final int PROGRAM_H = 240;
    static BufferedImage programCapture;
    static BufferedImage thumbCapture;

    static int maxPixel = 0;
    static int maxPixelCount = 0;

    private void parse() {

        Map<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();
        Map<Integer, List<Point>> pixelToPoint = new HashMap<Integer, List<Point>>();
        //Share the word
        //Introduce people to Jesus
        //Demonstrate evidence of Christ in our lives
        //learn to follow Jesus/Pray/Read and understand the bible
        //Show them
        //Focus on how to Reproduce (will it <stop with/spread through> me)
        //
        //Have an eternal perspective, Work while there is still time.
        //It should break our hearts that, unbelievers will be tormented by the horrors of hell every second of every day and night forever... and ever... and ever.  It will never end.  God have mercy!
        //
        //We as parents need to disciple our children
        //Gap year, to work among the unreached.

        for (int x = 0; x < programCapture.getWidth(); x++) {
            for (int y = 0; y < programCapture.getHeight(); y++) {
                int pixel = programCapture.getRGB(x, y);
                if (pixel != -13816531 && pixel != -1 && pixel != -12566502) {
                    int count = 0;
                    if (pixelMap.containsKey(pixel)) {
                        count = pixelMap.get(pixel);
                    }
                    pixelMap.put(pixel, count + 1);

                    List<Point> points = pixelToPoint.get(pixel);
                    if (!pixelToPoint.containsKey(pixel)) {
                        points = new ArrayList<Point>();
                        pixelToPoint.put(pixel, points);
                    }
                    points.add(new Point(x, y));
                }
            }
        }

        List<Integer> countList = new ArrayList<Integer>();
        for (Integer key : pixelMap.keySet()) {
//            System.out.println(pixelMap.get(key) + ", " + key);
            countList.add(pixelMap.get(key));
        }

        countList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer t, Integer t1) {
                return t - t1;
            }
        });
        for (Integer i : countList) {
//            System.out.println("  " + i);
        }

        Map<Integer, Point> cleanedPixelToPoint = new HashMap<Integer, Point>();
        for (Integer key : pixelToPoint.keySet()) {
//            System.out.println(key + " " + pixelToPoint.get(key).size());
            if (maxPixelCount < pixelToPoint.get(key).size()) {
                maxPixelCount = pixelToPoint.get(key).size();
                maxPixel = key;
            }

            double xs = 0;
            double ys = 0;
            int firstX = pixelToPoint.get(key).get(0).x;
            int firstY = pixelToPoint.get(key).get(0).y;
            int diffX = 0;
            int diffY = 0;
            for (Point p : pixelToPoint.get(key)) {
                xs += p.x;
                ys += p.y;

                diffX = diffX + Math.abs(p.x - firstX);
                diffY = diffY + Math.abs(p.y - firstY);
            }
            xs = xs / pixelToPoint.get(key).size();
            ys = ys / pixelToPoint.get(key).size();
//            System.out.println("  AVG: " + xs + "    " + ys + "   DIFF: " + diffX / pixelToPoint.get(key).size() + "   " + diffY / pixelToPoint.get(key).size());
//
//            System.out.print("     ");
//            for (Point p : pixelToPoint.get(key)) {
//                System.out.print(p.x + ", " + p.y + "  ");
//            }
//            System.out.println("\n");

            if (diffX > 1 && diffY > 1) {
                cleanedPixelToPoint.put(key, new Point((int) Math.round(xs), (int) Math.round(ys)));
            }
        }

        String origSceneName = currentSceneName;
        for (String key : ObsAutomationUtility.allScenes) {
            if (key.toUpperCase().equals(key)) {
                continue;
            }
            controller.changeSceneWithTransition(key, "Cut", callback);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ObsAutomationUtility.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedImage tempProgram = robot.createScreenCapture(new Rectangle(PROGRAM_X, PROGRAM_Y, PROGRAM_W, PROGRAM_H));
            imagePanel.add(new JLabel(new ImageIcon(tempProgram)), BorderLayout.SOUTH);
            imagePanel.setVisible(false);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    imagePanel.setVisible(true);
                }
            });
            analyzeProgramImage(cleanedPixelToPoint, key);
        }
    }

    private void analyzeProgramImage(Map<Integer, Point> basePixelToPoint, final String sceneName) {
        BufferedImage tempProgram = robot.createScreenCapture(new Rectangle(PROGRAM_X, PROGRAM_Y, PROGRAM_W, PROGRAM_H));

        Map<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();
        Map<Integer, List<Point>> pixelToPoint = new HashMap<Integer, List<Point>>();

        int greyMinX = programCapture.getWidth();
        int greyMinY = programCapture.getHeight();
        int greyMaxX = 0;
        int greyMaxY = 0;

//        System.out.println("MaxPixel: " + maxPixel + " " + maxPixelCount);
        for (int x = 0; x < tempProgram.getWidth(); x++) {
            for (int y = 0; y < tempProgram.getHeight(); y++) {
                int pixel = tempProgram.getRGB(x, y);
//                System.out.println("        " + pixel);
                if (pixel == maxPixel) {
//                    System.out.println("   " + x + ", " + y);
                    if (x > greyMaxX) {
                        greyMaxX = x;
                    }
                    if (y > greyMaxY) {
                        greyMaxY = y;
                    }
                    if (x < greyMinX) {
                        greyMinX = x;
                    }
                    if (y < greyMinY) {
                        greyMinY = y;
                    }
                }
            }
        }

        for (int x = greyMinX; x < greyMaxX; x++) {
            for (int y = greyMinY; y < greyMaxY; y++) {
                int pixel = tempProgram.getRGB(x, y);
                if (basePixelToPoint.containsKey(pixel)) {
                    int count = 0;
                    if (pixelMap.containsKey(pixel)) {
                        count = pixelMap.get(pixel);
                    }
                    pixelMap.put(pixel, count + 1);

                    List<Point> points = pixelToPoint.get(pixel);
                    if (!pixelToPoint.containsKey(pixel)) {
                        points = new ArrayList<Point>();
                        pixelToPoint.put(pixel, points);
                    }
                    points.add(new Point(x, y));
                }
            }
        }

        int maxCount = 0;
        for (Integer key : pixelToPoint.keySet()) {
            if (maxCount < pixelToPoint.get(key).size()) {
                maxCount = pixelToPoint.get(key).size();
            }
        }

        Map<Integer, Point> cleanedPixelToPoint = new HashMap<Integer, Point>();
        for (Integer key : pixelToPoint.keySet()) {
//                                                    System.out.println(key + " " + pixelToPoint.get(key).size());

            double xs = 0;
            double ys = 0;
            int firstX = pixelToPoint.get(key).get(0).x;
            int firstY = pixelToPoint.get(key).get(0).y;
            int diffX = 0;
            int diffY = 0;
            for (Point p : pixelToPoint.get(key)) {
                xs += p.x;
                ys += p.y;

                diffX = diffX + Math.abs(p.x - firstX);
                diffY = diffY + Math.abs(p.y - firstY);
            }
            xs = xs / pixelToPoint.get(key).size();
            ys = ys / pixelToPoint.get(key).size();
//                                                    System.out.println("  AVG: " + xs + "    " + ys + "   DIFF: " + diffX / pixelToPoint.get(key).size() + "   " + diffY / pixelToPoint.get(key).size());

//                                                    System.out.print("     ");
//            for (Point p : pixelToPoint.get(key)) {
//                System.out.print(p.x + ", " + p.y + "  ");
//            }
//                                                    System.out.println("\n");
            if (diffX > 1 && diffY > 1 && pixelToPoint.get(key).size() < maxCount) {
                cleanedPixelToPoint.put(key, new Point((int) Math.round(xs), (int) Math.round(ys)));
            }
        }
        int minX = programCapture.getWidth();
        int minY = programCapture.getHeight();
        int maxX = 0;
        int maxY = 0;

        for (Integer key : cleanedPixelToPoint.keySet()) {
            Point p = basePixelToPoint.get(key);
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.y < minY) {
                minY = p.y;
            }
        }

        final int finalMinX = minX;
        final int finalMaxX = maxX;
        final int finalMinY = minY;
        final int finalMaxY = maxY;

        final int finalGreyMinX = greyMinX;
        final int finalGreyMinY = greyMinY;
        final int finalGreyMaxX = greyMaxX;
        final int finalGreyMaxY = greyMaxY;

        tempImagePanel.add(new JLabel(""));
        tempImagePanel.add(new JLabel(sceneName));

//        System.out.println("    " + finalGreyMinX + ", " + finalGreyMinY + ", " + finalGreyMaxX + ", " + finalGreyMaxY);
        JLabel tempProgramLabel = new JLabel(new ImageIcon(tempProgram)) {
            @Override
            public void paint(Graphics grphcs) {
                super.paint(grphcs);
                Graphics2D g2d = (Graphics2D) grphcs;
                g2d.setColor(Color.BLACK);
                g2d.drawString(sceneName, 0, 30);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRect(finalGreyMinX, finalGreyMinY, finalGreyMaxX - finalGreyMinX, finalGreyMaxY - finalGreyMinY);
            }
        };

        tempImagePanel.add(tempProgramLabel);

        BufferedImage tempImg = programCapture.getSubimage(minX, minY, maxX - minX, maxY - minY);
        JLabel subImage = new JLabel(new ImageIcon(tempImg));
        tempImagePanel.add(subImage);
        JLabel tempLabel = new JLabel(new ImageIcon(programCapture)) {
            @Override
            public void paint(Graphics grphcs) {
                super.paint(grphcs);
                Graphics2D g2d = (Graphics2D) grphcs;
                g2d.setColor(Color.BLACK);
                g2d.drawString(sceneName, 0, 30);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRect(finalMinX, finalMinY, finalMaxX - finalMinX, finalMaxY - finalMinY);
            }
        };

        tempImagePanel.add(tempLabel);

        JLabel tempThumbnailLabel = new JLabel(new ImageIcon(ImageUtilities.getImage(thumbCapture, 2, 0))) {
            @Override
            public void paint(Graphics grphcs) {
                super.paint(grphcs);
                Graphics2D g2d = (Graphics2D) grphcs;
                g2d.setColor(Color.BLACK);
                g2d.drawString(sceneName, 0, 30);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRect(finalMinX, finalMinY, finalMaxX - finalMinX, finalMaxY - finalMinY);
            }
        };

//        Scene scene = sceneMap.get(sceneName);
//        String overlay = "";
//
//        for (Source source : scene.getSources()) {
//            if (source.getName().contains("verlay")) {
//                overlay = source.getName();
//                break;
//            }
//        }
        String mapName = sceneName.substring(0, sceneName.indexOf(",") - 1).replaceAll(Pattern.quote(" "), "");
        if (!slotNameOverlays.contains(mapName)) {
            System.out.println("slotOverlayMap.put(\"" + mapName + "\", new ArrayList<String>());");
            slotNameOverlays.add(mapName);
        }

        System.out.println("slotOverlayMap.get(\"" + mapName + "\").add(\"" + sceneName + "\");");
        System.out.println("sceneToRectMap.put(\"" + sceneName + "\", new Rectangle(" + minX + ", " + minY + ", " + (maxX - minX) + ", " + (maxY - minY) + "));");

        System.out.println("sceneToMidpointMap.put(\"" + sceneName + "\", new Point(" + (minX + ((maxX - minX) / 2)) + ", " + (minY + ((maxY - minY) / 2)) + "));");

        tempImagePanel.add(tempThumbnailLabel);

        imagePanel.setVisible(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                imagePanel.setVisible(true);
            }
        });
    }

    List<String> slotNameOverlays = new ArrayList<String>();

    public void transition(String scene, String paramTransition, int paramDuration) {
        int duration = paramDuration;
        String transition = paramTransition;
        controller.setTransitionDuration(duration, responseCallback);
        controller.changeSceneWithTransition(scene, transition, callback);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        imagePanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        messagePanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tempImagePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        jButton1.setText("Test 1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Test visibility");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("First");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("SceneList");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));
        jScrollPane1.setViewportView(jPanel1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        imagePanel.setLayout(new java.awt.BorderLayout());
        jScrollPane2.setViewportView(imagePanel);

        jPanel2.add(jScrollPane2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.add(messagePanel);

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        tempImagePanel.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setText("jLabel1");
        tempImagePanel.add(jLabel1);

        jScrollPane3.setViewportView(tempImagePanel);

        getContentPane().add(jScrollPane3, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            int cam1X = 50;
            int cam1Y = 280;
            int cam1W = 211;
            int cam1H = 110;

            final JLabel tempLabel = new JLabel();
            imagePanel.add(tempLabel);

            BufferedImage cam1 = robot.createScreenCapture(new Rectangle(new Point(cam1X, cam1Y), new Dimension(cam1W, cam1H)));
            BufferedImage sixD = robot.createScreenCapture(new Rectangle(new Point(483, cam1Y), new Dimension(cam1W, cam1H)));

            JLabel cam1Label = new JLabel(new ImageIcon(cam1));
            JLabel sixDLabel = new JLabel(new ImageIcon(sixD));
            imagePanel.add(cam1Label);
            imagePanel.add(sixDLabel);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    imagePanel.repaint();
                }
            });

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ObsAutomationUtility.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    for (String key : sceneMap.keySet()) {
                        Scene scene = sceneMap.get(key);
                        boolean containsHD = false;
                        boolean containsSD = false;

                        for (Source source : scene.getSources()) {
                            if (source.getType().equals("scene")) {
                                if (source.getName().equals("Generic SD Camera Template Scene")) {
                                    containsSD = true;
                                    break;
                                } else if (source.getName().equals("Generic HD Camera Template Scene")) {
                                    containsHD = true;
                                    break;
                                }
                            }
                        }

                        if (containsHD) {
                            transition(scene.getName(), "Cut", 0);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ObsAutomationUtility.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (containsSD) {
                            transition(scene.getName(), "Cut", 0);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ObsAutomationUtility.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        BufferedImage tempImg = robot.createScreenCapture(new Rectangle(new Point(483, 32), new Dimension(430, 240)));
                        tempLabel.setIcon(new ImageIcon(tempImg));

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                imagePanel.revalidate();
                                imagePanel.repaint();
                            }
                        });

                    }
                }
            });
            t.start();
        } catch (Exception ex) {
            Logger.getLogger(ObsAutomationUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    boolean on = true;
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Scene generic = sceneMap.get("Generic HD");
        on = !on;
//        controller.setMute("Laptop Camera", on, new Callback<SetMuteResponse>() {
//            @Override
//            public void run(SetMuteResponse rt) {
//                System.out.println(rt.getStatus() + " " + rt.getMessageId() + " " + rt.getError());
//            }
//        });

        controller.setSourceVisibility("Generic HD Camera Template Scene", "Generic HD Camera 2 Template Scene", on, new Callback<SetSceneItemPropertiesResponse>() {
            @Override
            public void run(SetSceneItemPropertiesResponse rt) {
                System.out.println(rt.getStatus() + " " + rt.getMessageId() + " " + rt.getError());
            }
        });

//        for(Source s: generic.getSources()){
//            
//        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private static String slotName = "";
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

//first slot:
//Thumb: (48, 276, 212, 117)
//Program: (482, 32, 429, 239)
        slotName = "firstSlot";
        thumbX = 48;
        thumbY = 276;
        thumbW = 212;
        thumbH = 117;

//third slot:
//Thumb: (482, 276, 212, 117)
//Program: (482, 32, 429, 239)
//        slotName = "thirdSlot";
//        thumbX = 482;
//        thumbY = 276;
//        thumbW = 212;
//        thumbH = 117;
//
//        programX = 482;
//        programY = 32;
//        programW = 429;
//        programH = 239;
//4th slot:
//Thumb: 699, 276, 212, 117
//slotName = "fourthSlot";
//        thumbX = 699;
//        thumbY = 276;
//        thumbW = 212;
//        thumbH = 117;
        jButton3.setVisible(false);

        System.out.println("private static Rectangle programRect = new Rectangle(" + PROGRAM_X + ", " + PROGRAM_Y + ", " + PROGRAM_W + ", " + PROGRAM_H + ");");
//        System.out.println("private static Map<String, List<String>>" + slotName + "OverlayMap = new HashMap<String, List<String>>();");
        System.out.println("slotThumbRect = new Rectangle(" + thumbX + ", " + thumbY + ", " + thumbW + ", " + thumbH + ");\n\n\n");
        final JButton temp = new JButton("Parse");
        temp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                parse();
            }

        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                temp.setVisible(true);
            }
        });
        messagePanel.add(temp);
        imagePanel.removeAll();
        imagePanel.setVisible(false);

        thumbCapture = robot.createScreenCapture(new Rectangle(thumbX, thumbY, thumbW, thumbH));
        imagePanel.add(new JLabel(new ImageIcon(thumbCapture)), BorderLayout.NORTH);
        programCapture = robot.createScreenCapture(new Rectangle(PROGRAM_X, PROGRAM_Y, PROGRAM_W, PROGRAM_H));
        Graphics2D g2d = (Graphics2D) programCapture.getGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(672 - PROGRAM_X, 238 - PROGRAM_Y, 49, 15);

        imagePanel.add(new JLabel(new ImageIcon(programCapture)), BorderLayout.CENTER);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                imagePanel.setVisible(true);
            }
        });

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        for (String s : sceneList) {
            System.out.println(s);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(ObsAutomationUtility.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ObsAutomationUtility.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ObsAutomationUtility.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ObsAutomationUtility.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false, "crefObsWebsockets", true);
                new ObsAutomationUtility(controller).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel imagePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel messagePanel;
    private javax.swing.JPanel tempImagePanel;
    // End of variables declaration//GEN-END:variables

    static List<String> allScenes;

    private static void initScenes() {
        if (allScenes != null) {
            return;
        }

        try {
//Build out scenes in CREFScenesBase based on the pattern SceneGroupName x maxX, maxY with one of the combo scenes present
//Copy the json output of this file to CREFScenesBuilt.json and import those scenes back into OBS

            InputStream in = ObsAutomationUtility.class.getResourceAsStream("/obsautomation/CREFScenesBuilt.json");
//            File f = new File(ObsAutomationUtility.class.getResource("/obsautomation/CREFScenesBuilt.json").toURI());
//            System.out.println(f.getAbsolutePath());
//            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringBuilder sb = new StringBuilder();
            String eachLine = br.readLine();

            while (eachLine != null) {
                sb.append(eachLine);
                sb.append("\n");
                eachLine = br.readLine();
            }
//            System.out.println(sb.toString());

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(sb.toString()).getAsJsonObject();

            JsonArray scenesJson = json.getAsJsonArray("scene_order");
            allScenes = new ArrayList<String>(scenesJson.size());
            for (int i = 0; i < scenesJson.size(); i++) {
                allScenes.add(scenesJson.get(i).getAsJsonObject().get("name").getAsString());
            }

            System.out.println(allScenes);

            if (allScenes.contains("COMBO SCENE 1") && allScenes.contains("COMBO SCENE 2")) {
                System.out.println("Done");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
