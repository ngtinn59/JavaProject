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
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Profile Service", user_name, password);
            System.out.println("Ket noi thanh cong!");
        } catch (Exception e) {
            System.out.println("KÃ¡ÂºÂ¿t nÃ¡Â»â€˜i Ã„â€˜Ã¡ÂºÂ¿n cÃ†Â¡ sÃ¡Â»Å¸ dÃ¡Â»Â¯ liÃ¡Â»â€¡u thÃ¡ÂºÂ¥t bÃ¡ÂºÂ¡i: " + e.getMessage());
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



    public void insertProfile(Profile newProfile) {
        String sql = "INSERT INTO public.profiles (users_id, name, title, phone, email, image, gender, location, website, birthday) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, newProfile.getUsers_id());
            pstmt.setString(2, newProfile.getName());
            pstmt.setString(3, newProfile.getTitle());
            pstmt.setString(4, newProfile.getPhone());
            pstmt.setString(5, newProfile.getEmail());
            pstmt.setString(6, newProfile.getImage());
            pstmt.setBoolean(7, newProfile.getGender());
            pstmt.setString(8, newProfile.getLocation());
            pstmt.setString(9, newProfile.getWebsite());
            pstmt.setString(10, newProfile.getBirthday());

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





	public Profile getProfileById(int profileId) {
	    String sql = "SELECT * FROM public.profiles WHERE users_id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, profileId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            Profile profile = new Profile();
	            profile.setId(rs.getLong("id"));
	            profile.setUsersId(rs.getLong("users_id"));
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
	    return null; // Tráº£ vá»� null náº¿u khÃ´ng tÃ¬m tháº¥y profile
	}
	
	
	// PhÆ°Æ¡ng thá»©c nÃ y sáº½ tráº£ vá»� má»™t ImageIcon dá»±a trÃªn Ä‘Æ°á»�ng dáº«n cá»§a áº£nh lÆ°u trong profile
	public ImageIcon getImageForProfile(Profile profile) {
	    String imagePath = profile.getImage();
	    ImageIcon image = new ImageIcon(imagePath);
	    return image;
	}

	// HÃ m main hoáº·c phÆ°Æ¡ng thá»©c nÃ o Ä‘Ã³ trong giao diá»‡n ngÆ°á»�i dÃ¹ng cÃ³ thá»ƒ gá»�i phÆ°Æ¡ng thá»©c nÃ y Ä‘á»ƒ hiá»ƒn thá»‹ áº£nh
	public static void main(String[] args) {
		ProfileResponsitory repository = new ProfileResponsitory();
	    Profile profile = repository.getProfileById(1); // Láº¥y profile vá»›i ID 1

	    if (profile != null) {
	        ImageIcon imageIcon = repository.getImageForProfile(profile);
	        
	        // Táº¡o JFrame Ä‘á»ƒ hiá»ƒn thá»‹ áº£nh
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








	public void deleteProfile(Profile existingProfile) {
	    String sql = "DELETE FROM public.profiles WHERE id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setLong(1, existingProfile.getId());

	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Profile deleted successfully!");
	        } else {
	            System.out.println("Failed to delete profile!");
	        }
	    } catch (SQLException e) {
	        System.out.println("Error deleting profile: " + e.getMessage());
	    }
	}


    
}
