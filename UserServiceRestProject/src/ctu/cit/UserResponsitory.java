package ctu.cit;
import java.sql.Connection;
import java.sql.Date;
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
            System.out.println("Ket noi thanh cong!");
        } catch (Exception e) {
            System.out.println("Kết nối đến cơ sở dữ liệu thất bại: " + e.getMessage());
        }
    }

    public void insertUser(User user) {
        String sql = "INSERT INTO users1 (name, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Người dùng được thêm vào cơ sở dữ liệu thành công!");
            } else {
                System.out.println("Thêm người dùng vào cơ sở dữ liệu không thành công!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm người dùng vào cơ sở dữ liệu: " + e.getMessage());
        }
    }
    
    public void insertProfile(Profile profile) {
        String sql = "INSERT INTO public.profiles(\r\n" + 
                "    id, users_id, name, title, phone, email, birthday, image, gender, location, website)\r\n" + 
                "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profile.getUser().getId());
            pstmt.setInt(2, profile.getUser().getId()); // Thiết lập cả user_id và id vì không rõ ý nghĩa của id trong profile
            pstmt.setString(3, profile.getName());
            pstmt.setString(4, profile.getTitle());
            pstmt.setString(5, profile.getPhone());
            pstmt.setString(6, profile.getEmail());
            pstmt.setString(7, profile.getBirthday());
            pstmt.setString(8, profile.getImage());
            pstmt.setBoolean(9, profile.getGender());
            pstmt.setString(10, profile.getLocation());
            pstmt.setString(11, profile.getWebsite());
          

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Profile inserted successfully!");
            } else {
                System.out.println("Failed to insert profile!");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting profile: " + e.getMessage());
        }
    }

   public static void main(String[] args) {
    // Tạo một đối tượng UserResponsitory
    UserResponsitory repository = new UserResponsitory();

    // Lấy profile cần cập nhật từ cơ sở dữ liệu
    Profile existingProfile = repository.getProfileByUserId(1); // Giả sử profile cần cập nhật có id là 1

    if (existingProfile != null) {
        // Cập nhật thông tin của profile
        existingProfile.setName("New Name");
        existingProfile.setTitle("New Title");
        existingProfile.setPhone("New Phone");
        existingProfile.setEmail("newemail@example.com");
        existingProfile.setImage("new-image-url");
        existingProfile.setGender(true);
        existingProfile.setLocation("New Location");
        existingProfile.setWebsite("New Website");

        // Gọi hàm updateProfile để cập nhật profile vào cơ sở dữ liệu
        repository.updateProfile(existingProfile);
    } else {
        System.out.println("Không tìm thấy profile cần cập nhật!");
    }
}


    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users1 WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
        return null;
    }

    public Profile getProfileByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE users_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile();
                    profile.setId(rs.getInt("id"));
                    profile.setUser(getUserById(rs.getInt("users_id")));
                    profile.setName(rs.getString("name"));
                    profile.setTitle(rs.getString("title"));
                    profile.setPhone(rs.getString("phone"));
                    profile.setEmail(rs.getString("email"));
                    profile.setBirthday(rs.getString("birthday"));
                    profile.setImage(rs.getString("image"));
                    profile.setGender(rs.getBoolean("gender"));
                    profile.setLocation(rs.getString("location"));
                    profile.setWebsite(rs.getString("website"));
                    return profile;
                }
            }
        } catch (SQLException e) {
            System.out.println("Damn! Error fetching profile: " + e.getMessage());
        }
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users1 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
        return null;
    }

    public void updateProfile(Profile existingProfile) {
        String sql = "UPDATE profiles SET name = ?, title = ?, phone = ?, email = ?, birthday = ?, image = ?, gender = ?, location = ?, website = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, existingProfile.getName());
            pstmt.setString(2, existingProfile.getTitle());
            pstmt.setString(3, existingProfile.getPhone());
            pstmt.setString(4, existingProfile.getEmail());
            pstmt.setString(5, existingProfile.getBirthday());
            pstmt.setString(6, existingProfile.getImage());
            pstmt.setBoolean(7, existingProfile.getGender());
            pstmt.setString(8, existingProfile.getLocation());
            pstmt.setString(9, existingProfile.getWebsite());
            pstmt.setInt(10, existingProfile.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile!");
            }
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    public Profile getProfileByEmail(String email) {
        String sql = "SELECT * FROM profiles WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile();
                    profile.setId(rs.getInt("id"));
                    profile.setUser(getUserById(rs.getInt("users_id")));
                    profile.setName(rs.getString("name"));
                    profile.setTitle(rs.getString("title"));
                    profile.setPhone(rs.getString("phone"));
                    profile.setEmail(rs.getString("email"));
                    profile.setBirthday(rs.getString("birthday"));
                    profile.setImage(rs.getString("image"));
                    profile.setGender(rs.getBoolean("gender"));
                    profile.setLocation(rs.getString("location"));
                    profile.setWebsite(rs.getString("website"));
                    return profile;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching profile by email: " + e.getMessage());
        }
        return null;
    }

    public Profile getProfileByEmails(String email) {
        String sql = "SELECT * FROM profiles INNER JOIN users1 ON profiles.users_id = users1.id WHERE users1.email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile();
                    profile.setId(rs.getInt("id"));
                    profile.setUser(getUserById(rs.getInt("users_id")));
                    profile.setName(rs.getString("name"));
                    profile.setTitle(rs.getString("title"));
                    profile.setPhone(rs.getString("phone"));
                    profile.setEmail(rs.getString("email"));
                    profile.setBirthday(rs.getString("birthday"));
                    profile.setImage(rs.getString("image"));
                    profile.setGender(rs.getBoolean("gender"));
                    profile.setLocation(rs.getString("location"));
                    profile.setWebsite(rs.getString("website"));
                    return profile;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching profile by email: " + e.getMessage());
        }
        return null;
    }


    
}
