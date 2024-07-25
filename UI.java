import java.awt.image.*;
import javax.swing.*;

public class UI {

  private JPanel panel; 
  private JFrame frame;
  private JLabel label;
  private BufferedImage img; 
  private BufferedImage processedImage; 
  private ImageProcessing imageProcessor = new ImageProcessing(); 

  public UI() {
    this.panel = new JPanel();
    this.frame = new JFrame("Image Recognition Maze Solver");
    this.label = new JLabel();

    frame.setSize(1200, 800);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setVisible(true); 

  }

  public void setImage(BufferedImage img) {
    this.img = img;
    processedImage = imageProcessor.processImage(img);
    renderImage(); 
  }

  /**
   * Displays an image in a window using a JFrame.
   */
  public void renderImage() { 
    label.setIcon(new ImageIcon(processedImage));
    frame.getContentPane().add(label);
    frame.setVisible(true);
  }

  
}
