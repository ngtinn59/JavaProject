import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserResponsitory {

    Connection conn = null;

    public UserResponsitory() {
        String user_name = "postgres";
        String password = "123";
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cse", user_name, password);
            System.out.println("Kết nối thành công đến cơ sở dữ liệu PostgreSQL!");
        } catch (Exception e) {
            System.out.println("Kết nối đến cơ sở dữ liệu thất bại: " + e.getMessage());
        }
    }

    public List<Users> getUsers() {
        List<Users> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int user_id = rs.getInt("user_id");
                String user_name = rs.getString("user_name");
                String email_id = rs.getString("email_id");
                String role = rs.getString("role");
                
                Users user = new Users(user_id, user_name, email_id, role);
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
        }
        return users;
    }

    public static void main(String[] args) {
        UserResponsitory repository = new UserResponsitory();
        List<Users> users = repository.getUsers();
        for (Users user : users) {
            System.out.println("User ID: " + user.getUser_id());
            System.out.println("User Name: " + user.getUser_name());
            System.out.println("Email ID: " + user.getEmail_id());
            System.out.println("Role: " + user.getRole());
            System.out.println();
        }
    }
}
