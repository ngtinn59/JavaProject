package ctu.cit;

import javax.json.JsonValue;
import javax.persistence.*;
import java.util.Date;
import sun.java2d.cmm.Profile;


@Entity
@Table(name = "educations")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String degree;

    private String institution;

    @Temporal(TemporalType.DATE)
    private String startDate;

    @Temporal(TemporalType.DATE)
    private String endDate;

    @Lob
    private String additionalDetail;

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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
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

    public String getAdditionalDetail() {
        return additionalDetail;
    }

    public void setAdditionalDetail(String additionalDetail) {
        this.additionalDetail = additionalDetail;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    // Constructors
    public Education() {
        super();
    }

    public Education(int id, String degree, String institution, String startDate, String endDate, String additionalDetail, Profile profile) {
        super();
        this.id = id;
        this.degree = degree;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        this.additionalDetail = additionalDetail;
        this.profile = profile;
    }

    public JsonValue getProfilesId() {
        // TODO Auto-generated method stub
        return null;
    }
}
