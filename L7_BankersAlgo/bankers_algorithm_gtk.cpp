#include <gtk/gtk.h>
#include <iostream>
#include <vector>
#include <string>
#include <sstream>

class BankersAlgorithm
{
private:
    int numProcesses;
    int numResources;
    std::vector<std::vector<int>> allocation;
    std::vector<std::vector<int>> maxNeed;
    std::vector<int> available;
    std::vector<std::vector<int>> need;

    GtkWidget *window;
    GtkWidget *processesEntry;
    GtkWidget *resourcesEntry;
    GtkWidget *allocationGrid;
    GtkWidget *maxNeedGrid;
    GtkWidget *availableGrid;
    GtkWidget *needGrid;
    GtkWidget *resultLabel;
    GtkTextBuffer *outputBuffer;
    GtkWidget *outputView;

public:
    BankersAlgorithm() : numProcesses(0), numResources(0) {}

    static void activate(GtkApplication *app, gpointer user_data)
    {
        BankersAlgorithm *bankersAlgo = static_cast<BankersAlgorithm *>(user_data);
        bankersAlgo->createWindow(app);
    }

    void createWindow(GtkApplication *app)
    {
        window = gtk_application_window_new(app);
        gtk_window_set_title(GTK_WINDOW(window), "Banker's Algorithm");
        gtk_window_set_default_size(GTK_WINDOW(window), 800, 600);

        GtkWidget *grid = gtk_grid_new();
        gtk_container_add(GTK_CONTAINER(window), grid);

        createInputSection(grid);
        createTableSection(grid);

        gtk_widget_show_all(window);
    }

    void createInputSection(GtkWidget *grid)
    {
        gtk_grid_attach(GTK_GRID(grid), gtk_label_new("Number of Processes:"), 0, 0, 1, 1);
        processesEntry = gtk_entry_new();
        gtk_grid_attach(GTK_GRID(grid), processesEntry, 1, 0, 1, 1);

        gtk_grid_attach(GTK_GRID(grid), gtk_label_new("Number of Resources:"), 2, 0, 1, 1);
        resourcesEntry = gtk_entry_new();
        gtk_grid_attach(GTK_GRID(grid), resourcesEntry, 3, 0, 1, 1);

        GtkWidget *createTablesButton = gtk_button_new_with_label("Create Tables");
        g_signal_connect(createTablesButton, "clicked", G_CALLBACK(onCreateTables), this);
        gtk_grid_attach(GTK_GRID(grid), createTablesButton, 4, 0, 1, 1);
    }

    void createTableSection(GtkWidget *grid)
    {
        GtkWidget *tablesGrid = gtk_grid_new();
        gtk_grid_set_column_spacing(GTK_GRID(tablesGrid), 10);
        gtk_grid_attach(GTK_GRID(grid), tablesGrid, 0, 1, 5, 1);

        allocationGrid = gtk_grid_new();
        maxNeedGrid = gtk_grid_new();
        availableGrid = gtk_grid_new();
        needGrid = gtk_grid_new();

        gtk_grid_attach(GTK_GRID(tablesGrid), gtk_label_new("Allocation:"), 0, 0, 1, 1);
        gtk_grid_attach(GTK_GRID(tablesGrid), allocationGrid, 0, 1, 1, 1);

        gtk_grid_attach(GTK_GRID(tablesGrid), gtk_label_new("Max Need:"), 1, 0, 1, 1);
        gtk_grid_attach(GTK_GRID(tablesGrid), maxNeedGrid, 1, 1, 1, 1);

        gtk_grid_attach(GTK_GRID(tablesGrid), gtk_label_new("Available:"), 2, 0, 1, 1);
        gtk_grid_attach(GTK_GRID(tablesGrid), availableGrid, 2, 1, 1, 1);

        gtk_grid_attach(GTK_GRID(tablesGrid), gtk_label_new("Need:"), 3, 0, 1, 1);
        gtk_grid_attach(GTK_GRID(tablesGrid), needGrid, 3, 1, 1, 1);

        GtkWidget *calculateButton = gtk_button_new_with_label("Calculate");
        g_signal_connect(calculateButton, "clicked", G_CALLBACK(onCalculate), this);
        gtk_grid_attach(GTK_GRID(grid), calculateButton, 0, 2, 1, 1);

        resultLabel = gtk_label_new("");
        gtk_grid_attach(GTK_GRID(grid), resultLabel, 0, 3, 5, 1);

        outputView = gtk_text_view_new();
        outputBuffer = gtk_text_view_get_buffer(GTK_TEXT_VIEW(outputView));
        GtkWidget *scrolledWindow = gtk_scrolled_window_new(NULL, NULL);
        gtk_container_add(GTK_CONTAINER(scrolledWindow), outputView);
        gtk_widget_set_size_request(scrolledWindow, -1, 100);
        gtk_grid_attach(GTK_GRID(grid), scrolledWindow, 0, 4, 5, 1);
    }

