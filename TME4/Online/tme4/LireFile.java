package tme4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LireFile {


	public Map<String, Map<String,Integer>> getDatabase(ArrayList<String> files, int nbfile) throws MalformedURLException {
		// pour chaque livre : les mots avec leurs occurences
		Map<String, Map<String,Integer>> database = new HashMap<>();
		for(int a = 0 ;a < nbfile;a++) {
			String nomLivre = files.get(a);
			//recupere le prefix du nom de fichier 
			int i = 0;
			char c = nomLivre.charAt(i);
			while(Character.isDigit(c)) {

				i++;
				c = nomLivre.charAt(i);
			}
			
			String prefix;
			if(i>5) {
				prefix = nomLivre.substring(0,5);
			}else {

				prefix= nomLivre.substring(0,i);
			}
			if(!nomLivre.endsWith("txt")) {
				nomLivre = nomLivre.substring(0, nomLivre.length() - 6);

			}
			// On stocke le mot et son occurence 
			Map<String, Integer> livre = new HashMap<String, Integer>();
			String titre = "";
			System.out.println("nomLivre : "+ nomLivre);
			URL oracle = new URL("http://www.gutenberg.org/files/"+prefix+"/"+nomLivre);
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

			System.out.println("nombre de mot = "+ livre.size()+" pour livre <<"+ titre +">");
			database.put(titre, livre);
		}

		System.out.println("Il y a "+ database.size()+"livres dans cette database");
		return database;


	}

	public static void main(String[] args) throws MalformedURLException {


	}
}
