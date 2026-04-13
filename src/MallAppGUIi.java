import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MallAppGUIi {

    private JFrame frame;
    private JTextField searchField;
    private JTextArea resultArea;
    private JTextField srcField, dstField;
    private JTextArea routeArea;
    private JTextArea locArea;

    private DBHelper db;
    private MallGraph graph;

    public MallAppGUIi() {
        try {
            db = new DBHelper();
            graph = new MallGraph();
            graph.loadFromDB(db);

            List<Location> locationList = db.loadAllLocations();
            List<Store> storeList = db.loadAllStores();

            Map<String, String> stores = new HashMap<>();
            for (Store s : storeList) {
                int locId = s.getLocationId();
                String locName = "";
                for (Location l : locationList) {
                    if (l.getId() == locId) {
                        locName = l.getName() + " (" + l.getType() + ", Floor " + l.getFloor() + ")";
                        break;
                    }
                }
                stores.put(s.getName().toLowerCase(), locName);
            }

            StringBuilder locText = new StringBuilder();
            for (Location l : locationList) {
                locText.append("• ").append(l.getName())
                        .append(" (").append(l.getType())
                        .append(", Floor ").append(l.getFloor()).append(")\n");
            }

            buildGUI(locationList, stores, locText);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading database: " + e.getMessage());
        }
    }

    private void buildGUI(List<Location> locationList, Map<String, String> stores, StringBuilder locText) {

        frame = new JFrame("🌟 Smart Mall Indoor Guide 🌟");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 750);
        frame.setLocationRelativeTo(null);

        // -------- Main Panel with Gradient Background --------
        JPanel mainPanel = new JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color color1 = new Color(200, 230, 255); // top-left light blue
        Color color2 = new Color(180, 210, 255); // bottom-right slightly darker blue
        int w = getWidth();
        int h = getHeight();
        g2d.setPaint(new GradientPaint(0, 0, color1, w, h, color2));
        g2d.fillRect(0, 0, w, h);
    }
};
mainPanel.setLayout(new GridBagLayout());
mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));





        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // -------- Heading --------
        JLabel heading = new JLabel("SMART MALL INDOOR NAVIGATION", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(50, 0, 100));
        heading.setBorder(new MatteBorder(0, 0, 4, 0, new Color(150, 0, 200)));
        frame.add(heading, BorderLayout.NORTH);

        // -------- Search Panel --------
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(new Color(255, 245, 245));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 0, 150), 2, true),
                "Search Store", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18), new Color(120, 0, 150)
        ));

        GridBagConstraints sGbc = new GridBagConstraints();
        sGbc.insets = new Insets(5, 5, 5, 5);
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.weightx = 1.0;

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(180, 50, 200));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchBtn.setFocusPainted(false);

        resultArea = new JTextArea(3, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultArea.setBackground(new Color(255, 250, 255));
        resultArea.setForeground(new Color(100, 0, 150));
        resultArea.setBorder(new LineBorder(new Color(180, 120, 200), 2, true));

        sGbc.gridx = 0; sGbc.gridy = 0; sGbc.gridwidth = 2;
        searchPanel.add(searchField, sGbc);
        sGbc.gridx = 2; sGbc.gridy = 0; sGbc.gridwidth = 1;
        searchPanel.add(searchBtn, sGbc);
        sGbc.gridx = 0; sGbc.gridy = 1; sGbc.gridwidth = 3;
        sGbc.weighty = 0.3;
        searchPanel.add(new JScrollPane(resultArea), sGbc);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.2;
        mainPanel.add(searchPanel, gbc);

        // -------- Route Panel --------
        JPanel routePanel = new JPanel(new GridBagLayout());
        routePanel.setBackground(new Color(230, 255, 250, 200));
        routePanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(0, 120, 180), 2, true),
                "Find Route", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18), new Color(0, 80, 120)
        ));

        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.insets = new Insets(5, 5, 5, 5);
        rGbc.fill = GridBagConstraints.HORIZONTAL;
        rGbc.weightx = 1.0;

        srcField = new JTextField();
        srcField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dstField = new JTextField();
        dstField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton routeBtn = new JButton("Find Route");
        routeBtn.setBackground(new Color(0, 120, 220));
        routeBtn.setForeground(Color.WHITE);
        routeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        routeBtn.setFocusPainted(false);

        routeArea = new JTextArea(4, 30);
        routeArea.setEditable(false);
        routeArea.setFont(new Font("Segoe UI", Font.BOLD, 16));
        routeArea.setBackground(new Color(240, 255, 255));
        routeArea.setForeground(new Color(0, 80, 150));
        routeArea.setBorder(new LineBorder(new Color(0, 120, 180), 2, true));

        rGbc.gridx = 0; rGbc.gridy = 0;
        routePanel.add(new JLabel("From:"), rGbc);
        rGbc.gridx = 1;
        routePanel.add(srcField, rGbc);

        rGbc.gridx = 0; rGbc.gridy = 1;
        routePanel.add(new JLabel("To:"), rGbc);
        rGbc.gridx = 1;
        routePanel.add(dstField, rGbc);

        rGbc.gridx = 0; rGbc.gridy = 2; rGbc.gridwidth = 2;
        routePanel.add(routeBtn, rGbc);

        rGbc.gridx = 0; rGbc.gridy = 3; rGbc.gridwidth = 2;
        rGbc.weighty = 0.3;
        rGbc.fill = GridBagConstraints.BOTH;
        routePanel.add(new JScrollPane(routeArea), rGbc);

        gbc.gridy = 1; gbc.weighty = 0.3;
        mainPanel.add(routePanel, gbc);

        // -------- All Locations Panel --------
        JPanel locPanel = new JPanel(new BorderLayout());
        locPanel.setBackground(new Color(255, 250, 235, 200));
        locPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 150, 80), 2, true),
                "All Locations", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18), new Color(150, 100, 0)
        ));

        locArea = new JTextArea(locText.toString());
        locArea.setEditable(false);
        locArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        locArea.setBackground(new Color(255, 245, 225));

        locPanel.add(new JScrollPane(locArea), BorderLayout.CENTER);

        gbc.gridy = 2; gbc.weighty = 0.5;
        mainPanel.add(locPanel, gbc);

        frame.add(mainPanel, BorderLayout.CENTER);

        // -------- Button Actions --------
        searchBtn.addActionListener(e -> {
            String name = searchField.getText().toLowerCase().trim();
            if (stores.containsKey(name)) {
                resultArea.setText("Found at: " + stores.get(name));
            } else {
                resultArea.setText("Store not found.");
            }
        });

        routeBtn.addActionListener(e -> {
            String src = srcField.getText().trim();
            String dst = dstField.getText().trim();

            Integer srcId = null, dstId = null;

            for (Location l : locationList) {
                if (l.getName().equalsIgnoreCase(src)) srcId = l.getId();
                if (l.getName().equalsIgnoreCase(dst)) dstId = l.getId();
            }

            if (srcId != null && dstId != null) {
                List<Integer> path = graph.bfs(srcId, dstId);
                if (!path.isEmpty()) {
                    routeArea.setText("Route: " + graph.pathToString(path) +
                            "\nDistance: " + graph.totalDistance(path) + " meters");
                } else {
                    routeArea.setText("No path found.");
                }
            } else {
                routeArea.setText("Unknown location(s).");
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MallAppGUI::new);
    }
}
