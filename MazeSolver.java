import java.awt.image.*;
import java.util.*;

public class MazeSolver {

  private class Node{
    private final int x;
    private final int y;
    private final ArrayList<Node> neighbors = new ArrayList<>();
    
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
      // addNeighbor(neighbor);
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
  private Node startingNode;
  private Node endingNode;
  private ArrayList<Node> nodes = new ArrayList<>(); 
  

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
    setNeighbors();
    validateNodes(); 
    findEndpoints();
  }

  /**
   * Generates nodes starting from the starting point.
   */
  private void generateNodes() {
    Node node = new Node(0, 0);
    System.out.println("Generating Nodes");
    node.addNeighbor(genXNodes(0, 0, node));
    nodes.add(node);
    
    //Now that all nodes have been generated in the X direction, we can generate nodes in the Y direction
    ArrayList<Node> horizontalNodes = new ArrayList<>(); 
    for (Node n : nodes) {
      horizontalNodes.add(n);
    }
    
    for (Node n : horizontalNodes) {
      n.addNeighbor(genYNodes(n.getX(), n.getY(), n));
    }
  }

  /**
   * Validates nodes by checking if they are on a wall.
   */
  private void validateNodes() {

    System.out.println("Validating Nodes");
    // ArrayList<Node> validatedNodes = new ArrayList<>(); //Nodes that are valid
    HashSet<Node> validatedNodes = new HashSet<>();
    
    for (Node node : nodes) { 
      validatedNodes.add(node);
    }

    for (Node node : nodes) { //Check pixels within node to see if they contain a wall
      boolean valid = true; 

        for (int x = node.getX(); x < node.getX() + pixelSize; x++) {
          for (int y = node.getY(); y < node.getY() + pixelSize; y++) {
            System.out.println("Validating Node: " + Integer.toString(x) + " , " + Integer.toString(y));
            if (img.getRGB(x, y) != -1) {
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

    nodes = new ArrayList<>();
    nodes.addAll(validatedNodes); //Copy the validated nodes back to the original list of nodes
  }

  /**
   * Finds the starting and ending nodes in the maze.
   */
  private void findEndpoints() {
    System.out.println("Finding Endpoints");
    for (Node node : nodes) {
      if (Math.abs(node.getX() - startingPoint[0]) <= pixelSize && Math.abs(node.getY() - startingPoint[1]) <= pixelSize) {
        startingNode = node;
      } 

      if (Math.abs(node.getX() - endingPoint[0]) <= pixelSize && Math.abs(node.getY() - endingPoint[1]) <= pixelSize) {
        endingNode = node;
      }

      if (startingNode != null && endingNode != null) {
        break;
      }
    }

    System.out.println("Found starting node: " + Integer.toString(startingNode.getX()) + " , " + Integer.toString(startingNode.getY()));
    System.out.println("Starting Point: " + Integer.toString(startingPoint[0]) + " , " + Integer.toString(startingPoint[1]));
    System.out.println("Found ending node: " + Integer.toString(endingNode.getX()) + " , " + Integer.toString(endingNode.getY()));
    System.out.println("Ending Point: " + Integer.toString(endingPoint[0]) + " , " + Integer.toString(endingPoint[1]));
  }

  /**
   * Called from UI to solve the maze.
   * @return The path from the starting node to the ending node.
   */
  public int[][] solve() {
    Node[] prev = findPath();
    ArrayList<Node> nodePath = reconstructPath(prev);

    if (nodePath != null) {
      int[][] coordPath = new int[nodePath.size()][2];

      for (int i = 0; i < nodePath.size(); i++) {
        coordPath[i][0] = nodePath.get(i).getX();
        coordPath[i][1] = nodePath.get(i).getY();
      }
      
      return coordPath;
    } 

    return null;
  }

  /**
   * Finds the path from the starting node to the ending node.
   * @return The path from the starting node to the ending node.
   */
  private Node[] findPath() {
    Queue<Node> queue = new LinkedList<>(); //Queue to store nodes to visit
    HashSet<Node> visited = new HashSet<>(); //Set to store visited nodes
    queue.add(startingNode); 
    visited.add(startingNode);
    Node[] prev = new Node[nodes.size()]; //Array to store the previous node in the path
    Arrays.fill(prev, null); //Fill the array with nulls

    while (!queue.isEmpty()) { //Modified BFS to find the path
      Node current = queue.remove();
      for (Node neighbor : current.getNeighbors()) {
        if (!visited.contains(neighbor)) {
          System.out.println("Visiting: " + Integer.toString(neighbor.getX()) + " , " + Integer.toString(neighbor.getY()));
          queue.add(neighbor);
          visited.add(neighbor);
          prev[nodes.indexOf(neighbor)] = current;
          if (neighbor.equals(endingNode)) {
            System.out.println("Found Ending Node");
            return prev;
          }
        }
      }
    }

    return prev;
  }

  /**
   * Reconstructs the path from the starting node to the ending node.
   * @param prev The array containing the previous node in the path.
   * @return The path from the starting node to the ending node.
   */
  private ArrayList<Node> reconstructPath(Node[] prev) {
    ArrayList<Node> path = new ArrayList<>(); 
    for (Node at = endingNode; at != null; at = prev[nodes.indexOf(at)]) {
      path.add(at);
    }

    Collections.reverse(path);

    if (path.get(0) == startingNode) {
      return path;
    } else {
      return null;
    }
  }
  
  /**
   * Generates nodes in the positive X direction.
   * @param x The x-coordinate of the node.
   * @param y The y-coordinate of the node.
   * @param neighbor The neighbor of the node.
   * @return The node generated.
   */
  private Node genXNodes(int x, int y, Node neighbor) {
      if (x + pixelSize < img.getWidth() - pixelSize) {
        Node node = new Node(x + pixelSize, y);
        node.addNeighbor(neighbor);
        node.addNeighbor(genXNodes(x + pixelSize, y, node));
        nodes.add(node);
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
  private Node genYNodes(int x, int y, Node neighbor) {
    if (y + pixelSize < img.getHeight() - pixelSize) {
      Node node = new Node(x, y + pixelSize);
      node.addNeighbor(neighbor);
      node.addNeighbor(genYNodes(x, y + pixelSize, node));
      nodes.add(node);
      return node; 
    } else {
      return null;
    }
  }

  private void setNeighbors() {
    Node[] nodeArray = nodes.toArray(Node[]::new); //Converts the list of nodes to an array
    int offsetIndex = (int) Math.floor(img.getHeight() / pixelSize) - 1; //The offset index to get the node to the right of the current node

    for (int i = 0; i < nodeArray.length; i++) {
      Node node = nodeArray[i];
      if (node.getY() != 0 && i + offsetIndex < nodeArray.length) {
        node.addNeighbor(nodeArray[i + offsetIndex]);
        nodeArray[i + offsetIndex].addNeighbor(node);
      }
    }
    
    nodes = new ArrayList<>(Arrays.asList(nodeArray));
    System.out.println("Set Neighbors: num of nodes: " + Integer.toString(nodes.size()));
  }

  /**
   * Helper method to print the neighbors of each node.
   */
  private void printNeighbors() {
    nodes.forEach(node -> System.out.println("Node: " + Integer.toString(node.getX()) + " , " + Integer.toString(node.getY()) + " Neighbors " + Integer.toString(node.getNeighbors().size())));
  }
}
