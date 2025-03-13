import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo {

    public void createOrder(String customerName, String productName, int quantity) {
        String findCustomerSql = "SELECT id FROM customers WHERE name = ?";
        String findProductSql  = "SELECT id, stock_quantity FROM products WHERE name = ?";
        String insertOrderSql  = "INSERT INTO orders (customer_id, product_id, quantity, order_date) VALUES (?, ?, ?, NOW())";
        String updateStockSql  = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int customerId;
            int productId;
            int currentStock;

            //hämta kund ID
            try (PreparedStatement stmt = conn.prepareStatement(findCustomerSql)) {
                stmt.setString(1, customerName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    customerId = rs.getInt("id");
                } else {
                    conn.rollback();
                    throw new SQLException("Customer not found: " + customerName);
                }
            }

            //hämta produkt ID + lagerstatus
            try (PreparedStatement stmt = conn.prepareStatement(findProductSql)) {
                stmt.setString(1, productName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    productId    = rs.getInt("id");
                    currentStock = rs.getInt("stock_quantity");
                } else {
                    conn.rollback();
                    throw new SQLException("Product not found: " + productName);
                }
            }

            //kontrollera lagerstatus
            if (currentStock < quantity) {
                conn.rollback();
                throw new SQLException("Insufficient stock for product: " + productName);
            }

            //lägg till order
            try (PreparedStatement stmt = conn.prepareStatement(insertOrderSql)) {
                stmt.setInt(1, customerId);
                stmt.setInt(2, productId);
                stmt.setInt(3, quantity);
                stmt.executeUpdate();
            }

            //uppdatera lagerstaus
            try (PreparedStatement stmt = conn.prepareStatement(updateStockSql)) {
                stmt.setInt(1, quantity);
                stmt.setInt(2, productId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    }
