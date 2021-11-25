package org.coonrapidsfree.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ImageCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedImage bimage = new BufferedImage(3840, 2160, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = (Graphics2D) bimage.getGraphics();
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(0, 0, 3840, 2160);
        boolean on = true;

//        for (int i = 0; i < 40; i++) {
//            for (int j = 0; j < 22; j++) {
//                Color c = new Color(i * 6, i * 6, i * 6);
//
//                g2d.setColor(c);
//                if (on) {
//                    g2d.fillRect(i * 100, j * 100, 100, 100);
//                }
//                on = !on;
//                
//            }
//            on = !on;
//        }
        g2d.setFont(new Font("Ariel", Font.PLAIN, 40));

        int colorInt = 0;
        String colorStr = "000050000";
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 22; j++) {
                Color c = new Color(i * 6, 40, 40);
                g2d.setColor(c);
                if (on) {
                    int red = 0;
                    int green = 0;
                    int blue = 0;

//                    for (int count = 0; count < 253; count++) {
//                        colorStr = incrementColorString(colorStr);
//                    }
                        colorStr = incrementColorString2(colorStr, 141, 11);
                    red = Integer.valueOf(colorStr.substring(colorStr.length() - 3));
                    blue = Integer.valueOf(colorStr.substring(colorStr.length() - 6, colorStr.length() - 3));
                    green = Integer.valueOf(colorStr.substring(colorStr.length() - 9, colorStr.length() - 6));
                    System.out.println(colorStr + " " + red + " " + green + " " + blue);

                    try {
                        c = new Color(red, green, blue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    g2d.setColor(c);

//                    if (clrSwitcher) {
//                        c = new Color(40, 255 - i * 6, 255 - j * 11);
//                        g2d.setColor(c);
//                    }
//                    clrSwitcher = !clrSwitcher;
                    g2d.fillRect(i * 102, j * 104, 100, 100);
//                    g2d.setColor(Color.white);
//                    g2d.drawString(i + "," + j, i * 100, j * 100);
                }
                on = !on;
            }
            on = !on;
        }
        File outputFile = new File("temp.png");
        System.out.println(colorInt);
        try {
            ImageIO.write(bimage, "PNG", outputFile);
        } catch (IOException ex) {
            Logger.getLogger(ImageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String incrementColorString(String colorStr) {
        int[] temp = new int[3];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = Integer.valueOf(colorStr.substring(colorStr.length() - (i * 3 + 3), colorStr.length() - i * 3));
//            System.out.println(i + ":" + temp[i]);
        }

        temp[0]++;

        for (int i = 0; i < temp.length; i++) {
            if (temp[i] > 255) {
                temp[i] = 0;
                temp[i + 1]++;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = temp.length - 1; i >= 0; i--) {
            String s = temp[i] + "";

            if (s.length() == 1) {
                sb.append("00");
            } else if (s.length() == 2) {
                sb.append("0");
            }
            sb.append(s);
        }

//        System.out.println(sb.toString() + "\n");
        return sb.toString();
    }

    private static String incrementColorString2(String colorStr, int increment, int subsequentIncrement) {
        int[] temp = new int[3];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = Integer.valueOf(colorStr.substring(colorStr.length() - (i * 3 + 3), colorStr.length() - i * 3));
//            System.out.println(i + ":" + temp[i]);
        }

        temp[0]+=increment;

        for (int i = 0; i < temp.length; i++) {
            if (temp[i] > 255) {
                temp[i]-=255;
                try {
                    temp[i + 1] += subsequentIncrement;
                } catch (Exception e) {
                    for (int j = 0; j < temp.length; j++) {
                        temp[j] = 10;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = temp.length - 1; i >= 0; i--) {
            String s = temp[i] + "";

            if (s.length() == 1) {
                sb.append("00");
            } else if (s.length() == 2) {
                sb.append("0");
            }
            sb.append(s);
        }

//        System.out.println(sb.toString() + "\n");
        return sb.toString();
    }

}
