package application;

class Route {
	private String source;
	private String destination;
	private double distance;
	private String vehicleType;
	private double cost;
	private double leastTrafficTime;

	public Route(String source, String destination, double distance, String vehicleType, double cost,
			double leastTrafficTime) {
		this.source = source;
		this.destination = destination;
		this.distance = distance;
		this.vehicleType = vehicleType;
		this.cost = cost;
		this.leastTrafficTime = leastTrafficTime;
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public double getDistance() {
		return distance;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public double getCost() {
		return cost;
	}

	public double getLeastTrafficTime() {
		return leastTrafficTime;
	}

	@Override
	public String toString() {
		return "Route{" + "source='" + source + '\'' + ", destination='" + destination + '\'' + ", distance=" + distance
				+ ", vehicleType='" + vehicleType + '\'' + ", cost=" + cost + ", leastTrafficTime=" + leastTrafficTime
				+ '}';
	}

}