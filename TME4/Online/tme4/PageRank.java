package tme4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageRank {
	void prodmatvect(Graph G, int n, double []A, double []B){
		List<Integer> range = IntStream.rangeClosed(0, n-1).boxed().collect(Collectors.toList());
		range.forEach(i-> {B[i] = 0;});
		range.forEach(i-> {G.neighbors(i).forEach(v-> B[v] += (A[i]/G.degree(i)));});
	}

	public double [] page_rank(Graph G, double alpha, int t, int n) {
		double[] I = new double[n];
		double[] P = new double[n];
		double[] Ptmp = new double[n];
		
		List<Integer> range = IntStream.rangeClosed(0, n-1).boxed().collect(Collectors.toList());
		range.forEach(i-> {I[i] = 1.0/n; Ptmp[i] = 1.0/n; P[i] = 1.0/n; });
		
		double norm = 0;
		boolean change = false;
		int i = 0;
		int j = 0;
		
		while (!change && i < t){
			change = true;
			prodmatvect(G, n, Ptmp, P);
			norm = 0;
			
			for(j=0; j<n; j++){
				P[j] = (1-alpha)*P[j] + alpha*I[j];
				norm += P[j];
			}
			
			for(j=0; j<n; j++){
				P[j] += (1-norm)/(1.0*n);
				if(Ptmp[j] != P[j]) change = false;
				Ptmp[j] = P[j];
			}
			i = i+1;
		}
		return P;
	}
	


}