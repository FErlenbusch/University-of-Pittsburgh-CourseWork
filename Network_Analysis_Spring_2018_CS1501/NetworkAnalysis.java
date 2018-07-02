import java.util.*;
import java.io.*;
import java.lang.*;


class Vertex {
	ArrayList<Edge> edges;

	public Vertex() {
		edges = new ArrayList<Edge>();
	}
}

class FlowVertex {
	ArrayList<FlowEdge> edges;

	public FlowVertex() {
		edges = new ArrayList<FlowEdge>();
	}
}

class Edge implements Comparable<Edge>{
	int vertex1;
	int vertex2;
	char type;
	int bandwidth;
	int length;
	double latency;
	

	public Edge(int vertex1, int vertex2, String type, int bandwidth, int length) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.type = type.charAt(0);
		this.bandwidth = bandwidth;
		this.length = length;
		
		if (this.type == 'c') {
			this.latency = (double) length / 230000000;
		}
		else {
			this.latency = (double) length / 200000000;
		}
	}

	public Edge(Edge edge) {
		this.vertex1 = edge.vertex1;
		this.vertex2 = edge.vertex2;
		this.type = edge.type;
		this.bandwidth = edge.bandwidth;
		this.length = edge.length;
		this.latency = edge.latency;
	}

	public int findVertex(int current) {
		if (this.vertex1 == current) 	// to determine if edge which end of edge is neighbor.
			return this.vertex2;
		else
			return this.vertex1;
	}

	@Override
    public int compareTo(Edge that) {
        return Double.compare(this.latency, that.latency);
    }

	public void printEdge() {
		System.out.println("Vert1: " + vertex1 + "  Vert2: " + vertex2 + "  Type: " + type
			+ "  Bandwidth: " + bandwidth + "Mbps  Length: " + length + "m  Latency: " + latency);
	}
}

class FlowEdge {
	final int v; 
	final int w;
	final int capacity; 
	int flow;   
	public FlowEdge(int v, int w, int capacity) {
		this.v = v;
		this.w = w;  
		this.capacity = capacity;
		this.flow = 0;
	}

	public FlowEdge(int v, int w, int capacity, int flow) {
		this.v = v;
		this.w = w;  
		this.capacity = capacity;
		this.flow = flow;
	}

	public FlowEdge(FlowEdge e) {
		this.v = e.v;
		this.w  = e.w;
		this.capacity = e.capacity;
		this.flow = e.flow;
	}

	public int other(int vertex) {
		if (vertex == v) 
			return w;
		else if (vertex == w)
			return v;
		else return -1;
	}

	public int residualCapacityTo(int vertex) {
		if (vertex == v) 
			return flow; 
		else if (vertex == w)
			return (capacity - flow);
		else 
			return -1;
		
	}

	public void addResidualFlowTo(int vertex, int delta) {
		if (vertex == v) 
			flow -= delta;
		else if (vertex == w) 
			flow += delta;
	}
}

class Network {
	Vertex[] graph;
	ArrayList<Edge> edges;
	int size;

	public Network(int n) {
		size = n;
		graph = new Vertex[n];
		edges = new ArrayList<Edge>();

		for (int i = 0; i < n; i++) {
			graph[i] = new Vertex();
		}
	}

	public void addEdge(Edge edge) {
		graph[edge.vertex1].edges.add(edge);
		graph[edge.vertex2].edges.add(new Edge(edge));
		edges.add(edge);
	}

	public void printGraph(Vertex[] graph) {
		System.out.println();
		for (int i = 0; i < size; i++) {
			System.out.print("[" + i + "]:  ");
			for (int j = 0; j < graph[i].edges.size(); j++) {
				Edge edge = graph[i].edges.get(j);
				if (i != edge.vertex1) {
					System.out.print("[" + edge.vertex1 + "]");
				}
				else {
					System.out.print("[" + edge.vertex2 + "]");
				}
			}
			System.out.println();
		}
	}

