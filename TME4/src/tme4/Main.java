package tme4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.text.DecimalFormat;

public class Main {
	private static double edgeThehard = 0.75;
	private Map<String, Integer> indexFiles = new HashMap<String, Integer>();
	private Map<Integer, String> filesIndex = new HashMap<Integer, String>();
	private Map<Integer, Set<Integer>> adjacencyList;
	final DecimalFormat df = new DecimalFormat("#0.000");
	private List<Integer> range; 
	// pour chaque livre : les mots avec leurs occurences
	private Map<String, Map<String,Long>> database = new HashMap<String, Map<String,Long>>();

	public static Map<String, Long> index(String fileName) throws IOException {
		return Files.lines(Paths.get(fileName)).
				map(String::toLowerCase).
				map(line -> line.split("[\\s,:;!?.]+")).
				flatMap(Arrays::stream).
				filter(s -> s.matches("[a-zA-z-']+")).
				collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
	
	public void init(ArrayList<String> files) throws IOException {
		int i = 0;
		
		List<Integer> range = IntStream.rangeClosed(0, files.size()-1).boxed().collect(Collectors.toList());
		adjacencyList = range.stream().collect(HashMap<Integer, Set<Integer>>::new, 
				                           (m, c) -> m.put(c, new HashSet<>()),
				                           (m, u) -> {});
		
		for (String file : files) {
			database.put(file, index(file));
        	indexFiles.put(file, i);
        	filesIndex.put(i, file);
        	i++;
		}
		System.out.println("Database contains following files");
		System.out.println(database.keySet());
		
	}
	
	public double[][] matDistJaccard(){
		double [][] mat = new double[filesIndex.size()][filesIndex.size()];
		int i = 0, j = 0;
		for(Entry<Integer,String> file1 : filesIndex.entrySet()) {
			for(Entry<Integer,String> file2: filesIndex.entrySet()) {
				if (file1 == file2) {
					mat[file1.getKey()][file2.getKey()] = 0;
				}
				else {
					
					mat[file1.getKey()][file2.getKey()] = DistanceJaccard.distanceJaccard(database.get(file1.getValue()), database.get(file2.getValue()));
				}
			}
		}
		return mat;
	}
	
	public ArrayList<ArrayList<Integer>> edges(ArrayList<String> files, double edgeThehard) throws IOException{

		ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>();
        files.forEach(d1 -> {
			files.forEach(d2 -> {
				if(!d1.equals(d2)) {
					try {
						double d = DistanceJaccard.distanceJaccard(index(d1),index(d2));
						if(DistanceJaccard.distanceJaccard(index(d1),index(d2)) <= edgeThehard){
							edges.add(new ArrayList<Integer>( 
						            Arrays.asList(indexFiles.get(d1), indexFiles.get(d2))));
							adjacencyList.get(indexFiles.get(d1)).add(indexFiles.get(d2));
							adjacencyList.get(indexFiles.get(d2)).add(indexFiles.get(d1));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		});
        return edges;
	}

	public void printMatJac(double[][] mat) {
		int n = filesIndex.size();
		String[] fileName = new String[n];
		filesIndex.forEach((k,v)-> fileName[k] = v);
		System.out.print("\t");
		int fixed_number = 8;
		Arrays.stream(fileName).forEach(f->{
			if(f.length() > fixed_number) System.out.print(f.substring(0, fixed_number) + "\t");
			else System.out.print(f + "\t");
			}
		);
		System.out.println();
		List<Integer> range = IntStream.rangeClosed(0, n-1).boxed().collect(Collectors.toList());
		range.forEach(i->{
			if(fileName[i].length() > fixed_number)
				System.out.print(fileName[i].substring(0, fixed_number) + "\t");
			else 
				System.out.print(fileName[i] + "\t");
			range.forEach(j->System.out.print(df.format(mat[i][j]) + "\t"));
			System.out.println();
		});
				
	}
	public static void main(String[] args) throws IOException{
		Main main = new Main();
		ArrayList<String> files = new ArrayList<>();
		files.add("Test/S.txt");
		files.add("Test/U.txt");
		files.add("Test/V.txt");
		files.add("Test/w.txt");
		
		main.init(files);
		ArrayList<ArrayList<Integer>> edges = main.edges(files, edgeThehard);
		double [][] distJac = main.matDistJaccard();
		System.out.println(".................matJac..................");
		main.printMatJac(distJac);
		Graph g = new Graph(main.adjacencyList, edges);
		g.saveGraph("edgesGraph.edges");
		
		PageRank pg = new PageRank();
		
		//WITH CONNEX GRAPH
		double[] page_rank = pg.page_rank(g, 0.1, 10, g.nbNodes()); 
		
			
		System.out.println("---------------PAGE RANK----------------");
		// main.range.forEach(i->System.out.println(i + " " + main.filesIndex.get(i) + " " +page_rank[i]));
		System.out.println("----------------------------------------\n");
	}

}
