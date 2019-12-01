set terminal png size 1200,800
set output "BetweennessDis500.png"
set grid
set xlabel "betweenness"
set ylabel "nombre de livres"
set title "Distribution de betweenness pour 500 livres "

plot "Results/BeDistribution500.txt" using 1:2 