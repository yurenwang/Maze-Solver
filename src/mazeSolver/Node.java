package mazeSolver;

import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

import java.awt.*;
import java.util.ArrayList;

// to represent a cell in a maze
public class Node {
    // node's coordinates
    int x;
    int y;
    // node's neighbors
    Node up;
    Node down;
    Node left;
    Node right;
    // has this node been visited in the search?
    boolean isVisited = false;
    // has this node been visited in initially calculating the path?
    boolean hiddenVisited = false;
    // is this node in the automatically found path to the target?
    boolean isPath = false;
    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Return this node represented as a string(key)
    public String toKey() {
        return Integer.toString(this.x) +
                Integer.toString(this.y);
    }

    // get the x value of the center of this node
    int getX() {
        return this.x * MazeWorld.EDGEWIDTH + MazeWorld.EDGEWIDTH  / 2;
    }

    // get the x value of the center of this node
    int getY() {
        return this.y * MazeWorld.EDGEHEIGHT + MazeWorld.EDGEHEIGHT / 2;
    }

    // return a WorldImage representation of this node
    WorldImage nodeImage() {
        Color c;
        if (this.isPath) {
            c = new Color(61, 118, 204);
        }
        else if (this.isVisited && MazeWorld.PATHVIS) {
            c = new Color(145, 184, 242);
        }
        else if (this.isTarget()) {
            c = new Color(108, 32, 128);
        }
        else if (this.isStart()) {
            c = new Color(32, 128, 70);
        }
        else {
            c = new Color(192, 192, 192);
        }
        return new RectangleImage(MazeWorld.EDGEWIDTH, MazeWorld.EDGEHEIGHT, OutlineMode.SOLID, c);
    }

    // is this node the start node for the maze?
    boolean isStart() {
        return this.x == 0 & this.y == 0;
    }

    // is this node the end node for the maze?
    boolean isTarget() {
        return this.x == MazeWorld.WORLDWIDTH  - 1 &&
                this.y == MazeWorld.WORLDHEIGHT - 1;
    }

    // Get the neighbors of this node
    ArrayList<Node> getNeighbors() {
        ArrayList<Node> result = new ArrayList<Node>();
        if (this.left instanceof Node) {
            result.add(this.left);
        }
        if (this.up instanceof Node) {
            result.add(this.up);
        }
        if (this.right instanceof Node) {
            result.add(this.right);
        }
        if (this.down instanceof Node) {
            result.add(this.down);
        }
        return result;
    }
}
