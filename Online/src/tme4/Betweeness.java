package tme4;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class Betweeness {

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

	public List<Pair> lireFile(String fileName){
		List<Pair> aretes = new   ArrayList<Pair>();
		InputStream fileStream;
		try {
			fileStream = new FileInputStream(fileName);
			InputStream gzipStream = new GZIPInputStream(fileStream);
			Reader reader = new InputStreamReader(gzipStream);
			String ligne;
			BufferedReader buffered = new BufferedReader(reader);
			Stream<Pair> stream =	buffered.lines()
					.filter(line -> 
					Integer.parseInt(line.split(" ")[2]) <= 1500 
					&& Integer.parseInt(line.split(" ")[2]) >= 1200 )
					.map(line -> 
					Integer.parseInt(line.split(" ")[1])<Integer.parseInt(line.split(" ")[0])?
							new Pair(Integer.parseInt(line.split(" ")[1]),Integer.parseInt(line.split(" ")[0])):
								new Pair(Integer.parseInt(line.split(" ")[0]),Integer.parseInt(line.split(" ")[1]))
							);
			aretes = stream.collect(Collectors.toList());

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return aretes;
	}

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

		//debug print m
		//		for(int i = 0 ; i< size;i++) 
		//			for(int j = i+1 ; j<size;j++)
		//				System.out.println("m ["+ i + "]["+j+"] = " + m[i][j] );


		return paths;
	}

	/**
	 * Calculer le betweeness pour tous les sommets 
	 * @param chemins
	 * @param size
	 * @return
	 */
	public HashMap<Integer,Double> calculerBetweeness(	ArrayList<Integer>[][] chemins, int size){
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(int i = 1; i < size; i++) {
			for(int j = 1;j<size;j++) {
				ArrayList<Integer> l = chemins[i][j];
				if(l.size() == 1 && l.contains(j)) continue;
				for(int noeud: l) {
					if(!map.containsKey(noeud)) {
						map.put(noeud, 1);
					}else {
						map.put(noeud, map.get(noeud)+1);
					}
					
				}
				
			}
		}
		
		for(Entry e : map.entrySet()) {
			int sommet = (int) e.getKey();
			int nb = (int) e.getValue();
			System.out.println("le sommet "+ sommet +" a apparu " + nb + " fois");
		}
		
		return null;
	}

	
	public ArrayList<ArrayList<Integer>>[][] transformeChemins(ArrayList<Integer>[][] paths, int size){
		//on transforme le resultat en une liste de chemins entre  i et j 
		ArrayList<ArrayList<Integer>>[][] chemins  = new ArrayList[size][size];
		for(int i = 1; i < size; i++) {
			for(int j = i+1;j<size;j++) {
				
				
				
				// les chemins entre i et j 
				ArrayList<ArrayList<Integer>> lesChemins = new ArrayList<ArrayList<Integer>>();
				
				//BFS
				ArrayList<Integer> current = paths[i][j];
				// quand on arrive au dernier sommet de chemins
				if(current.size()==1 && current.contains(j)) continue;
				
				Queue<ArrayList<Integer>> queue = new LinkedList<ArrayList<Integer>>();
				queue.add(current);
				while(!queue.isEmpty()) {
					
					
					for(int k = 0;k<queue.size();k++) {
						ArrayList<Integer> x = queue.poll();
						for(int s : x) {
							ArrayList l = paths[s][j];
							if(!(l.size() == 1 && l.contains(j))) {
								queue.add(l);
							}
						}
						
					}
					
				
				}
				
				
				
				ArrayList<Integer> chemin = new ArrayList<Integer>();
				lesChemins.add(chemin);
			
				for(int c : current) {
					ArrayList<ArrayList<Integer>> nouveauChemins = new ArrayList<ArrayList<Integer>>();
					
					for(ArrayList<Integer> che : lesChemins) {
						ArrayList<Integer> che2 = (ArrayList<Integer>) che.clone();
						che2.add(c);
						nouveauChemins.add(che2);
						
						
					}
					lesChemins = nouveauChemins;
				}
				chemins[i][j] = lesChemins;
			}
		}
		
		return chemins;
		
	}

	
	public void getAnswers(int i, int j,ArrayList<Integer>[][] paths,ArrayList<Integer> path, ArrayList<ArrayList<Integer>>  answers) {
		
	}
	public static void main(String[] args)  {

		Betweeness b = new Betweeness();
		String fileName = "test.txt";
		List<Pair> aretes =  b.lireTexte(fileName);
		ArrayList<Pair> liens = new ArrayList<Pair>();
		for(Pair pair: aretes) {
			if(!liens.contains(pair)) {
				liens.add(pair);
			}
		}
		System.out.println("nombre de liens" + liens.size());
		Collections.sort(liens);

		HashSet<Integer> sommets = new HashSet<Integer>();
		for(Pair p : liens)
		{
			//			System.out.println(p.p1+"	"+p.p2);
			sommets.add(p.p1);
			sommets.add(p.p2);
		}
		System.out.println("nombre de sommtes" + sommets.size());

		int size = sommets.size()+1;
		ArrayList<Integer>[][] result = b.calculShortestPaths(size,liens);
		b.calculerBetweeness(result,size);

//		//debug print chemins
		for(int i=1;i<size;i++) {
			for(int j = i+1;j<size;j++) {
				ArrayList<Integer> r = result[i][j];
				System.out.print("result ["+ i + "]["+j+"] = {");
				for(int x:r) {
					System.out.print(x+",");
				}
				System.out.println("}");


			}
		}
//	
	}
	
	
	
}