	public ArrayList<Integer> getShortestPath(int start, int destination) {
		ArrayList<Integer> path = new ArrayList<Integer>(); 
		double[] distances = new double[size];
		boolean[] visited = new boolean[size];
		double[] smallest = new double[3];		// [0] vertex w/ smallest latency, [1] latency of that vertex, [2] latency from parent
		int[] paths = new int[size];
		int minBandwidth = Integer.MAX_VALUE;
		int current = start;

		Arrays.fill(distances, Double.MAX_VALUE);
		path.add(destination);
		
		distances[start] = 0;
		paths[start] = -1;
		
		
		while (!visited[destination]) {
			smallest[1] = Double.MAX_VALUE;

			for (Edge edge: graph[current].edges) {
				int vertex = edge.findVertex(current);

				if (!visited[vertex]) {	
					if (edge.latency + smallest[2] < distances[vertex]) {
						distances[vertex] = edge.latency + smallest[2];
						paths[vertex] = current;
					}

					if (distances[vertex] < smallest[1] ) {	
						smallest[0] = vertex;
						smallest[1] = distances[vertex];
					}
				}
			}

			visited[current] = true;

			if ((int) smallest[0] == current) { 
				current = paths[current];
				
				if (current == -1) 		
					break;

				smallest[0] = current;
				smallest[2] = distances[current];

				continue;
			} 

			smallest[2] = smallest[1];
			current = (int) smallest[0];
		}

		current = destination;

		while (current != start) {		
			path.add(paths[current]);

			for (Edge edge: graph[current].edges) {	
				int vertex = edge.findVertex(current);

				if (vertex == paths[current]) {	
					if (edge.bandwidth < minBandwidth)
						minBandwidth = edge.bandwidth;
				}
			}

			current = paths[current];
		}

		path.add(minBandwidth);

		return path;
	}

	public boolean copperConnected() {
		if (new DepthFirstSearch(graph).count == size) 
			return true;
		else 
			return false;
	}

	public boolean vertexFailures() {
        for (int i = 0; i < size; i++) {
            for (int j = (i + 1) % size; j != i; j = (j + 1) % size) {
                Network broken = new Network(size);

                for (Edge edge: edges) {
                    if(!vertexMatch(edge, i, j)) 
                        broken.addEdge(edge);
                }

                if (new DepthFirstSearch(broken.graph, i, j).count != size - 2)
                    return false;
            }
        }

		return true;
	}

    public boolean vertexMatch(Edge edge, int i, int j) {
        if (edge.vertex1 == i || edge.vertex1 == j)
            return true;

        if (edge.vertex2 == i || edge.vertex2 == j)
            return true;

        return false;
    }
}

class FlowNetwork {
    FlowVertex[] adj;
    final int V;
    int E;
    
    
    public FlowNetwork(int V) {
        this.V = V;
        this.E = 0;
        adj = new FlowVertex[V];

        for (int i = 0; i < V; i++) {
            adj[i] = new FlowVertex();
        }
    }

    public FlowNetwork(Network network) {
    	this(network.size); 

    	for (Edge edge: network.edges) {
    		addEdge(new FlowEdge(edge.vertex1, edge.vertex2, edge.bandwidth));
    		addEdge(new FlowEdge(edge.vertex2, edge.vertex1, edge.bandwidth));
    	}
    }

    public void addEdge(FlowEdge e) {
        adj[e.v].edges.add(e);
        adj[e.w].edges.add(e);
        E++;
    }

    public ArrayList<FlowEdge> adj(int v) {
        return adj[v].edges;
    }

    // return list of all edges - excludes self loops
    public ArrayList<FlowEdge> edges() {
        ArrayList<FlowEdge> list = new ArrayList<FlowEdge>();

        for (int v = 0; v < V; v++) {
            for (FlowEdge edge: adj[v].edges) {
                if (edge.v != v)
                    list.add(edge);
            }
        }

        return list;
    }

    public int getBandwidth(int s, int t) {
    	return new FordFulkerson(this, s, t).value;
    }
}

class DepthFirstSearch {
	boolean[] marked;
	int count;

	public DepthFirstSearch(Vertex[] graph) {
		marked = new boolean[graph.length];
		count = 0;
		dfs(graph, 0);
	}

    public DepthFirstSearch(Vertex[] graph, int i, int j) {
        int v = 0;
        marked = new boolean[graph.length];
        count = 0;

        while(v == i || v == j) 
            v++;

        dfsBroken(graph, v);
    }

	public void dfs(Vertex[] graph, int v) {
		count++;
		marked[v] = true;

		for (Edge edge: graph[v].edges) {
			int w = edge.findVertex(v);

			if (!marked[w] && edge.type != 'o') 
				dfs(graph, w);
		}
	}

    public void dfsBroken(Vertex[] graph, int v) {
        count++;
        marked[v] = true;

        for (Edge edge: graph[v].edges) {
            int w = edge.findVertex(v);

            if (!marked[w]) 
                dfsBroken(graph, w);
        }
    }
}

class FordFulkerson {
    final int V;
    boolean[] marked;
    FlowEdge[] edgeTo;
    int value;
  
