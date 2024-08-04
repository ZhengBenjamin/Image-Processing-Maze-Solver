import java.awt.*;
import java.awt.image.*;

public class ImageProcessing {

  public BufferedImage processImage(BufferedImage img, int edgeDetectThreshold) {
    BufferedImage processedImage = img;
    processedImage = greyScale(processedImage);
    processedImage = gaussianBlur5(gaussianBlur5(processedImage));

    if (img.getWidth() > 900 || img.getHeight() > 900) {
      processedImage = resize(processedImage, 900);   
    }

    processedImage = gaussianBlur3(processedImage);
    processedImage = edgeDetect(processedImage, edgeDetectThreshold);

    System.out.println("Processed Edge Detection");
    return processedImage;
  }

  public BufferedImage preprocessImage(BufferedImage img, int edgeDetectThreshold) {
    BufferedImage processedImage = img;
    processedImage = greyScale(processedImage);


    processedImage = gaussianBlur3(processedImage);
    processedImage = gaussianBlur5(gaussianBlur5(processedImage));
    if (img.getWidth() > 900 || img.getHeight() > 900) {
      processedImage = resize(processedImage, 900);   
    }

    System.out.println("Processed Edge Detection");
    return processedImage;
  }

  public BufferedImage contrastDetect(BufferedImage img) {
    BufferedImage temp = greyScale(img); 
    BufferedImage processedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    int averagePixel = 0;

    for (int x = 0; x < temp.getWidth(); x++) {
      for (int y = 0; y < temp.getHeight(); y++) {
        averagePixel += (int)(temp.getRGB(x, y) & 0xFF);
      }
    }

    averagePixel /= (temp.getWidth() * temp.getHeight());

    for (int x = 0; x < temp.getWidth(); x++) {
      for (int y = 0; y < temp.getHeight(); y++) {
        int pixel = (int)(temp.getRGB(x, y) & 0xFF);
        if (pixel > averagePixel) {
          processedImage.setRGB(x, y, Color.WHITE.getRGB());
        } else {
          processedImage.setRGB(x, y, Color.BLACK.getRGB());
        }
      }
    }

    return processedImage; 
  }

  /**
   * Converts an image to greyscale.
   * @return The greyscale image.
   */
  public BufferedImage greyScale(BufferedImage img) {
    BufferedImage greyImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    Graphics converter = greyImage.getGraphics();
    converter.drawImage(img, 0, 0, null);
    converter.dispose();
    return greyImage; 
  }

  public BufferedImage gaussianBlur3(BufferedImage img) {
    BufferedImage blurredImage = new BufferedImage(img.getWidth() - 2, img.getHeight() - 2, BufferedImage.TYPE_BYTE_GRAY);
    int pixel;

    for (int x = 0; x < blurredImage.getWidth(); x++) {
      for (int y = 0; y < blurredImage.getHeight(); y++) {
        pixel = (int)(4 * (img.getRGB(x+1, y+1) & 0xFF) // Gaussian blur for 3x3 starting from topleft 
        + 2 * (img.getRGB(x+1, y) & 0xFF)
        + 2 * (img.getRGB(x+1, y+2) & 0xFF)
        + 2 * (img.getRGB(x, y+1) & 0xFF)
        + 2 * (img.getRGB(x+2, y+1) & 0xFF)
        + (img.getRGB(x,y) & 0xFF) 
        + (img.getRGB(x+2,y) & 0xFF)
        + (img.getRGB(x,y+2) & 0xFF)
        + (img.getRGB(x+2,y+2) & 0xFF))/16;
        int argb = (255<<24) | (pixel << 16) | (pixel << 8) | pixel;
        blurredImage.setRGB(x, y, argb);
      }
    }

    return blurredImage;
  }

