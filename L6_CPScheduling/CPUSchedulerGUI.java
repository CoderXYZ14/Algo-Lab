import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.Queue;

import javax.swing.table.DefaultTableModel;


public class CPUSchedulerGUI extends JFrame {
    private JTextField processCountField;
    private JPanel inputPanel;
    private JPanel algorithmPanel;
    private JPanel resultPanel;
    private JTable processTable;
    private JTable resultTable;
    private JPanel ganttChartPanel;
    private JCheckBox preemptiveCheckBox;
    private DefaultTableModel processTableModel;
    private DefaultTableModel resultTableModel;

    public CPUSchedulerGUI() {
        setTitle("CPU Scheduler Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panels
        createInputPanel();
        createAlgorithmPanel();
        createResultPanel();

        // Add panels to frame
        add(inputPanel, BorderLayout.NORTH);
        add(algorithmPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }

    private void createInputPanel() {
        inputPanel = new JPanel(new BorderLayout());
        JPanel topInputPanel = new JPanel(new FlowLayout());
        
        JLabel processCountLabel = new JLabel("Number of Processes:");
        processCountField = new JTextField(5);
        JButton createTableButton = new JButton("Create Table");
        
        topInputPanel.add(processCountLabel);
        topInputPanel.add(processCountField);
        topInputPanel.add(createTableButton);
        
        // Process input table
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        processTableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(processTableModel);
        JScrollPane tableScrollPane = new JScrollPane(processTable);
        
        createTableButton.addActionListener(e -> createProcessTable());
        
        inputPanel.add(topInputPanel, BorderLayout.NORTH);
        inputPanel.add(tableScrollPane, BorderLayout.CENTER);
    }

    private void createAlgorithmPanel() {
        algorithmPanel = new JPanel(new FlowLayout());
        
        JButton sjfButton = new JButton("SJF");
        JButton rrButton = new JButton("Round Robin");
        JButton priorityButton = new JButton("Priority");
        preemptiveCheckBox = new JCheckBox("Preemptive");
        JTextField quantumField = new JTextField(5);
        JLabel quantumLabel = new JLabel("Time Quantum:");
        
        algorithmPanel.add(sjfButton);
        algorithmPanel.add(rrButton);
        algorithmPanel.add(priorityButton);
        algorithmPanel.add(preemptiveCheckBox);
        algorithmPanel.add(quantumLabel);
        algorithmPanel.add(quantumField);
        
        sjfButton.addActionListener(e -> runSJF());
        rrButton.addActionListener(e -> {
            try {
                int quantum = Integer.parseInt(quantumField.getText());
                runRoundRobin(quantum);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid time quantum");
            }
        });
        priorityButton.addActionListener(e -> runPriority());
    }

    private void createResultPanel() {
        resultPanel = new JPanel(new BorderLayout());
        
        // Gantt chart panel
        ganttChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Gantt chart will be drawn here
            }
        };
        ganttChartPanel.setPreferredSize(new Dimension(700, 100));
        
        // Result table
        String[] resultColumnNames = {"Process ID", "Completion Time", "Turnaround Time", "Waiting Time", "Response Time"};
        resultTableModel = new DefaultTableModel(resultColumnNames, 0);
        resultTable = new JTable(resultTableModel);
        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        
        resultPanel.add(ganttChartPanel, BorderLayout.NORTH);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);
    }

    private void createProcessTable() {
        try {
            int processCount = Integer.parseInt(processCountField.getText());
            // if (processCount <= 0) {
            //     throw new IllegalArgumentException("Process count must be positive");
            // }
            processTableModel.setRowCount(0);
            for (int i = 0; i < processCount; i++) {
                processTableModel.addRow(new Object[]{String.valueOf(i + 1), "0", "1", "0"});
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid number for process count", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private ArrayList<Process> getProcessesFromTable() {
    ArrayList<Process> processes = new ArrayList<>();
    for (int i = 0; i < processTableModel.getRowCount(); i++) {
        try {
            int pid = Integer.parseInt(processTableModel.getValueAt(i, 0).toString());
            int arrivalTime = Integer.parseInt(processTableModel.getValueAt(i, 1).toString());
            int burstTime = Integer.parseInt(processTableModel.getValueAt(i, 2).toString());
            int priority = Integer.parseInt(processTableModel.getValueAt(i, 3).toString());
            
            // if (arrivalTime < 0 || burstTime <= 0 || priority < 0) {
            //     throw new IllegalArgumentException("Invalid input values");
            // }
            
            processes.add(new Process(pid, arrivalTime, burstTime, priority));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for all fields in row " + (i + 1), 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }
    return processes;
}

private void runSJF() {
    ArrayList<Process> processes = getProcessesFromTable();
    if (!processes.isEmpty()) {
        boolean isPreemptive = preemptiveCheckBox.isSelected();
        SJFScheduler scheduler = new SJFScheduler(processes, isPreemptive);
        displayResults(scheduler.schedule());
    }
}

private void runRoundRobin(int quantum) {
    ArrayList<Process> processes = getProcessesFromTable();
    if (!processes.isEmpty()) {
        RoundRobinScheduler scheduler = new RoundRobinScheduler(processes, quantum);
        displayResults(scheduler.schedule());
    }
}

private void runPriority() {
    ArrayList<Process> processes = getProcessesFromTable();
    if (!processes.isEmpty()) {
        boolean isPreemptive = preemptiveCheckBox.isSelected();
        PriorityScheduler scheduler = new PriorityScheduler(processes, isPreemptive);
        displayResults(scheduler.schedule());
    }
}
private void displayResults(SchedulingResult result) {
    // Clear previous results
    resultTableModel.setRowCount(0);

    // Update result table
    for (Process p : result.getProcesses()) {
        resultTableModel.addRow(new Object[]{
            p.pid,
            p.completionTime,
            p.turnaroundTime,
            p.waitingTime,
            p.responseTime
        });
    }

    // Update Gantt chart
    updateGanttChart(result.getGanttChart());
    
    // Display averages
    double avgTurnaroundTime = result.getAverageTurnaroundTime();
    double avgWaitingTime = result.getAverageWaitingTime();
    double avgResponseTime = result.getAverageResponseTime();
    
    JOptionPane.showMessageDialog(this,
        String.format("Average Turnaround Time: %.2f\nAverage Waiting Time: %.2f\nAverage Response Time: %.2f",
            avgTurnaroundTime, avgWaitingTime, avgResponseTime));
}


private void updateGanttChart(ArrayList<GanttChartEntry> ganttChart) {
    ganttChartPanel.removeAll();
    ganttChartPanel.setLayout(new BorderLayout());
    
    JPanel chartPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int totalTime = ganttChart.get(ganttChart.size() - 1).endTime;
            int width = getWidth() - 40; // Leave some margin
            int height = getHeight() - 40;
            int unitWidth = width / totalTime;
            int y = 20;
            
            // Draw time axis
            g2d.drawLine(20, height - 10, width + 20, height - 10);
            
            // Draw processes
            for (GanttChartEntry entry : ganttChart) {
                int x1 = 20 + (entry.startTime * unitWidth);
                int x2 = 20 + (entry.endTime * unitWidth);
                
                // Draw process box
                g2d.setColor(new Color(100, 149, 237)); // Cornflower blue
                g2d.fillRect(x1, y, x2 - x1, height - 40);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x1, y, x2 - x1, height - 40);
                
                // Draw process label
                String label = "P" + entry.processId;
                FontMetrics fm = g2d.getFontMetrics();
                int labelX = x1 + ((x2 - x1) - fm.stringWidth(label)) / 2;
                g2d.drawString(label, labelX, y + (height - 40) / 2);
                
                // Draw time labels
                g2d.drawString(String.valueOf(entry.startTime), x1, height - 5);
                if (entry == ganttChart.get(ganttChart.size() - 1)) {
                    g2d.drawString(String.valueOf(entry.endTime), x2, height - 5);
                }
            }
        }
    };
    
    chartPanel.setPreferredSize(new Dimension(700, 100));
    ganttChartPanel.add(chartPanel, BorderLayout.CENTER);
    ganttChartPanel.revalidate();
    ganttChartPanel.repaint();
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CPUSchedulerGUI gui = new CPUSchedulerGUI();
            gui.setVisible(true);
        });
    }
}

