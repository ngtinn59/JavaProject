package ctu.cit;

import java.sql.Date;

import javax.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    private String name;
    private String title;
    private String phone;
    private String email;
    private String birthday;
    private String image;
    private Boolean gender;
    private String location;
    private String website;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Boolean getGender() {
		return gender;
	}
	public void setGender(Boolean gender) {
		this.gender = gender;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public Profile() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Profile(int id, User user, String name, String title, String phone, String email, String birthday,
			String image, Boolean gender, String location, String website) {
		super();
		this.id = id;
		this.user = user;
		this.name = name;
		this.title = title;
		this.phone = phone;
		this.email = email;
		this.birthday = birthday;
		this.image = image;
		this.gender = gender;
		this.location = location;
		this.website = website;
	}

    // Getters and setters
}