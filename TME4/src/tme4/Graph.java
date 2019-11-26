package tme4;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Graph {
	public Map<Integer, Set<Integer>> adjacencyList; 
	public Map<String, Integer> indexFiles;
    public ArrayList<ArrayList<Integer>> edges;
    public double edgeThreshold = 0.75;
    
    public Graph(List<String> files) {
    	indexFiles = new HashMap<String, Integer>();
    	int i = 0;
        for(String file : files) {
        	indexFiles.put(file, ++i);
        }
	}

	public Graph(Map<Integer, Set<Integer>> adjacencyList, ArrayList<ArrayList<Integer>> edges) {
		this.adjacencyList = adjacencyList;
		this.edges = edges;
	}
	
    public void setEdges(ArrayList<ArrayList<Integer>> edges) {
    	this.edges = edges;    	
    }
    
    public ArrayList<ArrayList<Integer>> getEdges() {
    	return edges;
    }
    
    public Map<String, Integer> getIndexFiles(){
    	return indexFiles;
    }
	
	public void addEdge(int u, int v) {
		adjacencyList.get(u).add(v);
		adjacencyList.get(v).add(u);
	}

	public int degree(int u) {
		return adjacencyList.get(u).size();
	}

	public Set<Integer> neighbors(int u){
		return adjacencyList.get(u);
	}
	
	public int nbNodes() {
		return adjacencyList.size();
	}

	public void saveGraph(String OutputFile) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutputFile)))) {
			this.edges.forEach(edge -> {
		        try { writer.write(edges.get(0)+ " " + edges.get(0) + "\n"); }
		        catch (IOException ex) { throw new UncheckedIOException(ex); }
		    });
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
	}
	
}