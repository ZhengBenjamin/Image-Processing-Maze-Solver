import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics; // Add this import statement
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import javax.swing.*;

public class UI {
  
  private JFrame window = new JFrame();
  private JFrame solutionFrame; 
  private ImageProcessing imageProcessor = new ImageProcessing();
  private MazeSolver solver;

  private BufferedImage originalImage;
  private BufferedImage processedImage; 
  private BufferedImage markedImage;

  private JPanel images; // Panel for images (top)
  private JPanel markings; // Panel for marking options (middle)
  private JPanel buttons; // Panel for buttons (bottom)

  private int[] startingPoint = null;
  private int[] endingPoint = null;

  /**
   * Constructor for the newUI class.
   */
  public UI() {
    window = new JFrame("Maze Solver");
    window.setSize(200, 100);
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    renderStartingUI();
    window.setLocationRelativeTo(null);
    window.setVisible(true); 
  }

  private void setImage(BufferedImage img) {
    if (img.getWidth() > 900 || img.getHeight() > 900) {
      this.originalImage = imageProcessor.resize(img, 900);
    } else {
      this.originalImage = img;
    }

    processedImage = imageProcessor.contrastDetect(originalImage);
    window.setSize(originalImage.getWidth() * 2 + 50, originalImage.getHeight() + 150);
    window.setLocationRelativeTo(null);
  }

  // Render Methods 

  /**
   * Renders a button to the user to open an image.
   */
  private void renderStartingUI() {
    buttons = new JPanel();
    JButton openButton = new JButton("Open Image"); 
    openButton.addActionListener(e -> handleOpenImage()); 
    
    buttons.add(openButton);
    window.add(buttons);
  }

  /**
   * Renders both the original image and processed/marked image to the user in a side-by-side format.
   */
  private void renderImage() {
    if (images != null) {
      window.remove(images);
    }

    images = new JPanel();

    JLabel ogImgLabel = new JLabel(new ImageIcon(originalImage)); 
    JLabel processedImgLabel; 
    
    if (markedImage != null) { // If the image has been marked, display the marked image.
      processedImgLabel = new JLabel(new ImageIcon(markedImage)); 
      System.out.println("Marked Image");
    } else {
      processedImgLabel = new JLabel(new ImageIcon(processedImage)); 
    }

    images.add(ogImgLabel);
    images.add(processedImgLabel);

    window.add(images, BorderLayout.NORTH);
    window.repaint();
    window.revalidate();
  }

  private void renderMarkingOptions() {
    markings = new JPanel();
    
    JPanel markingOptions = new JPanel(); 
    JPanel info = new JPanel();
    JPanel buttons = new JPanel();
    JCheckBox useEdgeDetect = new JCheckBox("Use Edge Detection");
    JSlider edgeDetectThreshold = new JSlider(0, 20, 10);
    JButton setEndpoints = new JButton("Set Endpoints");
    JButton reset = new JButton("Reset");
    JButton quickSolve = new JButton("Quick Solve");
    JButton accurateSolve = new JButton("Accurate Solve");
    JLabel detectionMethod = new JLabel("Detection Method: Contrast Detection");
    JLabel threshold = new JLabel("Threshold: 10");

    markings.setLayout(new BoxLayout(markings, BoxLayout.Y_AXIS));
    info.add(detectionMethod);
    markingOptions.add(useEdgeDetect);
    buttons.add(setEndpoints);
    buttons.add(reset);
    buttons.add(quickSolve);
    buttons.add(accurateSolve);
    markings.add(info);
    markings.add(markingOptions);
    markings.add(buttons);

    window.add(markings, BorderLayout.CENTER);

    useEdgeDetect.addActionListener(e -> {
      if (useEdgeDetect.isSelected()) {
        markedImage = imageProcessor.processImage(originalImage, edgeDetectThreshold.getValue());
        markingOptions.add(edgeDetectThreshold);
        info.add(threshold);
        detectionMethod.setText("Detection Method: Edge Detection          ");
      } else {
        markedImage = imageProcessor.contrastDetect(originalImage); 
        detectionMethod.setText("Detection Method: Contrast Detection");
        try {
          markingOptions.remove(edgeDetectThreshold);
          info.remove(threshold);
        } catch (Exception ex) {
        }
      }
      renderImage(); 
    });

    edgeDetectThreshold.addChangeListener(e -> {
      if (edgeDetectThreshold.getValueIsAdjusting() == false) {
        markedImage = imageProcessor.processImage(originalImage, edgeDetectThreshold.getValue());
        renderImage(); 
      }
    });

    quickSolve.addActionListener(e -> handleSolve()); 
    accurateSolve.addActionListener(e -> handleAccurateSolve());
    setEndpoints.addActionListener(e -> renderSetEndpoints());
    reset.addActionListener(e -> handleReset());
  }

