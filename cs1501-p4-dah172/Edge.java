/**
 *  Adapted from textbook with following authors:
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */

public class Edge {
    private static final double FLOATING_POINT_EPSILON = 1E-10;
    private final int COPPER_SPEED = 230000000;
    private final int OPTIC_SPEED = 200000000;
    private final int v; // from v
    private final int w; // to w
    private final String type;
    private double length;
    private double bandwidth;
    private double latency;


    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with
     * the given {@code bandwidth}.
     * @param v the tail vertex
     * @param w the head vertex
     * @param bandwidth the bandwidth of the cable
     * @param length the length of the cable
     * @param type the type of cable (optical, copper)
     * @throws IllegalArgumentException if either {@code v} or {@code w}
     *    is a negative integer
     * @throws IllegalArgumentException if {@code bandwidth} is {@code NaN}
     */
    public Edge(int v, int w, double length, double bandwidth, String type) {
        if (v < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (w < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (Double.isNaN(bandwidth)) throw new IllegalArgumentException("bandwidth is NaN");
        this.v = v;
        this.w = w;
        this.length = length;
        this.bandwidth = bandwidth;
        this.type = type;
        if(this.type == "copper"){
          this.latency = length * ((double) 1)/COPPER_SPEED * Math.pow(10, 9);
        } else{
          this.latency = length * ((double) 1)/OPTIC_SPEED * Math.pow(10, 9);
        }
    }

    public int other(int vtx) {
        if      (vtx == v) return w;
        else if (vtx == w) return v;
        else throw new IllegalArgumentException("invalid endpoint");
    }

    /**
     * Returns the tail vertex of the directed edge.
     * @return the tail vertex of the directed edge
     */
    public int from() {
        return v;
    }

    /**
     * Returns the head vertex of the directed edge.
     * @return the head vertex of the directed edge
     */
    public int to() {
        return w;
    }

    /**
     * Returns the length of the cable
     * @return the length of the cable
     */
    public double length() {
        return length;
    }
    /**
     * Returns the bandwidth of the directed edge.
     * @return the bandwidth of the directed edge
     */
    public double bandwidth() {
        return bandwidth;
    }

    /**
     * Returns the latency of the cable.
     * @return the latency of the cable
     */
    public double latency() {
        return latency;
    }

    /**
     * Returns the type of the cable.
     * @return the type of the cable
     */
    public String type() {
        return type;
    }

    /**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    public String toString() {
        return type + " wire: \t" + "[" + v + "] to [" + w + "]"+" with bandwidth " + bandwidth + " mbps.";
    }
}
