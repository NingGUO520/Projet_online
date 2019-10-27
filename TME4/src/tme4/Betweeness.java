package tme4;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return aretes;
	}
	
		public String[][] calculShortestPaths(int size,ArrayList<Pair> liens) {
		String[][] paths=new String[size][size];
		for (int i=0;i<size;i++) for (int j=0;j<size;j++) paths[i][j]=Integer.toString(j) ;

		//		    matrice d'adjacence
		double[][] m = new double[size][size];
		for(Pair pair : liens) {
			m[pair.p1][pair.p2] = 1;
			m[pair.p2][pair.p1] = 1;
		}

		for(int i =0;i<size;i++) {
			for (int j=0;j<size;j++) {
				if(m[i][j]!=1)
					m[i][j]= Double.MAX_VALUE;

			}
		}
		
		for(int k = 0 ; k< size;k++) {
			for(int i = 0 ; i< size;i++) {
				for(int j = i+1 ; j<size;j++) {
					if(m[i][j]> m[i][k]+m[k][j]) {
						m[i][j]= m[i][k]+m[k][j];
						paths[i][j]=paths[i][k];
						
					}    
//					if(m[i][j] == m[i][k]+m[k][j]) {
//						paths[i][j]=paths[i][k]+"|"+paths[i][j];
//					}
					
						    	  	    	    	    	
				}    	           	        	
			}
		}
		return paths;
	}


	public static void main(String[] args)  {

		Betweeness b = new Betweeness();
		String fileName = "./rollernet.dyn.gz";
		List<Pair> aretes =  b.lireFile(fileName);
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
		String[][] result = b.calculShortestPaths(size,liens);
		for(int i=0;i<size;i++)
			for(int j = i+1;j<size;j++)
				System.out.println(result[i][j]);
		
		
		
		
	}
}
