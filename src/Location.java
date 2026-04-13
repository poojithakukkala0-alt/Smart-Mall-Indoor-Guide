public class Location {

    private int id;
    private String name;
    private String type;
    private int floor;

    public Location(int id, String name, String type, int floor) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.floor = floor;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getFloor() { return floor; }

    @Override
    public String toString() {
        return name + " (f" + floor + ")";
    }

}
