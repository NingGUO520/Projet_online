package tme4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * pour la test de de performance pour les 3 methodes : closenenss, betweenness, PageRank
 * @author Ning GUO
 * @author Katia AMICHI
 *
 */
public class TestPerformance {
	public void test(double edgeThehard, int nbFiles) throws IOException {
		System.out.println("---------------------Test pour edgeThehard = "+edgeThehard + " et nombre de livres "+nbFiles+"----------------------------");
		Main main = new Main(edgeThehard,nbFiles);
		Instant start, finish;

		ArrayList<String> files = main.initDataBase();
		System.out.println("nbfile : " + files.size());

		start = Instant.now();
		double [][] distJac = main.matDistJaccard();
		finish = Instant.now();
		long timeDistJac  = Duration.between(start, finish).toMillis(); // milliseconds
		System.out.println("timeDistJac : " + timeDistJac);
		
		System.out.println(".................matJac..................");
		// main.printMatJac(distJac);
		
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
		// main.printResutl(topPR);
		main.saveResutl("Results/ResultPageRank"+nbFiles, topPR);
		finish = Instant.now();
		long timePR = Duration.between(start, finish).toMillis(); // milliseconds 
		System.out.println("timeElapsed : " + timePR);
		System.out.println("-------------------Fin PR----------------------");
		
		g.saveGraph("Results/edgesGraph"+nbFiles+".edges");

		System.out.println("----------------- Betweeness ------------------");
		start = Instant.now();
		Betweeness b = new Betweeness();
		int size = distJac.length;
		ArrayList<Integer>[][] result = b.calculCourtsChemins(size,distJac,edgeThehard);
	
		ArrayList<ArrayList<Integer>>[][] chemins = b.transformeChemins(result,  size);
		Map<Integer,Double> mapBetweeness = b.calculerBetweeness(chemins,size);
		Map<Integer,Double> topBt = main.getTop10(mapBetweeness, main.nbTOP);
		
		// main.printResutl(topBt);
		main.saveResutl("Results/ResultBetweeness"+nbFiles, topBt);
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
		//main.printResutl(topCl);
		main.saveResutl("Results/ResultCloseness"+nbFiles, topCl);
		finish = Instant.now();		
		long timeCl = Duration.between(start, finish).toMillis();
		System.out.println("timeElapsed : " + timeCl);
		System.out.println("-------------------Fin Cl---------------------\n");
		
		System.out.println("sauvgarde des temps d'execution...");
		String resultTemps = "Results/TempsExecution.txt";
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(resultTemps),true))) {
			try {
				writer.write(main.nbFILE + "		" + timeDistJac + "		" + timePR + "		" + timeBt + "		" + timeCl + "\n");
				
			}catch (IOException e1) {e1.printStackTrace();}
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
		
		System.out.println("---------------------Fin de test pour edgeThehard = "+edgeThehard + " et nombre de livres "+nbFiles+"----------------------------");

	}

	public static void main(String[] args) throws IOException {
		String resultTemps = "Results/TempsExecution.txt";
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(resultTemps),true))) {
			try {
				writer.write("#Le temps d'execution pour chaque methode ");
				writer.write("#Nombre livres" + "	" + "DistJaccard" + "	" + "PageRank" + "		" + "Betweeness" + "	" + "Closeness" + "\n");
				
			}catch (IOException e1) {e1.printStackTrace();}
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
		
		TestPerformance tp = new TestPerformance();
		
		// le nombre de livres pour tester varie entre 50-2000
		int [] nbFileList = {50,100,200,500,1000,1500,2000};
		for( int n : nbFileList) {
			tp.test(0.75,n);
		}
		
	}
}
