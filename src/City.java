public class City {
    String name;
    LinkedList<Flight> connections;

    public City(String name) {
        this.name = name;
        this.connections = new LinkedList<>();
    }
}