    public FordFulkerson(FlowNetwork G, int s, int t) {
        V = G.V;

        if (s == t)
        	throw new IllegalArgumentException("Source equals sink");

        value = excess(G, t);

        while (hasAugmentingPath(G, s, t)) {

            int bottle = Integer.MAX_VALUE;

            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }

            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle); 
            }

            value += bottle;
        }
    }

    public boolean inCut(int v)  {
        return marked[v];
    }

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V];
        marked = new boolean[G.V];

        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(s);
        
        marked[s] = true;

        while (!queue.isEmpty() && !marked[t]) {
            int v = queue.poll();

            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);

                if (e.residualCapacityTo(w) > 0) {
                    if (!marked[w]) {
                        edgeTo[w] = e;
                        marked[w] = true;

                        queue.add(w);
                    }
                }
            }
        }

        return marked[t];
    }

    private int excess(FlowNetwork G, int v) {
        int excess = 0;
        for (FlowEdge e : G.adj(v)) {
            if (v == e.v) excess -= e.flow;
            else excess += e.flow;
        }
        return excess;
    }
}

class KruskalMST {          
    ArrayList<Edge> mst = new ArrayList<Edge>();
    double weight;              

    public KruskalMST(Network network) {
        MinPQ pq = new MinPQ();
        UF uf = new UF(network.size);

        for (Edge e : network.edges) {
            pq.insert(e);
        }

        while (!pq.isEmpty() && mst.size() < network.size - 1) {
            Edge edge = pq.delMin();
            int v = edge.vertex1;
            int w = edge.vertex2;

            if (!uf.connected(v, w)) {
                uf.union(v, w);
                mst.add(edge);
                weight += edge.latency;
            }
        }
    }
}

class MinPQ implements Iterable<Edge> {
    private Edge[] pq;
    private int n;
    private Comparator<Edge> comparator;

    public MinPQ(int initCapacity) {

        pq = new Edge[initCapacity + 1];
        n = 0;
    }

    public MinPQ() {
        this(1);
    }

    public MinPQ(int initCapacity, Comparator<Edge> comparator) {
        this.comparator = comparator;
        pq = new Edge[initCapacity + 1];
        n = 0;
    }

    public MinPQ(Comparator<Edge> comparator) {
        this(1, comparator);
    }

    public MinPQ(Edge[] keys) {
        n = keys.length;
        pq = new Edge[keys.length + 1];

        for (int i = 0; i < n; i++) {
            pq[i+1] = keys[i];
        }

        for (int k = n/2; k >= 1; k--) {
            sink(k);
        }

        assert isMinHeap();
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    public Edge min() {
        if (isEmpty()) 
        	throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    private void resize(int capacity) {
        assert capacity > n;
        Edge[] temp = new Edge[capacity];

        for (int i = 1; i <= n; i++) {
            temp[i] = pq[i];
        }

        pq = temp;
    }

    public void insert(Edge x) {
        if (n == pq.length - 1) 
        	resize(2 * pq.length);

        pq[++n] = x;
        swim(n);

        assert isMinHeap();
    }

    public Edge delMin() {
        if (isEmpty()) 
        	throw new NoSuchElementException("Priority queue underflow");

        Edge min = pq[1];

        exch(1, n--);
        sink(1);
        pq[n+1] = null;

        if ((n > 0) && (n == (pq.length - 1) / 4)) 
        	resize(pq.length / 2);

        assert isMinHeap();
        return min;
    }

    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;

            if (j < n && greater(j, j + 1))
            	j++;

            if (!greater(k, j)) 
            	break;

            exch(k, j);
            k = j;
        }
    }

    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Edge>) pq[i]).compareTo(pq[j]) > 0;
        }
        else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
        Edge swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    private boolean isMinHeap() {
        return isMinHeap(1);
    }

    private boolean isMinHeap(int k) {
        if (k > n) 
        	return true;

        int left = 2*k;
        int right = 2*k + 1;

        if (left  <= n && greater(k, left))  
        	return false;

        if (right <= n && greater(k, right)) 
        	return false;

        return isMinHeap(left) && isMinHeap(right);
    }

    public Iterator<Edge> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Edge> {
        private MinPQ copy;

        public HeapIterator() {
            if (comparator == null) 
            	copy = new MinPQ(size());
            else                    
            	copy = new MinPQ(size(), comparator);

            for (int i = 1; i <= n; i++) {
                copy.insert(pq[i]);
            }
        }

        public boolean hasNext() { 
        	return !copy.isEmpty();
        }

        public void remove() { 
        	throw new UnsupportedOperationException();  
        }

        public Edge next() {
            if (!hasNext()) 
            	throw new NoSuchElementException();

            return copy.delMin();
        }
    }
}

class UF {

    private int[] parent; 
    private byte[] rank; 
    private int count; 

    public UF(int n) {
        if (n < 0) 
        	throw new IllegalArgumentException();

        count = n;
        parent = new int[n];
        rank = new byte[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int p) {
        validate(p);

        while (p != parent[p]) {
            parent[p] = parent[parent[p]];
            p = parent[p];
        }

        return p;
    }

    public int count() {
        return count;
    }
  
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }
  
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);

        if (rootP == rootQ) 
        	return;

        if (rank[rootP] < rank[rootQ]) 
        	parent[rootP] = rootQ;
        else if (rank[rootP] > rank[rootQ]) 
        	parent[rootQ] = rootP;
        else {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }

        count--;
    }

    private void validate(int p) {
        int n = parent.length;

        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n-1));  
        }
    }
}


