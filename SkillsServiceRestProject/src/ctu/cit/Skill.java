package ctu.cit;

import javax.persistence.*;
import sun.java2d.cmm.Profile;


@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "profiles_id")
    private long profilesId;

    private String name;

    private int level;

    @ManyToOne
    @JoinColumn(name = "profiles_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Profile profile;

    // Constructors
    public Skill() {
        super();
    }

    public Skill(long id, long profilesId, String name, int level) {
        super();
        this.id = id;
        this.profilesId = profilesId;
        this.name = name;
        this.level = level;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProfilesId() {
        return profilesId;
    }

    public void setProfilesId(long profilesId) {
        this.profilesId = profilesId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
