package Inventario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class ProductAppXML {
    private static final String FILE_NAME = "productos.xml";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductAppXML().createAndShowGUI());
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

        loadProductsFromXML(tableModel);

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
            saveProductToXML(code, name, price, category);
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

    private void saveProductToXML(String code, String name, String price, String category) {
        try {
            File file = new File(FILE_NAME);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            if (file.exists()) {
                doc = builder.parse(file);
            } else {
                doc = builder.newDocument();
                Element rootElement = doc.createElement("Productos");
                doc.appendChild(rootElement);
            }

            Element product = doc.createElement("Producto");

            Element codeElement = doc.createElement("Codigo");
            codeElement.appendChild(doc.createTextNode(code));
            product.appendChild(codeElement);

            Element nameElement = doc.createElement("Nombre");
            nameElement.appendChild(doc.createTextNode(name));
            product.appendChild(nameElement);

            Element priceElement = doc.createElement("Precio");
            priceElement.appendChild(doc.createTextNode(price));
            product.appendChild(priceElement);

            Element categoryElement = doc.createElement("Categoria");
            categoryElement.appendChild(doc.createTextNode(category));
            product.appendChild(categoryElement);

            doc.getDocumentElement().appendChild(product);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el producto en el archivo XML.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductsFromXML(DefaultTableModel tableModel) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Producto");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String code = element.getElementsByTagName("Codigo").item(0).getTextContent();
                    String name = element.getElementsByTagName("Nombre").item(0).getTextContent();
                    String price = element.getElementsByTagName("Precio").item(0).getTextContent();
                    String category = element.getElementsByTagName("Categoria").item(0).getTextContent();

                    tableModel.addRow(new Object[]{code, name, price, category});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los productos desde el archivo XML.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
