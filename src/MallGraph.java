import java.util.*;

public class MallGraph {
    // locationId -> Location
    private Map<Integer, Location> locationMap = new HashMap<>();
    // locationId -> list of outgoing Path objects
    private Map<Integer, List<Path>> adj = new HashMap<>();
    // pathId -> Path (original direction stored in DB)
    private Map<Integer, Path> pathById = new HashMap<>();

    // Load all locations and paths from DBHelper
    public void loadFromDB(DBHelper db) throws Exception {
        locationMap.clear();
        adj.clear();
        pathById.clear();

        // Load locations
        for (Location loc : db.loadAllLocations()) {
            locationMap.put(loc.getId(), loc);
            adj.put(loc.getId(), new ArrayList<>());
        }

        // Load paths
        for (Path p : db.loadAllPaths()) {   // db.loadAllPaths() must exist in DBHelper
            pathById.put(p.getId(), p);

            // add directed edge p.src -> p.dest
            adj.get(p.getSourceId()).add(p);

            // also add reverse edge for walking
            Path rev = new Path(p.getId(), p.getDestId(), p.getSourceId(), p.getDistance(), p.isBlocked());
            adj.get(p.getDestId()).add(rev);
        }
    }

    public Location getLocation(int id) {
        return locationMap.get(id);
    }

    public Map<Integer, Location> getLocationMap() {
        return locationMap;
    }

    // BFS shortest path (skips blocked paths)
    public List<Integer> bfs(int srcId, int destId) {
        if (!locationMap.containsKey(srcId) || !locationMap.containsKey(destId)) return Collections.emptyList();

        Queue<Integer> q = new LinkedList<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        q.add(srcId);
        visited.add(srcId);

        while (!q.isEmpty()) {
            int u = q.poll();
            if (u == destId) break;
            for (Path p : adj.getOrDefault(u, Collections.emptyList())) {
                if (p.isBlocked()) continue;
                int v = p.getDestId();
                if (!visited.contains(v)) {
                    visited.add(v);
                    parent.put(v, u);
                    q.add(v);
                }
            }
        }

        if (!parent.containsKey(destId) && srcId != destId) return Collections.emptyList();

        // reconstruct path
        List<Integer> path = new ArrayList<>();
        int cur = destId;
        path.add(cur);
        while (cur != srcId) {
            Integer par = parent.get(cur);
            if (par == null) break;
            path.add(par);
            cur = par;
        }
        Collections.reverse(path);
        if (path.get(0) != srcId) return Collections.emptyList();
        return path;
    }

    // total distance for path
    public int totalDistance(List<Integer> nodePath) {
        int sum = 0;
        for (int i = 0; i < nodePath.size() - 1; i++) {
            int u = nodePath.get(i), v = nodePath.get(i + 1);
            boolean found = false;
            for (Path p : adj.getOrDefault(u, Collections.emptyList())) {
                if (p.getDestId() == v && !p.isBlocked()) {
                    sum += p.getDistance();
                    found = true;
                    break;
                }
            }
            if (!found) return -1; // inconsistent
        }
        return sum;
    }

    // utility: readable path string
    public String pathToString(List<Integer> nodePath) {
        if (nodePath == null || nodePath.isEmpty()) return "No route";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodePath.size(); i++) {
            Location loc = locationMap.get(nodePath.get(i));
            sb.append(loc != null ? loc.getName() : ("#" + nodePath.get(i)));
            if (i < nodePath.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    // get path map by id
    public Map<Integer, Path> getPathByIdMap() {
        return pathById;
    }
}
