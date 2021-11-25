package org.coonrapidsfree.obs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author dnyffeler
 */
public class ImageUtilities {

    public static BufferedImage getImageOfComponent(
            Component component) {
        return getImageOfComponent(component, 1.0);
    }

    /**
     *
     * @param component
     * @param percentageIncreaseInSize i.e. 100% = 1.0
     * @return
     */
    public static BufferedImage getImageOfComponent(
            Component component, double percentageIncreaseInSize) {
        return getImageOfComponent(component, percentageIncreaseInSize, Integer.MAX_VALUE);
    }

    public static BufferedImage getImageOfComponent(
            Component component, double percentageIncreaseInSize, int maxWidth) {

        int prefWidth = (int) Math.ceil(component.getSize().width * percentageIncreaseInSize);
        if (prefWidth > maxWidth) {
            prefWidth = maxWidth;
        }

        BufferedImage image = new BufferedImage(
                prefWidth,
                (int) Math.ceil(component.getSize().height * percentageIncreaseInSize),
                BufferedImage.TYPE_INT_ARGB
        );
        // call the Component's paint method, using
        // the Graphics object of the image.
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        if (percentageIncreaseInSize != 1.0) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, (int) (component.getWidth() * percentageIncreaseInSize), (int) (component.getHeight() * percentageIncreaseInSize));
        g2d.scale(percentageIncreaseInSize, percentageIncreaseInSize);
        component.paint(g2d);
        return image;
    }

    public static Image getImage(Image originalImage, double percentageIncreaseInSize, int rotation) {
        if (percentageIncreaseInSize == 1 && rotation == 0) {
            return originalImage;
        }

        int width = (int) Math.ceil(originalImage.getWidth(null) * percentageIncreaseInSize);
        int height = (int) Math.ceil(originalImage.getHeight(null) * percentageIncreaseInSize);
        if (rotation != 0) {
            int temp = width;
            width = height;
            height = temp;
        }

        BufferedImage image = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (rotation == 90) {
            g2d.translate(width, 0);
            g2d.rotate(Math.toRadians(rotation));
        } else if (rotation == -90) {
            g2d.translate(0, height);
            g2d.rotate(Math.toRadians(rotation));
        }
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, (int) image.getWidth(), (int) image.getHeight());
        g2d.scale(percentageIncreaseInSize, percentageIncreaseInSize);
        g2d.drawImage(originalImage, 0, 0, null);
        return image;
    }

    public static Image getImageSmooth(Image originalImage, double percentageIncreaseInSize, int rotation) {
        if (percentageIncreaseInSize == 1 && rotation == 0) {
            return originalImage;
        }

        int width = (int) Math.ceil(originalImage.getWidth(null) * percentageIncreaseInSize);
        int height = (int) Math.ceil(originalImage.getHeight(null) * percentageIncreaseInSize);
        if (rotation != 0) {
            int temp = width;
            width = height;
            height = temp;
        }

        if (percentageIncreaseInSize > 1) {
            Image temp = getImage(originalImage, percentageIncreaseInSize * 2, rotation);
            return temp.getScaledInstance(width, height, Image.SCALE_REPLICATE);
        } else {
            Image temp = originalImage;
            if (rotation != 0) {
                temp = getImage(originalImage, 1, rotation);
            }
            return temp.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        }
    }
}
