import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepo {
    // Point to your SQLite file (or other DB as needed)
    private static final String DB_URL = "jdbc:sqlite:webbutiken.db";
    private Connection connection;

    // Constructor: open a connection; create table/column if needed
    public ProductRepo() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
            ensureStockQuantityColumnExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creates the products table (without stock_quantity column) if it doesn’t exist
    private void createTableIfNotExists() {
        try {
            ensureConnection();
            String sql = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "price REAL" +
                    ")";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Checks if 'stock_quantity' column exists; if not, adds it
    private void ensureStockQuantityColumnExists() {
        try {
            ensureConnection();
            String sql = "PRAGMA table_info(products)";
            boolean hasStockQuantity = false;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String colName = rs.getString("name");
                    if ("stock_quantity".equalsIgnoreCase(colName)) {
                        hasStockQuantity = true;
                        break;
                    }
                }
            }
            if (!hasStockQuantity) {
                String alterSql = "ALTER TABLE products ADD COLUMN stock_quantity INTEGER DEFAULT 0";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(alterSql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ensures the connection is still valid/active
    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
    }

    // Retrieve all products; show name, price, stock_quantity, and stock status
    public List<String> getAllProducts() {
        List<String> products = new ArrayList<>();
        String sql = "SELECT name, price, stock_quantity FROM products";
        try {
            ensureConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name  = rs.getString("name");
                    double price = rs.getDouble("price");
                    int stockQty = rs.getInt("stock_quantity");
                    String status = (stockQty > 0) ? "In Stock" : "Out of Stock";
                    products.add(String.format(
                            "Name: %s | Price: %.2f | Stock: %d (%s)",
                            name, price, stockQty, status
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Search for products by name (partial match) and return details
    public List<String> searchProducts(String keyword) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT name, price, stock_quantity FROM products WHERE name LIKE ?";
        try {
            ensureConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, "%" + keyword + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String name  = rs.getString("name");
                        double price = rs.getDouble("price");
                        int stockQty = rs.getInt("stock_quantity");
                        String status = (stockQty > 0) ? "In Stock" : "Out of Stock";
                        results.add(String.format(
                                "Name: %s | Price: %.2f | Stock: %d (%s)",
                                name, price, stockQty, status
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }


    // Update stock_quantity for a product by name
    public void updateStock(String productName, int newStockQuantity) {
        String sql = "UPDATE products SET stock_quantity = ? WHERE name = ?";
        try {
            ensureConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, newStockQuantity);
                stmt.setString(2, productName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
