import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

// to hold utility methods
public class MazeUtils {
    // EFFECT: Sorts the given ArrayList of edges according to their edge weights
    static void sort(ArrayList<Edge> arr) {
        Collections.sort(arr, new EdgeComp());
    }

    // does the given HashMap contain more than one tree?
    static boolean moreThanOneTree(HashMap<String, String> h) {
        ArrayList<String> parents = new ArrayList<String>();
        Set<String> keySet = h.keySet();
        // get the parents of all nodes
        for (String s : keySet) {
            String parent = MazeUtils.findRoot(h, s);
            parents.add(parent);
        }

        if (parents.size() > 1) {
            String a = parents.get(0);
            for (String s : parents) {
                if (!a.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    // get the root of the tree given string is in
    static String findRoot(HashMap<String, String> h, String s) {
        String val = h.get(s);
        if (val.equals(s)) {
            return s;
        }
        else {
            return findRoot(h, val);
        }
    }

    // EFFECT: set value of key a to b
    static void reset(HashMap<String, String> h, String a, String b) {
        h.put(a, b);
    }

    // EFFECT: connect the two nodes in given edge
    static void connect(Edge e) {
        Node a = e.n1;
        Node b = e.n2;
        if (a.x == b.x && e.n1.y == e.n2.y + 1) {
            e.n1.up = e.n2;
            e.n2.down = e.n1;
        }
        else if (e.n1.x == e.n2.x && e.n1.y == e.n2.y - 1) {
            e.n1.down = e.n2;
            e.n2.up = e.n1;
        }
        else if (e.n1.x == e.n2.x + 1) {
            e.n1.left  = e.n2;
            e.n2.right = e.n1;
        }
        else if (e.n1.x == e.n2.x - 1) {
            e.n1.right = e.n2;
            e.n2.left  = e.n1;
        }
    }

    // Returns the starting node from given ArrayList of Nodes
    static Node getStart(ArrayList<ArrayList<Node>> src) {
        return src.get(0).get(0);
    }

    // return the path of nodes to reach given node through given hashmap
    static ArrayList<Node> reconstruct(HashMap<String, Edge> h, Node n) {
        ArrayList<Node> result = new ArrayList<Node>();
        result.add(n);
        Edge e = h.get(n.toString());
        Node prev = e.n1;
        while (!prev.isStart()) {
            result.add(prev);
            e = h.get(prev.toString());
            prev = e.n1;
        }
        result.add(prev);
        return result;
    }
}
