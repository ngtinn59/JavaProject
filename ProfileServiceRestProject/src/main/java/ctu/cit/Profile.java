package ctu.cit;

import javax.persistence.*;

import org.apache.catalina.User;


@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "users_id", nullable = false)
    private Long usersId;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String title;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String email;


    @Lob
    private String birthday;

    private String image;

    private Boolean gender;

    private String location;

    @Column(length = 100)
    private String website;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUsersId() {
		return usersId;
	}

	public void setUsersId(Long usersId) {
		this.usersId = usersId;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Profile(Long id, Long usersId, String name, String title, String phone, String email, String birthday,
			String image, Boolean gender, String location, String website, User user) {
		super();
		this.id = id;
		this.usersId = usersId;
		this.name = name;
		this.title = title;
		this.phone = phone;
		this.email = email;
		this.birthday = birthday;
		this.image = image;
		this.gender = gender;
		this.location = location;
		this.website = website;
		this.user = user;
	}

	public Profile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setUserId(int i) {
	    this.usersId = (long) i;
	}


    
    
    

}