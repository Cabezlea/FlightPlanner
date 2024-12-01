import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class FlightPlanner {
    private LinkedList<Flight>[] adjacencyList;
    private ArrayList<String> cityIndex;

    private class PathState {
        String city;
        Node<Flight> nextFlight;

        PathState(String city, Node<Flight> nextFlight) {
            this.city = city;
            this.nextFlight = nextFlight;
        }
    }

    @SuppressWarnings("unchecked")
    public FlightPlanner() {
        adjacencyList = new LinkedList[100];
        cityIndex = new ArrayList<>();
    }

    private boolean cityExists(String name) {
        return cityIndex.contains(name);
    }

    private int addCity(String name) {
        if (!cityExists(name)) {
            cityIndex.add(name);
            int index = cityIndex.size() - 1;
            adjacencyList[index] = new LinkedList<>();
            return index;
        }
        return cityIndex.indexOf(name);
    }

    public void addFlight(String source, String destination, int cost, int time) {
        int sourceIdx = addCity(source);
        int destIdx = addCity(destination);


        adjacencyList[sourceIdx].add(new Flight(destination, cost, time));
        adjacencyList[destIdx].add(new Flight(source, cost, time));
    }

    private int getCityIndex(String name) {
        return cityIndex.indexOf(name);
    }

    public LinkedList<FlightPath> findPaths(String source, String destination, boolean sortByTime) {
        LinkedList<FlightPath> allPaths = new LinkedList<>();
        Stack<PathState> stack = new Stack<>();
        HashSet<String> visited = new HashSet<>();

        int sourceIdx = getCityIndex(source);
        if (sourceIdx == -1) return allPaths;


        visited.add(source);
        LinkedList<Flight> sourceFlights = adjacencyList[sourceIdx];
        if (sourceFlights != null) {
            stack.push(new PathState(source, sourceFlights.getHead()));
        }

        while (!stack.isEmpty()) {
            PathState currentState = stack.getList().getHead().data;
            String currentCity = currentState.city;
            Node<Flight> currentFlight = currentState.nextFlight;

            if (currentFlight == null) {
                // Backtrack
                stack.pop();
                visited.remove(currentCity);
                continue;
            }


            Flight flight = currentFlight.data;
            stack.pop();
            stack.push(new PathState(currentCity, currentFlight.next));

            if (!visited.contains(flight.destination)) {
                if (flight.destination.equals(destination)) {
                    // Found a path
                    stack.push(new PathState(flight.destination, null));
                    FlightPath path = createPath(stack, source, destination);
                    allPaths.add(path);
                    stack.pop();
                } else {
                    // Continue searching
                    visited.add(flight.destination);
                    int nextIdx = getCityIndex(flight.destination);
                    if (nextIdx != -1 && adjacencyList[nextIdx] != null) {
                        stack.push(new PathState(flight.destination, adjacencyList[nextIdx].getHead()));
                    }
                }
            }
        }

        sortPaths(allPaths, sortByTime);
        return allPaths;
    }

    private void removeFromVisited(LinkedList<String> visited, String city) {
        LinkedList<String> newVisited = new LinkedList<>();
        Node<String> current = visited.getHead();
        while (current != null) {
            if (!current.data.equals(city)) {
                newVisited.add(current.data);
            }
            current = current.next;
        }
        visited = newVisited;
    }

    private FlightPath createPath(Stack<PathState> stack, String source, String destination) {
        FlightPath path = new FlightPath();
        ArrayList<String> cities = new ArrayList<>();

        // Build the path
        LinkedList<PathState> states = stack.getList();
        Node<PathState> current = states.getHead();
        while (current != null) {
            if (!cities.contains(current.data.city)) { // Only add if not already in path
                cities.add(current.data.city);
            }
            current = current.next;
        }

        // Add cities in correct order and calculate costs
        for (int i = cities.size() - 1; i >= 0; i--) {
            path.addCity(cities.get(i));
            if (i > 0) {
                Flight flight = findFlight(cities.get(i), cities.get(i-1));
                if (flight != null) {
                    path.addCost(flight.cost);
                    path.addTime(flight.time);
                }
            }
        }

        return path;
    }

    private Flight findFlight(String source, String destination) {
        int sourceIdx = getCityIndex(source);
        if (sourceIdx == -1) return null;

        Node<Flight> current = adjacencyList[sourceIdx].getHead();
        while (current != null) {
            if (current.data.destination.equals(destination)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    private void sortPaths(LinkedList<FlightPath> paths, boolean sortByTime) {
        Node<FlightPath> current = paths.getHead();
        boolean swapped;
        do {
            swapped = false;
            current = paths.getHead();
            while (current != null && current.next != null) {
                if (shouldSwap(current.data, current.next.data, sortByTime)) {
                    FlightPath temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    private boolean shouldSwap(FlightPath path1, FlightPath path2, boolean sortByTime) {
        if (sortByTime) {
            if (path1.getTotalTime() == path2.getTotalTime()) {
                return path1.getTotalCost() > path2.getTotalCost();
            }
            return path1.getTotalTime() > path2.getTotalTime();
        } else {
            if (path1.getTotalCost() == path2.getTotalCost()) {
                return path1.getTotalTime() > path2.getTotalTime();
            }
            return path1.getTotalCost() > path2.getTotalCost();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java FlightPlanner <flight_data> <requested_flights> <output_file>");
            return;
        }

        try {
            FlightPlanner planner = new FlightPlanner();

            // Read flight data
            BufferedReader flightData = new BufferedReader(new FileReader(args[0]));
            int numFlights = Integer.parseInt(flightData.readLine());

            for (int i = 0; i < numFlights; i++) {
                String[] flight = flightData.readLine().split("\\|");
                planner.addFlight(flight[0], flight[1],
                        Integer.parseInt(flight[2]),
                        Integer.parseInt(flight[3]));
            }

            // Read requested flights
            BufferedReader requestedFlights = new BufferedReader(new FileReader(args[1]));
            int numRequests = Integer.parseInt(requestedFlights.readLine());

            PrintWriter output = new PrintWriter(args[2]);

            for (int i = 1; i <= numRequests; i++) {
                String[] request = requestedFlights.readLine().split("\\|");
                String source = request[0];
                String destination = request[1];
                boolean sortByTime = request[2].equals("T");

                LinkedList<FlightPath> paths = planner.findPaths(source, destination, sortByTime);

                output.printf("Flight %d: %s, %s (%s)\n", i, source, destination,
                        sortByTime ? "Time" : "Cost");

                // Output at most 3 paths
                Node<FlightPath> pathNode = paths.getHead();
                int pathCount = 1;
                while (pathNode != null && pathCount <= 3) {
                    FlightPath path = pathNode.data;
                    output.printf("Path %d: ", pathCount);

                    // Print cities in path
                    Node<String> cityNode = path.getCities().getHead();
                    while (cityNode != null) {
                        output.print(cityNode.data);
                        if (cityNode.next != null) {
                            output.print(" -> ");
                        }
                        cityNode = cityNode.next;
                    }

                    output.printf(". Time: %d Cost: %.2f\n",
                            path.getTotalTime(), (float)path.getTotalCost());

                    pathNode = pathNode.next;
                    pathCount++;
                }

                if (paths.getHead() == null) {
                    output.println("No valid path found.");
                }
                output.println();
            }

            flightData.close();
            requestedFlights.close();
            output.close();

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }
    }
}
