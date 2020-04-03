import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.FontStyle;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldEnd;
import javalib.worldimages.WorldImage;

import java.awt.*;
import java.util.Queue;
import java.util.*;

// represents a maze world
public class MazeWorld extends World {
    // nodes in the board
    ArrayList<ArrayList<Node>> nodes = initNodes();

    // world size in nodes
    static int WORLDHEIGHT = 15;
    static int WORLDWIDTH  = 25;

    // sizes of edges in pixels
    static int EDGEHEIGHT = 600 / WORLDHEIGHT;
    static int EDGEWIDTH = 1000 / WORLDWIDTH;

    // random object used for edge weights
    Random random = new Random();

    // all edges in the world
    ArrayList<Edge> allEdges = initEdges();

    // which searching algorithm to be run on-tick
    // "bfs" - breadth-first. "dfs" - depth-first
    String tickSearch = "";

    // worklist for the bfs
    Queue<Node> breadthFirstWorkList = new LinkedList<Node>();

    // worklist for the dfs
    Stack<Node> depthFirstWorkList = new Stack<Node>();

    // hashmap to reconstruct the path from
    HashMap<String, Edge> cameFromEdge = new HashMap<String, Edge>();

    // The player of this world
    Player player = new Player(MazeUtils.getStart(this.nodes));

    // are the visited paths to be drawn?
    static boolean PATHVIS = true;

    // 0 for no direction bias, 1 for vertical, 2 for horizontal
    int bias = 0;

    // if true, prevent a new search from being started
    boolean newSearchBlock = false;

    // edges in this maze
    ArrayList<Edge> edges = initMaze();

    // the path from beginning to end of this maze
    ArrayList<Node> path = depthFirstSearch();


    // create a 2D ArrayList of Nodes based on world size
    ArrayList<ArrayList<Node>> initNodes() {
        ArrayList<ArrayList<Node>> result = new ArrayList<ArrayList<Node>>();

        for (int x = 0; x < WORLDWIDTH; x += 1) {
            result.add(new ArrayList<Node>());
            for (int y = 0; y < WORLDHEIGHT; y += 1) {
                Node n = new Node(x, y);
                result.get(x).add(n);
            }
        }
        return result;
    }

