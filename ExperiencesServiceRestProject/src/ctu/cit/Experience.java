package ctu.cit;

import javax.json.JsonValue;
import javax.persistence.*;
import java.util.Date;
import sun.java2d.cmm.Profile;


@Entity
@Table(name = "educations")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String position;

    private String company;
    private String responsibilities;

    @Temporal(TemporalType.DATE)
    private String startDate;

    @Temporal(TemporalType.DATE)
    private String endDate;

 

    @SuppressWarnings("restriction")
    @ManyToOne
    @JoinColumn(name = "profiles_id", referencedColumnName = "id")
    private Profile profile;

    

    public Experience() {
		super();
		// TODO Auto-generated constructor stub
	}



	public Experience(int id, String position, String company, String responsibilities, String startDate,
			String endDate, Profile profile) {
		super();
		this.id = id;
		this.position = position;
		this.company = company;
		this.responsibilities = responsibilities;
		this.startDate = startDate;
		this.endDate = endDate;
		this.profile = profile;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getPosition() {
		return position;
	}



	public void setPosition(String position) {
		this.position = position;
	}



	public String getCompany() {
		return company;
	}



	public void setCompany(String company) {
		this.company = company;
	}



	public String getResponsibilities() {
		return responsibilities;
	}



	public void setResponsibilities(String responsibilities) {
		this.responsibilities = responsibilities;
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



	public Profile getProfile() {
		return profile;
	}



	public void setProfile(Profile profile) {
		this.profile = profile;
	}



	public JsonValue getProfilesId() {
        // TODO Auto-generated method stub
        return null;
    }
}
