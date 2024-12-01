public class FlightPath implements Comparable<FlightPath> {

    LinkedList<String> cities;
    int totalCost;
    int totalTime;

    public FlightPath() {
        cities = new LinkedList<>();
        totalCost = 0;
        totalTime = 0;
    }

    public void addCity(String city) {
        cities.add(city);
    }

    public void addCost(int cost) {
        totalCost += cost;
    }

    public void addTime(int time) {
        totalTime += time;
    }

    public LinkedList<String> getCities() {
        return cities;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getTotalTime() {
        return totalTime;
    }

    // Used for sorting paths
    @Override
    public int compareTo(FlightPath other) {
        // Default comparison by cost
        return Integer.compare(this.totalCost, other.totalCost);
    }

    // Method to compare by time instead of cost
    public int compareByTime(FlightPath other) {
        return Integer.compare(this.totalTime, other.totalTime);
    }

    @Override
    public String toString() {
        StringBuilder path = new StringBuilder();
        Node<String> current = cities.getHead();

        while (current != null) {
            path.append(current.data);
            if (current.next != null) {
                path.append(" -> ");
            }
            current = current.next;
        }

        return path + ". Time: " + totalTime + " Cost: " + totalCost;
    }
}
