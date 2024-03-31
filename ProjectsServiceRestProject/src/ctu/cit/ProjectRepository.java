package ctu.cit;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {

    Connection conn = null;

    public ProjectRepository() {
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
    public Projects insertProject(Projects project, int profilesId) {
    	  String sql = "INSERT INTO public.projects(" +
    	        "title, description, profiles_id, start_date, end_date)" +
    	        "VALUES (?, ?, ?, ?, ?)" +
    	        "RETURNING *";
    	  try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    	    pstmt.setString(1, project.getTitle());
    	    pstmt.setString(2, project.getDescription());
    	    pstmt.setInt(3, profilesId);
    	    pstmt.setDate(4, Date.valueOf(project.getStartDate()));
    	    pstmt.setDate(5, Date.valueOf(project.getEndDate()));
    	    try (ResultSet rs = pstmt.executeQuery()) {
    	      if (rs.next()) {
    	        Projects insertedProject = new Projects();
    	        insertedProject.setId(rs.getInt("id"));
    	        insertedProject.setTitle(rs.getString("title"));
    	        insertedProject.setDescription(rs.getString("description"));
    	        insertedProject.setProfile(rs.getInt("profiles_id"));
    	        insertedProject.setStartDate(rs.getDate("start_date").toString());
    	        insertedProject.setEndDate(rs.getDate("end_date").toString());
    	        return insertedProject;
    	      } else {
    	        throw new SQLException("Insertion failed");
    	      }
    	    }
    	  } catch (SQLException e) {
    	    System.out.println("Error while inserting project into the database: " + e.getMessage());
    	    return null;
    	  }
    	}
    public List<Projects> getAllProjectsByProfileId(int profileId) {
        List<Projects> projects = new ArrayList<>();
        
        String sql = "SELECT * FROM public.projects WHERE profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Projects project = new Projects();
                    project.setId(rs.getInt("id"));
                    project.setTitle(rs.getString("title"));
                    project.setDescription(rs.getString("description"));
                    project.setProfile(rs.getInt("profiles_id"));
                    project.setStartDate(rs.getString("start_date"));
                    project.setEndDate(rs.getString("end_date"));
                    
                    projects.add(project);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving projects from the database: " + e.getMessage());
        }
        
        return projects;
    }
    
    public Projects getProjectById(int projectId, int profileId) {
        Projects project = null;
        
        String sql = "SELECT id, title, description, profiles_id, start_date, end_date " +
                     "FROM public.projects WHERE id = ? AND profiles_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    project = new Projects();
                    project.setId(rs.getInt("id"));
                    project.setTitle(rs.getString("title"));
                    project.setDescription(rs.getString("description"));
                    project.setProfile(rs.getInt("profiles_id"));
                    project.setStartDate(rs.getString("start_date"));
                    project.setEndDate(rs.getString("end_date"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving project from the database: " + e.getMessage());
        }
        
        return project;
    }

    
    public Projects updateProject(Projects project) {
        String sql = "UPDATE public.projects " +
                     "SET title = ?, description = ?, start_date = ?, end_date = ? " +
                     "WHERE id = ? " +
                     "RETURNING *";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, project.getTitle());
            pstmt.setString(2, project.getDescription());
            pstmt.setDate(3, Date.valueOf(project.getStartDate()));
            pstmt.setDate(4, Date.valueOf(project.getEndDate()));
            pstmt.setInt(5, project.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Projects updatedProject = new Projects();
                    updatedProject.setId(rs.getInt("id"));
                    updatedProject.setTitle(rs.getString("title"));
                    updatedProject.setDescription(rs.getString("description"));
                    updatedProject.setStartDate(rs.getString("start_date"));
                    updatedProject.setEndDate(rs.getString("end_date"));
                    return updatedProject;
                } else {
                    throw new SQLException("Update failed");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while updating project in the database: " + e.getMessage());
            return null;
        }
    }
    
    public boolean deleteProject(int projectId) {
    	  String sql = "DELETE FROM public.projects WHERE id = ?";
    	  try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    	    pstmt.setInt(1, projectId);
    	    int rowsAffected = pstmt.executeUpdate();
    	    return rowsAffected > 0;
    	  } catch (SQLException e) {
    	    System.out.println("Error while deleting project from the database: " + e.getMessage());
    	    return false;
    	  }
    	}
    
    
    public static void main(String[] args) {
        // Tạo một đối tượng ProjectRepository
        ProjectRepository repository = new ProjectRepository();

        // Xóa dự án có ID là 1 (Ví dụ)
        int projectIdToDelete = 1;
        boolean isDeleted = repository.deleteProject(projectIdToDelete);

        // Kiểm tra xem việc xóa thành công hay không
        if (isDeleted) {
            System.out.println("Dự án có ID " + projectIdToDelete + " đã được xóa thành công.");
        } else {
            System.out.println("Xóa dự án thất bại hoặc không tìm thấy dự án có ID " + projectIdToDelete);
        }
    }
}