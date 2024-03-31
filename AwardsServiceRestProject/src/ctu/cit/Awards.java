package ctu.cit;

import javax.persistence.*;
import java.util.Date;
import sun.java2d.cmm.Profile;

@Entity
@Table(name = "awards")
public class Awards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profiles_id")
    private Profile profile;

    private String title;
    private String provider;

    @Temporal(TemporalType.DATE)
    private String issueDate;

    private String description;

    // Constructors
    public Awards() {
    }

    public Awards(Profile profile, String title, String provider, String issueDate, String description) {
        this.profile = profile;
        this.title = title;
        this.provider = provider;
        this.issueDate = issueDate;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
