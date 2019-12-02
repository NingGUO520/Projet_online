package tme4;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @author Katia AMICHI
 * @author Ning GUO
 *
 */
public class DistanceJaccard {
	/**
	 * permet de calculer la distance de Jaccard entre deux documents
	 * 
	 * @param D1 le premier document (livre)
	 * @param D2 le deuxième document (livre)
	 * 
	 * @return la distance de Jaccard entre D1 et D2
	 */
	public static double distanceJaccard(Map<String, Integer> D1, Map<String, Integer> D2) {
		double intersection = 0.0;
		double unionD1D2 = 0.0;
		List<String> union = Stream.concat(D1.keySet().stream(), D2.keySet().stream()).distinct().collect(Collectors.toList());
		
		intersection = union.stream().
				map(m -> (D1.get(m) != null && D2.get(m) != null) ? 
						Math.max(D1.get(m), D2.get(m)) - Math.min(D1.get(m), D2.get(m)) : 
							D1.get(m) != null? D1.get(m) : D2.get(m)).
				collect(Collectors.toList()).stream().
				mapToInt(Integer::intValue).
				sum();
				
		unionD1D2 = union.stream().
				map(m -> (D1.get(m) != null && D2.get(m) != null) ? 
						Math.max(D1.get(m), D2.get(m)) : 
							D1.get(m) != null? D1.get(m) : D2.get(m)).
				collect(Collectors.toList()).stream().
				mapToInt(Integer::intValue).
				sum();
		
		return (unionD1D2 == 0 ? 1.0 : intersection / unionD1D2);		
	}
}
