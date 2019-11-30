package tme4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LireFile {


	public Map<String, Map<String,Integer>> getDatabase(int nbLivres) throws MalformedURLException {
		// pour chaque livre : les mots avec leurs occurences
		Map<String, Map<String,Integer>> database = new HashMap<>();
		for(int i = 8;i<8+nbLivres;i++) {
			String nomLivre;
			if(i<10) {
			 nomLivre = "1000"+i;
			}else {
				 nomLivre = "100"+i;
			}
			// On stocke le mot et son occurence 
			Map<String, Integer> livre = new HashMap<String, Integer>();

			URL oracle = new URL("http://www.gutenberg.org/files/"+nomLivre+"/"+nomLivre+".txt");
			BufferedReader in;
			try {
				in = new BufferedReader(
						new InputStreamReader(oracle.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					String[] words = inputLine.split("\\s+");
					for (int k = 0; k < words.length; k++) {
						words[k] = words[k].replaceAll("[^\\w]", "");
						//						System.out.print(words[k]+"|");
						if(words[k]!="") {
							if(!livre.containsKey(words[k])) {
								livre.put( words[k], 1);
							}else {
								livre.put(words[k], livre.get(words[k])+1);
							}
						}
					}
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("nombre de mot = "+ livre.size()+" pour livre "+ nomLivre);
			database.put(nomLivre, livre);
		}

		System.out.println("Il y a "+ database.size()+"livres dans cette database");
		return database;


	}

	public static void main(String[] args) throws MalformedURLException {

		LireFile l = new LireFile();
		l.getDatabase(50);

	}
}
