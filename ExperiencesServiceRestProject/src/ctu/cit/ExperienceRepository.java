package ctu.cit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExperienceRepository {

    Connection conn = null;

    public ExperienceRepository() {
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

    public Experience insertExperience(Experience experience, int profilesId) {
        String sql = "INSERT INTO public.experiences(" +
                "position, company, responsibilities, profiles_id, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, experience.getPosition());
            pstmt.setString(2, experience.getCompany());
            pstmt.setString(3, experience.getResponsibilities());
            pstmt.setInt(4, profilesId);
            pstmt.setString(5, experience.getStartDate()); 
            pstmt.setString(6, experience.getEndDate()); 

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insertion failed, no rows affected.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    experience.setId(id);
                    return experience;
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting experience into the database: " + e.getMessage());
            return null;
        }
    }


    public List<Experience> getAllExperienceByProfileId(int profileId) {
        List<Experience> experiences = new ArrayList<>();
        
        String sql = "SELECT * FROM public.experiences WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Experience experience = new Experience();
                    experience.setId(rs.getInt("id"));
                    experience.setPosition(rs.getString("position"));
                    experience.setCompany(rs.getString("company"));
                    experience.setStartDate(rs.getString("start_date"));
                    experience.setEndDate(rs.getString("end_date"));

                    experience.setResponsibilities(rs.getString("responsibilities"));
                    
                    experiences.add(experience);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving experiences from the database: " + e.getMessage());
        }
        
        return experiences;
    }

    public Experience getExperienceById(long experienceId, int profileId) {
        Experience experience = null;
        
        String sql = "SELECT id, position, company, start_date, end_date, responsibilities " +
                     "FROM public.experiences WHERE id = ? AND profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, experienceId);
            pstmt.setInt(2, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    experience = new Experience();
                    experience.setId(rs.getInt("id"));
                    experience.setPosition(rs.getString("position"));
                    experience.setCompany(rs.getString("company"));
                    experience.setStartDate(rs.getString("start_date"));
                    experience.setEndDate(rs.getString("end_date"));
                    experience.setResponsibilities(rs.getString("responsibilities"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving experience from the database: " + e.getMessage());
        }
        
        return experience;
    }
    
   
    public Experience updateExperience(Experience experience) {
        String sql = "UPDATE public.experiences " +
                     "SET position = ?, company = ?, start_date = ?, end_date = ?, responsibilities = ? " +
                     "WHERE id = ? " +
                     "RETURNING *";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, experience.getPosition());
            pstmt.setString(2, experience.getCompany());
            pstmt.setString(3, experience.getStartDate());
            pstmt.setString(4, experience.getEndDate());
            pstmt.setString(5, experience.getResponsibilities());
            pstmt.setLong(6, experience.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Experience updatedExperience = new Experience();
                    updatedExperience.setId(rs.getInt("id"));
                    updatedExperience.setPosition(rs.getString("position"));
                    updatedExperience.setCompany(rs.getString("company"));
                    updatedExperience.setStartDate(rs.getString("start_date"));
                    updatedExperience.setEndDate(rs.getString("end_date"));
                    updatedExperience.setResponsibilities(rs.getString("responsibilities"));
                    return updatedExperience;
                } else {
                    throw new SQLException("Update failed");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while updating experience in the database: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteExperience(long experienceId) {
        String sql = "DELETE FROM public.experiences WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, experienceId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting experience from the database: " + e.getMessage());
            return false;
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // Tạo một instance của ExperienceRepository
        ExperienceRepository repository = new ExperienceRepository();

        // Tạo một đối tượng Experience mới để thêm vào cơ sở dữ liệu
        Experience newExperience = new Experience();
        newExperience.setPosition("Software Engineer");
        newExperience.setCompany("ABC Company");
        newExperience.setResponsibilities("Developing new features");
        newExperience.setStartDate("2022-01-01");
        newExperience.setEndDate("2023-01-01");

        // Gọi phương thức insertExperience để chèn Experience mới vào cơ sở dữ liệu
        // Ở đây bạn cần chuyển vào ID của profiles liên quan, giả sử ID của profile là 1
        Experience insertedExperience = repository.insertExperience(newExperience, 36);

        // Kiểm tra xem việc chèn có thành công hay không
        if (insertedExperience != null) {
            System.out.println("Experience inserted successfully with ID: " + insertedExperience.getId());
        } else {
            System.out.println("Failed to insert experience.");
        }
    }


}
