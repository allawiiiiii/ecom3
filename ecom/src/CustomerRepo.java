import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRepo {

    public void registerCustomer(Customer customer) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO customers (name, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getEmail());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateCustomerEmail(String name, String newEmail) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE customers SET email = ? WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setString(2, name);
            stmt.executeUpdate();
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean customerExists(String name) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT COUNT(*) FROM customers WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