    // create an ArrayList of Edges with random weights between all of this world's nodes
    ArrayList<Edge> initEdges() {
        ArrayList<Edge> result = new ArrayList<Edge>();

        int width = nodes.size();
        int height = nodes.get(0).size();

        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                Node n = nodes.get(x).get(y);
                // if there is a node to the right of n
                if (x < width - 1) {
                    // initial random weight
                    int valr = this.random.nextInt(1000000);
                    if (this.bias == 2) {
                        // this edge will be chosen first with weight 0
                        valr = 0;
                    }
                    Node right = nodes.get(x + 1).get(y);
                    Edge e = new Edge(n, right, valr);
                    result.add(e);
                }
                // if there is a node below n
                if (y < height - 1) {
                    // initial random weight
                    int vald = this.random.nextInt(1000000);
                    if (this.bias == 1) {
                        // this edge will be chosen first with weight 0
                        vald = 0;
                    }
                    Node down = nodes.get(x).get(y + 1);
                    Edge e = new Edge(n, down, vald);
                    result.add(e);
                }
            }
        }
        return result;
    }

    // return an ArrayList<Edge> of edges in the maze
    ArrayList<Edge> initMaze() {
        HashMap<String, String> representatives = new HashMap<String, String>();
        ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
        ArrayList<Edge> worklist = this.allEdges;
        // sort edges by weight
        MazeUtils.sort(worklist);
        // initialize the HashMap
        initRepresentatives(representatives, this.nodes);

        int nextIdx = 0;
        while (MazeUtils.moreThanOneTree(representatives)) {
            Edge next = worklist.get(nextIdx);
            if (MazeUtils.findRoot(representatives, next.n1.toString()).equals(
                    MazeUtils.findRoot(representatives, next.n2.toString()))) {
                // DO NOTHING
            }
            else {
                edgesInTree.add(next);
                MazeUtils.reset(representatives,
                        MazeUtils.findRoot(representatives, next.n1.toString()),
                        MazeUtils.findRoot(representatives, next.n2.toString()));
            }
            nextIdx += 1;
        }
        // connect the nodes in the selected edges
        for (Edge e : edgesInTree) {
            MazeUtils.connect(e);
        }
        return edgesInTree;
    }

    // return a HashMap where each node in given ArrayList is mapped to itself
    void initRepresentatives(HashMap<String, String> h, ArrayList<ArrayList<Node>> nodes) {
        h.clear();
        for (ArrayList<Node> l : nodes) {
            for (Node n : l) {
                String asString = n.toString();
                h.put(asString, asString);
            }
        }
    }


    // draw the world
    public WorldScene makeScene() {
        WorldScene bg = new WorldScene(1000, 600);

        return worldImageHelp(bg, 0, MazeWorld.WORLDWIDTH);
    }

    // draw cells and edges
    WorldScene worldImageHelp(WorldScene bg, int min, int max) {
        for (ArrayList<Node> l : nodes) {
            for (Node n : l) {
                if (n.x >= min && n.x <= max) {
                    bg.placeImageXY(n.nodeImage(), n.getX(), n.getY());
                }
            }
        }
        //       ArrayList<Edge> allEdges = initializeEdges();
        allEdges.removeAll(edges);
        for (Edge e : allEdges) {
            if (e.n1.x >= min && e.n1.x <= max) {
                bg.placeImageXY(e.edgeImage(),
                        (e.n1.getX() + e.n2.getX()) / 2,
                        (e.n1.getY() + e.n2.getY()) / 2);
            }
        }
        bg.placeImageXY(player.playerImage(), player.loc.getX(), player.loc.getY());
        return bg;
    }

    // EFFECT: handle key events
    public void onKeyEvent(String s) {
        // reset the maze
        if (s.equals("r")) {
            this.nodes = initNodes();
            this.allEdges = initEdges();

            this.breadthFirstWorkList.clear();
            this.depthFirstWorkList.clear();
            this.cameFromEdge.clear();
            this.player = new Player(MazeUtils.getStart(this.nodes));
            this.edges = initMaze();
            this.newSearchBlock = false;
            this.tickSearch = "";
            MazeWorld.PATHVIS = true;
            //            this.path = depthFirstSearch();
        }
        // start breadth-first search
        if (s.equals("b") && !this.newSearchBlock) {
            this.newSearchBlock = true;
            breadthFirstWorkList.add(MazeUtils.getStart(this.nodes));
            this.tickSearch = "bfs";
        }
        // start depth-first search
        if (s.equals("d") && !this.newSearchBlock) {
            this.newSearchBlock = true;
            depthFirstWorkList.add(MazeUtils.getStart(this.nodes));
            this.tickSearch = "dfs";
        }
        // move the player up one
        if (s.equals("up")) {
            player.moveUp(this.path);
        }
        // move the player down one
        if (s.equals("down")) {
            player.moveDown(this.path);
        }
        // move the player left one
        if (s.equals("left")) {
            player.moveLeft(this.path);
        }
        // move the player right one
        if (s.equals("right")) {
            player.moveRight(this.path);
        }
        // toggle path visibility
        if (s.equals("t")) {
            MazeWorld.PATHVIS = !MazeWorld.PATHVIS;
        }
        // cycle through the direction biases
        if (s.equals("w")) {
            int val = this.bias;
            val = (val + 1) % 3;
            this.bias = val;
            onKeyEvent("r");
        }

    }

    // EFFECT: advance the world one tick
    public void onTick() {
        // run one tick of breadth first search
        if (this.tickSearch.equals("bfs")  && !breadthFirstWorkList.isEmpty()) {
            Node next = breadthFirstWorkList.poll();
            if (next.isVisited) {
                // DO NOTHING
            }
            else if (next.isTarget()) {
                reconstruct(cameFromEdge, next);
            }
            else {
                for (Node n : next.getNeighbors()) {
                    if (!n.isVisited) {
                        breadthFirstWorkList.add(n);
                        Edge temp = new Edge(next, n, 0);
                        cameFromEdge.put(n.toString(), temp);
                    }
                }
            }
            next.isVisited = true;
        }
        // run one tick of depth first search
        if (this.tickSearch.equals("dfs")  && !depthFirstWorkList.isEmpty()) {
            Node next = depthFirstWorkList.pop();
            if (next.isVisited) {
                // DO NOTHING
            }
            else if (next.isTarget()) {
                reconstruct(cameFromEdge, next);
            }
            else {
                for (Node n : next.getNeighbors()) {
                    if (!n.isVisited) {
                        depthFirstWorkList.add(n);
                        Edge temp = new Edge(next, n, 0);
                        cameFromEdge.put(n.toString(), temp);
                    }
                }
            }
            next.isVisited = true;
        }
    }

    // EFFECT: set all nodes' path field in the path to true
    void reconstruct(HashMap<String, Edge> h, Node n) {
        n.isPath = true;
        Edge e = h.get(n.toString());
        Node prev = e.n1;
        while (!prev.isStart()) {
            prev.isPath = true;
            e = h.get(prev.toString());
            prev = e.n1;
        }
        prev.isPath = true;
    }

    // ends the world if the algorithms or player have solved the maze
    public WorldEnd worldEnds() {
        // Solved maze WorldImage
        WorldImage solved = new TextImage("MAZE SOLVED", 48, FontStyle.BOLD, Color.RED);

        // if the first node of the maze is in the path to the end,
        // a path has been made to the end of the maze
        if (MazeUtils.getStart(this.nodes).isPath) {
            WorldScene w = this.makeScene();
            w.placeImageXY(solved, 500, 300);
            return new WorldEnd(true, w);
        }

        // reached end WorldImage
        WorldImage score = new TextImage(this.player.getScore(), 30, Color.RED);
        WorldImage end = new TextImage("YOU WON", 48, FontStyle.BOLD, Color.RED);

        // if the player has reached the end, end the world
        if (this.player.loc.isTarget()) {
            WorldScene w = this.makeScene();
            w.placeImageXY(end, 500, 275);
            w.placeImageXY(score, 500, 375);
            return new WorldEnd(true, w);
        }
        // otherwise, do not end the world
        WorldScene w2 = this.makeScene();
        return new WorldEnd(false, w2);
    }

    // Find the path to the end using depth-first search
    ArrayList<Node> depthFirstSearch() {
        HashMap<String, Edge> cameFromEdge = new HashMap<String, Edge>();
        Stack<Node> worklist = new Stack<Node>();
        ArrayList<Node> result = new ArrayList<Node>();

        worklist.add(MazeUtils.getStart(this.nodes));
        while (!worklist.isEmpty()) {
            Node next = worklist.pop();
            if (next.hiddenVisited) {
                // DO NOTHING
            }
            else if (next.isTarget()) {
                result = MazeUtils.reconstruct(cameFromEdge, next);
            }
            else {
                for (Node n : next.getNeighbors()) {
                    if (!n.hiddenVisited) {
                        worklist.add(n);
                        Edge temp = new Edge(next, n, 0);
                        cameFromEdge.put(n.toString(), temp);
                    }
                }
            }
            next.hiddenVisited = true;
        }
        return result;
    }
}
