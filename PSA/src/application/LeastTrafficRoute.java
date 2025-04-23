package application;

import java.util.List;

public class LeastTrafficRoute {
	private RouteManager routeManager;

	public LeastTrafficRoute(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

	// Finds the least traffic route from start to destination with optional
	// intermediate nodes
	public List<String> findLeastTrafficRoute(String start, List<String> intermediates, String destination,
			String vehicleType) {
		return routeManager.findLeastTrafficPathWithIntermediates(start, intermediates, destination, vehicleType);
	}

	// Calculates the total traffic of a given path for the specified vehicle type
	public double getRouteTraffic(List<String> path, String vehicleType) {
		return routeManager.getRouteTraffic(path, vehicleType);
	}

	// Calculates the distance of a given path for the specified vehicle type
	public double getRouteDistance(List<String> path, String vehicleType) {
		return routeManager.getRouteDistance(path, vehicleType);
	}

	// Calculates the cost of a given path for the specified vehicle type
	public double getRouteCost(List<String> path, String vehicleType) {
		return routeManager.getRouteCost(path, vehicleType);
	}
}
