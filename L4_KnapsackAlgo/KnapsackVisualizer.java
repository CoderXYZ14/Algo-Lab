import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.table.DefaultTableCellRenderer;

public class KnapsackVisualizer extends JFrame {
    private JTextField profitField, weightField, maxWeightField;
    private JSlider speedSlider;
    private JTable table;
    private JLabel resultLabel;
    private JPanel tablePanel;
    private JButton startButton;
    private HighlightRenderer highlightRenderer;

    public KnapsackVisualizer() {
        setTitle("0/1 Knapsack Algorithm Visualizer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel header = new JLabel("0/1 Knapsack Algorithm Visualizer", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 24));
        header.setForeground(new Color(0x2E4053)); // Professional dark blue-gray color

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBackground(new Color(0xF4F6F7)); // Light gray background
        profitField = new JTextField();
        weightField = new JTextField();
        maxWeightField = new JTextField();
        speedSlider = new JSlider(250, 1000, 500);

        // Labels for the input fields
        JLabel profitLabel = new JLabel("Enter Profit Array:", SwingConstants.CENTER);
        profitLabel.setForeground(new Color(0x1F618D)); // Dark blue color
        JLabel weightLabel = new JLabel("Enter Weight Array:", SwingConstants.CENTER);
        weightLabel.setForeground(new Color(0x1F618D));
        JLabel maxWeightLabel = new JLabel("Max Weight:", SwingConstants.CENTER);
        maxWeightLabel.setForeground(new Color(0x1F618D));
        JLabel speedLabel = new JLabel("Animation Speed:", SwingConstants.CENTER);
        speedLabel.setForeground(new Color(0x1F618D));

        // Adding components to the input panel
        inputPanel.add(profitLabel);
        inputPanel.add(profitField);
        inputPanel.add(weightLabel);
        inputPanel.add(weightField);
        inputPanel.add(maxWeightLabel);
        inputPanel.add(maxWeightField);
        inputPanel.add(speedLabel);
        inputPanel.add(speedSlider);

        // Table Panel
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(0xD5DBDB)); // Light gray background for the table panel
        table = new JTable();

        // Custom renderer for centering and highlighting
        highlightRenderer = new HighlightRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(highlightRenderer);
        }

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Serif", Font.BOLD, 18));
        resultLabel.setForeground(new Color(0xC0392B)); // Red color for result label
        tablePanel.add(resultLabel, BorderLayout.SOUTH);

        // Start Button
        startButton = new JButton("Start");
        startButton.setFont(new Font("Serif", Font.BOLD, 16));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(0x1ABC9C)); // Greenish-teal color
        startButton.setPreferredSize(new Dimension(100, 40)); // Small button size
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startKnapsack();
            }
        });

        // Arrange components
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(header, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(startButton, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void startKnapsack() {
        String profitInput = profitField.getText();
        String weightInput = weightField.getText();
        int maxWeight = Integer.parseInt(maxWeightField.getText());

        if (!profitInput.isEmpty() && !weightInput.isEmpty()) {
            int[] profits = Arrays.stream(profitInput.split(" ")).mapToInt(Integer::parseInt).toArray();
            int[] weights = Arrays.stream(weightInput.split(" ")).mapToInt(Integer::parseInt).toArray();
            int n = weights.length;

            int[][] dp = new int[n + 1][maxWeight + 1];
            updateTable(dp, n, maxWeight); // Initialize table with zeros

            animateTable(dp, n, maxWeight, weights, profits, speedSlider.getValue());
        } else {
            JOptionPane.showMessageDialog(this, "Please Fill All Fields!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(int[][] dp, int n, int maxWeight) {
        DefaultTableModel model = new DefaultTableModel(n + 1, maxWeight + 1);
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= maxWeight; j++) {
                model.setValueAt(dp[i][j], i, j);
            }
        }
        table.setModel(model);

        // Apply the highlight renderer to center text and allow highlighting
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(highlightRenderer);
        }
    }

    private void animateTable(int[][] dp, int n, int maxWeight, int[] weights, int[] profits, int speed) {
        Timer timer = new Timer(speed, new ActionListener() {
            int i = 1, j = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (i <= n) {
                    if (weights[i - 1] <= j) {
                        dp[i][j] = Math.max(dp[i - 1][j], profits[i - 1] + dp[i - 1][j - weights[i - 1]]);
                    } else {
                        dp[i][j] = dp[i - 1][j];
                    }

                    // Update the value in the table
                    table.setValueAt(dp[i][j], i, j);

                    // Highlight the current cell
                    highlightRenderer.setHighlightCell(i, j);
                    table.repaint();

                    // Move to the next cell
                    j++;
                    if (j > maxWeight) {
                        j = 1;
                        i++;
                    }
                } else {
                    ((Timer) e.getSource()).stop();
                    // Display the final result
                    resultLabel.setText("Maximum Profit: " + dp[n][maxWeight]);
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KnapsackVisualizer frame = new KnapsackVisualizer();
            frame.setVisible(true);
        });
    }
}

class HighlightRenderer extends DefaultTableCellRenderer {
    private int highlightRow = -1;
    private int highlightCol = -1;

    public void setHighlightCell(int row, int col) {
        this.highlightRow = row;
        this.highlightCol = col;
        repaint();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int col) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        // Center the text
        ((JLabel) cell).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel) cell).setVerticalAlignment(SwingConstants.CENTER);

        // Highlight the cell
        if (row == highlightRow && col == highlightCol) {
            cell.setBackground(Color.YELLOW); // Highlight color
        } else {
            cell.setBackground(Color.WHITE); // Normal cell color
        }

        return cell;
    }
}