    void createTables()
    {
        numProcesses = std::stoi(gtk_entry_get_text(GTK_ENTRY(processesEntry)));
        numResources = std::stoi(gtk_entry_get_text(GTK_ENTRY(resourcesEntry)));

        setupTable(allocationGrid, "Allocation");
        setupTable(maxNeedGrid, "Max Need");
        setupTable(availableGrid, "Available", true);
        setupTable(needGrid, "Need");
    }

    void setupTable(GtkWidget *grid, const std::string &name, bool isAvailable = false)
    {
        GList *children, *iter;
        children = gtk_container_get_children(GTK_CONTAINER(grid));
        for (iter = children; iter != NULL; iter = g_list_next(iter))
            gtk_widget_destroy(GTK_WIDGET(iter->data));
        g_list_free(children);

        int rows = isAvailable ? 1 : numProcesses;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < numResources; j++)
            {
                GtkWidget *entry = gtk_entry_new();
                gtk_entry_set_width_chars(GTK_ENTRY(entry), 5);
                gtk_grid_attach(GTK_GRID(grid), entry, j, i, 1, 1);
            }
        }

        gtk_widget_show_all(grid);
    }

    void runBankersAlgorithm()
    {
        getTableData();
        calculateNeed();
        updateNeedTable();

        std::vector<int> work = available;
        std::vector<bool> finish(numProcesses, false);
        std::vector<int> safeSequence;

        int count = 0;
        while (count < numProcesses)
        {
            bool found = false;
            for (int i = 0; i < numProcesses; i++)
            {
                if (!finish[i])
                {
                    int j;
                    for (j = 0; j < numResources; j++)
                        if (need[i][j] > work[j])
                            break;

                    if (j == numResources)
                    {
                        for (int k = 0; k < numResources; k++)
                            work[k] += allocation[i][k];
                        safeSequence.push_back(i);
                        finish[i] = true;
                        found = true;
                        count++;
                    }
                }
            }

            if (!found)
                break;
        }

        std::stringstream ss;
        if (count == numProcesses)
        {
            ss << "Safe sequence found: ";
            for (size_t i = 0; i < safeSequence.size(); i++)
            {
                ss << "P" << safeSequence[i];
                if (i < safeSequence.size() - 1)
                    ss << " -> ";
            }
            ss << "\n\nSystem is in a safe state.";
        }
        else
        {
            ss << "The system is in an unsafe state (deadlock detected).";
        }

        gtk_text_buffer_set_text(outputBuffer, ss.str().c_str(), -1);
    }

    void updateNeedTable()
    {
        for (int i = 0; i < numProcesses; i++)
        {
            for (int j = 0; j < numResources; j++)
            {
                GtkWidget *entry = gtk_grid_get_child_at(GTK_GRID(needGrid), j, i);
                gtk_entry_set_text(GTK_ENTRY(entry), std::to_string(need[i][j]).c_str());
            }
        }
    }

    void getTableData()
    {
        allocation.clear();
        maxNeed.clear();
        available.clear();

        for (int i = 0; i < numProcesses; i++)
        {
            std::vector<int> allocRow, maxRow;
            for (int j = 0; j < numResources; j++)
            {
                GtkWidget *allocEntry = gtk_grid_get_child_at(GTK_GRID(allocationGrid), j, i);
                GtkWidget *maxEntry = gtk_grid_get_child_at(GTK_GRID(maxNeedGrid), j, i);
                allocRow.push_back(std::stoi(gtk_entry_get_text(GTK_ENTRY(allocEntry))));
                maxRow.push_back(std::stoi(gtk_entry_get_text(GTK_ENTRY(maxEntry))));
            }
            allocation.push_back(allocRow);
            maxNeed.push_back(maxRow);
        }

        for (int j = 0; j < numResources; j++)
        {
            GtkWidget *availEntry = gtk_grid_get_child_at(GTK_GRID(availableGrid), j, 0);
            available.push_back(std::stoi(gtk_entry_get_text(GTK_ENTRY(availEntry))));
        }
    }

    void calculateNeed()
    {
        need.clear();
        need.resize(numProcesses, std::vector<int>(numResources));
        for (int i = 0; i < numProcesses; i++)
        {
            for (int j = 0; j < numResources; j++)
            {
                need[i][j] = maxNeed[i][j] - allocation[i][j];
            }
        }
    }

    static void onCreateTables(GtkWidget *widget, gpointer data)
    {
        BankersAlgorithm *bankersAlgo = static_cast<BankersAlgorithm *>(data);
        bankersAlgo->createTables();
    }

    static void onCalculate(GtkWidget *widget, gpointer data)
    {
        BankersAlgorithm *bankersAlgo = static_cast<BankersAlgorithm *>(data);
        bankersAlgo->runBankersAlgorithm();
    }
};

int main(int argc, char **argv)
{
    BankersAlgorithm bankersAlgo;

    GtkApplication *app = gtk_application_new("org.gtk.example", G_APPLICATION_DEFAULT_FLAGS);
    g_signal_connect(app, "activate", G_CALLBACK(BankersAlgorithm::activate), &bankersAlgo);
    int status = g_application_run(G_APPLICATION(app), argc, argv);
    g_object_unref(app);

    return status;
}