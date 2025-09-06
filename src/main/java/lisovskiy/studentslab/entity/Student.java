/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lisovskiy.studentslab.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 *
 * @author 8327
 */
@Entity
@Table(name = "Student")
@NamedQueries({
    @NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s"),
    @NamedQuery(name = "Student.findById", query = "SELECT s FROM Student s WHERE s.id = :id"),
    @NamedQuery(name = "Student.findByFamStatus", query = "SELECT s FROM Student s WHERE s.famStatus = :famStatus"),
    @NamedQuery(name = "Student.findByDateOfBirth", query = "SELECT s FROM Student s WHERE s.dateOfBirth = :dateOfBirth"),
    @NamedQuery(name = "Student.findByDateOfAdmission", query = "SELECT s FROM Student s WHERE s.dateOfAdmission = :dateOfAdmission"),
    @NamedQuery(name = "Student.findByCourse", query = "SELECT s FROM Student s WHERE s.course = :course")})
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "FamStatus")
    private String famStatus;
    @Column(name = "DateOfBirth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Column(name = "DateOfAdmission")
    @Temporal(TemporalType.DATE)
    private Date dateOfAdmission;
    @Column(name = "Course")
    private Integer course;
    @OneToMany(mappedBy = "studentid")
    private Collection<Feedback> feedbackCollection;
    @JsonIgnore
    @JoinColumn(name = "Faculty_id", referencedColumnName = "id")
    @ManyToOne
    private Faculty facultyid;
    @JsonIgnore
    @JoinColumn(name = "Specialty_id", referencedColumnName = "id")
    @ManyToOne
    private Specialty specialtyid;
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;
    @OneToMany(mappedBy = "studentid")
    private Collection<LegalRepresentative> legalRepresentativeCollection;

    public Student() {
    }

    public Student(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFamStatus() {
        return famStatus;
    }

    public void setFamStatus(String famStatus) {
        this.famStatus = famStatus;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfAdmission() {
        return dateOfAdmission;
    }

    public void setDateOfAdmission(Date dateOfAdmission) {
        this.dateOfAdmission = dateOfAdmission;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public Collection<Feedback> getFeedbackCollection() {
        return feedbackCollection;
    }

    public void setFeedbackCollection(Collection<Feedback> feedbackCollection) {
        this.feedbackCollection = feedbackCollection;
    }

    public Faculty getFacultyid() {
        return facultyid;
    }

    public void setFacultyid(Faculty facultyid) {
        this.facultyid = facultyid;
    }

    public Specialty getSpecialtyid() {
        return specialtyid;
    }

    public void setSpecialtyid(Specialty specialtyid) {
        this.specialtyid = specialtyid;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public Collection<LegalRepresentative> getLegalRepresentativeCollection() {
        return legalRepresentativeCollection;
    }

    public void setLegalRepresentativeCollection(Collection<LegalRepresentative> legalRepresentativeCollection) {
        this.legalRepresentativeCollection = legalRepresentativeCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Student)) {
            return false;
        }
        Student other = (Student) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "lisovskiy.studentslab.entity.Student[ id=" + id + " ]";
    }
    
}
