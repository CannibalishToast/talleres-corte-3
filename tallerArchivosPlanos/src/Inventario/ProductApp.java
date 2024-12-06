import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class ProductApp {
    private static final String FILE_NAME = "productos.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductApp().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Gestión de Productos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Código", "Nombre", "Precio", "Categoría"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        loadProductsFromFile(tableModel);

        JButton addButton = new JButton("Agregar Producto");
        addButton.addActionListener(e -> openProductForm(tableModel));

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(addButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void openProductForm(DefaultTableModel tableModel) {
        JDialog formDialog = new JDialog();
        formDialog.setTitle("Agregar Producto");
        formDialog.setSize(400, 300);
        formDialog.setLayout(new GridLayout(5, 2));
        formDialog.setModal(true);

        JLabel codeLabel = new JLabel("Código de Producto:");
        JTextField codeField = new JTextField();
        JLabel nameLabel = new JLabel("Nombre:");
        JTextField nameField = new JTextField();
        JLabel priceLabel = new JLabel("Precio:");
        JTextField priceField = new JTextField();
        JLabel categoryLabel = new JLabel("Categoría:");
        JTextField categoryField = new JTextField();

        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");

        saveButton.addActionListener(e -> {
            String code = codeField.getText();
            String name = nameField.getText();
            String price = priceField.getText();
            String category = categoryField.getText();

            if (code.isEmpty() || name.isEmpty() || price.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(formDialog, "Por favor complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Double.parseDouble(price);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(formDialog, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.addRow(new Object[]{code, name, price, category});
            saveProductToFile(code, name, price, category);
            formDialog.dispose();
        });

        cancelButton.addActionListener(e -> formDialog.dispose());

        formDialog.add(codeLabel);
        formDialog.add(codeField);
        formDialog.add(nameLabel);
        formDialog.add(nameField);
        formDialog.add(priceLabel);
        formDialog.add(priceField);
        formDialog.add(categoryLabel);
        formDialog.add(categoryField);
        formDialog.add(saveButton);
        formDialog.add(cancelButton);

        formDialog.setVisible(true);
    }

    private void saveProductToFile(String code, String name, String price, String category) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(code + "," + name + "," + price + "," + category + System.lineSeparator());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el producto en el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductsFromFile(DefaultTableModel tableModel) {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                if (data.length == 4) {
                    tableModel.addRow(data);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los productos desde el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