public class NetworkAnalysis {

	private static Network network;
	private static Scanner UserIn = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Invalid number of arguments!");
			return;
		}

		networkInit(args[0]);

		while(userMenu()) {}
	}

	public static void networkInit(String filename) throws IOException {
	
		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String str = reader.readLine();
		network = new Network(Integer.parseInt(str));

		while ((str = reader.readLine()) != null ) {
			String[] edgeInfo = str.split(" ");

			Edge edge = new Edge(Integer.parseInt(edgeInfo[0]), Integer.parseInt(edgeInfo[1]), 
				edgeInfo[2], Integer.parseInt(edgeInfo[3]), Integer.parseInt(edgeInfo[4]));

			network.addEdge(edge);
		}

		reader.close();
	}

	public static boolean userMenu() {
		int choice = 0;

		System.out.println("\nWhat would you like to do? (Enter number of choice)");
		System.out.println("\t(1) Find the lowest latency path between two points and it's available bandwidth.");
		System.out.println("\t(2) Determine if network is copper-only connected.");
		System.out.println("\t(3) Find the maximum amount of data that can be transmitted between two points.");
		System.out.println("\t(4) Find the lowest average latency spanning tree for the network.");
		System.out.println("\t(5) Determine whether network will fail if any two points in the network were to fail.");
		System.out.println("\t(6) Exit Program.");

		choice = Integer.parseInt(UserIn.nextLine());

		switch (choice) {
			case 1: lowestLatencyPath();
					break;
			case 2: copperOnly();
					break;
			case 3: maxData();
					break;
			case 4: lowestAvgLatencyTree();
					break;
			case 5: networkFail();
					break;
			case 6: System.out.println("\nGoodbye!");
					return false;
			default: System.out.println("\nInvalid Choice!");
					break;
		}

		return true;
	}

	public static void transition() {
		System.out.print("\nPress any button to continue:\t");
		UserIn.nextLine();
	}

	public static int[] getPoints() {
		int[] points = new int[2];

		try {
			System.out.print("\nEnter starting point: (as integer)\t");
			points[0] = Integer.parseInt(UserIn.nextLine());

			System.out.print("Enter ending point: (as integer)\t");
			points[1] = Integer.parseInt(UserIn.nextLine());
		}
		catch (Exception e) {
			System.out.println("Invalid Input!");
			return getPoints();
		}

		if (points[0] < 0 || points[1] < 0 || points[0] >= network.size || points[1] >= network.size) {
			System.out.println("Invalid Input!");
			return getPoints();
		}

		return points;
	}

	public static void lowestLatencyPath() {
		int[] points = getPoints();

		ArrayList<Integer> path = network.getShortestPath(points[0], points[1]);

		int bandwidth = path.remove(path.size() - 1);

		points[1] = path.remove(path.size() - 1);

		System.out.print("\nLowest Latency Path:  ");

		while (path.size() > 0) {
			points[0] = points[1];
			points[1] = path.remove(path.size() - 1);
			System.out.print("(" + points[0] + "," + points[1] + ")");
		}

		System.out.println("\nAvailable Bandwidth:  " + bandwidth + "Mbps");

		transition();
	}

	public static void copperOnly() {
		if (network.copperConnected())
			System.out.println("\nNetwork is copper-only connected.");
		else
			System.out.println("\nNetwork is not copper-only connected.");

		transition();
	}

	public static void maxData() {
		int[] points = getPoints();

		if (points[0] == points[1]) {
			System.out.println("\nError: Points cannot be the same!");
			transition();
			return;
		}

		int bandwidth = new FlowNetwork(network).getBandwidth(points[0], points[1]);

		System.out.println("\nThe maximum bandwidth from the vertex " + points[0] + " to vertex " + 
			points[1] + " is: " + bandwidth + "Mbsp");

		transition();
	}

	public static void lowestAvgLatencyTree() {
		KruskalMST tree = new KruskalMST(network);

		System.out.format("\nThe lowest average latency spanning tree's latency of:\t%.5e seconds\n", (tree.weight / tree.mst.size()));

		System.out.print("It's path is:\t");
		for (Edge edge: tree.mst) {
			System.out.print("(" + edge.vertex1 + "," + edge.vertex2 + ")");
		}
		System.out.println();

		transition();
	}

	public static void networkFail() {
		if (network.vertexFailures())
			System.out.println("\nNetwork does not become disconnected when 2 vertex fail.");
        else 
            System.out.println("\nNetwork becomes disconnected when 2 vertex fail.");

		transition();
	}
}