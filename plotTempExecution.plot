set terminal png size 1200,800
set output "resultTemps.png"
set grid
set xlabel "nombre de livres"
set ylabel "Temp d'execution"
set title "Temps d'execution par les indice de centralite "
set style line 11 lt 1 lc rgb "#10bb70" lw 3
set style line 10 linetype 1 linecolor rgb "#5080cb" lw 3
set style line 9 lt rgb "red" lw 3 pt 6
set style line 8 lt rgb "purple" lw 3 pt 6

plot "Results/TempsExecution.txt" using 1:3 title 'PageRank' with lines linestyle 10,\
     "Results/TempsExecution.txt" using 1:4 title 'Betweeness' with lines  linestyle 11 ,\
     "Results/TempsExecution.txt" using 1:5 title 'Closeness' with lines  linestyle 9