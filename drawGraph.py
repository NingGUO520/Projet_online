import networkx as nx
import matplotlib.pyplot as plt

input = "Results/edgesGraph.edges"
output = "Results/graph.png"

f=open(input, "r")

G=nx.Graph()
labels = []
l=0

for line in f:
    s=line.split(" ")
    G.add_edge(int(s[0]), int(s[1]))
    labels.append(str(l))
    l += 1

pos = nx.spring_layout(G,k=0.5,iterations=20)

nx.draw(G,pos, with_labels=True)
plt.savefig(output) # save as png
plt.show() # display
