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
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cse", user_name, password);
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

    public boolean deleteSkill(long skillId) {
        String sql = "DELETE FROM public.skills WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, skillId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting skill from the database: " + e.getMessage());
            return false;
        }
    }
    
    public static void main(String[] args) {
        // Tạo một instance của SkillRepository
        SkillRepository repository = new SkillRepository();

        // Tạo một skill mới và chèn vào cơ sở dữ liệu
        Skill newSkill = new Skill();
        newSkill.setName("Java");
        newSkill.setLevel(2);
        Skill insertedSkill = repository.insertSkill(newSkill, 1);

        // Kiểm tra xem việc chèn skill mới thành công hay không
        if (insertedSkill != null) {
            System.out.println("Inserted skill ID: " + insertedSkill.getId());
        } else {
            System.out.println("Failed to insert skill.");
        }
    }
}
