package edu.unh.cs980;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PageRankMain {
	static Float randomJump = (float) 0.15;
	static int number_of_edges = 0;
	static int number_of_nodes = 0;
	static int steps = 30;
	static int tops = 10;
	static boolean personlizedPageRank = false;
	static HashMap<Integer, PageNode> init_matrix;
	static HashMap<Integer, Float> score_map;

	static HashMap<Integer, PageNode> seed_matrix;

	public static void main(String[] args) {
		System.out.println("Start...");
		init_matrix = new HashMap<Integer, PageNode>();
		seed_matrix = new HashMap<Integer, PageNode>();

		// String filePath = args[0];

		if (args.length != 0) {
			personlizedPageRank = Boolean.valueOf(args[0].toLowerCase());
		}
		try {
			init_matrix = CreateNodesMap("input/graph.txt");
			if (personlizedPageRank) {
				seed_matrix = CreateNodesMap("input/seeds.txt");
			}

			System.out.println("Number of components: 1");
			for (int i = 0; i < steps; i++) {
				for (Map.Entry<Integer, PageNode> entry : init_matrix.entrySet()) {
					PageNode node = entry.getValue();
					if (i == 0) {
						if (personlizedPageRank) {
							if (seed_matrix.containsKey(node.getNodeId())) {
								node.setNodeScore((float) 1 / number_of_nodes);
							} else {
								node.setNodeScore(0);
							}
						} else {

							node.setNodeScore((float) 1 / number_of_nodes);
						}
						// node.setNodeScore((float) 1.0);
					} else {
						float prev_score = node.getNodeScore();
						node.setPrevScore(prev_score);
						float score = (float) randomJump / number_of_nodes;
						float partial_score = (float) 0.0;
						for (Map.Entry<Integer, PageNode> entry2 : init_matrix.entrySet()) {
							PageNode node2 = entry2.getValue();
							ArrayList<Integer> linkedNodes = node2.getLinkedNodes();

							if (!entry2.getKey().equals(entry.getKey())) {
								if (linkedNodes.contains(node.getNodeId())) {
									partial_score += (float) node2.getNodeScore() / linkedNodes.size();
								}
							}
						}
						score += (float) (1 - randomJump) * partial_score;
						node.setNodeScore(score);
					}
					init_matrix.put(entry.getKey(), node);
				}
			}

			score_map = new HashMap<Integer, Float>();
			// Normalize score;
			float norm = (float) 0.0;
			for (Map.Entry<Integer, PageNode> entry : init_matrix.entrySet()) {
				norm += entry.getValue().getNodeScore();
			}

			for (Map.Entry<Integer, PageNode> entry : init_matrix.entrySet()) {
				PageNode node = entry.getValue();
				float score = node.getNodeScore();
				score_map.put(node.getNodeId(), (float) score / norm);
			}

			// Print sorted results
			System.out.println("PageRank of nodes, in descending order:");
			int count = 1;
			for (Map.Entry<Integer, Float> entry : sortByValue(score_map).entrySet()) {
				if (count > 10) {
					break;
				}
				System.out.println("Rank: " + count + "        " + entry.getKey() + "  =====> " + entry.getValue());
				count += 1;
			}
			if (personlizedPageRank) {
				System.out.println("PageRank score for seeds set: ");
				for (Map.Entry<Integer, PageNode> entry : seed_matrix.entrySet()) {
					System.out.println(entry.getKey() + "   =====> " + score_map.get(entry.getKey()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashMap<Integer, PageNode> CreateNodesMap(String filepath) throws NumberFormatException, IOException {
		HashMap<Integer, PageNode> matrix = new HashMap<Integer, PageNode>();

		BufferedReader reader = new BufferedReader(new FileReader(filepath));

		String line;
		while ((line = reader.readLine()) != null) {
			// System.out.println(line);
			String[] list = line.split("\\s+");
			List<String> str_list = Arrays.asList(list);

			if (!str_list.isEmpty()) {
				ArrayList<Integer> linked_nodes = new ArrayList<Integer>();
				PageNode node = new PageNode();
				node.setNodeId(Integer.valueOf(str_list.get(0)));

				if (str_list.size() > 1) {
					for (int i = 1; i < str_list.size(); i++) {
						linked_nodes.add(Integer.valueOf(str_list.get(i)));
					}
					number_of_edges += linked_nodes.size();
				}
				node.setLinkedNodes(linked_nodes);
				matrix.put(node.getNodeId(), node);
				// System.out.println(node.toString());
			}
		}

		number_of_nodes = matrix.keySet().size();
		System.out.println("Number of edges: " + number_of_edges);
		System.out.println("Number of nodes: " + number_of_nodes);
		System.out.println("Random jump factor:  " + randomJump);

		reader.close();

		return matrix;
	}

	// Sort Descending HashMap<Integer, float>Map by its value
	private static HashMap<Integer, Float> sortByValue(Map<Integer, Float> unsortMap) {

		List<Map.Entry<Integer, Float>> list = new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {

			public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		HashMap<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
		for (Map.Entry<Integer, Float> entry : list)

		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

}
