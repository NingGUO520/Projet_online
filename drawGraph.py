import networkx as nx
import matplotlib.pyplot as plt

input = "Results/GraphRapport.edges"
output = "Results/graph.png"


def plotGraph():
    f = open(input, "r")

    G = nx.Graph()
    labels = []
    l = 0

    for line in f:
        s = line.split(" ")
        G.add_edge(int(s[0]), int(s[1]))
        labels.append(str(l))
        l += 1

    pos = nx.spring_layout(G, k=0.5, iterations=20)

    nx.draw(G, pos, with_labels=True)
    plt.savefig(output)  # save as png
    plt.show()  # display


def plotGraphColor():
    """
    G = nx.erdos_renyi_graph(20, 0.1)
    color_map = []
    for node in G:
        if node < 10:
            color_map.append('blue')
        else:
            color_map.append('green')
    nx.draw(G, node_color=color_map, with_labels=True)
    plt.show()
    """

    f = open(input, "r")
    color_map = []
    G = nx.Graph()
    labels = []
    l = 0
    nodes = []
    for line in f:
        # print("line : " + line)
        s = line.split(" ")
        G.add_edge(int(s[0]), int(s[1]))
        labels.append(str(l))
        l += 1
        if int(s[0]) not in nodes:
            nodes.append(int(s[0]))
        if int(s[1]) not in nodes:
            nodes.append(int(s[1]))

        # print("s[0] : " + s[0])
    print("node : ", nodes)
    for n in nodes:
        if n in [2]:
            color_map.append('navy')
        elif n in [5, 3, 9]:
            color_map.append('blue')
        elif n in [1, 4]:
            color_map.append('rayalblue')
        elif n in [7, 8]:
            color_map.append('skyblue')
        elif n in [0, 6]:
            color_map.append('paleturquoise')

    color_map = []

    for node in nodes:
        if node in [2]:
            color_map.append('navy')
        elif node in [5, 3, 9]:
            color_map.append('blue')
        elif node in [1, 4]:
            color_map.append('royalblue')
        elif node in [7, 8]:
            color_map.append('skyblue')
        elif node in [0, 6]:
            color_map.append('paleturquoise')


    print(color_map)

    # color_map = ["paleturquoise", "rayalblue", "navy", "blue",
      #            "rayalblue", "blue", "paleturquoise",
        #         "skyblue", "skyblue", "blue", ]

    pos = nx.spring_layout(G, k=0.5, iterations=20)

    nx.draw(G, pos, with_labels=True, node_color=color_map)
    plt.savefig(output)  # save as png
    plt.show()  # display


plotGraphColor()


def truc():
    G = nx.cycle_graph(10)
    pos = nx.spring_layout(G, iterations=200)

    nx.draw(G, pos, node_color=range(10), node_size=800, cmap=plt.cm.Blues)
    plt.show()
