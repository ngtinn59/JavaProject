package ctu.cit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AwardsRepository {

    private Connection conn;

    public AwardsRepository() {
        String user_name = "postgres";
        String password = "123";
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cse", user_name, password);
            System.out.println("Káº¿t ná»‘i thÃ nh cÃ´ng!");
        } catch (Exception e) {
            System.out.println("Káº¿t ná»‘i Ä‘áº¿n cÆ¡ sá»Ÿ dá»¯ liá»‡u tháº¥t báº¡i: " + e.getMessage());
        }
    }

    public Awards insertAward(Awards award, int profilesId) {
        String sql = "INSERT INTO public.awards(profiles_id, title, provider, issueDate, description) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, profilesId);
            pstmt.setString(2, award.getTitle());
            pstmt.setString(3, award.getProvider());
            pstmt.setString(4, award.getIssueDate());
            pstmt.setString(5, award.getDescription());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insertion failed, no rows affected.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    award.setId(id);
                    return award;
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting award into the database: " + e.getMessage());
            return null;
        }
    }

    public List<Awards> getAllAwardsByProfileId(int profileId) {
        List<Awards> awardsList = new ArrayList<>();
        
        String sql = "SELECT * FROM public.awards WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Awards award = new Awards();
                    award.setId(rs.getLong("id"));
                    award.setTitle(rs.getString("title"));
                    award.setProvider(rs.getString("provider"));
                    award.setIssueDate(rs.getString("issueDate"));
                    award.setDescription(rs.getString("description"));
                    awardsList.add(award);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving awards from the database: " + e.getMessage());
        }
        
        return awardsList;
    }

    public Awards getAwardById(long awardId, int profileId) {
        Awards award = null;
        
        String sql = "SELECT id, profiles_id, title, provider, issueDate, description FROM public.awards WHERE id = ? AND profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, awardId);
            pstmt.setInt(2, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    award = new Awards();
                    award.setId(rs.getLong("id"));
                    award.setTitle(rs.getString("title"));
                    award.setProvider(rs.getString("provider"));
                    award.setIssueDate(rs.getString("issueDate"));
                    award.setDescription(rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving award from the database: " + e.getMessage());
        }
        
        return award;
    }
    
    public boolean updateAward(Awards award) {
        String sql = "UPDATE public.awards SET title = ?, provider = ?, issueDate = ?, description = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, award.getTitle());
            pstmt.setString(2, award.getProvider());
            pstmt.setString(3, award.getIssueDate());
            pstmt.setString(4, award.getDescription());
            pstmt.setLong(5, award.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while updating award in the database: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAward(long awardId) {
        String sql = "DELETE FROM public.awards WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, awardId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting award from the database: " + e.getMessage());
            return false;
        }
    }

    // Other methods can be added as needed

    public static void main(String[] args) {
        // Experimentation code or calling methods from here
    }
}
