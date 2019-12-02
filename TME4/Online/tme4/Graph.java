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

/**
 * structure de notre Graph 
 * 
 * @author Katia AMICHI
 * @author Ning GUO
 *
 */
public class Graph {
	public Map<Integer, Set<Integer>> adjacencyList; 
	public Map<String, Integer> indexFiles;
    public double edgeThreshold = 0.75;
    
    /**
     * initialisation du graphe avec une liste de documents
     * 
     * @param files liste de nom de livres
     */
    public Graph(List<String> files) {
    	indexFiles = new HashMap<String, Integer>();
    	int i = 0;
        for(String file : files) {
        	indexFiles.put(file, ++i);
        }
	}
   
    /**
     * initialisation du graphe avec un nombre de documents,
     * initialisation de la matrice d'adjacence de taille n 
     * 
     * @param n nombre de documents (sommets) dans notre graphe
     */
    public Graph(int n) {
    	adjacencyList = IntStream.rangeClosed(0, n-1).boxed().collect(Collectors.toList()).stream().collect(HashMap<Integer, Set<Integer>>::new, 
				                           (m, c) -> m.put(c, new HashSet<>()),
				                           (m, u) -> {});
		
    }

    /**
     * initialisation du graphe avec une matrice adjacencyList 
     * 
     * @param adjacencyList
     */
	public Graph(Map<Integer, Set<Integer>> adjacencyList) {
		this.adjacencyList = adjacencyList;
		System.out.println("this.adjacencyList : " + this.adjacencyList);
	}
	
	/**
	 * récupérer la map d'index de fichiers, (pour chaque fichier son id dans le graphe
	 * 
	 * @return amp pour chaque ficher son id dans le graphe
	 */
	public Map<String, Integer> getIndexFiles(){
    	return indexFiles;
    }
	
	/**
	 * 
	 * @param u
	 * @return
	 */
	public int degree(int u) {
		return adjacencyList.get(u).size();
	}

	/**
	 * 
	 * @param u
	 * @return
	 */
	public Set<Integer> neighbors(int u){
		return adjacencyList.get(u);
	}
	
	/**
	 * 
	 * @return
	 */
	public int nbNodes() {
		return adjacencyList.size();
	}

	/**
	 * 
	 * @param u
	 * @param v
	 */
	public void addEdge(int u, int v) {
		adjacencyList.get(u).add(v);
		adjacencyList.get(v).add(u);
	}
	
	/**
	 * 
	 * @param edgeThreshold
	 * @param mat
	 * @return
	 */
	public Graph getMatGraph(double edgeThreshold, double[][] mat) {
		int n = mat.length;
		Graph graph = new Graph(n);
		IntStream.rangeClosed(0, n-1).forEach(i->{IntStream.rangeClosed(i+1, n-1).forEach(j->{
				if(i!=j && mat[i][j] <= edgeThreshold)
					graph.addEdge(i, j);});
		});		
		graph.edgeThreshold = edgeThreshold;
		return graph;
	}
	
	/**
	 * 
	 * @param OutputFile
	 * @throws IOException
	 */
	public void saveGraph(String OutputFile) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutputFile)))) {
			adjacencyList.entrySet().forEach(e->{e.getValue().forEach(v-> {
					if(e.getKey()<v) 
						try {writer.write(e.getKey() + " " + v + "\n");} catch (IOException e1) {e1.printStackTrace();}
				});
			});
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
	}
	
}