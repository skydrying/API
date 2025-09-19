package group.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.io.Serializable;

@Entity
@Table(name = "sale_item")
@NamedQueries({
    @NamedQuery(name = "SaleItem.findAll", query = "SELECT s FROM SaleItem s"),
    @NamedQuery(name = "SaleItem.findById", query = "SELECT s FROM SaleItem s WHERE s.id = :id"),
    @NamedQuery(name = "SaleItem.findByProductType", query = "SELECT s FROM SaleItem s WHERE s.productType = :productType"),
    @NamedQuery(name = "SaleItem.findByProductID", query = "SELECT s FROM SaleItem s WHERE s.productID = :productID"),
    @NamedQuery(name = "SaleItem.findByQuantity", query = "SELECT s FROM SaleItem s WHERE s.quantity = :quantity"),
    @NamedQuery(name = "SaleItem.findByPrice", query = "SELECT s FROM SaleItem s WHERE s.price = :price"),
    @NamedQuery(name = "SaleItem.findBySubtotal", query = "SELECT s FROM SaleItem s WHERE s.subtotal = :subtotal")})
public class SaleItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "ProductType")
    private String productType;
    @Column(name = "ProductID")
    private Integer productID;
    @Column(name = "Quantity")
    private Integer quantity;
    @Column(name = "Price")
    private Integer price;
    @Column(name = "Subtotal")
    private Integer subtotal;
    @JsonIgnore
    @JoinColumn(name = "SaleID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Sale saleID;

    public SaleItem() {
    }

    public SaleItem(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Sale getSaleID() {
        return saleID;
    }

    public void setSaleID(Sale saleID) {
        this.saleID = saleID;
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
        if (!(object instanceof SaleItem)) {
            return false;
        }
        SaleItem other = (SaleItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "group.api.entity.SaleItem[ id=" + id + " ]";
    }
    
}
