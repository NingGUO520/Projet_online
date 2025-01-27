package tme4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.text.DecimalFormat;
import java.time.*;

/**
 * 
 * @author Katia AMICHI
 * @author Ning GUO
 *
 */
public class Main {
	private static double edgeThehard = 0.75;
	private Map<String, Integer> indexFiles = new HashMap<String, Integer>();
	private Map<Integer, String> filesIndex = new HashMap<Integer, String>();
	Map<Integer, Set<Integer>> adjacencyList;
	final DecimalFormat df = new DecimalFormat("#0.000");
	private List<Integer> range; 
	final int nbTOP = 10;
	int nbFILE = 10;

	
	public Main(double edgeThehard, int nbFiles) {
		this.edgeThehard = edgeThehard;
		this.nbFILE = nbFiles;
	}
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
		System.out.println("files.size() : " + files.size());
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
	
	public ArrayList<String> initDataBase() throws IOException {
		ArrayList<String> files = new ArrayList<String>();
		System.out.println("initialisation DataBase...");

		LireFile l = new LireFile();
		database = l.getDatabase(nbFILE, "Results/nameFales"+nbFILE+".files");
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
		
		range = IntStream.rangeClosed(0, i-1).boxed().collect(Collectors.toList());
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
	
	public void setAdjacencyList(ArrayList<String> files, double [][] distJac, double edgeThehard) throws IOException{
		range.forEach(i->{range.forEach(j->{if(i!=j) {
			if(distJac[i][j]<edgeThehard) {
				adjacencyList.get(i).add(j);
				adjacencyList.get(j).add(i);
			}}}); 
		});
	}

	public void printMatJac(double[][] mat) {
		int n = filesIndex.size();
		String[] fileName = new String[n];
		filesIndex.forEach((k,v)-> fileName[k] = v);
		System.out.print("\t");
		int fixed_number = 6;
		Arrays.stream(fileName).forEach(f->{
			f = f.replace("Test/", "");
			if(f.length() > fixed_number) System.out.print(f.substring(0, fixed_number) + "\t");
			else System.out.print(f + "\t");
			}
		);
		System.out.println();
		
		Stream.of(fileName).forEach(i->{
			String ii = i.replace("Test/", "");
			if(ii.length() > fixed_number)
				System.out.print(ii.substring(0, fixed_number) + "\t");
			else 
				System.out.print(ii + "\t");
			range.forEach(j->System.out.print(df.format(mat[indexFiles.get(i)][j]) + "\t"));
			System.out.println();
		});
		
				
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
	
	public static void saveResutlList(String OutputFile, ArrayList<String> listString) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutputFile)))) {
			listString.forEach(l -> {
		        try {
		        	writer.write(l + "\n");}
		        catch (IOException ex) { throw new UncheckedIOException(ex); }
		    });
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
	}
	
	public static void main(String[] args) throws IOException{
		Main main = new Main(0.75,20);
		Instant start, finish;

		
//		ArrayList<String> files = main.getFiles();
		ArrayList<String> files = new ArrayList<String>();


		for(int id = 0; id<10;id++) {
			files.add("Test/test"+id+".txt");
		}
				
		// rrayList<String> files = main.initDataBase();
		main.init(files);
		System.out.println("nbfile : " + files.size());

		start = Instant.now();
		double [][] distJac = main.matDistJaccard();
		finish = Instant.now();
		long timeDistJac  = Duration.between(start, finish).toMillis(); // milliseconds
		System.out.println("timeDistJac : " + timeDistJac);
		
		System.out.println(".................matJac..................");
		main.printMatJac(distJac);
		
		System.out.println("---------------PAGE RANK----------------");
		start = Instant.now();
		main.setAdjacencyList(files, distJac, edgeThehard);
		Graph g = new Graph(main.adjacencyList);
		
		PageRank pg = new PageRank();
		double[] page_rank = pg.page_rank(g, 0.1, 10, g.nbNodes()); 
		HashMap<Integer, Double> mapPR = new HashMap<Integer, Double>();
		
		for(int i = 0; i < page_rank.length; i++)
			mapPR.put(i, page_rank[i]);

		Map<Integer,Double> topPR = main.getTop10(mapPR, main.nbTOP);
		main.printResutl(topPR);
		// main.saveResutl("Results/ResultPageRank.result", topPR);
		finish = Instant.now();
		long timePR = Duration.between(start, finish).toMillis(); // milliseconds 
		System.out.println("timeElapsed : " + timePR);
		System.out.println("-------------------Fin PR----------------------");
		
		g.saveGraph("Results/GraphRapport.edges");

		System.out.println("----------------- Betweeness ------------------");
		start = Instant.now();
		Betweeness b = new Betweeness();
		int size = distJac.length;
		ArrayList<Integer>[][] result = b.calculCourtsChemins(size,distJac,edgeThehard);
	
		ArrayList<ArrayList<Integer>>[][] chemins = b.transformeChemins(result,  size);
		Map<Integer,Double> mapBetweeness = b.calculerBetweeness(chemins,size);
		Map<Integer,Double> topBt = main.getTop10(mapBetweeness, main.nbTOP);
		
		main.printResutl(topBt);
		main.saveResutl("Results/ResultBetweeness.result", topBt);
		finish = Instant.now();
		long timeBt = Duration.between(start, finish).toMillis();
		System.out.println("timeElapsed : " + timeBt);
		System.out.println("-------------------Fin Bt----------------------");
		
		System.out.println("------------------- Closeness -----------------");
		start = Instant.now();
		Closeness closeness = new Closeness();
		double [][] W = closeness.floydWarshall(distJac);
		Map<Integer, Double> mapCloseness = closeness.closeness(W);
		
		Map<Integer,Double> topCl = main.getTop10(mapCloseness, main.nbTOP);
		main.printResutl(topCl);
		main.saveResutl("Results/ResultCloseness.result", topCl);
		finish = Instant.now();		
		long timeCl = Duration.between(start, finish).toMillis();
		System.out.println("timeElapsed : " + timeCl);
		System.out.println("-------------------Fin Cl---------------------\n");
		
		System.out.println("sauvgarde des temps d'execution...");
		String resultTemps = "Results/TempsExecution.csv";
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(resultTemps)))) {
			try {
				writer.write("BD" + ", " + "nbFile" + ", " + "tDistJac" + ", " + "tPr" + ", " + "tBt" + ", " + "tCl" + "\n");
				writer.write("BD0" + ", " + main.nbFILE + ", " + timeDistJac + ", " + timePR + ", " + timeBt + ", " + timeCl + "\n");
				
			}catch (IOException e1) {e1.printStackTrace();}
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
		
	}
	
	public  ArrayList<String> getFiles(){
		 ArrayList<String> files = new  ArrayList<String>();
		 try (FileReader reader = new FileReader("Results/ListBooks");
	             BufferedReader br = new BufferedReader(reader)) {

	            // read line by line
	            String line;
	            while ((line = br.readLine()) != null) {
	            	files.add(line);
	            }

	        } catch (IOException e) {
	            System.err.format("IOException: %s%n", e);
	        }
		 return files;
	}

	public static ArrayList<String> buildDataBase(String nameDir) throws IOException {
		File dir = new File(nameDir);
		ArrayList<String> livres = new ArrayList<String>();
		
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				// String pathFile = child.getAbsolutePath();
				livres.add(child.getName());
			}
		} else {
			System.out.println(dir.getName() + " is not a directory");
		}
		return livres;
	}
	
}

