import java.util.*;

public class MallApp {
    public static void main(String[] args) {
        DBHelper db = null;
        try {
            db = new DBHelper();           // automatically uses DBConnection
            MallGraph graph = new MallGraph();
            graph.loadFromDB(db);

            // load stores to map name -> location id
            Map<String, Integer> storeToLoc = new HashMap<>();
            for (Store s : db.loadAllStores()) {
                storeToLoc.put(s.getName().toLowerCase(), s.getLocationId());
            }

            Scanner sc = new Scanner(System.in);
            System.out.println("=== Smart Mall Indoor Guide (Console) ===");
            System.out.println("Type 'help' for commands.");

            while (true) {
                System.out.print("\n> ");
                String line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) break;

                if (line.equalsIgnoreCase("help")) {
                    System.out.println("Commands:");
                    System.out.println(" search <store-name>");
                    System.out.println(" route <source-location-name> <destination-location-name>");
                    System.out.println(" route-store <source-location-name> <store-name>");
                    System.out.println(" block-path <path-id>");
                    System.out.println(" unblock-path <path-id>");
                    System.out.println(" reload (reload DB into memory)");
                    System.out.println(" list-locations");
                    System.out.println(" quit");
                    continue;
                }

                String[] parts = line.split(" ", 2);
                String cmd = parts[0].toLowerCase();

                switch(cmd) {
                    case "search":
                        if (parts.length < 2) { System.out.println("Usage: search <store-name>"); break; }
                        Integer locId = db.findLocationIdByStoreName(parts[1].toLowerCase());
                        if (locId != null) System.out.println("Found at: " + graph.getLocation(locId));
                        else System.out.println("Store not found in DB.");
                        break;

                    case "route":
                        if (parts.length < 2) { System.out.println("Usage: route <source> <destination>"); break; }
                        String[] args2 = parts[1].split(" ", 2);
                        if (args2.length < 2) { System.out.println("Usage: route <source> <destination>"); break; }
                        Integer srcId = findLocationIdByName(graph, args2[0]);
                        Integer dstId = findLocationIdByName(graph, args2[1]);
                        if (srcId == null || dstId == null) { System.out.println("Unknown location(s)."); break; }
                        List<Integer> path = graph.bfs(srcId, dstId);
                        if (path.isEmpty()) System.out.println("No path found.");
                        else {
                            System.out.println("Route: " + graph.pathToString(path));
                            System.out.println("Distance: " + graph.totalDistance(path) + " meters");
                        }
                        break;

                    case "route-store":
                        if (parts.length < 2) { System.out.println("Usage: route-store <source> <store>"); break; }
                        String[] args3 = parts[1].split(" ", 2);
                        if (args3.length < 2) { System.out.println("Usage: route-store <source> <store>"); break; }
                        Integer sourceId = findLocationIdByName(graph, args3[0]);
                        Integer destLoc = db.findLocationIdByStoreName(args3[1].toLowerCase());
                        if (sourceId == null || destLoc == null) { System.out.println("Unknown source/store."); break; }
                        List<Integer> path2 = graph.bfs(sourceId, destLoc);
                        if (path2.isEmpty()) System.out.println("No path found.");
                        else {
                            System.out.println("Route: " + graph.pathToString(path2));
                            System.out.println("Distance: " + graph.totalDistance(path2) + " meters");
                        }
                        break;

                    case "block-path":
                    case "unblock-path":
                        if (parts.length < 2) { System.out.println("Usage: block-path/unblock-path <id>"); break; }
                        try {
                            int pathId = Integer.parseInt(parts[1]);
                            boolean block = cmd.equals("block-path");
                            db.setPathBlocked(pathId, block);
                            graph.loadFromDB(db); // reload graph
                            System.out.println("Path " + pathId + " set to " + (block ? "blocked" : "unblocked"));
                        } catch (NumberFormatException e) { System.out.println("Invalid path id."); }
                        break;

                    case "reload":
                        graph.loadFromDB(db);
                        System.out.println("Reloaded from DB.");
                        break;

                    case "list-locations":
                        for (Location l : graph.getLocationMap().values())
                            System.out.printf("[%d] %s - %s (floor %d)\n", l.getId(), l.getName(), l.getType(), l.getFloor());
                        break;

                    default:
                        System.out.println("Unknown command. Type 'help'.");
                }
            }

            sc.close();
            System.out.println("Goodbye.");

        } catch (Exception ex) { ex.printStackTrace(); }
        finally { if (db != null) db.close(); }
    }

    private static Integer findLocationIdByName(MallGraph g, String name) {
        for (Location l : g.getLocationMap().values())
            if (l.getName().equalsIgnoreCase(name.trim())) return l.getId();
        return null;
    }
}
