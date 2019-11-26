package tme4;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.*;

public class Jaccard {
	
	public static Map<String, Double> closenessCentrality(ArrayList<String> files) throws IOException{
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
	}
	
	
	public static void writeMap(Map<String, Integer> mHashMap, String OutputFile) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutputFile)))) {
		    mHashMap.forEach((key, value) -> {
		        try { writer.write(key + " " + value + System.lineSeparator()); }
		        catch (IOException ex) { throw new UncheckedIOException(ex); }
		    });
		} catch(UncheckedIOException ex) { throw ex.getCause(); }
	}

	public static double distanceJaccard(Map<String, Long> D1, Map<String, Long> D2) {
		double intersection = 0.0;
		double unionD1D2 = 0.0;

		intersection = D1.keySet().stream().collect(Collectors.toList()).stream().
				map(k -> D2.get(k) != null ? Math.max(D1.get(k), D2.get(k)) - Math.min(D1.get(k), D2.get(k)) : D1.get(k)).
				collect(Collectors.toList()).stream().
				mapToLong(Long::longValue).
				sum() + 
				D2.keySet().stream().
				filter(k -> !D1.containsKey(k)).collect(Collectors.toList()).stream().
				map(k -> D2.get(k)).collect(Collectors.toList()).stream().
				mapToLong(Long::longValue).
				sum();

		unionD1D2 = D1.keySet().stream().collect(Collectors.toList()).stream().
				map(p -> D2.get(p) != null ? Math.max(D1.get(p), D2.get(p)) : D1.get(p)).collect(Collectors.toList()).stream().
				mapToLong(Long::longValue).
				sum() + 
				D2.keySet().stream().
				filter(p -> !D1.containsKey(p)).collect(Collectors.toList()).stream().
				map(p -> D2.get(p)).collect(Collectors.toList()).stream().
				mapToLong(Long::longValue).
				sum();
		
		double result = (unionD1D2 == 0 ? 1.0 : intersection / unionD1D2);
		/*System.out.println(" >> intersection : " + intersection);
		System.out.println(" >> unionD1D2 : " + unionD1D2);
		System.out.println("result : " + result);*/
		return result;
	}
	
	public static Map<String, Long> index(String fileName) throws IOException {
		return Files.lines(Paths.get(fileName)).
				map(String::toLowerCase).
				map(line -> line.split("[\\s,:;!?.]+")).
				flatMap(Arrays::stream).
				filter(s -> s.matches("[a-zA-z-']+")).
				collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	public static void edges(ArrayList<String> files, String fileEdges, double edgeThehard) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileEdges)));
        
        Map<String, Integer> indexFiles = new HashMap<String, Integer>();
        
        ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>();
        
        int i =0;
        for(String file : files) {
        	i++;
        	indexFiles.put(file, i);
        }

        files.forEach(d1 -> {
			files.forEach(d2 -> {
				if(!d1.equals(d2)) {
					try {
						double d = distanceJaccard(index(d1),index(d2));
						System.out.println("d : " + d);
						if(distanceJaccard(index(d1),index(d2)) <= edgeThehard){
							edges.add(new ArrayList<Integer>( 
						            Arrays.asList(indexFiles.get(d1), indexFiles.get(d2))));
							writer.write(indexFiles.get(d1) + " " + indexFiles.get(d2) + " " + d + "\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		});
        writer.close();
        writeMap(indexFiles, "fileIndex.txt");
	}

	
}