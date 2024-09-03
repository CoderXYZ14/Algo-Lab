import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

public class KnapsackVisualizer extends JFrame {
    private JTextField profitField, weightField, maxWeightField;
    private JSlider speedSlider;
    private JTable table01, tableFractional;
    private JLabel resultLabel01, resultLabelFractional;
    private JPanel tablePanel01, tablePanelFractional;
    private JButton startButton;
    private HighlightRenderer highlightRenderer01, highlightRendererFractional;

    public KnapsackVisualizer() {
        setTitle("0/1 and Fractional Knapsack Algorithm Visualizer");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel header = new JLabel("0/1 and Fractional Knapsack Algorithm Visualizer", SwingConstants.CENTER);
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

        // Table Panels
        tablePanel01 = new JPanel(new BorderLayout());
        tablePanel01.setBackground(new Color(0xD5DBDB)); // Light gray background for the table panel
        table01 = new JTable();

        highlightRenderer01 = new HighlightRenderer();
        tablePanel01.add(new JScrollPane(table01), BorderLayout.CENTER);
        resultLabel01 = new JLabel("", SwingConstants.CENTER);
        resultLabel01.setFont(new Font("Serif", Font.BOLD, 18));
        resultLabel01.setForeground(new Color(0xC0392B)); // Red color for result label
        tablePanel01.add(resultLabel01, BorderLayout.SOUTH);

        tablePanelFractional = new JPanel(new BorderLayout());
        tablePanelFractional.setBackground(new Color(0xD5DBDB)); // Light gray background for the table panel
        tableFractional = new JTable();

        highlightRendererFractional = new HighlightRenderer();
        tablePanelFractional.add(new JScrollPane(tableFractional), BorderLayout.CENTER);
        resultLabelFractional = new JLabel("", SwingConstants.CENTER);
        resultLabelFractional.setFont(new Font("Serif", Font.BOLD, 18));
        resultLabelFractional.setForeground(new Color(0xC0392B)); // Red color for result label
        tablePanelFractional.add(resultLabelFractional, BorderLayout.SOUTH);

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
        add(tablePanel01, BorderLayout.CENTER);
        add(tablePanelFractional, BorderLayout.SOUTH);
    }

    private void startKnapsack() {
        String profitInput = profitField.getText();
        String weightInput = weightField.getText();
        int maxWeight = Integer.parseInt(maxWeightField.getText());

        if (!profitInput.isEmpty() && !weightInput.isEmpty()) {
            int[] profits = Arrays.stream(profitInput.split(" ")).mapToInt(Integer::parseInt).toArray();
            int[] weights = Arrays.stream(weightInput.split(" ")).mapToInt(Integer::parseInt).toArray();
            int n = weights.length;

            // 0/1 Knapsack
            int[][] dp = new int[n + 1][maxWeight + 1];
            updateTable(dp, table01, highlightRenderer01, n, maxWeight); // Initialize table with zeros

            Timer timer = new Timer(speedSlider.getValue(), new ActionListener() {
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
                        table01.setValueAt(dp[i][j], i, j);

                        // Highlight the current cell
                        highlightRenderer01.setHighlightCell(i, j);
                        table01.repaint();

                        // Move to the next cell
                        j++;
                        if (j > maxWeight) {
                            j = 1;
                            i++;
                        }
                    } else {
                        ((Timer) e.getSource()).stop();
                        // Display the final result
                        resultLabel01.setText("Maximum Profit (0/1 Knapsack): " + dp[n][maxWeight]);

                        // Start fractional knapsack after 0/1 knapsack is done
                        startFractionalKnapsack(profits, weights, maxWeight);
                    }
                }
            });
            timer.start();
        } else {
            JOptionPane.showMessageDialog(this, "Please Fill All Fields!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startFractionalKnapsack(int[] profits, int[] weights, int maxWeight) {
        int n = weights.length;
        double[][] items = new double[n][3]; // 0: profit, 1: weight, 2: profit/weight

        for (int i = 0; i < n; i++) {
            items[i][0] = profits[i];
            items[i][1] = weights[i];
            items[i][2] = (double) profits[i] / weights[i];
        }

        // Sort items based on profit/weight ratio
        Arrays.sort(items, Comparator.comparingDouble(o -> -o[2]));

        DefaultTableModel model = new DefaultTableModel(n, 4);
        model.setColumnIdentifiers(new String[] { "Profit", "Weight", "Profit/Weight", "Selected Fraction" });
        tableFractional.setModel(model);

        final double[] totalProfit = { 0 };
        final int[] currentWeight = { 0 };

        Timer timer = new Timer(speedSlider.getValue(), new ActionListener() {
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (i < n) {
                    double selectedFraction = 0;

                    if (currentWeight[0] + items[i][1] <= maxWeight) {
                        selectedFraction = 1;
                        currentWeight[0] += items[i][1];
                        totalProfit[0] += items[i][0];
                    } else {
                        selectedFraction = (maxWeight - currentWeight[0]) / items[i][1];
                        totalProfit[0] += selectedFraction * items[i][0];
                        currentWeight[0] = maxWeight;
                    }

                    model.setValueAt(items[i][0], i, 0);
                    model.setValueAt(items[i][1], i, 1);
                    model.setValueAt(items[i][2], i, 2);
                    model.setValueAt(selectedFraction, i, 3);

                    // Highlight the current row
                    highlightRendererFractional.setHighlightCell(i, 3);
                    tableFractional.repaint();

                    i++;
                    if (currentWeight[0] == maxWeight) {
                        ((Timer) e.getSource()).stop();
                        // Display the final result
                        resultLabelFractional.setText("Maximum Profit (Fractional Knapsack): " + totalProfit[0]);
                    }
                } else {
                    ((Timer) e.getSource()).stop();
                    // Display the final result
                    resultLabelFractional.setText("Maximum Profit (Fractional Knapsack): " + totalProfit[0]);
                }
            }
        });

        timer.start();
    }

    private void updateTable(int[][] dp, JTable table, HighlightRenderer renderer, int n, int maxWeight) {
        DefaultTableModel model = new DefaultTableModel(n + 1, maxWeight + 1);
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= maxWeight; j++) {
                model.setValueAt(dp[i][j], i, j);
            }
        }
        table.setModel(model);

        // Apply the highlight renderer to center text and allow highlighting
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
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
