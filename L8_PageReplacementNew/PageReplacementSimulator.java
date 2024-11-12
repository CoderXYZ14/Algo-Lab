import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PageReplacementSimulator extends JFrame {
    private JTextField referenceStringInput, frameInput;
    private JButton fifoButton, optimalButton, lruButton;
    private JTextArea resultArea;
    private JTable table;
    private DefaultTableModel tableModel;
    private int frames;
    private String referenceString;

    public PageReplacementSimulator() {
        setTitle("Page Replacement Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Reference String (comma-separated):"));
        referenceStringInput = new JTextField();
        inputPanel.add(referenceStringInput);
        inputPanel.add(new JLabel("Number of Frames:"));
        frameInput = new JTextField();
        inputPanel.add(frameInput);
        add(inputPanel, BorderLayout.NORTH);

        // Panel for algorithm buttons
        JPanel buttonPanel = new JPanel();
        fifoButton = new JButton("FIFO");
        optimalButton = new JButton("Optimal");
        lruButton = new JButton("LRU");

        buttonPanel.add(fifoButton);
        buttonPanel.add(optimalButton);
        buttonPanel.add(lruButton);
        add(buttonPanel, BorderLayout.CENTER);

        // Result area to display hits, faults, ratios
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Table for frame visualization
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.EAST);

        // Button listeners
        fifoButton.addActionListener(e -> performPageReplacement("FIFO"));
        optimalButton.addActionListener(e -> performPageReplacement("Optimal"));
        lruButton.addActionListener(e -> performPageReplacement("LRU"));
    }

    private void performPageReplacement(String algorithm) {
        referenceString = referenceStringInput.getText().trim();
        try {
            frames = Integer.parseInt(frameInput.getText().trim());
            int[] pages = Arrays.stream(referenceString.split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            // Prepare the table for the new data
            prepareTable(pages.length);

            int pageFaults;
            switch (algorithm) {
                case "FIFO":
                    pageFaults = fifoPageReplacement(pages, frames);
                    break;
                case "Optimal":
                    pageFaults = optimalPageReplacement(pages, frames);
                    break;
                case "LRU":
                    pageFaults = lruPageReplacement(pages, frames);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid algorithm");
            }

            int pageHits = pages.length - pageFaults;
            double hitRatio = (double) pageHits / pages.length;
            double faultRatio = (double) pageFaults / pages.length;

            resultArea.setText("Algorithm: " + algorithm + "\n" +
                    "Page Faults: " + pageFaults + "\n" +
                    "Page Hits: " + pageHits + "\n" +
                    "Hit Ratio: " + hitRatio + "\n" +
                    "Fault Ratio: " + faultRatio);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        }
    }

    private void prepareTable(int columns) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(columns);
        for (int i = 0; i < frames; i++) {
            tableModel.addRow(new Object[columns]);
        }
    }

    private void updateTable(int step, int[] frameContent) {
        for (int i = 0; i < frames; i++) {
            if (i < frameContent.length) {
                tableModel.setValueAt(frameContent[i], i, step);
            } else {
                tableModel.setValueAt("", i, step);
            }
        }
    }

    private int fifoPageReplacement(int[] pages, int capacity) {
        HashSet<Integer> s = new HashSet<>(capacity);
        Queue<Integer> indexes = new LinkedList<>();
        int pageFaults = 0;
        int[] frameContent = new int[capacity];
        Arrays.fill(frameContent, -1);

        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];

            if (s.size() < capacity) {
                if (!s.contains(page)) {
                    s.add(page);
                    pageFaults++;
                    indexes.add(page);
                    frameContent[s.size() - 1] = page;
                }
            } else {
                if (!s.contains(page)) {
                    int val = indexes.poll();
                    s.remove(val);
                    s.add(page);
                    indexes.add(page);

                    for (int j = 0; j < frameContent.length; j++) {
                        if (frameContent[j] == val) {
                            frameContent[j] = page;
                            break;
                        }
                    }
                    pageFaults++;
                }
            }
            updateTable(i, frameContent.clone());
        }
        return pageFaults;
    }

    private int optimalPageReplacement(int[] pages, int capacity) {
        int[] frameContent = new int[capacity];
        Arrays.fill(frameContent, -1);
        int pageFaults = 0;
        int index = 0;

        for (int i = 0; i < pages.length; i++) {
            // Check if the page is already in the frame
            if (!search(pages[i], frameContent)) {
                pageFaults++;

                // If there is an empty slot in frameContent, use it
                if (index < capacity) {
                    frameContent[index++] = pages[i];
                } else {
                    // Find the page to replace using the optimized predict function
                    int j = predict(pages, frameContent, pages.length, i + 1);
                    frameContent[j] = pages[i];
                }
            }

            // Update the table for each step
            updateTable(i, frameContent.clone());
        }
        return pageFaults;
    }

    // Helper function to check if a page is already in the frame
    private boolean search(int page, int[] frameContent) {
        for (int i : frameContent) {
            if (i == page) {
                return true;
            }
        }
        return false;
    }

    // Optimized predict function to find the page to replace
    private int predict(int[] pages, int[] frameContent, int totalPages, int currentIndex) {
        int farthest = currentIndex;
        int indexToReplace = -1;

        // Iterate over each page in the frame
        for (int i = 0; i < frameContent.length; i++) {
            int j;
            // Find the next occurrence of frameContent[i] in pages
            for (j = currentIndex; j < totalPages; j++) {
                if (frameContent[i] == pages[j]) {
                    if (j > farthest) {
                        farthest = j;
                        indexToReplace = i;
                    }
                    break;
                }
            }

            // If the page will not be used again, select it for replacement
            if (j == totalPages) {
                return i;
            }
        }

        // If no page found with farthest use, replace the first one
        return (indexToReplace == -1) ? 0 : indexToReplace;
    }

    private int lruPageReplacement(int[] pages, int capacity) {
        int[] queue = new int[capacity]; // Represents frames with pages
        int[] distance = new int[capacity]; // Distance array to track LRU
        int occupied = 0; // Number of pages currently in the queue
        int pageFaults = 0; // Counter for page faults
        int[] frameContent = new int[capacity];
        Arrays.fill(queue, -1); // Initialize frames as empty
        Arrays.fill(frameContent, -1);

        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];

            // Use checkHit to see if the page is already in the queue
            boolean hit = checkHit(page, queue, occupied);

            if (!hit) {
                pageFaults++; // Page fault occurs

                if (occupied < capacity) {
                    // If there is space in the queue, add the page at the next available slot
                    queue[occupied] = page;
                    occupied++;
                } else {
                    // Calculate LRU based on distance
                    int maxDistance = Integer.MIN_VALUE;
                    int indexToReplace = -1;

                    for (int j = 0; j < capacity; j++) {
                        distance[j] = 0;
                        // Calculate distance from current page to last occurrence
                        for (int k = i - 1; k >= 0; k--) {
                            distance[j]++;
                            if (queue[j] == pages[k])
                                break;
                        }

                        if (distance[j] > maxDistance) {
                            maxDistance = distance[j];
                            indexToReplace = j;
                        }
                    }

                    // Replace the LRU page in the queue at its original position
                    queue[indexToReplace] = page;
                }
            }

            // Update frameContent for GUI
            System.arraycopy(queue, 0, frameContent, 0, capacity);

            // Update the table in GUI for each iteration
            updateTable(i, frameContent.clone());
        }

        return pageFaults;
    }

    // Helper function to check if a page is in the current frames
    static boolean checkHit(int incomingPage, int[] queue, int occupied) {
        for (int i = 0; i < occupied; i++) {
            if (incomingPage == queue[i])
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PageReplacementSimulator simulator = new PageReplacementSimulator();
            simulator.setVisible(true);
        });
    }
}
