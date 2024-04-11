package ctu.cit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AboutmeRepository {

    private Connection conn;

    public AboutmeRepository() {
        String user_name = "postgres";
        String password = "123";
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/About Me Service", user_name, password);
            System.out.println("Kết nối thành công!");
        } catch (Exception e) {
            System.out.println("Kết nối đến cơ sở dữ liệu thất bại: " + e.getMessage());
        }
    }

    public Aboutme insertAboutme(Aboutme aboutme, int profilesId) {
        String sql = "INSERT INTO public.aboutme(profiles_id, description) VALUES (?, ?) RETURNING id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, profilesId);
            pstmt.setString(2, aboutme.getDescription());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insertion failed, no rows affected.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    aboutme.setId(id);
                    return aboutme;
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting aboutme into the database: " + e.getMessage());
            return null;
        }
    }

    public List<Aboutme> getAllAboutmeByProfileId(int profileId) {
        List<Aboutme> aboutmes = new ArrayList<>();
        
        String sql = "SELECT * FROM public.aboutme WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Aboutme aboutme = new Aboutme();
                    aboutme.setId(rs.getInt("id"));
                    aboutme.setDescription(rs.getString("description"));
                    aboutmes.add(aboutme);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving aboutme from the database: " + e.getMessage());
        }
        
        return aboutmes;
    }

    public Aboutme getAboutmeById(int aboutmeId, int profileId) {
        Aboutme aboutme = null;
        
        String sql = "SELECT id, profiles_id, description FROM public.aboutme WHERE id = ? AND profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, aboutmeId);
            pstmt.setInt(2, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    aboutme = new Aboutme();
                    aboutme.setId(rs.getInt("id"));
                    aboutme.setDescription(rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving aboutme from the database: " + e.getMessage());
        }
        
        return aboutme;
    }
    
    public List<Aboutme> getAboutmeByprofileId(int profileId) {
        List<Aboutme> aboutmes = new ArrayList<>();
        
        String sql = "SELECT id, profiles_id, description FROM public.aboutme WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Aboutme aboutme = new Aboutme();
                    aboutme.setId(rs.getInt("id"));
                    aboutme.setDescription(rs.getString("description"));
                    aboutmes.add(aboutme);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving aboutme from the database: " + e.getMessage());
        }
        
        return aboutmes;
    }

    
    public boolean updateAboutme(Aboutme aboutme, int profileId) {
        // Kiểm tra xem đối tượng aboutme và profileId có tồn tại không
        if (aboutme == null || profileId <= 0) {
            System.out.println("Error: Invalid Aboutme object or Profile ID.");
            return false;
        }

        String sql = "UPDATE public.aboutme SET description = ? WHERE id = ? AND profiles_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, aboutme.getDescription());
            pstmt.setInt(2, aboutme.getId());
            pstmt.setInt(3, profileId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while updating aboutme in the database: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteAboutme(int aboutmeId) {
        String sql = "DELETE FROM public.aboutme WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, aboutmeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting aboutme from the database: " + e.getMessage());
            return false;
        }
    }

    // Phương thức khác cũng có thể được thêm vào tùy theo yêu cầu

    public static void main(String[] args) {
        AboutmeRepository repository = new AboutmeRepository();

        // Thêm mới một Aboutme
        Aboutme newAboutme = new Aboutme();
        newAboutme.setDescription("This is a new Aboutme description.");
        Aboutme insertedAboutme = repository.insertAboutme(newAboutme, 1); // Thay 1 bằng profilesId thích hợp
        if (insertedAboutme != null) {
            System.out.println("Inserted Aboutme: " + insertedAboutme.getId());
        } else {
            System.out.println("Failed to insert Aboutme.");
        }

        // Lấy tất cả Aboutme theo profilesId
        List<Aboutme> aboutmes = repository.getAllAboutmeByProfileId(1); // Thay 1 bằng profilesId thích hợp
        System.out.println("Aboutme list:");
        for (Aboutme aboutme : aboutmes) {
            System.out.println("ID: " + aboutme.getId() + ", Description: " + aboutme.getDescription());
        }

        // Lấy một Aboutme theo ID và profilesId
        Aboutme retrievedAboutme = repository.getAboutmeById(1, 1); // Thay 1 bằng aboutmeId và profilesId thích hợp
        if (retrievedAboutme != null) {
            System.out.println("Retrieved Aboutme: ID: " + retrievedAboutme.getId() + ", Description: " + retrievedAboutme.getDescription());
        } else {
            System.out.println("Aboutme not found.");
        }

        // Cập nhật một Aboutme
        Aboutme aboutmeToUpdate = new Aboutme();
        aboutmeToUpdate.setId(1); // Thay 1 bằng ID của Aboutme cần cập nhật
        aboutmeToUpdate.setDescription("Updated description.");
        boolean updateResult = repository.updateAboutme(aboutmeToUpdate, 1); // Thay 1 bằng profilesId thích hợp
        if (updateResult) {
            System.out.println("Aboutme updated successfully.");
        } else {
            System.out.println("Failed to update Aboutme.");
        }

        // Xóa một Aboutme
        boolean deleteResult = repository.deleteAboutme(1); // Thay 1 bằng ID của Aboutme cần xóa
        if (deleteResult) {
            System.out.println("Aboutme deleted successfully.");
        } else {
            System.out.println("Failed to delete Aboutme.");
        }
    }
}
