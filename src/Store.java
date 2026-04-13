public class Store {
    private int id;
    private String name;
    private String category;
    private int locationId;

    public Store(int id, String name, String category, int locationId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.locationId = locationId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getLocationId() { return locationId; }
}
