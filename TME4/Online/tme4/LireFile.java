package tme4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class LireFile {


	public Map<String, Map<String,Integer>> getDatabase( int nbfile) throws MalformedURLException {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(livre.size()==0) {
				i++;
				continue;
			}
			int sum = 0;
			for(Entry<String, Integer> e : livre.entrySet()) {
				String mot = e.getKey();
				int nb = e.getValue();
				sum+=nb;
			}
			if(sum<10000){
				System.out.println("The book <<"+ titre + ">> hasn't enough words (<10000 words), we abandon it\n" );

				i++;
				continue;
			}
			System.out.println(sum+" words in The book <<"+ titre + ">>  has been read by success");
//			System.out.println("nombre de mot  = "+sum+" pour livre <<"+ titre +">>");
			database.put(titre, livre);
			i++;
		}

		System.out.println("Il y a "+ database.size()+"livres dans cette database");
		return database;


	}

	public static void main(String[] args) throws MalformedURLException {


	}
}
