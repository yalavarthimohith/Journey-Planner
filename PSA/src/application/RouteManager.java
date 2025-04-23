package application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.scene.control.ComboBox;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class RouteManager {
	private Map<String, Map<String, List<Route>>> graph;
	private ComboBox<String> sourceComboBox;
	private ComboBox<String> destinationComboBox;
	private static final String ROUTE_DATA_FILE = "routesdatabase.json";
	private final Gson gson = new Gson();

	public RouteManager(ComboBox<String> sourceComboBox, ComboBox<String> destinationComboBox) {
		this.graph = new HashMap<>();
		this.sourceComboBox = sourceComboBox;
		this.destinationComboBox = destinationComboBox;
		loadRoutesFromFile();
	}

	// Load routes from JSON file
	public void loadRoutesFromFile() {
		File file = new File(ROUTE_DATA_FILE);
		if (!file.exists())
			return;

		try (FileReader reader = new FileReader(file)) {
			Type type = new TypeToken<Map<String, Map<String, List<Route>>>>() {
			}.getType();
			Map<String, Map<String, List<Route>>> loadedGraph = gson.fromJson(reader, type);
			if (loadedGraph != null) {
				graph = loadedGraph;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to get unique source locations
	public Set<String> getSourceLocations() {
		Set<String> sourceLocations = new HashSet<>();
		for (Map<String, List<Route>> vehicleRoutes : graph.values()) {
			for (List<Route> routes : vehicleRoutes.values()) {
				for (Route route : routes) {
					sourceLocations.add(route.getSource());
				}
			}
		}
		return sourceLocations;
	}

	// Method to get unique destination locations
	public Set<String> getDestinationLocations() {
		Set<String> destinationLocations = new HashSet<>();
		for (Map<String, List<Route>> vehicleRoutes : graph.values()) {
			for (List<Route> routes : vehicleRoutes.values()) {
				for (Route route : routes) {
					destinationLocations.add(route.getDestination());
				}
			}
		}
		return destinationLocations;
	}

	// Save routes to JSON file
	private void saveRoutesToFile() {
		try (FileWriter writer = new FileWriter(ROUTE_DATA_FILE)) {
			gson.toJson(graph, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Add a route with support for vehicle types
	public void addRoute(Route route, boolean bidirectional) {
		String source = route.getSource();
		String destination = route.getDestination();
		String vehicleType = route.getVehicleType();

		if (source == null || source.isEmpty() || destination == null || destination.isEmpty() || vehicleType == null
				|| vehicleType.isEmpty()) {
			System.out.println("Error: Route source, destination, or vehicle type is missing: " + route);
			return;
		}

		// Add the route to the graph for the specified vehicle type
		graph.computeIfAbsent(source, k -> new HashMap<>()).computeIfAbsent(vehicleType, k -> new ArrayList<>())
				.removeIf(r -> r.getDestination().equals(destination));
		graph.get(source).get(vehicleType).add(route);

		// Add reverse route if bidirectional
		if (bidirectional) {
			Route reverseRoute = new Route(destination, source, route.getDistance(), vehicleType, route.getCost(),
					route.getLeastTrafficTime());
			graph.computeIfAbsent(destination, k -> new HashMap<>())
					.computeIfAbsent(vehicleType, k -> new ArrayList<>())
					.removeIf(r -> r.getDestination().equals(source));
			graph.get(destination).get(vehicleType).add(reverseRoute);
		}

		saveRoutesToFile();
		updateComboBoxes();
		System.out.println("Route added: " + source + " -> " + destination + " for vehicle type: " + vehicleType);
	}

	// Rebuild the graph from file
	public void rebuildGraph() {
		System.out.println("Rebuilding the graph...");
		loadRoutesFromFile();
		System.out.println("Graph rebuilt: " + graph);
	}

	// Update ComboBoxes with new source and destination data
	private void updateComboBoxes() {
		Set<String> sourceLocations = getSourceLocations();
		Set<String> destinationLocations = getDestinationLocations();

		sourceComboBox.getItems().clear();
		destinationComboBox.getItems().clear();

		sourceComboBox.getItems().addAll(sourceLocations);
		destinationComboBox.getItems().addAll(destinationLocations);
	}

	// Finds the shortest path with intermediate nodes

	public List<String> findShortestPathWithIntermediates(String start, List<String> intermediates, String destination,
			String vehicleType) {
		if (intermediates == null || intermediates.isEmpty()) {
			return findShortestPath(start, destination, vehicleType);
		}

		List<String> fullPath = new ArrayList<>();
		String current = start;

		for (String intermediate : intermediates) {
			// Find the shortest path to the next intermediate
			List<String> pathSegment = findShortestPath(current, intermediate, vehicleType);
			if (pathSegment.isEmpty()) {
				System.out.println(
						"No path found from " + current + " to " + intermediate + " for vehicle type: " + vehicleType);
				return new ArrayList<>();
			}

			// Avoid duplicate nodes when concatenating segments
			if (!fullPath.isEmpty()) {
				fullPath.remove(fullPath.size() - 1);
			}
			fullPath.addAll(pathSegment);

			current = intermediate;
		}

		// Add the final segment to the destination
		List<String> finalSegment = findShortestPath(current, destination, vehicleType);
		if (finalSegment.isEmpty()) {
			System.out.println(
					"No path found from " + current + " to " + destination + " for vehicle type: " + vehicleType);
			return new ArrayList<>();
		}

		if (!fullPath.isEmpty()) {
			fullPath.remove(fullPath.size() - 1);
		}
		fullPath.addAll(finalSegment);

		return new ArrayList<>(new LinkedHashSet<>(fullPath));
	}

	// Dijkstra's algorithm to find the shortest path

	private List<String> findShortestPath(String start, String destination, String vehicleType) {
		// Validate start and destination
		if (!graph.containsKey(start) || !graph.get(start).containsKey(vehicleType)) {
			System.out.println("Error: Start node or vehicle type does not exist in the graph.");
			return new ArrayList<>();
		}
		if (!graph.containsKey(destination)) {
			System.out.println("Error: Destination node does not exist in the graph.");
			return new ArrayList<>();
		}

		// Initialize data structures
		Map<String, Double> distances = new HashMap<>();
		Map<String, String> previousNodes = new HashMap<>();
		PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
		Set<String> visited = new HashSet<>();

		// Set initial distances to infinity except for the start node
		for (String node : graph.keySet()) {
			distances.put(node, Double.POSITIVE_INFINITY);
		}
		distances.put(start, 0.0);
		queue.add(start);

		// Process nodes using Dijkstra's algorithm
		while (!queue.isEmpty()) {
			String currentNode = queue.poll();

			if (visited.contains(currentNode))
				continue;
			visited.add(currentNode);

			// Stop if we reach the destination
			if (currentNode.equals(destination))
				break;

			// Relax edges for all neighbors for the specific vehicle type
			for (Route edge : graph.getOrDefault(currentNode, new HashMap<>()).getOrDefault(vehicleType,
					new ArrayList<>())) {
				String neighbor = edge.getDestination();
				double routeDistance = edge.getDistance();

				if (routeDistance <= 0)
					continue;

				double newDistance = distances.get(currentNode) + routeDistance;
				if (newDistance < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
					distances.put(neighbor, newDistance);
					previousNodes.put(neighbor, currentNode);
					queue.add(neighbor);
				}
			}
		}

		// If destination is unreachable
		if (!visited.contains(destination)) {
			System.out.println("Error: Destination " + destination + " is unreachable from " + start
					+ " for vehicle type: " + vehicleType);
			return new ArrayList<>();
		}

		// Reconstruct the path
		List<String> path = reconstructPath(start, destination, previousNodes);

		// Validate the path
		if (path.isEmpty()) {
			System.out.println("No valid shortest path found from " + start + " to " + destination
					+ " for vehicle type: " + vehicleType);
			return new ArrayList<>();
		}

		System.out.println("Shortest route found from " + start + " to " + destination + " for vehicle type: "
				+ vehicleType + ": " + path + " (Distance: " + distances.get(destination) + " km)");
		return path;
	}

	private List<String> reconstructPath(String start, String destination, Map<String, String> previousNodes) {
		List<String> path = new ArrayList<>();
		for (String at = destination; at != null; at = previousNodes.get(at)) {
			path.add(at);
			if (at.equals(start))
				break;
		}
		Collections.reverse(path);

		// Ensure the reconstructed path starts with the 'start' node
		if (!path.isEmpty() && path.get(0).equals(start)) {
			return path;
		}

		System.out.println("Warning: Reconstructed path is invalid.");
		return new ArrayList<>();
	}

	// Finds the cost optimal path with intermediate nodes

	public List<String> findCostOptimalPathWithIntermediates(String start, List<String> intermediates,
			String destination, String vehicleType) {
		if (intermediates == null || intermediates.isEmpty()) {
			return findCostOptimalPath(start, destination, vehicleType);
		}

		List<String> fullPath = new ArrayList<>();
		String current = start;
		double totalCost = 0.0;

		// Traverse through each intermediate node
		for (String intermediate : intermediates) {
			// Find the cost-optimal path to the next intermediate
			List<String> pathSegment = findCostOptimalPath(current, intermediate, vehicleType);
			if (pathSegment.isEmpty()) {
				System.out.println("No cost-optimal path found from " + current + " to " + intermediate
						+ " for vehicle type: " + vehicleType);
				return new ArrayList<>();
			}

			// Accumulate total cost for the segment
			totalCost += getRouteCost(pathSegment, vehicleType);

			// Avoid duplicate nodes when concatenating segments
			if (!fullPath.isEmpty()) {
				fullPath.remove(fullPath.size() - 1);
			}
			fullPath.addAll(pathSegment);

			current = intermediate;
		}

		// Add the final segment to the destination
		List<String> finalSegment = findCostOptimalPath(current, destination, vehicleType);
		if (finalSegment.isEmpty()) {
			System.out.println("No cost-optimal path found from " + current + " to " + destination
					+ " for vehicle type: " + vehicleType);
			return new ArrayList<>();
		}

		totalCost += getRouteCost(finalSegment, vehicleType);
		if (!fullPath.isEmpty()) {
			fullPath.remove(fullPath.size() - 1);
		}
		fullPath.addAll(finalSegment);

		System.out.println("Cost-optimal route from " + start + " to " + destination + " for vehicle type: "
				+ vehicleType + ": " + fullPath + " (Cost: " + totalCost + ")");
		return fullPath;
	}

	// Dijkstra's algorithm to find the cost-optimal path

	private List<String> findCostOptimalPath(String start, String destination, String vehicleType) {
		// Validate start and destination
		if (!graph.containsKey(start) || !graph.get(start).containsKey(vehicleType)) {
			System.out.println("Error: Start node or vehicle type does not exist in the graph.");
			return new ArrayList<>();
		}
		if (!graph.containsKey(destination)) {
			System.out.println("Error: Destination node does not exist in the graph.");
			return new ArrayList<>();
		}

		// Initialize data structures
		Map<String, Double> costs = new HashMap<>();
		Map<String, String> previousNodes = new HashMap<>();
		PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingDouble(costs::get));
		Set<String> visited = new HashSet<>();

		// Set initial costs to infinity except for the start node
		for (String node : graph.keySet()) {
			costs.put(node, Double.POSITIVE_INFINITY);
		}
		costs.put(start, 0.0);

		// Add the start node to the queue
		queue.add(start);

		// Process nodes in the priority queue
		while (!queue.isEmpty()) {
			String current = queue.poll();

			// Skip nodes already visited
			if (visited.contains(current))
				continue;
			visited.add(current);

			// Stop if we reach the destination
			if (current.equals(destination))
				break;

			// Relax edges for all neighbors for the specific vehicle type
			for (Route edge : graph.getOrDefault(current, new HashMap<>()).getOrDefault(vehicleType,
					new ArrayList<>())) {
				String neighbor = edge.getDestination();
				double newCost = costs.get(current) + edge.getCost();

				// Update costs and queue only if a better path is found
				if (newCost < costs.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
					costs.put(neighbor, newCost);
					previousNodes.put(neighbor, current);
					queue.add(neighbor);
				}
			}
		}

		// Reconstruct the path from the destination back to the start
		List<String> path = reconstructPath(start, destination, previousNodes);

		// Validate the reconstructed path
		if (path.isEmpty() || !path.get(0).equals(start)) {
			System.out.println("No valid cost-optimal path found from " + start + " to " + destination
					+ " for vehicle type: " + vehicleType);
			return new ArrayList<>();
		}

		// Check for redundant nodes and remove loops
		path = removeRedundantNodes(path);

		// Display the cost-optimal path
		System.out.println("Cost-optimal route found from " + start + " to " + destination + " for vehicle type: "
				+ vehicleType + ": " + path + " (Cost: " + costs.get(destination) + ")");
		return path;
	}

	// Utility method to remove redundant nodes in the path
	private List<String> removeRedundantNodes(List<String> path) {
		Set<String> seen = new HashSet<>();
		List<String> cleanPath = new ArrayList<>();
		for (String node : path) {
			if (!seen.contains(node)) {
				cleanPath.add(node);
				seen.add(node);
			}
		}
		return cleanPath;
	}

	// Gets the total toll cost for the given path

	public double getTotalTollCost(List<String> path, String vehicleType) {
		double totalTollCost = 0.0;

		for (int i = 0; i < path.size() - 1; i++) {
			String from = path.get(i);
			String to = path.get(i + 1);
			boolean foundRoute = false;

			// Retrieve routes for the specific vehicle type
			for (Route route : graph.getOrDefault(from, new HashMap<>()).getOrDefault(vehicleType, new ArrayList<>())) {
				if (route.getDestination().equals(to)) {
					totalTollCost += route.getCost();
					foundRoute = true;
					break;
				}
			}

			// If no route is found between 'from' and 'to', log an error
			if (!foundRoute) {
				System.out.println(
						"Error: No direct route between " + from + " and " + to + " for vehicle type: " + vehicleType);
				return Double.POSITIVE_INFINITY;
			}
		}

		return totalTollCost;
	}

	// Placeholder methods for getting all stations and routes from a given station
	public List<String> getAllStations() {
		// Implementation to return all station names
		return new ArrayList<>(graph.keySet());
	}

	public List<Route> getRoutesFrom(String station, String vehicleType) {
		// Check if the station exists and contains routes for the specified vehicle
		// type
		return graph.getOrDefault(station, new HashMap<>()).getOrDefault(vehicleType, new ArrayList<>());
	}

	// Finds the least traffic path with intermediate nodes

	public List<String> findLeastTrafficPathWithIntermediates(String start, List<String> intermediates,
			String destination, String vehicleType) {
		if (intermediates == null || intermediates.isEmpty()) {
			return findLeastTrafficPath(start, destination, vehicleType);
		}

		List<String> fullPath = new ArrayList<>();
		String current = start;

		for (String intermediate : intermediates) {
			// Find the least-traffic path to the next intermediate
			List<String> pathSegment = findLeastTrafficPath(current, intermediate, vehicleType);
			if (pathSegment.isEmpty()) {
				System.out.println(
						"No path found from " + current + " to " + intermediate + " for vehicle type: " + vehicleType);
				return new ArrayList<>(); // Return empty list if any segment fails
			}

			// Avoid duplicate nodes when concatenating segments
			if (!fullPath.isEmpty()) {
				fullPath.remove(fullPath.size() - 1);
			}
			fullPath.addAll(pathSegment);
			current = intermediate;
		}

		// Find the least-traffic path to the final destination
		List<String> finalSegment = findLeastTrafficPath(current, destination, vehicleType);
		if (finalSegment.isEmpty()) {
			System.out.println(
					"No path found from " + current + " to " + destination + " for vehicle type: " + vehicleType);
			return new ArrayList<>();
		}

		if (!fullPath.isEmpty()) {
			fullPath.remove(fullPath.size() - 1);
		}
		fullPath.addAll(finalSegment);

		return new ArrayList<>(new LinkedHashSet<>(fullPath));
	}

//  Dijkstra's algorithm to find the least traffic route

	private List<String> findLeastTrafficPath(String start, String destination, String vehicleType) {
		// Validate if the start and vehicleType exist in the graph
		if (!graph.containsKey(start) || !graph.get(start).containsKey(vehicleType)) {
			System.out.println("Error: Start node or vehicle type does not exist in the graph.");
			return new ArrayList<>();
		}
		if (!graph.containsKey(destination)) {
			System.out.println("Error: Destination node does not exist in the graph.");
			return new ArrayList<>();
		}

		// Check for a direct route first
		if (graph.get(start).containsKey(vehicleType)) {
			for (Route route : graph.get(start).get(vehicleType)) {
				if (route.getDestination().equals(destination)) {
					// Direct route found
					System.out.println("Direct route found: " + start + " -> " + destination + " (Traffic: "
							+ route.getLeastTrafficTime() + " hrs)");
					return Arrays.asList(start, destination);
				}
			}
		}

		// Initialize data structures for Dijkstra's algorithm
		Map<String, Double> trafficLevels = new HashMap<>();
		Map<String, String> previousNodes = new HashMap<>();
		PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingDouble(trafficLevels::get));
		Set<String> visited = new HashSet<>();

		// Set initial traffic levels to infinity except for the start node
		for (String node : graph.keySet()) {
			trafficLevels.put(node, Double.POSITIVE_INFINITY);
		}
		trafficLevels.put(start, 0.0);
		queue.add(start);

		// Process nodes using Dijkstra's algorithm
		while (!queue.isEmpty()) {
			String currentNode = queue.poll();

			if (visited.contains(currentNode))
				continue;
			visited.add(currentNode);

			if (currentNode.equals(destination))
				break;

			// Relax edges for all neighbors for the specific vehicle type
			List<Route> edges = graph.getOrDefault(currentNode, new HashMap<>()).getOrDefault(vehicleType,
					new ArrayList<>());
			for (Route edge : edges) {
				String neighbor = edge.getDestination();
				double newTraffic = trafficLevels.get(currentNode) + edge.getLeastTrafficTime();

				// Update traffic levels if a better path is found
				if (newTraffic < trafficLevels.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
					trafficLevels.put(neighbor, newTraffic);
					previousNodes.put(neighbor, currentNode);
					queue.add(neighbor);
				}
			}
		}

		// Reconstruct the path from the destination back to the start
		List<String> path = new ArrayList<>();
		for (String at = destination; at != null; at = previousNodes.get(at)) {
			path.add(at);
			if (at.equals(start))
				break;
		}
		Collections.reverse(path);

		// Validate the reconstructed path
		if (path.isEmpty() || !path.get(0).equals(start)) {
			System.out.println(
					"No valid path found from " + start + " to " + destination + " for vehicle type: " + vehicleType);
			return new ArrayList<>();
		}

		System.out.println("Least traffic route found from " + start + " to " + destination + " for vehicle type: "
				+ vehicleType + ": " + path);
		return path;
	}

	public double getRouteDistance(List<String> path, String vehicleType) {
		double totalDistance = 0.0;

		for (int i = 0; i < path.size() - 1; i++) {
			String from = path.get(i);
			String to = path.get(i + 1);
			boolean foundRoute = false;

			// Retrieve routes for the specific vehicle type
			for (Route route : graph.getOrDefault(from, new HashMap<>()).getOrDefault(vehicleType, new ArrayList<>())) {
				if (route.getDestination().equals(to)) {
					totalDistance += route.getDistance();
					foundRoute = true;
					break;
				}
			}

			// If no route is found between 'from' and 'to', log an error
			if (!foundRoute) {
				System.out.println(
						"Error: No direct route between " + from + " and " + to + " for vehicle type: " + vehicleType);
				return Double.POSITIVE_INFINITY; // Return infinity to indicate failure
			}
		}

		return totalDistance;
	}

	public double getRouteTraffic(List<String> path, String vehicleType) {
		double totalTraffic = 0.0;

		for (int i = 0; i < path.size() - 1; i++) {
			String from = path.get(i);
			String to = path.get(i + 1);
			boolean foundRoute = false;

			// Retrieve routes for the specific vehicle type
			for (Route route : graph.getOrDefault(from, new HashMap<>()).getOrDefault(vehicleType, new ArrayList<>())) {
				if (route.getDestination().equals(to)) {
					totalTraffic += route.getLeastTrafficTime();
					foundRoute = true;
					break;
				}
			}

			// If no route is found between 'from' and 'to', log an error
			if (!foundRoute) {
				System.out.println(
						"Error: No direct route between " + from + " and " + to + " for vehicle type: " + vehicleType);
				return Double.POSITIVE_INFINITY;
			}
		}

		return totalTraffic;
	}

	public double getRouteCost(List<String> path, String vehicleType) {
		double totalCost = 0.0;

		for (int i = 0; i < path.size() - 1; i++) {
			String from = path.get(i);
			String to = path.get(i + 1);
			boolean foundRoute = false;

			// Retrieve routes for the specific vehicle type
			for (Route route : graph.getOrDefault(from, new HashMap<>()).getOrDefault(vehicleType, new ArrayList<>())) {
				if (route.getDestination().equals(to)) {
					totalCost += route.getCost();
					foundRoute = true;
					break;
				}
			}

			// Log the running total cost for debugging purposes
			System.out.println("Total cost so far: " + totalCost);

			// If no route is found between 'from' and 'to', log an error
			if (!foundRoute) {
				System.out.println(
						"Error: No direct route between " + from + " and " + to + " for vehicle type: " + vehicleType);
				return Double.POSITIVE_INFINITY;
			}
		}

		return totalCost;
	}

}
