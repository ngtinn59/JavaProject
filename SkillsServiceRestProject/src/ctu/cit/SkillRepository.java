package ctu.cit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SkillRepository {

    Connection conn = null;

    public SkillRepository() {
        String user_name = "postgres";
        String password = "123";
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Skills Service", user_name, password);
            System.out.println("Ket noi thanh cong!");
        } catch (Exception e) {
            System.out.println("Kết nối đến cơ sở dữ liệu thất bại: " + e.getMessage());
        }
    }

    public Skill insertSkill(Skill skill, int profilesId) {
        String sql = "INSERT INTO public.skills(name, level, profiles_id) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, skill.getName());
            pstmt.setInt(2, skill.getLevel());
            pstmt.setInt(3, profilesId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insertion failed, no rows affected.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    skill.setId(id);
                    return skill;
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting skill into the database: " + e.getMessage());
            return null;
        }
    }

    public List<Skill> getAllSkillsByProfileId(int profileId) {
        List<Skill> skills = new ArrayList<>();

        String sql = "SELECT * FROM public.skills WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = new Skill();
                    skill.setId(rs.getLong("id"));
                    skill.setName(rs.getString("name"));
                    skill.setLevel(rs.getInt("level"));
                    skill.setProfilesId(rs.getLong("profiles_id"));

                    skills.add(skill);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving skills from the database: " + e.getMessage());
        }

        return skills;
    }

    public Skill getSkillById(long skillId, int profileId) {
        String sql = "SELECT * FROM public.skills WHERE id = ? AND profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, skillId);
            pstmt.setInt(2, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Skill skill = new Skill();
                    skill.setId(rs.getLong("id"));
                    skill.setName(rs.getString("name"));
                    skill.setLevel(rs.getInt("level"));
                    skill.setProfilesId(rs.getLong("profiles_id"));
                    return skill;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving skill from the database: " + e.getMessage());
        }
        return null;
    }

    public Skill updateSkill(Skill skill) {
        String sql = "UPDATE public.skills SET name = ?, level = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, skill.getName());
            pstmt.setInt(2, skill.getLevel());
            pstmt.setLong(3, skill.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return skill;
            }
        } catch (SQLException e) {
            System.out.println("Error while updating skill in the database: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteSkill(long profileId) {
        String sql = "DELETE FROM public.skills WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, profileId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting skills from the database: " + e.getMessage());
            return false;
        }
    }

    
    public static void main(String[] args) {
        // Tạo một instance của SkillRepository
        SkillRepository repository = new SkillRepository();

        // ID của người dùng cần xóa kỹ năng
        long profileIdToDelete = 43; // Thay đổi thành profileId thích hợp

        // Gọi phương thức deleteSkill và in ra kết quả
        boolean isDeleted = repository.deleteSkill(profileIdToDelete);
        if (isDeleted) {
            System.out.println("Skills for profile with ID " + profileIdToDelete + " have been deleted successfully.");
        } else {
            System.out.println("Failed to delete skills for profile with ID " + profileIdToDelete + ".");
        }
    }


    public List<Skill> insertMultipleSkills(List<Skill> newSkills, int profileId) {
        List<Skill> insertedSkills = new ArrayList<>();

        String sql = "INSERT INTO public.skills(name, level, profiles_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (Skill skill : newSkills) {
                pstmt.setString(1, skill.getName());
                pstmt.setInt(2, skill.getLevel());
                pstmt.setInt(3, profileId);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    System.out.println("Insertion failed for skill: " + skill.getName());
                    continue; // Skip to the next skill if insertion fails
                }

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        skill.setId(id);
                        insertedSkills.add(skill);
                    } else {
                        System.out.println("Insertion failed for skill: " + skill.getName() + ", no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting skills into the database: " + e.getMessage());
        }

        return insertedSkills;
    }

    public boolean hasSkills(int profileId) {
        String sql = "SELECT COUNT(*) FROM public.skills WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking for skills in the database: " + e.getMessage());
        }
        return false;
    }

	

}
