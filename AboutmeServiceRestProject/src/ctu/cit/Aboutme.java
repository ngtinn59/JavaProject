package ctu.cit;

import javax.json.JsonValue;
import javax.persistence.*;
import java.util.Date;
import sun.java2d.cmm.Profile;


@Entity
@Table(name = "aboutme")
public class Aboutme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;


    @SuppressWarnings("restriction")
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

   
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }


    public Aboutme() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Aboutme(int id, String description, Profile profile) {
		super();
		this.id = id;
		this.description = description;
		this.profile = profile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JsonValue getProfilesId() {
        // TODO Auto-generated method stub
        return null;
    }
}
