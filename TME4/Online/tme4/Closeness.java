package tme4;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.io.*;

public class Closeness {
	public double[][] floydWarshall(double[][] mat){
		int n = mat.length;
		double [][] W = mat;
		
		for(int k = 0; k < n; k++) 
			for(int i = 0; i < n; i++) 
				for(int j = 0; j < n; j++) 
					W[i][j] = Math.min(W[i][j],W[i][k] + W[k][j]);
		return W;
	}
	
	public Map<Integer, Double> closeness(double [][] W) throws IOException{
		Map<Integer, Double> CCFiles = new HashMap<Integer, Double>();
		int n = W.length;
				
		for(int i = 0; i < n; i++)
			CCFiles.put(i, n/(Arrays.stream(W[i]).sum() - W[i][i]));
		
		return CCFiles;
	}

	public static Map<String, Long> index(String fileName) throws IOException {
		return Files.lines(Paths.get(fileName)).
				map(String::toLowerCase).
				map(line -> line.split("[\\s,:;!?.]+")).
				flatMap(Arrays::stream).
				filter(s -> s.matches("[a-zA-z-']+")).
				collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
	
	public void printResult(Map<Integer, Double> closenessResult) {
		closenessResult.entrySet().forEach(entry -> {
		    System.out.println("closeness du sommet " + entry.getKey() + " = " + entry.getValue());
		}); 	
	}
	
	public static void writeMap(Map<String, Integer> mHashMap, String OutputFile) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutputFile)))) {
		    mHashMap.forEach((key, value) -> {
		        try { writer.write(key + " " + value + System.lineSeparator()); }
		        catch (IOException ex) { throw new UncheckedIOException(ex); }
		    });
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
	}
	
}