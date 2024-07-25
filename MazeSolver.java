import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class MazeSolver {
  
  private static UI ui;

  public static void main(String[] args) {
    File file = new File("maze.jpg");
    BufferedImage img = null;

    try {
      img = ImageIO.read(file);
    } catch (IOException e) {
      System.out.println("Error: " + e);
    }

    ui = new UI();
    ui.setImage(img);

  }

}