class Process implements Comparable<Process> {
    int pid;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;
    int responseTime;
    boolean started;

    public Process(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.started = false;
    }

    @Override
    public int compareTo(Process other) {
        return Integer.compare(this.burstTime, other.burstTime);
    }
}


class SchedulingResult {
    private ArrayList<Process> processes;
    private ArrayList<GanttChartEntry> ganttChart;

    public SchedulingResult(ArrayList<Process> processes, ArrayList<GanttChartEntry> ganttChart) {
        this.processes = processes;
        this.ganttChart = ganttChart;
    }

    public ArrayList<Process> getProcesses() {
        return processes;
    }

    public ArrayList<GanttChartEntry> getGanttChart() {
        return ganttChart;
    }

    public double getAverageTurnaroundTime() {
        return processes.stream().mapToDouble(p -> p.turnaroundTime).average().orElse(0);
    }

    public double getAverageWaitingTime() {
        return processes.stream().mapToDouble(p -> p.waitingTime).average().orElse(0);
    }

    public double getAverageResponseTime() {
        return processes.stream().mapToDouble(p -> p.responseTime).average().orElse(0);
    }
}

abstract class Scheduler {
    protected ArrayList<Process> processes;
    
    public Scheduler(ArrayList<Process> processes) {
        this.processes = new ArrayList<>(processes);
    }
    
