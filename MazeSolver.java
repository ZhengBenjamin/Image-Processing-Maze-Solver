import java.awt.image.*;
import java.util.*;

public class MazeSolver {

  private class Node{
    private int x;
    private int y;
    private ArrayList<Node> neighbors = new ArrayList<>();
    
    /**
     * Constructor for the Node class.
     * @param x x-coordinate of the node.
     * @param y y-coordinate of the node.
     */
    public Node(int x, int y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Constructor for the Node class.
     * @param x x-coordinate of the node.
     * @param y y-coordinate of the node.
     */
    public Node(int x, int y, Node neighbor) {
      this.x = x;
      this.y = y;
      addNeighbor(neighbor);
    }

    /**
     * Adds a neighbor to the node.
     * @param neighbor
     */
    public final void addNeighbor(Node neighbor) {
      neighbors.add(neighbor);
      neighbors.remove(null);
    }

    /**
     * Removes a neighbor from the node.
     * @param neighbor The neighbor to remove.
     */
    public void removeNeighbor(Node neighbor) {
      if (neighbors.contains(neighbor)) {
        neighbors.remove(neighbor);
      }
    }

    /**
     * Gets node's neighbors.
     * @return
     */
    public ArrayList<Node> getNeighbors() {
      return neighbors;
    }

    /**
     * Gets the x-coordinate of the node.
     * @return x-coordinate of the node.
     */
    public int getX() {
      return x;
    }

    /**
     * Gets the y-coordinate of the node.
     * @return y-coordinate of the node.
     */
    public int getY() {
      return y;
    }

  }
  
  private BufferedImage img;
  private int pixelSize; 
  private int[] startingPoint;
  private int[] endingPoint;
  private HashSet<Node> nodes = new HashSet<>(); 
  private Queue<Node> queue = new LinkedList<>();

  /**
   * Constructor for the MazeSolver class.
   * @param img The image to solve.
   * @param pixelSize The size of the pixels when solving the maze.
   * @param startingPoint The starting point of the maze.
   * @param endingPoint The ending point of the maze.
   */
  public MazeSolver(BufferedImage img, int pixelSize, int[] startingPoint, int[] endingPoint) {
    this.img = img;
    this.pixelSize = pixelSize;
    this.startingPoint = startingPoint;
    this.endingPoint = endingPoint;

    generateNodes(); 
    validateNodes(); 
  }

  /**
   * Generates nodes starting from the starting point.
   */
  private void generateNodes() {
    Node start = new Node(startingPoint[0], startingPoint[1]);
    System.out.println("StartingNode: " + Integer.toString(startingPoint[0]) + ", " + Integer.toString(startingPoint[1]));
    start.addNeighbor(genPosXNodes(startingPoint[0], startingPoint[1], start));
    start.addNeighbor(genNegXNodes(startingPoint[0], startingPoint[1], start));
    nodes.add(start);
    
    //Now that all nodes have been generated in the X direction, we can generate nodes in the Y direction
    HashSet<Node> horizontalNodes = new HashSet<>(); 
    for (Node node : nodes) {
      horizontalNodes.add(node);
    }
    
    for (Node node : horizontalNodes) {
      node.addNeighbor(genPosYNodes(node.getX(), node.getY(), node));
      node.addNeighbor(genNegYNodes(node.getX(), node.getY(), node));
    }
    
    System.out.println(nodes.size());
    System.out.println(Integer.toString(img.getWidth()) + Integer.toString(img.getHeight()) + " Tot Pixels = " + (img.getWidth() * img.getHeight()));
  }

  /**
   * Validates nodes by checking if they are on a wall.
   */
  private void validateNodes() {

    HashSet<Node> validatedNodes = new HashSet<>(); //Nodes that are valid
    
    for (Node node : nodes) { 
      validatedNodes.add(node);
    }

    for (Node node : nodes) { //Check pixels within node to see if they contain a wall
      boolean valid = true; 

        for (int x = node.getX(); x < node.getX() + pixelSize; x++) {
          for (int y = node.getY(); y < node.getY() + pixelSize; y++) {
            System.out.println("Checking pixel at: " + Integer.toString(x) + " , " + Integer.toString(y) + ": " + Integer.toString(img.getRGB(x, y)));
            if (img.getRGB(x, y) != -1) {
              System.out.println(false);
              valid = false;
              break;
            }
          }
        }

        if (!valid) { //If the node is not valid, remove it from other node's neighbors + remove it from the list of nodes
          node.getNeighbors().forEach(neighbor -> neighbor.removeNeighbor(node));
          validatedNodes.remove(node);
        }        
    }

    nodes = validatedNodes; //Copy the validated nodes back to the original list of nodes
  }
  
  /**
   * Generates nodes in the positive X direction.
   * @param x The x-coordinate of the node.
   * @param y The y-coordinate of the node.
   * @param neighbor The neighbor of the node.
   * @return The node generated.
   */
  private Node genPosXNodes(int x, int y, Node neighbor) {
      if (x + pixelSize < img.getWidth() - 1) {
        Node node = new Node(x + pixelSize, y);
        node.addNeighbor(neighbor);
        node.addNeighbor(genPosXNodes(x + pixelSize, y, node));
        nodes.add(node);
        System.out.println("xPos Added node at: " + Integer.toString(x + pixelSize) + " , " + Integer.toString(y));
        return node; 
      } else {
        return null;
      }
    }

    /**
     * Generates nodes in the negative X direction.
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param neighbor The neighbor of the node.
     * @return The node generated.
     */
    private Node genNegXNodes(int x, int y, Node neighbor) {
      if (x - pixelSize >= 0) {
        Node node = new Node(x - pixelSize, y);
        node.addNeighbor(neighbor);
        node.addNeighbor(genNegXNodes(x - pixelSize, y, node));
        nodes.add(node);
        System.out.println("xNeg Added node at: " + Integer.toString(x - pixelSize) + " , " + Integer.toString(y));
        return node; 
      } else {
        return null;
      }
    }

    /**
     * Generates nodes in the positive Y direction.
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param neighbor The neighbor of the node.
     * @return The node generated.
     */
    private Node genPosYNodes(int x, int y, Node neighbor) {
      if (y + pixelSize < img.getHeight() - 1) {
        Node node = new Node(x, y + pixelSize);
        node.addNeighbor(neighbor);
        node.addNeighbor(genPosYNodes(x, y + pixelSize, node));
        nodes.add(node);
        System.out.println("yPos Added node at: " + Integer.toString(x) + " , " + Integer.toString(y + pixelSize));
        return node; 
      } else {
        return null;
      }
    }

    /**
     * Generates nodes in the negative Y direction.
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param neighbor The neighbor of the node.
     * @return The node generated.
     */
    private Node genNegYNodes(int x, int y, Node neighbor) {
      if (y - pixelSize >= 0) {
        Node node = new Node(x, y - pixelSize);
        node.addNeighbor(neighbor);
        node.addNeighbor(genNegYNodes(x, y - pixelSize, node));
        nodes.add(node);
        System.out.println("yNeg Added node at: " + Integer.toString(x) + " , " + Integer.toString(y - pixelSize));
        return node; 
      } else {
        return null;
      }
    }
}
