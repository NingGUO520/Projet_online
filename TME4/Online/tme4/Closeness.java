package tme4;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.*;

public class Closeness {
	public double[][] floydWarshall(double[][] mat){
		int n = mat.length;
		double [][] W = mat;
		
		for(int k = 0; k < n; k++) {
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					W[i][j] = Math.min(W[i][j],W[i][k] + W[k][j]);
				}
			}
		}
		return W;
	}
	
	public Map<Integer, Double> closeness(double [][] W) throws IOException{
		Map<Integer, Double> CCFiles = new HashMap<Integer, Double>();
		int n = W.length;
		double val = 0.0;
				
		for(int i = 0; i < n; i++) {
			val = 0.0;
			for(int j = 0; j < n; j++) {
				if(i!=j) {
					val += W[i][j];
				}
			}
			CCFiles.put(i, n/val);
		}
		
		return CCFiles;
	}
	public void printResult(Map<Integer, Double> closenessResult) {
		closenessResult.entrySet().forEach(entry -> {
		    System.out.println("closeness du sommet " + entry.getKey() + " = " + entry.getValue());
		}); 	
	}
	
	/*public static Map<String, Double> closenessCentrality(ArrayList<String> files) throws IOException{
		Map<String, Double> CCFiles = new HashMap<String, Double>();
		
		class Value{
			double value = 0.0; 
			public void incremValue(double val) {this.value += val;}
			public void setValue() {this.value = 0.0;}
			public double getValue() {return this.value;}
		}
		
		Value val = new Value();
		files.forEach(d1 -> {
			val.setValue();
			files.forEach(d2 -> {
				if(!d1.equals(d2)) {
					try {
						val.incremValue(distanceJaccard(index(d1),index(d2)));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			CCFiles.put(d1, 1.0/val.getValue());
		});
		return CCFiles;
	}*/
	
	
	public static void writeMap(Map<String, Integer> mHashMap, String OutputFile) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutputFile)))) {
		    mHashMap.forEach((key, value) -> {
		        try { writer.write(key + " " + value + System.lineSeparator()); }
		        catch (IOException ex) { throw new UncheckedIOException(ex); }
		    });
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
	}
	
	public static Map<String, Long> index(String fileName) throws IOException {
		return Files.lines(Paths.get(fileName)).
				map(String::toLowerCase).
				map(line -> line.split("[\\s,:;!?.]+")).
				flatMap(Arrays::stream).
				filter(s -> s.matches("[a-zA-z-']+")).
				collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
	
}