    abstract public SchedulingResult schedule();
    
    protected void calculateTimes(Process p, int completionTime) {
        p.completionTime = completionTime;
        p.turnaroundTime = p.completionTime - p.arrivalTime;
        p.waitingTime = p.turnaroundTime - p.burstTime;
    }
}

class SJFScheduler extends Scheduler {
    private boolean isPreemptive;
    
    public SJFScheduler(ArrayList<Process> processes, boolean isPreemptive) {
        super(processes);
        this.isPreemptive = isPreemptive;
    }
    
    @Override
    public SchedulingResult schedule() {
        ArrayList<GanttChartEntry> ganttChart = new ArrayList<>();
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));
        ArrayList<Process> tempProcesses = new ArrayList<>(processes);
        int currentTime = 0;
        int completed = 0;
        Process currentProcess = null;
        int lastSwitchTime = 0;

        while (completed < processes.size()) {
            // Add newly arrived processes to ready queue
            for (Iterator<Process> iterator = tempProcesses.iterator(); iterator.hasNext();) {
                Process p = iterator.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    iterator.remove();
                }
            }

            if (isPreemptive && currentProcess != null && !readyQueue.isEmpty()) {
                Process shortestJob = readyQueue.peek();
                if (shortestJob.remainingTime < currentProcess.remainingTime) {
                    readyQueue.add(currentProcess);
                    ganttChart.add(new GanttChartEntry(currentProcess.pid, lastSwitchTime, currentTime));
                    currentProcess = readyQueue.poll();
                    lastSwitchTime = currentTime;
                }
            }

            if (currentProcess == null || currentProcess.remainingTime == 0) {
                if (currentProcess != null) {
                    ganttChart.add(new GanttChartEntry(currentProcess.pid, lastSwitchTime, currentTime));
                    calculateTimes(currentProcess, currentTime);
                    completed++;
                }
                
                if (!readyQueue.isEmpty()) {
                    currentProcess = readyQueue.poll();
                    if (!currentProcess.started) {
                        currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                        currentProcess.started = true;
                    }
                    lastSwitchTime = currentTime;
                } else if (tempProcesses.isEmpty()) {
                    break;
                } else {
                    currentTime = tempProcesses.get(0).arrivalTime;
                    continue;
                }
            }

            currentProcess.remainingTime--;
            currentTime++;
        }

        if (currentProcess != null && currentProcess.remainingTime == 0) {
            ganttChart.add(new GanttChartEntry(currentProcess.pid, lastSwitchTime, currentTime));
            calculateTimes(currentProcess, currentTime);
        }

        return new SchedulingResult(new ArrayList<>(processes), ganttChart);
    }
}

