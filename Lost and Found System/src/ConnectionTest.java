import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ConnectionTest
 * --------------
 * Stage 2 test program. Confirms Java can talk to MySQL and
 * counts the rows in every table. (Can be deleted before submission.)
 */
public class ConnectionTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("          LOST AND FOUND SYSTEM");
        System.out.println("           CONNECTION TEST");
        System.out.println("========================================");

        if (!DatabaseConnection.testConnection()) {
            System.out.println("\nTest FAILED. Fix the problem above and run again.");
            return;
        }
        System.out.println("[OK] Connected to lost_and_found_db\n");

        String[] tables = {"users", "admins", "lost_items", "found_items", "claims"};

        // try-with-resources closes the Connection, PreparedStatement and ResultSet automatically
        try (Connection con = DatabaseConnection.getConnection()) {
            for (String table : tables) {
                String sql = "SELECT COUNT(*) FROM " + table;   // table names come from our own array, not user input
                try (PreparedStatement ps = con.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.printf("%-12s : %d row(s)%n", table, rs.getInt(1));
                    }
                }
            }
            System.out.println("\nTest PASSED. JDBC is working.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + DatabaseConnection.explain(e));
        }
    }
}