  /**
   * Applies a 5x5 Gaussian blur to an image.
   * @param img The image to blur.
   * @return The blurred image.
   */
  public BufferedImage gaussianBlur5(BufferedImage img) {
    BufferedImage blurredImage = new BufferedImage(img.getWidth() - 4, img.getHeight() - 4, BufferedImage.TYPE_BYTE_GRAY);
    int pixel;

    for (int x = 0; x < blurredImage.getWidth(); x++) {
      for (int y = 0; y < blurredImage.getHeight(); y++) {
        pixel = (int)( (41 * (img.getRGB(x+2, y+2) & 0xFF) // Gaussian blur for 5x5 starting from topleft
        + 26 * (img.getRGB(x+1, y+2) & 0xFF)
        + 26 * (img.getRGB(x+2, y+1) & 0xFF)
        + 26 * (img.getRGB(x+3, y+2) & 0xFF)
        + 26 * (img.getRGB(x+2, y+3) & 0xFF)
        + 16 * (img.getRGB(x+1, y+1) & 0xFF)
        + 16 * (img.getRGB(x+3, y+1) & 0xFF)
        + 16 * (img.getRGB(x+1, y+3) & 0xFF)
        + 16 * (img.getRGB(x+3, y+3) & 0xFF)
        + 7 * (img.getRGB(x, y+2) & 0xFF)
        + 7 * (img.getRGB(x+2, y) & 0xFF)
        + 7 * (img.getRGB(x+4, y+2) & 0xFF)
        + 7 * (img.getRGB(x+2, y+4) & 0xFF)
        + 4 * (img.getRGB(x, y+1) & 0xFF)
        + 4 * (img.getRGB(x+1, y) & 0xFF)
        + 4 * (img.getRGB(x+4, y+1) & 0xFF)
        + 4 * (img.getRGB(x+1, y+4) & 0xFF)
        + 4 * (img.getRGB(x, y+3) & 0xFF)
        + 4 * (img.getRGB(x+3, y) & 0xFF)
        + 4 * (img.getRGB(x+4, y+3) & 0xFF)
        + 4 * (img.getRGB(x+3, y+4) & 0xFF)
        + (img.getRGB(x, y) & 0xFF)
        + (img.getRGB(x+4, y) & 0xFF)
        + (img.getRGB(x, y+4) & 0xFF)
        + (img.getRGB(x+4, y+4) & 0xFF))/273);
        int argb = (255<<24) | (pixel << 16) | (pixel << 8) | pixel;
        blurredImage.setRGB(x, y, argb);
      }
    }

    return blurredImage;
  }

  /**
   * Detects edges in an image.
   * @param img The image to detect edges in.
   * @return The edge-detected image.
   */
  public BufferedImage edgeDetect(BufferedImage img, int threshold) {
    BufferedImage edgeImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    int[][] vertDetect = new int[img.getWidth()][img.getHeight()]; 
    int[][] horizDetect = new int[img.getWidth()][img.getHeight()];
    int[][] edgeDetect = new int[img.getWidth()][img.getHeight()];
    int pixel = 0;

    for (int x = 1; x < img.getWidth() - 1; x++) {
      for (int y = 1; y < img.getHeight() - 1; y++) {
        horizDetect[x][y] = (int)((((img.getRGB(x-1, y-1) & 0xFF)
        + (img.getRGB(x, y-1) & 0xFF)
        + (img.getRGB(x+1, y-1) & 0xFF))
        - ((img.getRGB(x-1, y+1) & 0xFF)
        + (img.getRGB(x, y+1) & 0xFF)
        + (img.getRGB(x+1, y+1) & 0xFF)))/6);

        vertDetect[x][y] = (int)((((img.getRGB(x-1, y-1) & 0xFF)
        + (img.getRGB(x-1, y) & 0xFF)
        + (img.getRGB(x-1, y+1) & 0xFF))
        - ((img.getRGB(x+1, y-1) & 0xFF)
        + (img.getRGB(x+1, y) & 0xFF)
        + (img.getRGB(x+1, y+1) & 0xFF)))/6);

        edgeDetect[x][y] = (int)(Math.sqrt(Math.pow(horizDetect[x][y], 2) + Math.pow(vertDetect[x][y], 2)));
        
        if (edgeDetect[x][y] < threshold) {
          pixel = (255<<24) | (pixel << 16) | (pixel << 8) | 255;
        } else {
          pixel = 0;
        }
        
        edgeImage.setRGB(x, y, pixel);
      }
    }

    return edgeImage;
  }

  /**
   * Resizes current image to desired width and height.
   * @param img The image to resize.
   * @param maxSide The maximum side length of the image.
   * @return The resized image.
   */
  public BufferedImage resize(BufferedImage img, int maxSide) {
    int width;
    int height;

    if (maxSide < img.getWidth() || maxSide < img.getHeight()) {
      if (img.getWidth() > img.getHeight()) {
        int difference = maxSide - img.getWidth();
        width = maxSide;
        height = img.getHeight() + difference * img.getHeight() / img.getWidth();
      } else if (img.getWidth() < img.getHeight()) {
        int difference = maxSide - img.getHeight();
        height = maxSide;
        width = img.getWidth() + difference * img.getWidth() / img.getHeight();
      } else {
        width = maxSide;
        height = maxSide;
      }
    } else {
      throw new IllegalArgumentException("Image is already smaller than the desired size.");
    }

    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(img, 0, 0, width, height, null);
    g.dispose();

    System.out.println("Resized");
    return resizedImage;
  }

}

