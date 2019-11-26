package tme4;

import java.util.Map;
import java.util.stream.Collectors;

public class DistanceJaccard {

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
		return result;
	}
	
}
