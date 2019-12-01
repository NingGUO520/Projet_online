# Projet_online
L'arborescence du projet:
	-projet_online  (le projet java)
		-src :les fichiers sources (.java) 
			-TestPerformance.java: on lance pour les tests
			-Main.java
			...
		-bin : fichiers .class
	-test : un repertoire contenant les peitis instances de test
	-plotDistrib.plot: plot le graphe de distribution de betweenness
	-plotTempExecution.plot : apres avoir execute TestPerformance.java, on peut plot un graphe
	-Results : tous les fichiers generes par les programme sont ici 
	-livres : un repertoire contenant les livres


Démarrage:		
	pour lancer: Main.java TestPerformance.java

		
Visualiser les results de test:

	$ gnuplot plotTempExecution.plot 
	$ gnuplot plotDistrib.plot

les graphes du resultat : resultTemps.png, BetweennessDis100.png, BetweennessDis200.png,
						  BetweennessDis500.png
		
Ning GUO, Katia AMICHI
02/12/2019