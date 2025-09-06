/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lisovskiy.studentslab.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "Users")
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
    @NamedQuery(name = "Users.findBySecondName", query = "SELECT u FROM Users u WHERE u.secondName = :secondName"),
    @NamedQuery(name = "Users.findByFirstName", query = "SELECT u FROM Users u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "Users.findByMiddleName", query = "SELECT u FROM Users u WHERE u.middleName = :middleName"),
    @NamedQuery(name = "Users.findByDateOfBirth", query = "SELECT u FROM Users u WHERE u.dateOfBirth = :dateOfBirth"),
    @NamedQuery(name = "Users.findByDateOfEmployment", query = "SELECT u FROM Users u WHERE u.dateOfEmployment = :dateOfEmployment"),
    @NamedQuery(name = "Users.findByPassportData", query = "SELECT u FROM Users u WHERE u.passportData = :passportData"),
    @NamedQuery(name = "Users.findBySnils", query = "SELECT u FROM Users u WHERE u.snils = :snils"),
    @NamedQuery(name = "Users.findByPhotoLink", query = "SELECT u FROM Users u WHERE u.photoLink = :photoLink"),
    @NamedQuery(name = "Users.findByLogins", query = "SELECT u FROM Users u WHERE u.logins = :logins"),
    @NamedQuery(name = "Users.findByPasswords", query = "SELECT u FROM Users u WHERE u.passwords = :passwords")})
public class Users implements Serializable {

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
    @Column(name = "DateOfBirth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Column(name = "DateOfEmployment")
    @Temporal(TemporalType.DATE)
    private Date dateOfEmployment;
    @Column(name = "PassportData")
    private String passportData;
    @Column(name = "SNILS")
    private String snils;
    @Column(name = "PhotoLink")
    private String photoLink;
    @Basic(optional = false)
    @Column(name = "Logins")
    private String logins;
    @Basic(optional = false)
    @Column(name = "Passwords")
    private String passwords;
    @OneToMany(mappedBy = "userId")
    private Collection<ClassTeacher> classTeacherCollection;
    @OneToMany(mappedBy = "userId")
    private Collection<Director> directorCollection;
    @OneToMany(mappedBy = "userId")
    private Collection<HeadOfDepartment> headOfDepartmentCollection;
    @OneToMany(mappedBy = "userId")
    private Collection<Student> studentCollection;

    public Users() {
    }

    public Users(Integer id) {
        this.id = id;
    }

    public Users(Integer id, String secondName, String firstName, String logins, String passwords) {
        this.id = id;
        this.secondName = secondName;
        this.firstName = firstName;
        this.logins = logins;
        this.passwords = passwords;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfEmployment() {
        return dateOfEmployment;
    }

    public void setDateOfEmployment(Date dateOfEmployment) {
        this.dateOfEmployment = dateOfEmployment;
    }

    public String getPassportData() {
        return passportData;
    }

    public void setPassportData(String passportData) {
        this.passportData = passportData;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getLogins() {
        return logins;
    }

    public void setLogins(String logins) {
        this.logins = logins;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public Collection<ClassTeacher> getClassTeacherCollection() {
        return classTeacherCollection;
    }

    public void setClassTeacherCollection(Collection<ClassTeacher> classTeacherCollection) {
        this.classTeacherCollection = classTeacherCollection;
    }

    public Collection<Director> getDirectorCollection() {
        return directorCollection;
    }

    public void setDirectorCollection(Collection<Director> directorCollection) {
        this.directorCollection = directorCollection;
    }

    public Collection<HeadOfDepartment> getHeadOfDepartmentCollection() {
        return headOfDepartmentCollection;
    }

    public void setHeadOfDepartmentCollection(Collection<HeadOfDepartment> headOfDepartmentCollection) {
        this.headOfDepartmentCollection = headOfDepartmentCollection;
    }

    public Collection<Student> getStudentCollection() {
        return studentCollection;
    }

    public void setStudentCollection(Collection<Student> studentCollection) {
        this.studentCollection = studentCollection;
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
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "lisovskiy.studentslab.entity.Users[ id=" + id + " ]";
    }
    
}
