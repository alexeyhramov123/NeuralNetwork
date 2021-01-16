import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class DigitPanel extends JPanel {

    DigitPanel(){                                                                                                       //sets panel black when it is created
        super();
        this.setBackground(Color.black);
    }

    public void paintImage(double[] pixels) {                                                                           //paints image of the digit
        BufferedImage digitImage = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);                 //creates empty image
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 27; j++) {
                int brightness = (int) (pixels[j * 28 + i] * 255);
                digitImage.setRGB(i, j, new Color(brightness, brightness, brightness).getRGB());                        //sets pixels to brightness level
            }
        }
        //scaling image to size of the panel
        AffineTransform at= AffineTransform.getScaleInstance((double) this.getHeight() / 28,(double) this.getWidth() / 28);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage digitImageScaled = new BufferedImage(700, 700, BufferedImage.TYPE_INT_RGB);
        digitImageScaled = ato.filter(digitImage, digitImageScaled);
        this.getGraphics().drawImage(digitImageScaled, 0, 0, null);                                      //draws the image
    }
}
