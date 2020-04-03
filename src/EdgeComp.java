import java.util.Comparator;

//to compare two edges by their edge weights
public class EdgeComp implements Comparator<Edge> {
    // returns negative if e1 comes first, 0 if they are tied,
    // positive if e2 comes first
    public int compare(Edge e1, Edge e2) {
        return e1.weight - e2.weight;
    }
}