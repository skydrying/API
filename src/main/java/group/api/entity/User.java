package group.api.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
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
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByMiddleName", query = "SELECT u FROM User u WHERE u.middleName = :middleName"),
    @NamedQuery(name = "User.findByPhone", query = "SELECT u FROM User u WHERE u.phone = :phone"),
    @NamedQuery(name = "User.findByDateOfBirth", query = "SELECT u FROM User u WHERE u.dateOfBirth = :dateOfBirth"),
    @NamedQuery(name = "User.findByDateOfEmployment", query = "SELECT u FROM User u WHERE u.dateOfEmployment = :dateOfEmployment"),
    @NamedQuery(name = "User.findByPassportData", query = "SELECT u FROM User u WHERE u.passportData = :passportData"),
    @NamedQuery(name = "User.findBySnils", query = "SELECT u FROM User u WHERE u.snils = :snils"),
    @NamedQuery(name = "User.findByPhotoLink", query = "SELECT u FROM User u WHERE u.photoLink = :photoLink"),
    @NamedQuery(name = "User.findByLogin", query = "SELECT u FROM User u WHERE u.login = :login"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "LastName")
    private String lastName;
    @Column(name = "FirstName")
    private String firstName;
    @Column(name = "MiddleName")
    private String middleName;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "dateOfBirth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Column(name = "dateOfEmployment")
    @Temporal(TemporalType.DATE)
    private Date dateOfEmployment;
    @Column(name = "passportData")
    private String passportData;
    @Column(name = "snils")
    private String snils;
    @Column(name = "photoLink")
    private String photoLink;
    @Column(name = "Login")
    private String login;
    @Column(name = "Password")
    private String password;
    @OneToMany(mappedBy = "idUser")
    private Collection<Seller> sellerCollection;
    @OneToMany(mappedBy = "idUser")
    private Collection<Director> directorCollection;
    @OneToMany(mappedBy = "productionMasterID")
    private Collection<CustomFrameOrder> customFrameOrderCollection;
    @OneToMany(mappedBy = "idUser")
    private Collection<Productionmaster> productionmasterCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sellerID")
    private Collection<Sale> saleCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sellerID")
    private Collection<Order> order1Collection;

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Seller> getSellerCollection() {
        return sellerCollection;
    }

    public void setSellerCollection(Collection<Seller> sellerCollection) {
        this.sellerCollection = sellerCollection;
    }

    public Collection<Director> getDirectorCollection() {
        return directorCollection;
    }

    public void setDirectorCollection(Collection<Director> directorCollection) {
        this.directorCollection = directorCollection;
    }

    public Collection<CustomFrameOrder> getCustomFrameOrderCollection() {
        return customFrameOrderCollection;
    }

    public void setCustomFrameOrderCollection(Collection<CustomFrameOrder> customFrameOrderCollection) {
        this.customFrameOrderCollection = customFrameOrderCollection;
    }

    public Collection<Productionmaster> getProductionmasterCollection() {
        return productionmasterCollection;
    }

    public void setProductionmasterCollection(Collection<Productionmaster> productionmasterCollection) {
        this.productionmasterCollection = productionmasterCollection;
    }

    public Collection<Sale> getSaleCollection() {
        return saleCollection;
    }

    public void setSaleCollection(Collection<Sale> saleCollection) {
        this.saleCollection = saleCollection;
    }

    public Collection<Order> getOrder1Collection() {
        return order1Collection;
    }

    public void setOrder1Collection(Collection<Order> order1Collection) {
        this.order1Collection = order1Collection;
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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.User[ id=" + id + " ]";
    }
    
}
