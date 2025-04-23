
package application;

import java.util.List;
import java.util.Set;

public interface RouteManagerInterface {
    void addRoute(Route route, boolean bidirectional);
    void removeRoute(String source, String destination, String vehicleType);
    List<String> findShortestPathWithIntermediates(String start, List<String> intermediates, String destination, String vehicleType);
    List<String> findLeastTrafficPathWithIntermediates(String start, List<String> intermediates, String destination, String vehicleType);
    List<String> findCostOptimalPathWithIntermediates(String start, List<String> intermediates, String destination, String vehicleType);
    double getRouteDistance(List<String> path, String vehicleType);
    double getRouteTraffic(List<String> path, String vehicleType);
    double getRouteCost(List<String> path, String vehicleType);
    Set<String> getSourceLocations();
    Set<String> getDestinationLocations();
}