class RoundRobinScheduler extends Scheduler {
    private int quantum;
    
    public RoundRobinScheduler(ArrayList<Process> processes, int quantum) {
        super(processes);
        this.quantum = quantum;
    }
    
    @Override
    public SchedulingResult schedule() {
        ArrayList<GanttChartEntry> ganttChart = new ArrayList<>();
        Queue<Process> readyQueue = new LinkedList<>();
        ArrayList<Process> tempProcesses = new ArrayList<>(processes);
        int currentTime = 0;
        int completed = 0;
        
        while (completed < processes.size()) {
            // Add newly arrived processes to ready queue
            for (Iterator<Process> iterator = tempProcesses.iterator(); iterator.hasNext();) {
                Process p = iterator.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    iterator.remove();
                }
            }

            if (readyQueue.isEmpty() && !tempProcesses.isEmpty()) {
                currentTime = tempProcesses.get(0).arrivalTime;
                continue;
            }

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.poll();
                if (!currentProcess.started) {
                    currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.started = true;
                }

                int executionTime = Math.min(quantum, currentProcess.remainingTime);
                ganttChart.add(new GanttChartEntry(currentProcess.pid, currentTime, currentTime + executionTime));
                currentTime += executionTime;
                currentProcess.remainingTime -= executionTime;

                // Add newly arrived processes during this quantum
                for (Iterator<Process> iterator = tempProcesses.iterator(); iterator.hasNext();) {
                    Process p = iterator.next();
                    if (p.arrivalTime <= currentTime) {
                        readyQueue.add(p);
                        iterator.remove();
                    }
                }

                if (currentProcess.remainingTime > 0) {
                    readyQueue.add(currentProcess);
                } else {
                    calculateTimes(currentProcess, currentTime);
                    completed++;
                }
            } else {
                currentTime++;
            }
        }

        return new SchedulingResult(new ArrayList<>(processes), ganttChart);
    }
}

class PriorityScheduler extends Scheduler {
    private boolean isPreemptive;
    
    public PriorityScheduler(ArrayList<Process> processes, boolean isPreemptive) {
        super(processes);
        this.isPreemptive = isPreemptive;
    }
    
    @Override
    public SchedulingResult schedule() {
        ArrayList<GanttChartEntry> ganttChart = new ArrayList<>();
        ArrayList<Process> readyQueue = new ArrayList<>();
        ArrayList<Process> tempProcesses = new ArrayList<>(processes);
        int currentTime = 0;
        int completed = 0;

        while (completed < processes.size()) {
            // Add newly arrived processes to ready queue
            for (Iterator<Process> iterator = tempProcesses.iterator(); iterator.hasNext();) {
                Process p = iterator.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    iterator.remove();
                }
            }

            // Sort ready queue by priority (higher number = higher priority)
            readyQueue.sort((p1, p2) -> p2.priority - p1.priority);

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.get(0);
                
                if (!currentProcess.started) {
                    currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.started = true;
                }

                int executionTime = 1; // Execute for 1 time unit
                ganttChart.add(new GanttChartEntry(currentProcess.pid, currentTime, currentTime + executionTime));
                currentTime += executionTime;
                currentProcess.remainingTime -= executionTime;

                if (currentProcess.remainingTime == 0) {
                    readyQueue.remove(0);
                    calculateTimes(currentProcess, currentTime);
                    completed++;
                } else if (isPreemptive) {
                    // For preemptive, we'll re-evaluate priorities after each time unit
                    readyQueue.remove(0);
                    readyQueue.add(currentProcess);
                }
            } else {
                currentTime++;
            }
        }

        return new SchedulingResult(new ArrayList<>(processes), ganttChart);
    }
}