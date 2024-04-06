package ctu.cit;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ProfileResponsitory {

    Connection conn = null;

    public ProfileResponsitory() {
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

 

    

   
 

    public void updateProfile(Profile existingProfile) {
        String sql = "UPDATE public.profiles " +
                     "SET users_id=?, name=?, title=?, phone=?, email=?, image=?, gender=?, location=?, website=?, birthday=? " +
                     "WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, existingProfile.getUsersId());
            pstmt.setString(2, existingProfile.getName());
            pstmt.setString(3, existingProfile.getTitle());
            pstmt.setString(4, existingProfile.getPhone());
            pstmt.setString(5, existingProfile.getEmail());
            pstmt.setString(6, existingProfile.getImage());
            pstmt.setBoolean(7, existingProfile.getGender());
            pstmt.setString(8, existingProfile.getLocation());
            pstmt.setString(9, existingProfile.getWebsite());
            pstmt.setString(10, existingProfile.getBirthday());
            pstmt.setLong(11, existingProfile.getId());

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








	public Profile getProfileById(int profileId) {
	    String sql = "SELECT * FROM public.profiles WHERE id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, profileId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            Profile profile = new Profile();
	            profile.setId(rs.getLong("id"));
	            profile.setUserId(rs.getInt("users_id"));
	            profile.setName(rs.getString("name"));
	            profile.setTitle(rs.getString("title"));
	            profile.setPhone(rs.getString("phone"));
	            profile.setEmail(rs.getString("email"));
	            profile.setImage(rs.getString("image"));
	            profile.setGender(rs.getBoolean("gender"));
	            profile.setLocation(rs.getString("location"));
	            profile.setWebsite(rs.getString("website"));
	            profile.setBirthday(rs.getString("birthday"));
	            return profile;
	        }
	    } catch (SQLException e) {
	        System.out.println("Error retrieving profile: " + e.getMessage());
	    }
	    return null; // Trả về null nếu không tìm thấy profile
	}
	
	
	// Phương thức này sẽ trả về một ImageIcon dựa trên đường dẫn của ảnh lưu trong profile
	public ImageIcon getImageForProfile(Profile profile) {
	    String imagePath = profile.getImage();
	    ImageIcon image = new ImageIcon(imagePath);
	    return image;
	}

	// Hàm main hoặc phương thức nào đó trong giao diện người dùng có thể gọi phương thức này để hiển thị ảnh
	public static void main(String[] args) {
		ProfileResponsitory repository = new ProfileResponsitory();
	    Profile profile = repository.getProfileById(1); // Lấy profile với ID 1

	    if (profile != null) {
	        ImageIcon imageIcon = repository.getImageForProfile(profile);
	        
	        // Tạo JFrame để hiển thị ảnh
	        JFrame frame = new JFrame();
	        JLabel label = new JLabel(imageIcon);
	        frame.add(label);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
	        frame.setVisible(true);
	    } else {
	        System.out.println("Profile not found");
	    }
	}


    
}
