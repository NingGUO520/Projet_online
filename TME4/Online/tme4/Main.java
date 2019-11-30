package tme4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
	private Map<String, Map<String,Integer>> database = new HashMap<String, Map<String,Integer>>();

	public Map<String, Integer> index(String nameFile) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		
		String[] commands  = {"./awksh.sh", nameFile};
        Process process = runtime.exec(commands);
        Map<String, Integer> indexMots = new HashMap<String, Integer>();
        
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        lineReader.lines().forEach(e->{
        	String [] line = e.split(" "); 
        	indexMots.put(line[0],  Integer.parseInt(line[1]));
        });

        return indexMots;
	}
	
	public void init(ArrayList<String> files) throws IOException {
		int i = 0;
		
		range = IntStream.rangeClosed(0, files.size()-1).boxed().collect(Collectors.toList());
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
	
	public ArrayList<String> initDataBase() throws MalformedURLException {
		System.out.println("initialisation DataBase...");

		ArrayList<String> files = new ArrayList<>();
		LireFile l = new LireFile();
		database = l.getDatabase(50);
		int i = 0;
		for(String file :database.keySet() ) {
			files.add(file);
			indexFiles.put(file, i);
        	filesIndex.put(i, file);
        	i++;
		}
		System.out.println("Initialisation reussi ! ");

		System.out.println("Database contains following files");
		System.out.println(database.keySet());
		
		range = IntStream.rangeClosed(0, files.size()-1).boxed().collect(Collectors.toList());
		adjacencyList = range.stream().collect(HashMap<Integer, Set<Integer>>::new, 
				                           (m, c) -> m.put(c, new HashSet<>()),
				                           (m, u) -> {});
		
		
		return files;
	}
	
	public double[][] matDistJaccard(){
		double [][] mat = new double[filesIndex.size()][filesIndex.size()];
		filesIndex.forEach((k1,v1) -> {
			filesIndex.forEach((k2,v2) -> {
				if (k1 == k2)
					mat[k1][k2] = 0;
				else
					mat[k1][k2] = DistanceJaccard.distanceJaccard(database.get(v1), database.get(v2));
			});
		});
		return mat;
	}
	
	public Set<ArrayList<Integer>> edges(ArrayList<String> files, double edgeThehard) throws IOException{
		Set<ArrayList<Integer>> edges = new  HashSet();
        files.forEach(d1 -> {
			files.forEach(d2 -> {
				if(!d1.equals(d2)) {
					try {
						double d = DistanceJaccard.distanceJaccard(index(d1),index(d2));
						if(DistanceJaccard.distanceJaccard(index(d1),index(d2)) <= edgeThehard){
							edges.add(indexFiles.get(d1) < indexFiles.get(d2) ? 
									new ArrayList<Integer>(Arrays.asList(indexFiles.get(d1), indexFiles.get(d2))) :
										new ArrayList<Integer>(Arrays.asList(indexFiles.get(d2), indexFiles.get(d1))) 
									);
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
		int fixed_number = 6;
		Arrays.stream(fileName).forEach(f->{
			if(f.length() > fixed_number) System.out.print(f.substring(0, fixed_number) + "\t");
			else System.out.print(f + "\t");
			}
		);
		System.out.println();
		range.forEach(i->{
			if(fileName[i].length() > fixed_number)
				System.out.print(fileName[i].substring(0, fixed_number) + "\t");
			else 
				System.out.print(fileName[i] + "\t");
			range.forEach(j->System.out.print(df.format(mat[i][j]) + "\t"));
			System.out.println();
		});
				
	}

	public static void main2(String[] args) throws IOException{
		Main main = new Main();
		ArrayList<String> files = new ArrayList<>();
//		files.add("Test/S.txt");
//		files.add("Test/U.txt");
//		files.add("Test/V.txt");
//		files.add("Test/w.txt");
//		
		for(int i = 0; i<10;i++) {
			files.add("Test/test"+i+".txt");
		}
		
		main.init(files);
		Set<ArrayList<Integer>> edges = main.edges(files, edgeThehard);
		double [][] distJac = main.matDistJaccard();
		System.out.println(".................matJac..................");
		main.printMatJac(distJac);
		
		Graph g = new Graph(main.adjacencyList, edges);
		g.saveGraph("Test/edgesGraph.edges");

		Map<String, Integer> indexMots = main.index("Test/S.txt");
		System.out.println("indexMots  : " + indexMots );
		System.out.println("...... Fin de lancement sh ........");
		
		System.out.println(".................closeness..................");
		Closeness closeness = new Closeness();
		double [][] W = closeness.floydWarshall(distJac);
		Map<Integer, Double> closenessResult = closeness.closeness(W);
		System.out.println("closenessResult  : " + closenessResult);
	}
	
	public static void main(String[] args) throws IOException{
		Main main = new Main();
//		ArrayList<String> files = new ArrayList<>();
		
		/*files.add("Test/S.txt");
		files.add("Test/U.txt");
		files.add("Test/V.txt");
		files.add("Test/w.txt");*/
		
//		for(int id = 0; id<10;id++) {
//			files.add("Test/test"+id+".txt");
//		}
//		
//		main.init(files);
		ArrayList<String> files = main.initDataBase();
//		Set<ArrayList<Integer>> edges = main.edges(files, edgeThehard);
		double [][] distJac = main.matDistJaccard();
		System.out.println(".................matJac..................");
		main.printMatJac(distJac);
//		Graph g = new Graph(main.adjacencyList, edges);
//		g.saveGraph("Test/edgesGraph.edges");
		
		PageRank pg = new PageRank();
//		
		//WITH CONNEX GRAPH
//		double[] page_rank = pg.page_rank(g, 0.1, 10, g.nbNodes()); 
//			
//		System.out.println("---------------PAGE RANK----------------");
//		main.range.forEach(i->System.out.println(i + " " + main.filesIndex.get(i) + " " +page_rank[i]));
//		System.out.println("----------------------------------------\n");
//		
		
		//Calculer le betweenness 
		Betweeness b = new Betweeness();
		int size = distJac.length;
		ArrayList<Integer>[][] result = b.calculCourtsChemins(size,distJac,edgeThehard);
		
//	tem.out.println("result ["+i+"]["+j+"] = "+result[i][j]);
	
		ArrayList<ArrayList<Integer>>[][] chemins = b.transformeChemins(result,  size);
//
//		for(int i = 0; i < size;i++)
//		for(int j = 0; j< size;j++)
//	System.out.println("chemins ["+i+"]["+j+"] = "+chemins[i][j]);
		HashMap<Integer,Double> mapBetweeness = b.calculerBetweeness(chemins,size);
		b.printResult(mapBetweeness);
////		b.printResultDistribution(mapBetweeness);
	}

}
