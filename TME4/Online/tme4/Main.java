package tme4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.text.DecimalFormat;

public class Main {
	private static double edgeThehard = 0.75;
	private Map<String, Integer> indexFiles = new HashMap<String, Integer>();
	private Map<Integer, String> filesIndex = new HashMap<Integer, String>();
	private Map<Integer, Set<Integer>> adjacencyList;
	final DecimalFormat df = new DecimalFormat("#0.000");
	private List<Integer> range; 
	final int nbTOP = 5;

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

	public static void main1(String[] args) throws IOException{
		Main main = new Main();
		ArrayList<String> files = new ArrayList<>();

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

	public Map<Integer,Double> getTop10(Map<Integer, Double> mapIn, int nbTop) {
		return mapIn.entrySet()
        .stream()
        .sorted((Map.Entry.<Integer,Double>comparingByValue().reversed()))
        .limit(nbTop)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	
	public void printResutl(Map<Integer, Double> map) {
		map.forEach((k,v)->{
			System.out.println("livre : " + filesIndex.get(k) + " (id:" + k + ") Count : " + v);
		});
	}
	
	public void saveResutl(String OutputFile, Map<Integer, Double> map) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutputFile)))) {
			map.forEach((k,v)-> {
		        try {
		        	writer.write(filesIndex.get(k)+ ", " + k + ", " + v + "\n");}
		        catch (IOException ex) { throw new UncheckedIOException(ex); }
		    });
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
	}
	
	public static void main(String[] args) throws IOException{
		Main main = new Main();
		ArrayList<String> files = new ArrayList<>();
		
		for(int id = 0; id<10;id++) {
			files.add("Test/test"+id+".txt");
		}
		
		// files = buildDataBase("./livres");
		
		main.init(files);
		Set<ArrayList<Integer>> edges = main.edges(files, edgeThehard);
		double [][] distJac = main.matDistJaccard();
		System.out.println(".................matJac..................");
		main.printMatJac(distJac);
		Graph g = new Graph(main.adjacencyList, edges);
		g.saveGraph("Test/edgesGraph.edges");
		
		

		System.out.println("---------------PAGE RANK----------------");
		PageRank pg = new PageRank();
		double[] page_rank = pg.page_rank(g, 0.1, 10, g.nbNodes()); 
		HashMap<Integer, Double> mapPR = new HashMap<Integer, Double>();
		
		for(int i = 0; i < page_rank.length; i++)
			mapPR.put(i, page_rank[i]);

		Map<Integer,Double> topPR = main.getTop10(mapPR, main.nbTOP);
		main.printResutl(topPR);
		main.saveResutl("Results/ResultPageRank.result", topPR);
		System.out.println("-------------------Fin PR----------------------");

		System.out.println("----------------- Betweeness ------------------");
		Betweeness b = new Betweeness();
		int size = distJac.length;
		ArrayList<Integer>[][] result = b.calculCourtsChemins(size,distJac,edgeThehard);
		ArrayList<ArrayList<Integer>>[][] chemins = b.transformeChemins(result,  size);
		Map<Integer,Double> mapBetweeness = b.calculerBetweeness(chemins,size);
		Map<Integer,Double> topBt = main.getTop10(mapBetweeness, main.nbTOP);
		
		main.printResutl(topBt);
		main.saveResutl("Results/ResultBetweeness.result", topBt);
		System.out.println("-------------------Fin Bt----------------------");
		
		System.out.println("------------------- Closeness -----------------");
		Closeness closeness = new Closeness();
		double [][] W = closeness.floydWarshall(distJac);
		Map<Integer, Double> mapCloseness = closeness.closeness(W);
		
		closeness.printResult(mapCloseness);
		Map<Integer,Double> topCl = main.getTop10(mapCloseness, main.nbTOP);
		main.printResutl(topCl);
		main.saveResutl("Results/ResultCloseness.result", topCl);
				
		System.out.println("-------------------Fin Cl---------------------\n");
	}
	

	public static ArrayList<String> buildDataBase(String nameDir) throws IOException {
		File dir = new File(nameDir);
		ArrayList<String> livres = new ArrayList<String>();
		
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				String pathFile = child.getAbsolutePath();
				// System.out.println("Computing for " + pathFile);
				livres.add(pathFile);
			}
		} else {
			System.out.println(dir.getName() + " is not a directory");
		}
		return livres;
	}
	
}
