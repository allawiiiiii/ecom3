import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    private static ProductService productService = new ProductService();
    private static CustomerService customerService = new CustomerService();
    private static OrderService orderService = new OrderService();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("E-com");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        //Product Tab
        tabbedPane.addTab("Products", createProductPanel());

        //Customer Tab
        tabbedPane.addTab("Customers", createCustomerPanel());

        //Order Tab
        tabbedPane.addTab("Orders", createOrderPanel());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    //PRODUCT PANEL
    private static JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new BorderLayout());

        //Top tabs
        JPanel inputPanel = new JPanel();

        JTextField prodNameField = new JTextField(8);
        JTextField prodPriceField = new JTextField(5);
        JTextField prodStockField = new JTextField(5);

        JButton listProductsButton = new JButton("List All");
        JTextField searchField = new JTextField(6);
        JButton searchButton = new JButton("Search");
        JButton updateStockButton = new JButton("Update Stock");

        //Center text
        JTextArea productArea = new JTextArea();
        productArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(productArea);


        //Lista alla produkter
        listProductsButton.addActionListener(e -> {
            List<String> products = productService.getAllProducts();
            productArea.setText("");
            if (products.isEmpty()) {
                productArea.append("No products found.");
            } else {
                for (String p : products) {
                    productArea.append(p + "\n");
                }
            }
        });

        //Sök produkter
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            productArea.setText("");
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(productPanel, "Enter a search term.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<String> results = productService.searchProducts(keyword);
            if (results.isEmpty()) {
                productArea.append("No matching products found for: " + keyword);
            } else {
                for (String p : results) {
                    productArea.append(p + "\n");
                }
            }
        });

        //Uppdatera stock
        updateStockButton.addActionListener(e -> {
            String productName = JOptionPane.showInputDialog(productPanel, "Enter product name:");
            if (productName != null && !productName.trim().isEmpty()) {
                String newStockStr = JOptionPane.showInputDialog(productPanel, "Enter new stock quantity:");
                try {
                    int newStock = Integer.parseInt(newStockStr);
                    productService.updateStock(productName.trim(), newStock);
                    JOptionPane.showMessageDialog(productPanel, "Stock updated!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(productPanel, "Invalid stock number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(productPanel, "Enter a valid product name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //Input panel
        inputPanel.add(listProductsButton);

        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        inputPanel.add(updateStockButton);

        productPanel.add(inputPanel, BorderLayout.NORTH);
        productPanel.add(scrollPane, BorderLayout.CENTER);

        return productPanel;
    }

    //CUSTOMER PANEL
    private static JPanel createCustomerPanel() {
        JPanel customerPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();

        JTextField custNameField = new JTextField(10);
        JTextField custEmailField = new JTextField(12);

        JButton registerButton = new JButton("Register");
        JButton updateEmailButton = new JButton("Update Email");

        JTextArea customerArea = new JTextArea();
        customerArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(customerArea);

        //Registrera kund, automatiskt ge lösenord "password"
        registerButton.addActionListener(e -> {
            String name = custNameField.getText().trim();
            String email = custEmailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(customerPanel, "Enter name and email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        //uppdatera kund email när den inte ändras
        updateEmailButton.addActionListener(e -> {
            String name = custNameField.getText().trim();
            String newEmail = custEmailField.getText().trim();

            if (name.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(customerPanel, "Enter a valid name and email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (customerService.customerExists(name)) {
                customerService.updateCustomerEmail(name, newEmail);
                JOptionPane.showMessageDialog(customerPanel, "Email updated!");
            } else {
                JOptionPane.showMessageDialog(customerPanel, "Customer not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(custNameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(custEmailField);
        inputPanel.add(registerButton);
        inputPanel.add(updateEmailButton);

        customerPanel.add(inputPanel, BorderLayout.NORTH);
        customerPanel.add(scrollPane, BorderLayout.CENTER);

        return customerPanel;
    }

    //ORDER PANEL
    private static JPanel createOrderPanel() {
        JPanel orderPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JTextField orderCustField = new JTextField(8);
        JTextField orderProdField = new JTextField(8);
        JTextField orderQtyField  = new JTextField(5);

        JButton createOrderButton = new JButton("Create Order");
        JButton historyButton     = new JButton("Order History");

        JTextArea orderArea = new JTextArea();
        orderArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderArea);

        //Skapa order
        createOrderButton.addActionListener(e -> {
            String custName = orderCustField.getText().trim();
            String prodName = orderProdField.getText().trim();
            int quantity;
            try {
                quantity = Integer.parseInt(orderQtyField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(orderPanel, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (custName.isEmpty() || prodName.isEmpty()) {
                JOptionPane.showMessageDialog(orderPanel, "Enter a customer name and product name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //kontrollera ifall kunden finns
            if (!customerService.customerExists(custName)) {
                JOptionPane.showMessageDialog(orderPanel, "Customer not found! Register first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Skapa order
            try {
                orderService.createOrder(custName, prodName, quantity);
                JOptionPane.showMessageDialog(orderPanel, "Order created successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(orderPanel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        inputPanel.add(new JLabel("Customer:"));
        inputPanel.add(orderCustField);
        inputPanel.add(new JLabel("Product:"));
        inputPanel.add(orderProdField);
        inputPanel.add(new JLabel("Qty:"));
        inputPanel.add(orderQtyField);
        inputPanel.add(createOrderButton);
        inputPanel.add(historyButton);

        orderPanel.add(inputPanel, BorderLayout.NORTH);
        orderPanel.add(scrollPane, BorderLayout.CENTER);

        return orderPanel;
    }
}
