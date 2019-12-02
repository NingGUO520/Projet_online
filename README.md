# Projet_online
L'arborescence du projet:

	*TME4  (le projet java)
		*src :les fichiers sources (.java) 
			* TestPerformance.java: on lance pour evaluer le performance des 3 indices
			* Main.java : par defaut, quand on lance, il va tester sur les instances de tests
			...
		*bin : fichiers .class
	*test : un repertoire contenant les peitis instances de test
	*plotDistrib.plot: plot le graphe de distribution de betweenness
	*plotTempExecution.plot : apres avoir execute TestPerformance.java, on peut plot un graphe
	*Results : tous les fichiers generes par les programme sont ici 
	*livres : un repertoire contenant les livres


Démarrage:		
	lancer sous eclipse les fichiers:
	
	*Main.java 
		 pour tester sur les petits instances
	*TestPerformance.java 
		 pour tests sur une base de donnees de 50,100,200,500,1000,1500,2000 livres par les 3 indices
		 les résultat sont stoké dans le répertoire Results/

		
Visualiser les results de test:

	$ gnuplot plotTempExecution.plot 
	$ gnuplot plotDistrib.plot

les graphes du resultat : resultTemps.png,
			  BetweennessDis100.png,
			  BetweennessDis200.png,
			  BetweennessDis500.png
			  ...
		
Ning GUO, Katia AMICHI
02/12/2019
