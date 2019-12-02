package tme4;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @author Ning GUO
 * @author Katia AMICHI
 *
 */
public class Betweeness {

	/**
	 * 
	 * @author Ning GUO
	 * @author Katia AMICHI
	 *
	 */
	public class Pair implements Comparable<Pair>{
		int p1;
		int p2;
		public Pair(int p1, int p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
		@Override
		public int compareTo(Pair o) {
			if( o.p1 > this.p1) return 1;
			if( o.p1 < this.p1) return -1;
			if ( this.p2 > o.p2 )return -1;

			if ( this.p2 < o.p2 )return 1;
			return 0;
		}

		@Override
		public boolean equals(Object obj) 
		{
			Pair pair = (Pair)obj;
			if(this.p1 == pair.p1 && this.p2 == pair.p2) return true;
			return false;
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public List<Pair> lireTexte(String fileName){
		List<Pair> aretes = new   ArrayList<Pair>();
		String ligne;
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(fileName));
			while ((ligne = reader.readLine()) != null) {
				String[] arres = ligne.split("	");
				int p1 =  Integer.parseInt(arres[0]) ;
				int p2 =  Integer.parseInt(arres[1]) ;
				Pair pair = new Pair(p1,p2);
				aretes.add(pair);


			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return aretes;

	}
	
	/**
	 * 
	 * @param size
	 * @param liens
	 * @return
	 */
	public ArrayList<Integer>[][] calculShortestPaths(int size,ArrayList<Pair> liens) {
		ArrayList<Integer>[][] paths= new ArrayList[size][size];
		//initialiser paths
		for (int i=0;i<size;i++) {
			for (int j=0;j<size;j++) {
				ArrayList<Integer> l = new ArrayList<Integer>() ;
				l.add(j);
				paths[i][j] = l; 
			}
		}

		//		    matrice d'adjacence
		double[][] m = new double[size][size];
		for(Pair pair : liens) {
			m[pair.p1][pair.p2] = 1;
			m[pair.p2][pair.p1] = 1;
		}

		for(int i =0;i<size;i++) {
			for (int j=0;j<size;j++) {
				if(i == j) m[i][j] = 0;
				if(m[i][j]!=1)
					m[i][j]= Double.MAX_VALUE;
			}
		}

		// calculer les plus courts chemins
		for(int k = 0 ; k< size;k++) {
			for(int i = 0 ; i< size;i++) {
				for(int j = 0 ; j<size;j++) {

					if(i == j) continue;
					if(m[i][j]> m[i][k]+m[k][j]) {
						m[i][j]= m[i][k]+m[k][j];
						paths[i][j]= paths[i][k];
					}    
					if(m[i][j] == m[i][k]+m[k][j]) {
						ArrayList<Integer> list = (ArrayList<Integer>) paths[i][k].clone();
						for(int path:paths[i][j]) {
							if(!list.contains(path))
								list.add(path);
						}
						paths[i][j]=list;
					}
				}    	           	        	
			}
		}


		return paths;
	}
	
	/**
	 * methode pour calculer les chemins plus court a partir d'une matrice de distance
	 * @param size
	 * @param matrice
	 * @param edgeThehard
	 * @return
	 */
	public ArrayList<Integer>[][] calculCourtsChemins(int size,double [][] matrice,double edgeThehard){
		ArrayList<Integer>[][] paths= new ArrayList[size][size];
		//initialiser paths
		for (int i=0;i<size;i++) {
			for (int j=0;j<size;j++) {
				ArrayList<Integer> l = new ArrayList<Integer>() ;
				l.add(j);
				paths[i][j] = l; 
			}
		}
		//		    matrice d'adjacence
		double[][] m = new double[size][size];
		
		for(int i =0;i<size;i++) {
			for (int j=0;j<size;j++) {
				if(i == j) m[i][j] = 0;
				if(matrice[i][j]>=edgeThehard) {
					m[i][j]= Double.MAX_VALUE;
				}else {
					m[i][j] = matrice[i][j];
				}
			}
		}
		// calculer les plus courts chemins
		for(int k = 0 ; k< size;k++) {
			for(int i = 0 ; i< size;i++) {
				for(int j = 0 ; j<size;j++) {

					if(i == j || i == k) continue;
					
					if(m[i][j]> m[i][k]+m[k][j]) {
						m[i][j]= m[i][k]+m[k][j];
						paths[i][j]= paths[i][k];
					}    
					if(m[i][j] == m[i][k]+m[k][j] && m[i][j]!= Double.MAX_VALUE ) {
						ArrayList<Integer> list = (ArrayList<Integer>) paths[i][k].clone();
						for(int path:paths[i][j]) {
							if(!list.contains(path))
								list.add(path);
						}
						paths[i][j]=list;
					}
				}    	           	        	
			}
		}
		
		return paths;
	}

	/**
	 * Calculer le betweeness pour tous les sommets de 1 a n 
	 * @param chemins
	 * @param size
	 * @return
	 */
	public HashMap<Integer,Double> calculerBetweeness(	ArrayList<ArrayList<Integer>>[][] chemins, int size){
		HashMap<Integer,Double> map = new HashMap<Integer,Double>();
		for(int i = 0; i < size; i++) {
			for(int j = 0;j<size;j++) {
				ArrayList<ArrayList<Integer>> l = chemins[i][j];

				for(ArrayList<Integer> chemin: l) {
					for(int noeud:chemin) {
						if(!map.containsKey(noeud)) {
							map.put(noeud, (Double)1.0/l.size());
						}else {
							map.put(noeud, map.get(noeud)+(Double)1.0/l.size());
						}
					}
				}

			}
		}

		for(int i = 0 ; i < size;i++) {
			if(map.containsKey(i)) {
				map.put(i, map.get(i)/2);
			}else {
				map.put(i, 0.0);
			}
		}
	
	
		return map;
	}


	/**
	 * 
	 * @param paths
	 * @param size
	 * @return
	 */
	public ArrayList<ArrayList<Integer>>[][] transformeChemins(ArrayList<Integer>[][] paths, int size){
		//on transforme le resultat en une liste de chemins entre  i et j 
		ArrayList<ArrayList<Integer>>[][] chemins  = new ArrayList[size][size];
		for(int i =0;i<size;i++) {
			for(int j =0;j<size;j++) {
				ArrayList<ArrayList<Integer>> answers = new ArrayList<ArrayList<Integer>>();
				getAnswers(i,j,paths,new ArrayList<Integer>(),answers);
				chemins[i][j] = answers;
			}
		}
		return chemins;

	}


	/**
	 * 
	 * @param i
	 * @param j
	 * @param paths
	 * @param currentPath
	 * @param answers
	 */
	public void getAnswers(int i, int j,ArrayList<Integer>[][] paths,ArrayList<Integer> currentPath, ArrayList<ArrayList<Integer>>  answers) {
		ArrayList<Integer> ensPath = paths[i][j];
		if(ensPath.size() == 1 && ensPath.contains(j)) {
			answers.add(new ArrayList<Integer>(currentPath));

		}else {
			for(int p : ensPath) {
				currentPath.add(p);
				getAnswers(p,j,paths,currentPath,answers);
				currentPath.remove(currentPath.size()-1);

			}
		}


	}
	
	/**
	 * 
	 * @param mapBetweeness
	 */
	public void printResult(Map<Integer,Double> mapBetweeness) {
		
		for(Entry e : mapBetweeness.entrySet()) {
			int sommet = (int) e.getKey();
			Double nb = (Double) e.getValue();
		
			System.out.println("betweeness du sommet "+ sommet +" = " + nb );

		}
	}
	
	/**
	 * 
	 * @param mapBetweeness
	 * @param nbLivres
	 */
	public void printResultDistribution(Map<Integer, Double> mapBetweeness, int nbLivres) {
		HashMap<Double,Integer> map = new HashMap<Double,Integer>();
		
		
		for(Entry e : mapBetweeness.entrySet()) {
			int sommet = (int) e.getKey();
			Double nb = (Double) e.getValue();
			if(!map.containsKey(nb)) {
				map.put(nb, 1);
			}else {
				map.put(nb, map.get(nb)+1);
			}
			
		}
		
		PrintWriter sortie;
		try {
			sortie = new PrintWriter("Results/BeDistribution"+nbLivres+".txt");
			for(Entry e : map.entrySet()) {
				Double betweenness = (Double) e.getKey();
				int  nb =  (int) e.getValue();
				String txt = betweenness + "		"+nb;
				sortie.println(txt);
			}
			sortie.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}
}
