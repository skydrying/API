package group.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "orders")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o"),
    @NamedQuery(name = "Orders.findById", query = "SELECT o FROM Orders o WHERE o.id = :id"),
    @NamedQuery(name = "Orders.findByOrderDate", query = "SELECT o FROM Orders o WHERE o.orderDate = :orderDate"),
    @NamedQuery(name = "Orders.findByTotalAmount", query = "SELECT o FROM Orders o WHERE o.totalAmount = :totalAmount"),
    @NamedQuery(name = "Orders.findByStatus", query = "SELECT o FROM Orders o WHERE o.status = :status"),
    @NamedQuery(name = "Orders.findByDueDate", query = "SELECT o FROM Orders o WHERE o.dueDate = :dueDate"),
    @NamedQuery(name = "Orders.findByCompletionDate", query = "SELECT o FROM Orders o WHERE o.completionDate = :completionDate")})
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "OrderDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    @Column(name = "TotalAmount")
    private Integer totalAmount;
    @Column(name = "Status")
    private String status;
    @Column(name = "DueDate")
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Column(name = "CompletionDate")
    @Temporal(TemporalType.DATE)
    private Date completionDate;
    @Lob
    @Column(name = "Notes")
    private String notes;
    @JsonIgnore
    @JoinColumn(name = "CustomerID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Customer customerID;
    @JsonIgnore
    @JoinColumn(name = "ProductionMasterID", referencedColumnName = "id")
    @ManyToOne
    private Productionmaster productionMasterID;
    @JsonIgnore
    @JoinColumn(name = "SellerID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private User sellerID;

    public Orders() {
    }

    public Orders(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Customer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Customer customerID) {
        this.customerID = customerID;
    }

    public Productionmaster getProductionMasterID() {
        return productionMasterID;
    }

    public void setProductionMasterID(Productionmaster productionMasterID) {
        this.productionMasterID = productionMasterID;
    }

    public User getSellerID() {
        return sellerID;
    }

    public void setSellerID(User sellerID) {
        this.sellerID = sellerID;
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
        if (!(object instanceof Orders)) {
            return false;
        }
        Orders other = (Orders) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.Orders[ id=" + id + " ]";
    }
    
}
