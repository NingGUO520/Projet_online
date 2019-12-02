package tme4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * lire les livres sur la base http://www.gutenberg.org/files
 * @author Ning GUO
 * @author Katia AMICHI
 *
 */
public class LireFile {
	public Map<String, Map<String,Integer>> getDatabase( int nbfile, String saveNameFiles) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveNameFiles)));
		
		// pour chaque livre : les mots avec leurs occurences
		Map<String, Map<String,Integer>> database = new HashMap<>();
		int i = 0;
		while(database.size()<nbfile) {
			String nomLivre;
			if(i<10) {
				nomLivre = "1000"+i;
			}else if(i<100) {
				nomLivre = "100"+i;
			}else if(i<1000) {
				nomLivre = "10"+i;
			}else {
				nomLivre = "1"+i;
			}
			// On stocke le mot et son occurence 
			Map<String, Integer> livre = new HashMap<String, Integer>();
			String titre = "";
			URL oracle = new URL("http://www.gutenberg.org/files/"+nomLivre+"/"+nomLivre+".txt");
			BufferedReader in;
			try {
				in = new BufferedReader(
						new InputStreamReader(oracle.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if(inputLine.startsWith("Title:")) {
						titre =  inputLine.substring(6);

					}
					String[] words = inputLine.split("\\s+");
					for (int k = 0; k < words.length; k++) {
						words[k] = words[k].replaceAll("[^\\w]", "");
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
			} catch (FileNotFoundException e) {
//				System.out.println("files/"+nomLivre+"/"+nomLivre+".txt no found, passer a livre suivant" );
			} catch (IOException e) {
				e.printStackTrace();
			}

			// si on trouve pas le livre
			if(livre.size()==0) {
				i++;
				continue;
			}
			
			// la somme de mots dans ce livre
			int sum = 0;
			for(Entry<String, Integer> e : livre.entrySet()) {
				String mot = e.getKey();
				int nb = e.getValue();
				sum+=nb;
			}
			//Si ce livre ne contient pas assez de mots, on l'abandonne
			if(sum<10000){
				System.out.println("The book <<"+ titre + ">> hasn't enough words (<10000 words), we abandon it\n" );
				i++;
				continue;
			}
			System.out.println(sum+" words in The book <<"+ titre + ">>  has been read by success");
			database.put(titre, livre);
			i++;
		
			writer.write("id" + ", " + "titre" + ", " + " nbWords" + "\n");
			writer.write(nomLivre + ", " + titre + ", " + sum + "\n");			
		}
		writer.close();

		System.out.println("Il y a "+ database.size()+"livres dans cette database");
		return database;
	}
}
