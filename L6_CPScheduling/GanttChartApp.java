import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class GanttChartEntry {
    public int processId;
    public int startTime;
    public int endTime;

    public GanttChartEntry(int processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

public class GanttChartApp extends JFrame {
    private List<GanttChartEntry> ganttChart;

    public GanttChartApp() {
        // Initialize the Gantt chart entries
        ganttChart = new ArrayList<>();
        ganttChart.add(new GanttChartEntry(1, 0, 3));
        ganttChart.add(new GanttChartEntry(2, 1, 4));
        ganttChart.add(new GanttChartEntry(3, 3, 5));

        // Set up the frame
        setTitle("Gantt Chart");
        setSize(800, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create and add the Gantt chart panel
        JPanel ganttChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int totalWidth = getWidth();
                int totalTime = ganttChart.stream().mapToInt(entry -> entry.endTime).max().orElse(1);
                int unitWidth = totalWidth / totalTime;

                for (GanttChartEntry entry : ganttChart) {
                    int width = (entry.endTime - entry.startTime) * unitWidth;
                    g.setColor(Color.BLUE); // Set color for process blocks
                    g.fillRect(entry.startTime * unitWidth, 0, width, 50);
                    g.setColor(Color.WHITE);
                    g.drawString("P" + entry.processId, entry.startTime * unitWidth + width / 2 - 10, 25);
                }
            }
        };

        // Add the Gantt chart panel to the frame
        add(ganttChartPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GanttChartApp app = new GanttChartApp();
            app.setVisible(true);
        });
    }
}
