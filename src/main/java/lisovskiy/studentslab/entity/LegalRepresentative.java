/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lisovskiy.studentslab.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
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
import jakarta.persistence.Table;

/**
 *
 * @author 8327
 */
@Entity
@Table(name = "LegalRepresentative")
@NamedQueries({
    @NamedQuery(name = "LegalRepresentative.findAll", query = "SELECT l FROM LegalRepresentative l"),
    @NamedQuery(name = "LegalRepresentative.findById", query = "SELECT l FROM LegalRepresentative l WHERE l.id = :id"),
    @NamedQuery(name = "LegalRepresentative.findBySecondName", query = "SELECT l FROM LegalRepresentative l WHERE l.secondName = :secondName"),
    @NamedQuery(name = "LegalRepresentative.findByFirstName", query = "SELECT l FROM LegalRepresentative l WHERE l.firstName = :firstName"),
    @NamedQuery(name = "LegalRepresentative.findByMiddleName", query = "SELECT l FROM LegalRepresentative l WHERE l.middleName = :middleName"),
    @NamedQuery(name = "LegalRepresentative.findByHomeAddress", query = "SELECT l FROM LegalRepresentative l WHERE l.homeAddress = :homeAddress"),
    @NamedQuery(name = "LegalRepresentative.findByPhone", query = "SELECT l FROM LegalRepresentative l WHERE l.phone = :phone"),
    @NamedQuery(name = "LegalRepresentative.findByRelationshipDegree", query = "SELECT l FROM LegalRepresentative l WHERE l.relationshipDegree = :relationshipDegree")})
public class LegalRepresentative implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "SecondName")
    private String secondName;
    @Basic(optional = false)
    @Column(name = "FirstName")
    private String firstName;
    @Column(name = "MiddleName")
    private String middleName;
    @Column(name = "HomeAddress")
    private String homeAddress;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "RelationshipDegree")
    private String relationshipDegree;
    @JsonIgnore
    @JoinColumn(name = "Student_id", referencedColumnName = "id")
    @ManyToOne
    private Student studentid;

    public LegalRepresentative() {
    }

    public LegalRepresentative(Integer id) {
        this.id = id;
    }

    public LegalRepresentative(Integer id, String secondName, String firstName) {
        this.id = id;
        this.secondName = secondName;
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelationshipDegree() {
        return relationshipDegree;
    }

    public void setRelationshipDegree(String relationshipDegree) {
        this.relationshipDegree = relationshipDegree;
    }

    public Student getStudentid() {
        return studentid;
    }

    public void setStudentid(Student studentid) {
        this.studentid = studentid;
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
        if (!(object instanceof LegalRepresentative)) {
            return false;
        }
        LegalRepresentative other = (LegalRepresentative) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "lisovskiy.studentslab.entity.LegalRepresentative[ id=" + id + " ]";
    }
    
}
