package sayit.helpers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * A class containing helper methods for images.
 */
public final class ImageHelper {
    /**
     * Gets the image icon from the given file name.
     *
     * @param fileName The file name of the image.
     * @param size     The size of the image.
     * @return The image icon.
     */
    public static ImageIcon getImageIcon(String fileName, int size) {
        Image img;
        try {
            img = ImageIO.read(new File(fileName));
            return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            throw new RuntimeException("Unable to load image: " + fileName + "\n" + e.getMessage());
        }
    }
}
