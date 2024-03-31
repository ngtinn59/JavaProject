
public class Users {
	private  int user_id;
	private String user_name;
	private String email_id;
	private String role;
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Users(int user_id, String user_name, String email_id, String role) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
		this.email_id = email_id;
		this.role = role;
	}
	public Users() {
		super();
		// TODO Auto-generated constructor stub
	}

}
