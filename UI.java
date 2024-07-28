import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import javax.swing.*;

public class UI {

  private final JFrame window;
  private final ImageProcessing imageProcessor = new ImageProcessing(); 
  private BufferedImage originalImage; 
  private BufferedImage processedImage; 
  private BufferedImage markedImage; 
  private JPanel imagePanel; 
  private int[] startingPoint = new int[]{0, 0};
  private int[] endingPoint = new int[]{0, 0};

  private JFrame testFrame = new JFrame("Test"); 
  private JLabel testLabel = new JLabel(); 
    private int x;

  /**
   * Constructor for the UI class.
   */
  public UI() {
    this.window = new JFrame("Image Recognition Maze Solver");

    window.setSize(1600, 900);
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    renderStartingUI();
  }

  /**
   * Sets the image to be solved
   * @param img The image to be solved
   */
  public void setImage(BufferedImage img) {
    this.originalImage = imageProcessor.resize(img);
    processedImage = imageProcessor.processImage(img);
    renderImage(); 
  }

  /**
   * Renders the starting UI. Prompts user to select an image.
   */
  private void renderStartingUI() {
    JPanel loadImage = new JPanel();
    JButton loadButton = new JButton("Load Image");

    loadImage.add(loadButton);
    window.add(loadImage, BorderLayout.NORTH);
    window.setVisible(true);

    loadButton.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.showOpenDialog(null);
      File file = fileChooser.getSelectedFile();
      BufferedImage img = null;

      try {
        img = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("Error: " + ex);
      }

      window.remove(loadImage);
      setImage(img);
    });

  }

  /**
   * Renders the starting UI.
   */
  private void renderButtons() {
    JPanel buttonPanel = new JPanel(); // Panel for buttons
    JButton solveButton = new JButton("Solve Maze"); // Button to solve the maze
    JButton resetButton = new JButton("Reset Maze"); // Button to reset the maze
    JButton changeMazeImage = new JButton("Change"); // Button to change the maze image

    changeMazeImage.addActionListener(e -> { // Change the maze image
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.showOpenDialog(null);
      File file = fileChooser.getSelectedFile();
      BufferedImage img = null;

      try {
        img = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("Error: " + ex);
      }

      setImage(img);
      });

    solveButton.addActionListener(e -> { // Solves the maze 
      MazeSolver solver = new MazeSolver(processedImage);
      solver.solve();
      renderSolution();
    });

    resetButton.addActionListener(e -> { // Resets the maze 
      setImage(originalImage);
      markedImage = null;
      startingPoint = new int[]{0, 0};
      endingPoint = new int[]{0, 0};
    });

    buttonPanel.add(solveButton);
    buttonPanel.add(resetButton);
    buttonPanel.add(changeMazeImage);
    window.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Displays an image in a window using a JFrame.
   */
  private void renderImage() { 
    if (imagePanel != null) { // Removes the previous image if it exists
      window.remove(imagePanel);
    }

    imagePanel = new JPanel();
    JLabel originalLabel = new JLabel();
    JLabel processedLabel = new JLabel();
    JLabel markedLabel = new JLabel();

    imagePanel.setBackground(Color.RED);
    originalLabel.setIcon(new ImageIcon(originalImage));
    
    imagePanel.add(originalLabel);
    if (markedImage != null) {
      markedLabel.setIcon(new ImageIcon(markedImage));
      imagePanel.add(markedLabel);
      System.out.println("Marked Image");
    } else {
      processedLabel.setIcon(new ImageIcon(processedImage));
      imagePanel.add(processedLabel);
      System.out.println("Processed Image");
    }

    window.add(imagePanel, BorderLayout.NORTH);
    renderButtons();
    promptSelectPoints();
    window.repaint();
    window.revalidate();
  }

  private void promptSelectPoints() {
    JPanel selectPoints = new JPanel(); 
    JSlider startingX = new JSlider(0, processedImage.getWidth() - 1, startingPoint[0]);
    JSlider startingY = new JSlider(0, processedImage.getHeight() - 1, startingPoint[1]);
    JSlider endingX = new JSlider(0, processedImage.getWidth() - 1, endingPoint[0]);
    JSlider endingY = new JSlider(0, processedImage.getHeight() - 1, endingPoint[1]);

    selectPoints.add(startingX);
    selectPoints.add(startingY);
    selectPoints.add(endingX);
    selectPoints.add(endingY);
    window.add(selectPoints, BorderLayout.CENTER);

    startingX.addChangeListener(e -> {
      startingPoint[0] = startingX.getValue();
      renderSelectedPoints();
    });

    startingY.addChangeListener(e -> {
      startingPoint[1] = startingY.getValue();
      renderSelectedPoints();
    });

    endingX.addChangeListener(e -> {
      endingPoint[0] = endingX.getValue();
      renderSelectedPoints();
    });

    endingY.addChangeListener(e -> {
      endingPoint[1] = endingY.getValue();
      renderSelectedPoints();
    });
  }

  private void renderSelectedPoints() {
    markedImage = new BufferedImage(processedImage.getWidth() + 10, processedImage.getHeight() + 10, BufferedImage.TYPE_INT_ARGB);
    Graphics g = markedImage.getGraphics();
    g.drawImage(processedImage, 0, 0, null);
    g.dispose(); 
    
    for (int x = startingPoint[0]; x < startingPoint[0] + 10; x++) {
      for (int y = startingPoint[1]; y < startingPoint[1] + 10; y++) {
        markedImage.setRGB(x, y, Color.GREEN.getRGB());
      }
    }

    for (int x = endingPoint[0]; x < endingPoint[0] + 10; x++) {
      for (int y = endingPoint[1]; y < endingPoint[1] + 10; y++) {
        markedImage.setRGB(x, y, Color.RED.getRGB());
      }
    }

    markedImage.setRGB(startingPoint[0], startingPoint[1], Color.GREEN.getRGB());
    markedImage.setRGB(endingPoint[0], endingPoint[1], Color.RED.getRGB());

    System.out.println("Starting Point: " + startingPoint[0] + " , " + startingPoint[1] + " Ending Point: " + endingPoint[0] + " , " + endingPoint[1]);

    renderImage(); 
  }
  
  /**
   * Renders the solution to the maze 
   */
  private void renderSolution() {
    
  }

  private void renderImage(BufferedImage img) {
    testFrame.remove(testLabel);
    testLabel = new JLabel(); 
    testLabel.setIcon(new ImageIcon(img));
    testFrame.add(testLabel);
    testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    testFrame.repaint();
    testFrame.revalidate();
    testFrame.setVisible(true);
  }

  
}
