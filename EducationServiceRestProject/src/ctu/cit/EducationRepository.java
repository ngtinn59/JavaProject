package ctu.cit;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EducationRepository {

    Connection conn = null;

    public EducationRepository() {
        String user_name = "postgres";
        String password = "123";
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cse", user_name, password);
            System.out.println("Ket noi thanh cong!");
        } catch (Exception e) {
            System.out.println("Káº¿t ná»‘i Ä‘áº¿n cÆ¡ sá»Ÿ dá»¯ liá»‡u tháº¥t báº¡i: " + e.getMessage());
        }
    }

    public Education insertEducation(Education education, int profilesId) {
        String sql = "INSERT INTO public.educations(\r\n" + 
                "degree, institution, additionaldetail, start_date, end_date, profiles_id)\r\n" + 
                "VALUES (?, ?, ?, ?, ?, ?)" +
                "RETURNING id"; // Trả về chỉ ID thay vì tất cả các trường

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, education.getDegree());
            pstmt.setString(2, education.getInstitution());
            pstmt.setString(3, education.getAdditionalDetail());
            pstmt.setString(4, education.getStartDate()); // Chuyển đổi từ java.util.Date sang java.sql.Date
            pstmt.setString(5, education.getEndDate()); // Chuyển đổi từ java.util.Date sang java.sql.Date
            pstmt.setInt(6, profilesId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insertion failed, no rows affected.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    education.setId(id);
                    return education;
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting education into the database: " + e.getMessage());
            return null;
        }
    }

    public List<Education> getAllEducationByProfileId(int profileId) {
        List<Education> educations = new ArrayList<>();
        
        String sql = "SELECT * FROM public.educations WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Education education = new Education();
                    education.setId(rs.getInt("id"));
                    education.setDegree(rs.getString("degree"));
                    education.setInstitution(rs.getString("institution"));
                    education.setStartDate(rs.getString("start_date"));
                    education.setEndDate(rs.getString("end_date"));
                    education.setAdditionalDetail(rs.getString("additionaldetail"));
                    
                    educations.add(education);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving educations from the database: " + e.getMessage());
        }
        
        return educations;
    }

    public Education getEducationById(int educationId, int profileId) {
        Education education = null;
        
        String sql = "SELECT id, degree, institution, additionaldetail, start_date, end_date " +
                     "FROM public.educations WHERE id = ? AND profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, educationId);
            pstmt.setInt(2, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    education = new Education();
                    education.setId(rs.getInt("id"));
                    education.setDegree(rs.getString("degree"));
                    education.setInstitution(rs.getString("institution"));
                    education.setAdditionalDetail(rs.getString("additionaldetail"));
                    education.setStartDate(rs.getString("start_date"));
                    education.setEndDate(rs.getString("end_date"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving education from the database: " + e.getMessage());
        }
        
        return education;
    }
    
   
    public Education updateEducation(Education education) {
        String sql = "UPDATE public.educations " +
                     "SET degree = ?, institution = ?, additionaldetail = ?, start_date = ?, end_date = ? " +
                     "WHERE id = ? " +
                     "RETURNING *";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, education.getDegree());
            pstmt.setString(2, education.getInstitution());
            pstmt.setString(3, education.getAdditionalDetail());
            pstmt.setDate(4, Date.valueOf(education.getStartDate()));
            pstmt.setDate(5, Date.valueOf(education.getEndDate()));
            pstmt.setInt(6, education.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Education updatedEducation = new Education();
                    updatedEducation.setId(rs.getInt("id"));
                    updatedEducation.setDegree(rs.getString("degree"));
                    updatedEducation.setInstitution(rs.getString("institution"));
                    updatedEducation.setAdditionalDetail(rs.getString("additionaldetail"));
                    updatedEducation.setStartDate(rs.getString("start_date"));
                    updatedEducation.setEndDate(rs.getString("end_date"));
                    return updatedEducation;
                } else {
                    throw new SQLException("Update failed");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while updating education in the database: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteEducation(int educationId) {
        String sql = "DELETE FROM public.educations WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, educationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting education from the database: " + e.getMessage());
            return false;
        }
    }

  
    public static void main(String[] args) {
        // Tạo một đối tượng EducationRepository
        
    }


}
