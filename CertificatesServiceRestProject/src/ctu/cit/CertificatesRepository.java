package ctu.cit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CertificatesRepository {

    private Connection conn;

    public CertificatesRepository() {
        String user_name = "postgres";
        String password = "123";
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cse", user_name, password);
            System.out.println("Kết nối thành công!");
        } catch (Exception e) {
            System.out.println("Kết nối đến cơ sở dữ liệu thất bại: " + e.getMessage());
        }
    }

    public Certificate insertCertificate(Certificate certificate, int profilesId) {
        String sql = "INSERT INTO public.certificates(profiles_id, title, provider, issueDate, description, certificateUrl) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, profilesId);
            pstmt.setString(2, certificate.getTitle());
            pstmt.setString(3, certificate.getProvider());
            pstmt.setString(4, certificate.getIssueDate());
            pstmt.setString(5, certificate.getDescription());
            pstmt.setString(6, certificate.getCertificateUrl());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insertion failed, no rows affected.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    certificate.setId(id);
                    return certificate;
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting certificate into the database: " + e.getMessage());
            return null;
        }
    }

    public List<Certificate> getAllCertificatesByProfileId(int profileId) {
        List<Certificate> certificatesList = new ArrayList<>();
        
        String sql = "SELECT * FROM public.certificates WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Certificate certificate = new Certificate();
                    certificate.setId(rs.getLong("id"));
                    certificate.setTitle(rs.getString("title"));
                    certificate.setProvider(rs.getString("provider"));
                    certificate.setIssueDate(rs.getString("issueDate"));
                    certificate.setDescription(rs.getString("description"));
                    certificate.setCertificateUrl(rs.getString("certificateUrl"));
                    certificatesList.add(certificate);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving certificates from the database: " + e.getMessage());
        }
        
        return certificatesList;
    }

    public Certificate getCertificateById(long certificateId, int profileId) {
        Certificate certificate = null;
        
        String sql = "SELECT id, profiles_id, title, provider, issueDate, description, certificateUrl FROM public.certificates WHERE id = ? AND profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, certificateId);
            pstmt.setInt(2, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    certificate = new Certificate();
                    certificate.setId(rs.getLong("id"));
                    certificate.setTitle(rs.getString("title"));
                    certificate.setProvider(rs.getString("provider"));
                    certificate.setIssueDate(rs.getString("issueDate"));
                    certificate.setDescription(rs.getString("description"));
                    certificate.setCertificateUrl(rs.getString("certificateUrl"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving certificate from the database: " + e.getMessage());
        }
        
        return certificate;
    }
    
    public boolean updateCertificate(Certificate certificate) {
        String sql = "UPDATE public.certificates SET title = ?, provider = ?, issueDate = ?, description = ?, certificateUrl = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, certificate.getTitle());
            pstmt.setString(2, certificate.getProvider());
            pstmt.setString(3, certificate.getIssueDate());
            pstmt.setString(4, certificate.getDescription());
            pstmt.setString(5, certificate.getCertificateUrl());
            pstmt.setLong(6, certificate.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while updating certificate in the database: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCertificate(long certificateId) {
        String sql = "DELETE FROM public.certificates WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, certificateId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting certificate from the database: " + e.getMessage());
            return false;
        }
    }

    // Other methods can be added as needed

    public static void main(String[] args) {
        // Experimentation code or calling methods from here
    }
}
