import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

import java.awt.*;

// to represent a weighted edge between two nodes
public class Edge {
    // nodes connected
    Node n1;
    Node n2;
    // weight of this edge
    int weight;

    Edge(Node a, Node b, int weight) {
        this.n1 = a;
        this.n2 = b;
        this.weight = weight;
    }

    // draw this edge
    WorldImage edgeImage() {
        Color c = new Color(102, 102, 102);
        if (this.n1.getX() == this.n2.getX()) {
            return new RectangleImage(MazeWorld.EDGEWIDTH, 2,
                    OutlineMode.SOLID, c);
        }
        else {
            return new RectangleImage(2, MazeWorld.EDGEWIDTH,
                    OutlineMode.SOLID, c);
        }
    }
}