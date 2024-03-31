package ctu.cit;

import javax.json.JsonValue;
import javax.persistence.*;

import sun.java2d.cmm.Profile;

import java.util.Date;

@Entity
@Table(name = "projects")
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "profiles_id", referencedColumnName = "id")
    private Profile profile;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Profile getProfile() {
        return profile;
    }
    
    public void setProfile(int profilesId) {
        this.profile = profile;
    }
    
    

    // Constructors
    public Projects() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Projects(int id, String title, String startDate, String endDate, String description, Profile profile) {
        super();
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.profile = profile;
    }

	public JsonValue getProfilesId() {
		// TODO Auto-generated method stub
		return null;
	}
}