  private void renderSetEndpoints() {
    JFrame selectPoints = new JFrame();
    JLabel image = new JLabel(new ImageIcon(originalImage));
    JLabel instructions = new JLabel("Select the starting point");

    markedImage = null;
    startingPoint = null;
    endingPoint = null;

    selectPoints.setSize(originalImage.getWidth(), originalImage.getHeight() + 100);
    selectPoints.add(image, BorderLayout.NORTH);
    selectPoints.add(instructions, BorderLayout.SOUTH);
    selectPoints.setLocationRelativeTo(null);
    selectPoints.setVisible(true);

    image.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (startingPoint == null) {
          try {
            startingPoint = new int[2];
            startingPoint[0] = e.getX();
            startingPoint[1] = e.getY();
            instructions.setText("Select the ending point");
            markedImage = markImage(processedImage, startingPoint[0], startingPoint[1], "start");
            image.setIcon(new ImageIcon(markImage(originalImage, startingPoint[0], startingPoint[1], "start")));
            selectPoints.repaint();
            selectPoints.revalidate();
          } catch (Exception ex) {
            renderPopUp("Invalid point selected / Too close to edges.");
          }
        } else {
          try {
            endingPoint = new int[2];
            endingPoint[0] = e.getX();
            endingPoint[1] = e.getY();
            markedImage = markImage(markedImage, endingPoint[0], endingPoint[1], "end");
            renderImage();
            selectPoints.dispose();
          } catch (Exception ex) {
            renderPopUp("Invalid point selected / Too close to edges.");
          }
        }
      }
    });
  }


  /**
   * Renders a pop-up message to the user with a message; 
   * @param message The message to be displayed.
   */
  private void renderPopUp(String message) {
    JOptionPane.showMessageDialog(window, message);
  }

  // Event Handlers 

  /**
   * Handles the opening of an iamge.
   */
  private void handleOpenImage() { 
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.showOpenDialog(null);
    File file = fileChooser.getSelectedFile();
    try {
      BufferedImage img = ImageIO.read(file);
      setImage(img);
      renderImage(); 
      renderMarkingOptions(); 

      window.remove(buttons);
      window.repaint();
      window.revalidate();
    } catch (IOException ex) {
      renderPopUp("Unable to load image / Invalid image format.");
    }
  }

  private void handleReset() {
    window.dispose();
    window = new JFrame("Maze Solver"); 
    startingPoint = null;
    endingPoint = null;
    markedImage = null;
    originalImage = null;
    processedImage = null;
    window.setSize(200, 100);
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    renderStartingUI();
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  /**
   * Calls MazeSolver to solve the maze starting with a pixel size of 3.
   */
  private void handleSolve() {
    if (startingPoint == null || endingPoint == null) {
      renderPopUp("Please select both a starting and ending point.");
      return;
    }

    solutionFrame = new JFrame("Solving... This might take a while");
    solutionFrame.setSize(400, 0);
    solutionFrame.setLocationRelativeTo(null);
    solutionFrame.setVisible(true);

    int[][] solutionCoords = null; 
    int pixelSize = 3; 

    while (solutionCoords == null && pixelSize >= 1) {
      solver = new MazeSolver(processedImage, pixelSize, startingPoint, endingPoint);
      solutionCoords = solver.solve(); 
      pixelSize -= 1; 
    }
    
    try {
      renderSolution(solver.solve());
    } catch (NullPointerException ex) {
      renderPopUp("No solution found.");
      solutionFrame.dispose();
    }   
  }

  /**
   * Calls MazeSolver to solve the maze with pixel size of 1.
   */
  private void handleAccurateSolve() {
    if (startingPoint == null || endingPoint == null) {
      renderPopUp("Please select both a starting and ending point.");
      return;
    } 

    solutionFrame = new JFrame("Solving... Finding the most accurate path takes longer than the quick solve.");
    solutionFrame.setSize(700, 0);
    solutionFrame.setLocationRelativeTo(null);
    solutionFrame.setVisible(true);

    solver = new MazeSolver(processedImage, 1, startingPoint, endingPoint);

    try {
      renderSolution(solver.solve());
    } catch (NullPointerException ex) {
      renderPopUp("No solution found.");
    }
  }

  /**
   * Renders the solution to the maze 
   */
  private void renderSolution(int[][] solutionCoords) {
    BufferedImage solutionImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = solutionImage.getGraphics();
    g.drawImage(originalImage, 0, 0, null);
    g.dispose();

    for (int[] coord : solutionCoords) {
      for (int x = coord[0]; x < coord[0] + 3; x++) {
        for (int y = coord[1]; y < coord[1] + 3; y++) {
          solutionImage.setRGB(x, y, Color.BLUE.getRGB());
        }
      }
    }

    solutionFrame.dispose();
    solutionFrame = new JFrame();
    JLabel solutionLabel = new JLabel(new ImageIcon(solutionImage));

    solutionFrame.setSize(originalImage.getWidth(), originalImage.getHeight());
    solutionFrame.add(solutionLabel);
    solutionFrame.setLocationRelativeTo(null);
    solutionFrame.setVisible(true);
  }

  /**
   * Helper method to mark a point on an image.
   * @param img The image to mark.
   * @param x The x-coordinate of the point to mark.
   * @param y The y-coordinate of the point to mark.
   * @param pointType The type of point to mark.
   * @return The marked image.
   */
  private BufferedImage markImage(BufferedImage img, int x, int y, String pointType) {
    BufferedImage markedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = markedImage.createGraphics();
    g.drawImage(img, 0, 0, null);
    g.dispose(); 

    int color;

    switch (pointType) {
      case "start" -> color = Color.GREEN.getRGB();
      case "end" -> color = Color.RED.getRGB();
      default -> throw new IllegalArgumentException("Invalid point type.");
    }

    for (int xPix = x - 2; xPix <= x + 2; xPix++) {
      for (int yPix = y - 2; yPix <= y + 2; yPix++) {
        markedImage.setRGB(xPix, yPix, color);
      }
    }

    return markedImage;
  }
}