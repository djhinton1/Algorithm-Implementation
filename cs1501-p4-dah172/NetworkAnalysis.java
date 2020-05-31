import java.io.*;
import java.util.*;

public class NetworkAnalysis {
    public static GraphedNetwork g;
    public static Scanner scanner;
    public static DijkstraAllPairsSP shortestPaths;

    public static Edge makeEdge(String[] edgeInfo) {
        int from = Integer.parseInt(edgeInfo[0]);
        int to = Integer.parseInt(edgeInfo[1]);
        String type = edgeInfo[2];
        int capacity = Integer.parseInt(edgeInfo[3]);
        int length = Integer.parseInt(edgeInfo[4]);
        return new Edge(from, to, capacity, length, type);
    }

    public static void loadFromFile(String filename) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            int V = Integer.parseInt(in.readLine());
            g = new GraphedNetwork(V);
            String line;
            while ((line = in.readLine()) != null) {
                String[] edgeInfo = line.split(" ");
                Edge e = makeEdge(edgeInfo);
                g.addEdge(e);
                String tmpVertex = edgeInfo[0];
                edgeInfo[0] = edgeInfo[1];
                edgeInfo[1] = tmpVertex;
                Edge otherE = makeEdge(edgeInfo);
                g.addEdge(otherE);
                shortestPaths = new DijkstraAllPairsSP(g);
            }

        } catch (FileNotFoundException e) {
            System.out.println("No File Found.");
        } catch (IOException e) {
            System.out.println("Loading Error.");
        }
    }

    public static void options() {
        System.out.println("\nWhat would you like to do? ");
        System.out.println("1. Find lowest latency path ");
        System.out.println("2. Determine the if the graph is copper connected");
        System.out.println("3. Determine if network remains connected if any two vertices fail");
        System.out.println("4. Exit");
    }

    public static int getUserOption() {
        System.out.print("Enter option: ");
        String opt = scanner.nextLine();
        char num = opt.charAt(0);
        if(opt.length() == 1) {
            return Character.getNumericValue(num);
        }
        return -1;
    }

    public static void select(int choice) {
        switch(choice) {
            case 1:
                lowestLatency();
                break;
            case 2:
                copperConnected();
                break;
            case 3:
                checkFailVert();
                break;
            case 4:
                scanner.close();
                System.exit(0);
                break;
            default:
                System.out.println("Enter a number between 1 - 4. ");
        }
        System.out.println("\n");
    }

    public static void checkFailVert() {
        int V = g.V();
        if(V <= 3) {
            System.out.println("Too few verticies. ");
            return;
        }
        boolean connected = true;
        for(int v1=0; v1<V; v1++) {
            for(int v2=v1+1; v2<V; v2++) {
                if(!checkFailVert(v1, v2)) {
                    connected = false;
                    System.out.println("Removing " + v1 + " and " + v2 + " would disconnect the graph.");
                    break;
                }
            }
        }
        if(connected) {
            System.out.println("Graph would remain connected.");
        }

    }

    public static boolean checkFailVert(int v1, int v2) {
        boolean[] state = new boolean[g.V()]; // state[v] = is there an s-v path
        Queue<Integer> q = new LinkedList<Integer>();
        state[v1] = true;
        state[v2] = true;
        int begin = 0;
        if(begin == v1) begin = 1;
        if(begin == v2) begin = 2;
        state[begin] = true;
        q.add(begin);

        int count = 1;
        while (!q.isEmpty()) {
            int v = q.remove();
            for (Edge e : g.adj(v)) {
                int w = e.to();
                if (!state[w]) {
                    state[w] = true;
                    count++;
                    q.add(w);
                }
            }
        }
        return count+2 == g.V();
    }



    public static void lowestLatency() {
        boolean validVertices = false;
        while(!validVertices) {
            System.out.print("\nVertex 1: ");
            int v1 = Integer.parseInt(scanner.nextLine());
            System.out.print("Vertex 2: ");
            int v2 = Integer.parseInt(scanner.nextLine());
            validVertices = v1 >= 0 && v2 >= 0;
            lpCalc(v1, v2);
        }
    }

    /*
        Does a BFS on the graph only with copper wires.
     */
    public static void copperConnected() {
        int V = g.V();
        int count = bfs(0);
        if(count == V) {
            System.out.println("Graph is connected via copper wires only.");
        } else {
            System.out.println("Graph is not copper exclusively.");
        }
    }

    // breath first search to identify copper wires
    private static int bfs(int s) {
        boolean[] state = new boolean[g.V()]; // state[v] = is there an s-v path
        Queue<Integer> q = new LinkedList<Integer>();
        state[s] = true;
        q.add(s);
        int count = 1;
        while (!q.isEmpty()) {
            int v = q.remove();
            for (Edge e : g.adj(v)) {
                int w = e.to();
                if (!state[w] && e.type() == "copper") {
                    state[w] = true;
                    count++;
                    q.add(w);
                }
            }
        }
        return count;
    }

    public static void lpCalc(int v1, int v2) {
        if(shortestPaths.hasPath(v1, v2)) {
            System.out.println("\n");
            double minBandwidth = Double.MAX_VALUE;
            for(Edge e : shortestPaths.path(v1, v2)) {
                minBandwidth = Math.min(minBandwidth, e.bandwidth());
                System.out.println(e);
            }
            System.out.println("Avaiable Bandwidth: " + minBandwidth);
        } else {
            System.out.println("No Path Exists.");
        }
    }

    public static void optLoop() {
        int opt = getUserOption();
        while(opt > -1) {
            select(opt);
            options();
            opt = getUserOption();
        }
    }

    public static void menu() {
        options();
        optLoop();
    }

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        String fileName = args[0];
        loadFromFile(fileName);
        menu();
        scanner.close();
    }
}
