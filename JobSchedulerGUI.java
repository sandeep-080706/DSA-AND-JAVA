
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class JobSchedulerGUI extends JFrame {
    private JTextField txtPackageID, txtPriority, txtDestination, txtStatus, txtSender, txtReceiver, txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> statusFilter;
    private JLabel summaryLabel;

    public JobSchedulerGUI() {
        setTitle("Logistics Tracking System");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel inputPanel = new JPanel(new GridLayout(3, 6, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Package Details"));

        txtPackageID = new JTextField();
        txtPriority = new JTextField();
        txtDestination = new JTextField();
        txtStatus = new JTextField();
        txtSender = new JTextField();
        txtReceiver = new JTextField();

        inputPanel.add(new JLabel("Package ID:"));
        inputPanel.add(txtPackageID);
        inputPanel.add(new JLabel("Priority (1-3):"));
        inputPanel.add(txtPriority);
        inputPanel.add(new JLabel("Destination:"));
        inputPanel.add(txtDestination);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(txtStatus);
        inputPanel.add(new JLabel("Sender:"));
        inputPanel.add(txtSender);
        inputPanel.add(new JLabel("Receiver:"));
        inputPanel.add(txtReceiver);

        JButton btnAdd = new JButton("Add Package");
        btnAdd.setBackground(new Color(100, 200, 100));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addPackage());
        inputPanel.add(btnAdd);

        add(inputPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(
                new String[] { "PackageID", "Priority", "Destination", "Status", "SenderName", "ReceiverName" });
        table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                String priority = model.getValueAt(row, 1).toString();
                if (!isRowSelected(row)) {
                    switch (priority) {
                        case "1":
                            c.setBackground(new Color(255, 102, 102));
                            break;
                        case "2":
                            c.setBackground(new Color(255, 255, 153));
                            break;
                        case "3":
                            c.setBackground(new Color(153, 255, 153));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(new Color(184, 207, 229));
                }
                return c;
            }
        };

        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnExport = new JButton("Export");
        JButton btnImport = new JButton("Import");
        JButton btnExit = new JButton("Exit");

        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnExport.addActionListener(e -> exportToFile());
        btnImport.addActionListener(e -> importCSV());
        btnExit.addActionListener(e -> System.exit(0));

        controlPanel.add(btnEdit);
        controlPanel.add(btnDelete);
        controlPanel.add(btnExport);
        controlPanel.add(btnImport);
        controlPanel.add(btnExit);
        add(controlPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtSearch = new JTextField(10);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchTable(txtSearch.getText());
            }
        });

        statusFilter = new JComboBox<>(new String[] { "All", "Dispatched", "In Transit", "Delivered" });
        statusFilter.addActionListener(e -> filterStatus((String) statusFilter.getSelectedItem()));

        topPanel.add(new JLabel("Search:"));
        topPanel.add(txtSearch);
        topPanel.add(new JLabel("Status Filter:"));
        topPanel.add(statusFilter);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 0));
        add(summaryLabel, BorderLayout.AFTER_LAST_LINE);

        importCSV();
        updateSummary();
        setVisible(true);
    }

    private void addPackage() {
        model.addRow(new String[] {
                txtPackageID.getText(),
                txtPriority.getText(),
                txtDestination.getText(),
                txtStatus.getText(),
                txtSender.getText(),
                txtReceiver.getText()
        });
        clearFields();
        updateSummary();
    }

    private void clearFields() {
        txtPackageID.setText("");
        txtPriority.setText("");
        txtDestination.setText("");
        txtStatus.setText("");
        txtSender.setText("");
        txtReceiver.setText("");
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row != -1) {
            model.removeRow(row);
            updateSummary();
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtPackageID.setText(model.getValueAt(row, 0).toString());
            txtPriority.setText(model.getValueAt(row, 1).toString());
            txtDestination.setText(model.getValueAt(row, 2).toString());
            txtStatus.setText(model.getValueAt(row, 3).toString());
            txtSender.setText(model.getValueAt(row, 4).toString());
            txtReceiver.setText(model.getValueAt(row, 5).toString());
            model.removeRow(row);
            updateSummary();
        }
    }

    private void exportToFile() {
        try (FileWriter fw = new FileWriter("exported_logistics.csv")) {
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    fw.write(model.getValueAt(i, j).toString() + (j == model.getColumnCount() - 1 ? "\n" : ","));
                }
            }
            JOptionPane.showMessageDialog(this, "Exported Successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void importCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (Scanner sc = new Scanner(selectedFile)) {
                model.setRowCount(0); // Clear table before importing
                if (sc.hasNextLine())
                    sc.nextLine(); // Skip header
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] data = line.split(",");
                    if (data.length == 6) {
                        model.addRow(data);
                    }
                }
                updateSummary();
                JOptionPane.showMessageDialog(this, "Imported successfully from: " + selectedFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Import cancelled.");
        }
    }

    private void searchTable(String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
    }

    private void filterStatus(String status) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        if (status.equals("All")) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(status, 3));
        }
    }

    private void updateSummary() {
        int total = model.getRowCount();
        int dispatched = 0, inTransit = 0, delivered = 0;
        for (int i = 0; i < total; i++) {
            String status = model.getValueAt(i, 3).toString().toLowerCase();
            if (status.contains("dispatch"))
                dispatched++;
            else if (status.contains("in transit"))
                inTransit++;
            else if (status.contains("deliver"))
                delivered++;
        }
        summaryLabel.setText("Total: " + total + " | Dispatched: " + dispatched + " | In Transit: " + inTransit
                + " | Delivered: " + delivered);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JobSchedulerGUI::new);
    }
}