package group.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
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
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "sale")
@NamedQueries({
    @NamedQuery(name = "Sale.findAll", query = "SELECT s FROM Sale s"),
    @NamedQuery(name = "Sale.findById", query = "SELECT s FROM Sale s WHERE s.id = :id"),
    @NamedQuery(name = "Sale.findBySaleDate", query = "SELECT s FROM Sale s WHERE s.saleDate = :saleDate"),
    @NamedQuery(name = "Sale.findByTotalAmount", query = "SELECT s FROM Sale s WHERE s.totalAmount = :totalAmount"),
    @NamedQuery(name = "Sale.findByDiscountAmount", query = "SELECT s FROM Sale s WHERE s.discountAmount = :discountAmount"),
    @NamedQuery(name = "Sale.findByFinalAmount", query = "SELECT s FROM Sale s WHERE s.finalAmount = :finalAmount")})
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "SaleDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date saleDate;
    @Column(name = "TotalAmount")
    private Integer totalAmount;
    @Column(name = "DiscountAmount")
    private Integer discountAmount;
    @Column(name = "FinalAmount")
    private Integer finalAmount;
    @JoinColumn(name = "CustomerID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Customer customerID;
    @JoinColumn(name = "SellerID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private User sellerID;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "saleID")
    private Collection<SaleItem> saleItemCollection;

    public Sale() {
    }

    public Sale(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Integer finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Customer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Customer customerID) {
        this.customerID = customerID;
    }

    public User getSellerID() {
        return sellerID;
    }

    public void setSellerID(User sellerID) {
        this.sellerID = sellerID;
    }

    public Collection<SaleItem> getSaleItemCollection() {
        return saleItemCollection;
    }

    public void setSaleItemCollection(Collection<SaleItem> saleItemCollection) {
        this.saleItemCollection = saleItemCollection;
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
        if (!(object instanceof Sale)) {
            return false;
        }
        Sale other = (Sale) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.Sale[ id=" + id + " ]";
    }
    
}
