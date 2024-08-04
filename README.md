### Description of the Program

This program is designed for solving mazes through image processing techniques. It is composed of two main classes: `ImageProcessing` and `MazeSolver`. Additionally, there's a `UI` class that provides a graphical interface for interacting with the program.

#### ImageProcessing Class

The `ImageProcessing` class handles various image processing tasks necessary for preparing the maze image for the solving algorithm. Here are the main functions performed by this class:

1. **Image Processing Pipeline**: Processes an input image through several stages including grayscale conversion, Gaussian blurring (both 3x3 and 5x5), resizing, and edge detection.
2. **Grayscale Conversion**: Converts a color image to grayscale to simplify further processing.
3. **Gaussian Blur**: Applies Gaussian blur filters of different kernel sizes (3x3 and 5x5) to the image to reduce noise and detail.
4. **Edge Detection**: Detects edges in the image using a custom algorithm based on intensity differences and applies a threshold to highlight significant edges.
5. **Contrast Detection**: Identifies high-contrast areas in the image by comparing pixel values to the average pixel intensity and binarizes the image.
6. **Image Resizing**: Resizes the image to ensure it fits within a specified dimension while maintaining aspect ratio.

![Screenshot 2024-08-03 at 10 16 20 PM](https://github.com/user-attachments/assets/ea4e65ef-55a8-41fa-b3fc-831f5c603414)
![Screenshot 2024-08-03 at 10 58 17 PM](https://github.com/user-attachments/assets/88f85538-472a-436f-819d-d26d53ac0049)

#### MazeSolver Class

The `MazeSolver` class is responsible for interpreting the processed image as a maze and finding a path from the start to the end point. The key components and processes in this class include:

1. **Node Class**: Represents a point in the maze, with coordinates and a list of neighboring nodes.
2. **Node Generation**: Generates nodes at regular intervals throughout the maze image based on the pixel size.
3. **Node Validation**: Ensures nodes are placed on valid paths (non-wall areas) by checking pixel values within the node's area.
4. **Neighbor Assignment**: Sets neighboring relationships between nodes based on their proximity and connectivity in the maze.
5. **Endpoint Identification**: Determines the start and end nodes in the maze based on provided coordinates.
6. **Pathfinding**: Uses a modified breadth-first search (BFS) algorithm to find a path from the start node to the end node, reconstructing the path once found.


![Screenshot 2024-08-04 at 3 33 18 PM](https://github.com/user-attachments/assets/99fef7c8-f887-4308-8da8-de63ab0f4510)
![Screenshot 2024-08-04 at 3 48 50 PM](https://github.com/user-attachments/assets/406c0088-ff4f-49bf-967a-7e941350bb8e)
![Screenshot 2024-08-04 at 3 47 57 PM](https://github.com/user-attachments/assets/48bb182b-fa6b-4a87-bfca-d0d03974e2c7)
![Screenshot 2024-08-04 at 3 46 36 PM](https://github.com/user-attachments/assets/fcb83dc0-c9c4-4ce8-8585-3843e3ebf884)
![Screenshot 2024-08-04 at 3 43 00 PM](https://github.com/user-attachments/assets/75d5ca17-b2c9-403f-a395-6e1bccdc2ba8)

#### How to Run

Compile and run `Main.java` to start the program. 
