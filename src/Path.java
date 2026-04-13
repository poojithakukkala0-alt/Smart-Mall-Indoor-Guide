public class Path {
    private int id;
    private int sourceId;
    private int destId;
    private int distance;
    private boolean isBlocked;

    public Path(int id, int sourceId, int destId, int distance, boolean isBlocked) {
        this.id = id;
        this.sourceId = sourceId;
        this.destId = destId;
        this.distance = distance;
        this.isBlocked = isBlocked;
    }

    public int getId() { return id; }
    public int getSourceId() { return sourceId; }
    public int getDestId() { return destId; }
    public int getDistance() { return distance; }
    public boolean isBlocked() { return isBlocked; }
}